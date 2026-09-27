package dev.celestiacraft.deep_tech.common.block.machine.bionic.advanced_sculk_furnace;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.gui.MachineItemSlots;
import dev.celestiacraft.deep_tech.api.gui.widget.EnergyBarWidget;
import dev.celestiacraft.deep_tech.api.gui.widget.VerticalProgressBarWidget;
import dev.celestiacraft.deep_tech.common.block.machine.bionic.BionicMachineBlockEntity;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.AdvancedSculkFurnaceConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 高级幽匿电炉: 普通幽匿电炉的升级版. 相比普通电炉:
 * <ul>
 *     <li>速度是普通的 {@code speed_multiplier} 倍(默认 2 倍);</li>
 *     <li>有更多的输入槽/输出槽;</li>
 *     <li>有更大的能量缓存;</li>
 *     <li>把可熔炼的方块放在机器顶面, 等处理时间走完就会把那个方块消耗掉, 产物进输出槽.</li>
 * </ul>
 * 配方直接复用原版熔炼({@link RecipeType#SMELTING}), 所以外面放个箱子/漏斗就能用,
 * 也可以被其他模组的"方块形式升级插件"当成普通熔炉来用.
 * <p>
 * 和别的仿生机器一样, 旁边贴加速插件还能再快(一个 x2, 最多两个), 见
 * {@link BionicMachineBlockEntity#applySpeedPlugins}.
 * <p>
 * 一次只处理一件事: 先看输入槽, 输入槽里没有能熔炼的东西时再看顶上的方块.
 * 换处理对象(换物品/配方)时进度会归零, 免得把上一个东西的进度带到下一个上.
 */
public class AdvancedSculkFurnaceBlockEntity extends BionicMachineBlockEntity<AdvancedSculkFurnaceBlockEntity> implements IUIHolder.BlockEntityUI {
	/**
	 * 当前要处理的东西: 输入槽里的某个物品, 或者顶上那个方块(的方块物品).
	 */
	private record Job(SmeltingRecipe recipe, ItemStack input, boolean fromBlock) {
		/**
		 * 和上一 tick 的处理对象是不是同一个. 相同就继续攒进度, 不同就从头开始.
		 */
		boolean sameAs(@Nullable Job other) {
			return other != null
					&& other.fromBlock == fromBlock
					&& other.recipe.getId().equals(recipe.getId())
					&& ItemStack.isSameItemSameTags(other.input, input);
		}
	}

	/**
	 * 给"单个物品"查熔炼配方用的容器, 复用一份, 避免每 tick 新建.
	 */
	private final SimpleContainer probe = new SimpleContainer(1);
	/**
	 * 正在处理的对象, 只用来判断进度要不要重算, 不需要存档(重新算一遍就行).
	 */
	@Nullable
	private Job activeJob;

	public AdvancedSculkFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getMachineMaxEnergy() {
		return AdvancedSculkFurnaceConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return AdvancedSculkFurnaceConfig.MAX_RECEIVE.get();
	}

	@Override
	public int getItemInputSlotCount() {
		return 2;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 2;
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, AdvancedSculkFurnaceBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		Job job = findJob(level, pos);

		// 没东西可处理
		if (job == null) {
			stopWorking(level, pos, state);
			return;
		}

		// 换处理对象了, 进度从头开始
		if (!job.sameAs(activeJob)) {
			activeJob = job;
			setProgress(0);
			setSyncCounter(0);
			sync();
		}

		int energyCost = AdvancedSculkFurnaceConfig.ENERGY_PER_TICK.get();
		setMaxProgress(getProcessTime(job));

		ItemStack output = job.recipe().getResultItem(level.registryAccess());
		boolean isWorking = canAcceptOutput(output) && getEnergy() >= energyCost;

		// 更新方块光照状态
		if (state.getValue(AdvancedSculkFurnaceBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(AdvancedSculkFurnaceBlock.LIT, isWorking), 3);
		}

		if (!isWorking) {
			setSyncCounter(0);
			return;
		}

		setEnergy(getEnergy() - energyCost);
		setProgress(getProgress() + 1);

		setSyncCounter(getSyncCounter() + 1);
		if (getSyncCounter() % 5 == 0) {
			sync();
		}

		if (getProgress() >= getMaxProgress()) {
			finishJob(level, pos, job, output);
		}
	}

	/**
	 * 找这一 tick 要处理的东西: 先输入槽, 后顶上放的方块.
	 *
	 * @return 没有能处理的东西时返回 null
	 */
	@Nullable
	private Job findJob(Level level, BlockPos pos) {
		for (int i = 0; i < getItemInputSlotCount(); i++) {
			ItemStack stack = getItemHandler().getStackInSlot(getItemInputSlotIndex(i));
			if (stack.isEmpty()) {
				continue;
			}

			SmeltingRecipe recipe = findRecipe(level, stack);
			if (recipe != null) {
				return new Job(recipe, stack.copy(), false);
			}
		}

		ItemStack blockItem = getBlockItemAbove(level, pos);
		if (blockItem.isEmpty()) {
			return null;
		}

		SmeltingRecipe recipe = findRecipe(level, blockItem);
		return recipe == null ? null : new Job(recipe, blockItem, true);
	}

	private SmeltingRecipe findRecipe(Level level, ItemStack stack) {
		probe.setItem(0, stack);
		return level.getRecipeManager()
				.getRecipeFor(RecipeType.SMELTING, probe, level)
				.orElse(null);
	}

	/**
	 * 机器顶上那个方块的方块物品, 没有方块物品的方块(水, 火之类)算作不能处理.
	 */
	private static ItemStack getBlockItemAbove(Level level, BlockPos pos) {
		BlockState above = level.getBlockState(pos.above());
		if (above.isAir()) {
			return ItemStack.EMPTY;
		}

		Item item = above.getBlock().asItem();
		return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
	}

	/**
	 * 处理一个东西要多少 tick:
	 * 输入槽里的物品按配置的基准时间(普通电炉是 100), 顶上的方块按配方本身的时间,
	 * 两者都先按机器自己的速度倍率缩短(默认 2 倍, 比普通电炉快一倍),
	 * 再算上旁边加速插件的倍率(一个 x2, 最多两个 x4).
	 */
	private int getProcessTime(Job job) {
		int base = job.fromBlock()
				? job.recipe().getCookingTime()
				: AdvancedSculkFurnaceConfig.PROCESS_TIME.get();

		int machineSpeed = Math.max(1, AdvancedSculkFurnaceConfig.SPEED_MULTIPLIER.get());
		return applySpeedPlugins(Math.max(1, base / machineSpeed));
	}

	/**
	 * 处理完成: 消耗掉处理对象, 产物放进输出槽.
	 */
	private void finishJob(Level level, BlockPos pos, Job job, ItemStack output) {
		if (job.fromBlock()) {
			// 顶上的方块还是原来那个才破坏它(中途被换掉的话进度早就归零了)
			if (ItemStack.isSameItemSameTags(getBlockItemAbove(level, pos), job.input())) {
				level.destroyBlock(pos.above(), false);
			}
		} else {
			consumeInput(job.input());
		}

		insertOutput(output);
		activeJob = null;
		setProgress(0);
		setSyncCounter(0);
		setChanged();
		sync();
	}

	/**
	 * 消耗掉一个和指定物品相同的输入物品.
	 */
	private void consumeInput(ItemStack input) {
		for (int i = 0; i < getItemInputSlotCount(); i++) {
			int slot = getItemInputSlotIndex(i);
			ItemStack stack = getItemHandler().getStackInSlot(slot);
			if (!stack.isEmpty() && ItemStack.isSameItemSameTags(stack, input)) {
				stack.shrink(1);
				return;
			}
		}
	}

	/**
	 * 输出槽里还有没有地方放这个产物.
	 */
	private boolean canAcceptOutput(ItemStack output) {
		for (int i = 0; i < getItemOutputSlotCount(); i++) {
			ItemStack current = getItemHandler().getStackInSlot(getItemOutputSlotIndex(i));
			if (current.isEmpty()) {
				return true;
			}
			if (ItemStack.isSameItemSameTags(current, output)
					&& current.getCount() + output.getCount() <= current.getMaxStackSize()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 把产物塞进输出槽. 用 setStackInSlot/grow 而不是 insertItem:
	 * {@code canInsertItem} 只允许输入槽插入, 输出槽是给机器自己用的.
	 */
	private void insertOutput(ItemStack output) {
		for (int i = 0; i < getItemOutputSlotCount(); i++) {
			int slot = getItemOutputSlotIndex(i);
			ItemStack current = getItemHandler().getStackInSlot(slot);

			if (current.isEmpty()) {
				getItemHandler().setStackInSlot(slot, output.copy());
				return;
			}
			if (ItemStack.isSameItemSameTags(current, output)
					&& current.getCount() + output.getCount() <= current.getMaxStackSize()) {
				current.grow(output.getCount());
				return;
			}
		}
	}

	private void stopWorking(Level level, BlockPos pos, BlockState state) {
		if (state.getValue(AdvancedSculkFurnaceBlock.LIT)) {
			level.setBlock(pos, state.setValue(AdvancedSculkFurnaceBlock.LIT, false), 3);
		}

		if (getProgress() > 0 || activeJob != null) {
			activeJob = null;
			setProgress(0);
			setSyncCounter(0);
			setChanged();
			sync();
		}

		setMaxProgress(AdvancedSculkFurnaceConfig.PROCESS_TIME.get());
	}

	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
		// 暂时借用普通幽匿电炉的 GUI 贴图
		group.setBackground(new ResourceTexture(DeepTech.loadGui("sculk_furnace")));

		LabelWidget title = new LabelWidget(
				8,
				8,
				MachineBlocks.ADVANCED_SCULK_FURNACE.get().getName()
		);
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				18,
				25,
				this::getEnergyStored,
				getMaxEnergyStored()
		));

		group.addWidget(new VerticalProgressBarWidget(
				68, 40, 14, 14,
				this::getProgress,
				this::getMaxProgress,
				new ResourceTexture(DeepTech.loadGui("elements/progress_furnace_back")),
				new ResourceTexture(DeepTech.loadGui("elements/progress_furnace_front"))
		));

		/*
		 * 输入槽和输出槽各两个, 竖着排: 上面那个正好落在借来的 GUI 画好的槽位框里,
		 * 下面那个暂时没有框(等以后画专门的 GUI 贴图).
		 */
		MachineItemSlots.addSlot(group, getItemHandler(), getItemInputSlotIndex(0), new Position(41, 38), true, true);
		MachineItemSlots.addSlot(group, getItemHandler(), getItemInputSlotIndex(1), new Position(41, 56), true, true);
		MachineItemSlots.addSlot(group, getItemHandler(), getItemOutputSlotIndex(0), new Position(97, 38), true, false);
		MachineItemSlots.addSlot(group, getItemHandler(), getItemOutputSlotIndex(1), new Position(97, 56), true, false);

		addPlayerInventory(group, player);
		return group;
	}
}

package dev.celestiacraft.deep_tech.common.block.machine.bionic;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * "会处理东西"的仿生机器基类: 把并行处理、顶面方块输入、进度/能量/光照这套公共逻辑收在一块.
 * <p>
 * 子类只要给出输入输出槽数量、实现 {@link #findRecipe}(查配方, 同时决定这一条线要多久/多少电)、
 * 自己画 GUI, 就自动获得:
 * <ul>
 *     <li>输入槽并行: 有几个槽能处理, 一轮就出几份产物(两个槽各放一个沙子, 一轮出两个);</li>
 *     <li>机器顶面放可处理的方块, 处理完把它消耗掉, 产物进输出槽;</li>
 *     <li>旁边加速插件的加速({@link BionicMachineBlockEntity#applySpeedPlugins});</li>
 *     <li>方块光照状态, 进度同步, 输出槽放不下时先不参与而不是卡住整台机器.</li>
 * </ul>
 * 一轮只有一份进度, 所以并行的几条线共用同一份时间(按最慢的那条算).
 */
public abstract class ParallelBionicMachineBlockEntity<T extends ParallelBionicMachineBlockEntity<T>> extends BionicMachineBlockEntity<T> implements IUIHolder.BlockEntityUI {
	/**
	 * 一条并行线要用的配方信息, 由子类查配方时填好.
	 *
	 * @param id          配方 id, 只用来判断处理对象换没换
	 * @param output      产物(基类不会改它, 用之前都会 copy)
	 * @param processTime 这一条线一轮要多少 tick, 机器自己的速度倍率已经算进去(加速插件在后面算)
	 * @param energyCost  这一条线每 tick 要多少 FE
	 */
	public record LaneRecipe(ResourceLocation id, ItemStack output, int processTime, int energyCost) {
	}

	/**
	 * 一条并行线: 某个输入槽里能处理的东西, 或者机器顶上那个方块.
	 * <p>
	 * {@code slot} 只有顶上那条线是 -1(它没有槽位, 直接对着 {@code pos.above()}).
	 */
	private record Lane(int slot, LaneRecipe recipe, ItemStack input, boolean fromBlock) {
	}

	/**
	 * 这一轮要处理的一组并行线, 以及一轮要花多少 tick.
	 */
	private record Job(List<Lane> lanes, boolean fromBlock, int processTime) {
		/**
		 * 和上一 tick 的处理对象是不是同一个. 相同就继续攒进度, 不同就从头开始.
		 */
		boolean sameAs(@Nullable Job other) {
			if (other == null || other.fromBlock != fromBlock || other.lanes.size() != lanes.size()) {
				return false;
			}

			for (int i = 0; i < lanes.size(); i++) {
				Lane mine = lanes.get(i);
				Lane theirs = other.lanes.get(i);

				if (mine.slot != theirs.slot
						|| !mine.recipe.id().equals(theirs.recipe.id())
						|| !ItemStack.isSameItemSameTags(mine.input, theirs.input)) {
					return false;
				}
			}

			return true;
		}
	}

	/**
	 * 查单个物品配方用的容器, 复用一份, 避免每 tick 新建.
	 */
	private final SimpleContainer probe = new SimpleContainer(1);
	/**
	 * 正在处理的对象, 只用来判断进度要不要重算, 不需要存档(重新算一遍就行).
	 */
	@Nullable
	private Job activeJob;

	public ParallelBionicMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ------------------------------------------------------------------
	// 子类实现
	// ------------------------------------------------------------------

	/**
	 * 查一个输入能处理成什么.
	 *
	 * @param input     要处理的物品(顶上的方块给的是它的方块物品)
	 * @param fromBlock 这条线是不是来自机器顶上的方块
	 * @return 没配方返回 null
	 */
	@Nullable
	protected abstract LaneRecipe findRecipe(Level level, ItemStack input, boolean fromBlock);

	/**
	 * 没有东西可处理时进度条上显示的时长, 只是个显示值.
	 */
	protected int getIdleProcessTime() {
		return 100;
	}

	/**
	 * 用复用容器查单个物品的配方, 免得每个子类都自己写一遍 setItem.
	 */
	@Nullable
	protected <R extends Recipe<Container>> R getRecipe(RecipeType<R> type, Level level, ItemStack input) {
		probe.setItem(0, input);
		return level.getRecipeManager()
				.getRecipeFor(type, probe, level)
				.orElse(null);
	}

	// ------------------------------------------------------------------
	// tick
	// ------------------------------------------------------------------

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, T entity) {
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

		// 输出槽放得下的那些并行线这一轮才干活
		List<Lane> working = selectWorkingLanes(level, job);

		// 电不够就少跑几条(能跑几条跑几条), 总比整台机器干等着强
		int affordable = affordableLanes(working);
		if (affordable < working.size()) {
			working = new ArrayList<>(working.subList(0, affordable));
		}

		setMaxProgress(job.processTime());

		boolean isWorking = !working.isEmpty();
		boolean hasLitState = state.hasProperty(BasicBlock.LIT);

		// 更新方块光照状态
		if (hasLitState && state.getValue(BasicBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(BasicBlock.LIT, isWorking), 3);
		}

		if (!isWorking) {
			setSyncCounter(0);
			return;
		}

		setEnergy(getEnergy() - energyCost(working));
		setProgress(getProgress() + 1);

		setSyncCounter(getSyncCounter() + 1);
		if (getSyncCounter() % 5 == 0) {
			sync();
		}

		if (getProgress() >= getMaxProgress()) {
			finishJob(level, pos, working);
		}
	}

	/**
	 * 找这一轮要处理的东西: 先输入槽(能处理的槽全都并行), 输入槽没活干才轮到顶上的方块.
	 *
	 * @return 没有能处理的东西时返回 null
	 */
	@Nullable
	private Job findJob(Level level, BlockPos pos) {
		List<Lane> lanes = new ArrayList<>();

		for (int i = 0; i < getItemInputSlotCount(); i++) {
			int slot = getItemInputSlotIndex(i);
			ItemStack stack = getItemHandler().getStackInSlot(slot);
			if (stack.isEmpty()) {
				continue;
			}

			LaneRecipe recipe = findRecipe(level, stack, false);
			if (recipe != null) {
				lanes.add(new Lane(slot, recipe, stack.copy(), false));
			}
		}

		// 只要有一个输入槽能处理, 这一轮就是所有能处理的槽一起上
		if (!lanes.isEmpty()) {
			return new Job(lanes, false, jobProcessTime(lanes));
		}

		// 顶上那个方块只有一条线
		ItemStack blockItem = getBlockItemAbove(level, pos);
		if (blockItem.isEmpty()) {
			return null;
		}

		LaneRecipe recipe = findRecipe(level, blockItem, true);
		if (recipe == null) {
			return null;
		}

		List<Lane> blockLane = List.of(new Lane(-1, recipe, blockItem, true));
		return new Job(blockLane, true, jobProcessTime(blockLane));
	}

	/**
	 * 一轮要多少 tick: 并行的槽位共用一份进度, 所以按最慢的那条线算, 最后再乘上加速插件的倍率.
	 */
	private int jobProcessTime(List<Lane> lanes) {
		int time = 1;

		for (Lane lane : lanes) {
			time = Math.max(time, lane.recipe().processTime());
		}

		return applySpeedPlugins(time);
	}

	/**
	 * 这一轮一共要多少 FE/t(每条线各算自己那份).
	 */
	private static int energyCost(List<Lane> lanes) {
		int cost = 0;

		for (Lane lane : lanes) {
			cost += Math.max(0, lane.recipe().energyCost());
		}

		return cost;
	}

	/**
	 * 现有电量够跑几条线(按输入槽顺序一条条扣).
	 */
	private int affordableLanes(List<Lane> lanes) {
		int energy = getEnergy();
		int count = 0;

		for (Lane lane : lanes) {
			int cost = Math.max(0, lane.recipe().energyCost());

			if (cost > energy) {
				break;
			}

			energy -= cost;
			count++;
		}

		return count;
	}

	/**
	 * 挑出这一轮真的能出产物的并行线.
	 * <p>
	 * 拿输出槽的副本按顺序"试放"一遍产物, 放不下的那条线这一轮先不参与:
	 * 不然两条线抢同一个槽, 后放的那份产物就没地方去了.
	 */
	private List<Lane> selectWorkingLanes(Level level, Job job) {
		List<ItemStack> simulated = new ArrayList<>(getItemOutputSlotCount());

		for (int i = 0; i < getItemOutputSlotCount(); i++) {
			simulated.add(getItemHandler().getStackInSlot(getItemOutputSlotIndex(i)).copy());
		}

		List<Lane> working = new ArrayList<>(job.lanes().size());

		for (Lane lane : job.lanes()) {
			if (simulateInsert(simulated, lane.recipe().output())) {
				working.add(lane);
			}
		}

		return working;
	}

	/**
	 * 在输出槽的副本里试着放一份产物.
	 */
	private static boolean simulateInsert(List<ItemStack> slots, ItemStack output) {
		for (int i = 0; i < slots.size(); i++) {
			ItemStack current = slots.get(i);

			if (current.isEmpty()) {
				slots.set(i, output.copy());
				return true;
			}

			if (ItemStack.isSameItemSameTags(current, output)
					&& current.getCount() + output.getCount() <= current.getMaxStackSize()) {
				slots.set(i, current.copyWithCount(current.getCount() + output.getCount()));
				return true;
			}
		}

		return false;
	}

	/**
	 * 一轮走完: 每条并行线各自消耗一个输入, 产物分别进输出槽.
	 */
	private void finishJob(Level level, BlockPos pos, List<Lane> working) {
		for (Lane lane : working) {
			if (lane.fromBlock()) {
				// 顶上的方块还是原来那个才破坏它(中途被换掉的话进度早就归零了)
				if (ItemStack.isSameItemSameTags(getBlockItemAbove(level, pos), lane.input())) {
					level.destroyBlock(pos.above(), false);
				}
			} else {
				// 这一轮开始时这个槽里还是 lane.input(), 中途被换掉的话进度已经归零, 走不到这
				getItemHandler().getStackInSlot(lane.slot()).shrink(1);
			}

			insertOutput(level, pos, lane.recipe().output());
		}

		activeJob = null;
		setProgress(0);
		setSyncCounter(0);
		setChanged();
		sync();
	}

	/**
	 * 把产物塞进输出槽. 用 setStackInSlot/grow 而不是 insertItem:
	 * {@code canInsertItem} 只允许输入槽插入, 输出槽是给机器自己用的.
	 * <p>
	 * 挑并行线的时候已经保证放得下, 这里最后兜个底: 真放不下就掉在机器旁边, 别让产物凭空消失.
	 */
	private void insertOutput(Level level, BlockPos pos, ItemStack output) {
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

		Block.popResource(level, pos, output.copy());
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

	private void stopWorking(Level level, BlockPos pos, BlockState state) {
		if (state.hasProperty(BasicBlock.LIT) && state.getValue(BasicBlock.LIT)) {
			level.setBlock(pos, state.setValue(BasicBlock.LIT, false), 3);
		}

		if (getProgress() > 0 || activeJob != null) {
			activeJob = null;
			setProgress(0);
			setSyncCounter(0);
			setChanged();
			sync();
		}

		setMaxProgress(getIdleProcessTime());
	}
}

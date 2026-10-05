package dev.celestiacraft.deep_tech.common.block.machine.bionic.advanced_crusher;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.gui.MachineItemSlots;
import dev.celestiacraft.deep_tech.api.gui.widget.EnergyBarWidget;
import dev.celestiacraft.deep_tech.api.gui.widget.ProgressBarWidget;
import dev.celestiacraft.deep_tech.common.block.machine.bionic.ParallelBionicMachineBlockEntity;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.AdvancedCrusherConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 高级粉碎机: 普通粉碎机的升级版. 相比普通粉碎机:
 * <ul>
 *     <li>速度是普通的 {@code speed_multiplier} 倍(默认 2 倍);</li>
 *     <li>两个输入槽并行: 两个槽各放一个矿石, 一轮就是 2 份产物; 两个堆在一个槽里, 一轮还是一份;</li>
 *     <li>输出槽变两个, 能量缓存更大;</li>
 *     <li>把可粉碎的方块放在机器顶面, 等处理时间走完就会把那个方块消耗掉, 产物进输出槽;</li>
 *     <li>旁边贴加速插件还能再快, 见 {@link ParallelBionicMachineBlockEntity}.</li>
 * </ul>
 * 配方用本模组自己的粉碎配方({@link DTRecipes#CRUSHING}), 一轮的时间和能耗都按配方来,
 * 所以并行时每条线各自算自己那份能耗.
 */
public class AdvancedCrusherBlockEntity extends ParallelBionicMachineBlockEntity<AdvancedCrusherBlockEntity> {
	public AdvancedCrusherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getMachineMaxEnergy() {
		return AdvancedCrusherConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return AdvancedCrusherConfig.MAX_RECEIVE.get();
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
	protected @Nullable LaneRecipe findRecipe(Level level, ItemStack input, boolean fromBlock) {
		CrushingRecipe recipe = getRecipe(DTRecipes.CRUSHING.getRecipeType(), level, input);
		if (recipe == null) {
			return null;
		}

		int machineSpeed = Math.max(1, AdvancedCrusherConfig.SPEED_MULTIPLIER.get());

		return new LaneRecipe(
				recipe.getId(),
				recipe.getOutput(),
				Math.max(1, recipe.getProcessingTime() / machineSpeed),
				recipe.getEnergyCost()
		);
	}

	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
		// 暂时借用普通粉碎机的 GUI 贴图
		group.setBackground(new ResourceTexture(DeepTech.loadGui("crusher")));

		LabelWidget title = new LabelWidget(
				8,
				8,
				MachineBlocks.ADVANCED_CRUSHER.get().getName()
		);
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				18,
				25,
				this::getEnergyStored,
				getMaxEnergyStored()
		));

		group.addWidget(new ProgressBarWidget(
				68, 39, 16, 16,
				this::getProgress,
				this::getMaxProgress,
				new ResourceTexture(DeepTech.loadGui("elements/progress_crusher_back")),
				new ResourceTexture(DeepTech.loadGui("elements/progress_crusher_front"))
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

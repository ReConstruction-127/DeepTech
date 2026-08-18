package dev.celestiacraft.deep_tech.common.block.machine.assembler.capability;

import dev.celestiacraft.deep_tech.common.block.machine.assembler.AssemblerBlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 组装机的能力(capability)统一入口, 单独类实现:
 * <ul>
 *   <li>能量: {@link ForgeCapabilities#ENERGY}, 上限与接收速率由 AssemblerConfig 驱动</li>
 *   <li>物品: {@link ForgeCapabilities#ITEM_HANDLER}, 16 输入槽 + 1 催化剂槽 + 4 输出槽</li>
 *   <li>流体: {@link ForgeCapabilities#FLUID_HANDLER}, 2 输入罐 + 1 输出罐</li>
 * </ul>
 */
public class AssemblerCapability {
	private final AssemblerBlockEntity machine;
	private final LazyOptional<IEnergyStorage> energyCap;
	private final LazyOptional<IItemHandler> itemCap;
	private final LazyOptional<IFluidHandler> fluidCap;

	public AssemblerCapability(AssemblerBlockEntity machine) {
		this.machine = machine;
		this.energyCap = LazyOptional.of(machine::getEnergyStorage);
		this.itemCap = LazyOptional.of(machine::getItemHandler);
		this.fluidCap = LazyOptional.of(machine::getFluidHandler);
	}

	public <T> @NotNull LazyOptional<T> get(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == ForgeCapabilities.ENERGY) {
			return energyCap.cast();
		}
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return itemCap.cast();
		}
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return fluidCap.cast();
		}
		return LazyOptional.empty();
	}

	public void invalidate() {
		energyCap.invalidate();
		itemCap.invalidate();
		fluidCap.invalidate();
	}
}
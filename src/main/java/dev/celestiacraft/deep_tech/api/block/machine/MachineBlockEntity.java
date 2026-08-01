package dev.celestiacraft.deep_tech.api.block.machine;

import dev.celestiacraft.deep_tech.api.block.machine.capability.MachineEnergyCapability;
import dev.celestiacraft.deep_tech.api.block.machine.capability.MachineFluidHandler;
import dev.celestiacraft.deep_tech.api.block.machine.capability.MachineItemHandler;
import dev.celestiacraft.deep_tech.api.block.machine.config.IMachineEnergyConfig;
import dev.celestiacraft.deep_tech.api.block.machine.config.IMachineFluidConfig;
import dev.celestiacraft.deep_tech.api.block.machine.config.IMachineItemConfig;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import dev.celestiacraft.libs.api.register.block.ITickableBlockEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Getter
@Setter
public abstract class MachineBlockEntity<T extends MachineBlockEntity> extends BasicBlockEntity implements ITickableBlockEntity<T>, IMachineItemConfig, IMachineFluidConfig, IMachineEnergyConfig {
	private int progress = 0;
	private int maxProgress = 100;
	private int energy = 0;
	// 用于控制 sync 频率的计数器
	private int syncCounter = 0;
	// 复用 inventoryWrapper，避免每 tick 创建
	private final SimpleMachineInventory inventory;

	public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new SimpleMachineInventory(itemHandler);
	}

	@Override
	public int getItemInputSlotCount() {
		return 0;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 0;
	}

	@Override
	public int getMachineMaxEnergy() {
		return 0;
	}

	@Override
	public int getMaxReceive() {
		return 0;
	}

	/**
	 * 由于这属于处理机器
	 * <p>
	 * 因此让电力输出为0
	 * <p>
	 * (什么叫你的工作机器的电能抽出来)
	 *
	 * @return
	 */
	@Override
	public int getMaxExtract() {
		return 0;
	}

	// 在 MachineBlockEntity 中添加这个方法
	public void sync() {
		if (level != null && !level.isClientSide) {
			setChanged();
			// 强制发送 BlockEntity 数据包，不依赖 BlockState 变化
			if (level instanceof ServerLevel serverLevel) {
				serverLevel.getChunkSource().blockChanged(worldPosition);
			}
		}
	}

	@Getter
	private final IEnergyStorage energyStorage = new MachineEnergyCapability(this);
	@Getter
	private final MachineItemHandler itemHandler = new MachineItemHandler(this);
	@Getter
	private final MachineFluidHandler fluidHandler = new MachineFluidHandler(this);

	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energyStorage);
	private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> itemHandler);
	private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(() -> fluidHandler);

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side) {
		if (capability == ForgeCapabilities.ENERGY) {
			return energyCap.cast();
		}
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return itemCap.cast();
		}
		if (capability == ForgeCapabilities.FLUID_HANDLER && getMaxMachineTank() > 0) {
			return fluidCap.cast();
		}
		return super.getCapability(capability, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		energyCap.invalidate();
		itemCap.invalidate();
		fluidCap.invalidate();
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Inventory", itemHandler.serializeNBT());
		tag.putInt("Energy", energy);
		tag.putInt("Progress", progress);
		tag.putInt("MaxProgress", maxProgress);
		tag.put("Fluids", fluidHandler.serializeNBT());
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putInt("Energy", energy);
		tag.putInt("Progress", progress);
		tag.putInt("MaxProgress", maxProgress);
		tag.put("Fluids", fluidHandler.serializeNBT());
		return tag;
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tag = pkt.getTag();
		if (tag != null) {
			handleUpdateTag(tag);
		}
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		energy = tag.getInt("Energy");
		progress = tag.getInt("Progress");
		maxProgress = tag.getInt("MaxProgress");
		if (tag.contains("Fluids")) {
			fluidHandler.deserializeNBT(tag.getCompound("Fluids"));
		}
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		itemHandler.deserializeNBT(tag.getCompound("Inventory"));
		energy = tag.getInt("Energy");
		progress = tag.getInt("Progress");
		maxProgress = tag.getInt("MaxProgress");
		if (tag.contains("Fluids")) {
			fluidHandler.deserializeNBT(tag.getCompound("Fluids"));
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (level != null && !level.isClientSide) {
			sync();
		}
	}

	public int getEnergyStored() {
		return energy;
	}

	public int getMaxEnergyStored() {
		return getMachineMaxEnergy();
	}
}

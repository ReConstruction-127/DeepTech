package dev.celestiacraft.deep_tech.api.block;

import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import dev.celestiacraft.libs.api.register.block.ITickableBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Getter
public abstract class MachineBlockEntity<T extends MachineBlockEntity> extends BasicBlockEntity implements ITickableBlockEntity<T>, IMachineEnergyConfig {
	protected int progress = 0;
	protected int maxProgress = 100;
	protected int energy = 0;

	public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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

	protected final IEnergyStorage energyStorage = new MachineEnergyCapability(this);

	@Getter
	protected final ItemStackHandler inventory = new ItemStackHandler(2) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}
	};

	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energyStorage);
	private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> new IItemHandler() {
		@Override
		public int getSlots() {
			return inventory.getSlots();
		}

		@Override
		public @NotNull ItemStack getStackInSlot(int slot) {
			return inventory.getStackInSlot(slot);
		}

		@Override
		public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
			if (slot != 0) return stack;
			return inventory.insertItem(slot, stack, simulate);
		}

		@Override
		public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot != 1) return ItemStack.EMPTY;
			return inventory.extractItem(slot, amount, simulate);
		}

		@Override
		public int getSlotLimit(int slot) {
			return inventory.getSlotLimit(slot);
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return slot == 0 && inventory.isItemValid(slot, stack);
		}
	});

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side) {
		if (capability == ForgeCapabilities.ENERGY) {
			return energyCap.cast();
		}
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return itemCap.cast();
		}
		return super.getCapability(capability, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		energyCap.invalidate();
		itemCap.invalidate();
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Inventory", inventory.serializeNBT());
		tag.putInt("Energy", energy);
		tag.putInt("Progress", progress);
		tag.putInt("MaxProgress", maxProgress);
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putInt("Energy", energy);
		tag.putInt("Progress", progress);
		tag.putInt("MaxProgress", maxProgress);
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
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		inventory.deserializeNBT(tag.getCompound("Inventory"));
		energy = tag.getInt("Energy");
		progress = tag.getInt("Progress");
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

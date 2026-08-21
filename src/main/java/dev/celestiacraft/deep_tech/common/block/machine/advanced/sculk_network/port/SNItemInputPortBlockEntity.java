package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port;

import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.center.SNCenterBlockEntity;
import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 物品输入端口本身不执行任何逻辑.
 * 网络扫描与物品转运全部由中枢({@link SNCenterBlockEntity})的 BFS 承载.
 */
@Getter
public class SNItemInputPortBlockEntity extends BasicBlockEntity {
	private ItemStack filter = ItemStack.EMPTY;

	public SNItemInputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void setFilter(ItemStack filter) {
		this.filter = filter.copy();
		this.filter.setCount(1);
		markDirtyAndUpdate();
	}

	public void clearFilter() {
		filter = ItemStack.EMPTY;
		markDirtyAndUpdate();
	}

	@Override
	protected void write(CompoundTag tag) {
		if (!filter.isEmpty()) {
			tag.put("Filter", filter.save(new CompoundTag()));
		}
	}

	@Override
	protected void read(CompoundTag tag) {
		filter = tag.contains("Filter") ? ItemStack.of(tag.getCompound("Filter")) : ItemStack.EMPTY;
	}

	// ========== 同步(总是写入 Filter,即使为空) ==========
	@Override
	protected void writeSync(CompoundTag tag) {
		tag.put("Filter", filter.save(new CompoundTag()));
	}

	@Override
	protected void readSync(CompoundTag tag) {
		filter = tag.contains("Filter") ? ItemStack.of(tag.getCompound("Filter")) : ItemStack.EMPTY;
	}
}
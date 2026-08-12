package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.side.fluid.forge.FluidHelperImpl;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor.SNAccessorBlockEntity.FluidEntry;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor.SNAccessorBlockEntity.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 访问器列表控件:左侧物品 / 右侧流体各一个实例。
 * <p>
 * 数据流:服务端 detectAndSendChanges 时从 BE 读取网络汇总(间隔缓存),
 * 序列化后通过 writeUpdateInfo 推送;客户端 readUpdateInfo 解析后保存全量列表,
 * 本地按名称排序 + 搜索过滤 + 滚轮滚动,全部在客户端完成。
 */
public class SNAccessorListWidget extends Widget {

	public enum Kind {
		ITEMS,
		FLUIDS
	}

	private static final int ROW_HEIGHT = 16;
	private static final int UPDATE_ID = 0;

	/** 单行显示数据(图标 + 名称 + 总数) */
	private static final class Row {
		private final ItemStack item;
		private final com.lowdragmc.lowdraglib.side.fluid.FluidStack fluid;
		private final String name;
		private final long amount;

		private Row(ItemStack item, com.lowdragmc.lowdraglib.side.fluid.FluidStack fluid, String name, long amount) {
			this.item = item;
			this.fluid = fluid;
			this.name = name;
			this.amount = amount;
		}
	}

	private final SNAccessorBlockEntity accessor;
	private final Kind kind;

	// 客户端数据(由服务端同步的全量条目)
	private final List<ItemEntry> allItems = new ArrayList<>();
	private final List<FluidEntry> allFluids = new ArrayList<>();
	private final List<Row> rows = new ArrayList<>();

	private String filter = "";
	private int scrollOffset = 0;

	// 服务端变更检测
	private String lastSent = "";

	public SNAccessorListWidget(SNAccessorBlockEntity accessor, Kind kind, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.accessor = accessor;
		this.kind = kind;
	}

	public Kind getKind() {
		return kind;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String text) {
		this.filter = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
		applyFilterAndSort();
	}

	// ============================================================
	//  服务端:变更检测与推送
	// ============================================================

	@Override
	public void detectAndSendChanges() {
		if (isRemote()) {
			return;
		}
		accessor.refreshIfNeeded();

		net.minecraft.nbt.CompoundTag tag = buildTag();
		String key = tag.toString();
		if (!key.equals(lastSent)) {
			lastSent = key;
			writeUpdateInfo(UPDATE_ID, buf -> buf.writeNbt(tag));
		}
	}

	private net.minecraft.nbt.CompoundTag buildTag() {
		net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
		net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();
		if (kind == Kind.ITEMS) {
			for (ItemEntry entry : accessor.getItemEntries()) {
				net.minecraft.nbt.CompoundTag c = new net.minecraft.nbt.CompoundTag();
				c.put("Stack", entry.stack().save(new net.minecraft.nbt.CompoundTag()));
				c.putLong("Count", entry.count());
				list.add(c);
			}
		} else {
			for (FluidEntry entry : accessor.getFluidEntries()) {
				net.minecraft.nbt.CompoundTag c = new net.minecraft.nbt.CompoundTag();
				c.put("Stack", entry.stack().writeToNBT(new net.minecraft.nbt.CompoundTag()));
				c.putLong("Amount", entry.amount());
				list.add(c);
			}
		}
		tag.put("List", list);
		return tag;
	}

	// ============================================================
	//  客户端:接收数据并重建显示列表
	// ============================================================

	@Override
	public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
		if (id != UPDATE_ID) {
			return;
		}
		net.minecraft.nbt.CompoundTag tag = buffer.readNbt();
		if (tag == null) {
			return;
		}
		if (kind == Kind.ITEMS) {
			allItems.clear();
			for (var c : tag.getList("List", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
				net.minecraft.nbt.CompoundTag cc = (net.minecraft.nbt.CompoundTag) c;
				ItemStack stack = ItemStack.of(cc.getCompound("Stack"));
				if (stack.isEmpty()) {
					continue;
				}
				allItems.add(new ItemEntry(stack, cc.getLong("Count")));
			}
			applyFilterAndSort();
		} else {
			allFluids.clear();
			for (var c : tag.getList("List", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
				net.minecraft.nbt.CompoundTag cc = (net.minecraft.nbt.CompoundTag) c;
				net.minecraftforge.fluids.FluidStack forge = net.minecraftforge.fluids.FluidStack.loadFluidStackFromNBT(cc.getCompound("Stack"));
				if (forge.isEmpty()) {
					continue;
				}
				allFluids.add(new FluidEntry(forge, cc.getLong("Amount")));
			}
			applyFilterAndSort();
		}
	}

	/**
	 * 应用搜索过滤并按显示名称排序(客户端,实时语言环境)。
	 */
	private void applyFilterAndSort() {
		rows.clear();
		if (kind == Kind.ITEMS) {
			for (ItemEntry entry : allItems) {
				String name = entry.stack().getHoverName().getString();
				if (matchesFilter(name)) {
					rows.add(new Row(entry.stack(), null, name, entry.count()));
				}
			}
		} else {
			for (FluidEntry entry : allFluids) {
				com.lowdragmc.lowdraglib.side.fluid.FluidStack ld = FluidHelperImpl.toFluidStack(entry.stack());
				String name = FluidHelperImpl.getDisplayName(ld).getString();
				if (matchesFilter(name)) {
					rows.add(new Row(null, ld, name, entry.amount()));
				}
			}
		}
		rows.sort(Comparator.comparing(row -> row.name));

		int maxScroll = Math.max(0, rows.size() * ROW_HEIGHT - getSize().height);
		scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
	}

	private boolean matchesFilter(String name) {
		return filter.isEmpty() || name.toLowerCase(Locale.ROOT).contains(filter);
	}

	// ============================================================
	//  渲染与交互(客户端)
	// ============================================================

	@Override
	public void drawInBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (rows.isEmpty()) {
			return;
		}
		int x = getPosition().x;
		int y = getPosition().y;
		int width = getSize().width;
		int height = getSize().height;

		int startRow = scrollOffset / ROW_HEIGHT;
		int pixelOffset = scrollOffset % ROW_HEIGHT;
		var font = Minecraft.getInstance().font;

		for (int i = startRow; ; i++) {
			int rowY = y + i * ROW_HEIGHT - pixelOffset;
			if (rowY >= y + height || rowY + ROW_HEIGHT <= y) {
				break;
			}
			if (i < 0 || i >= rows.size()) {
				continue;
			}
			Row row = rows.get(i);

			// 图标
			if (kind == Kind.ITEMS) {
				graphics.renderItem(row.item, x + 2, rowY + 2);
			} else if (row.fluid != null) {
				DrawerHelper.drawFluidForGui(graphics, row.fluid, x + 2, rowY + 2, 12, 12);
			}

			// 名称(超宽截断)
			int textX = x + 18;
			int countX = x + width - 4;
			int nameWidth = countX - textX - 26;
			String name = row.name;
			if (font.width(name) > nameWidth && nameWidth > 0) {
				name = font.plainSubstrByWidth(name, nameWidth - 2) + "…";
			}
			graphics.drawString(font, name, textX, rowY + 4, 0xFFE0E0E0, false);

			// 总数(右对齐)
			String countText = "× " + row.amount;
			graphics.drawString(font, countText, countX - font.width(countText), rowY + 4, 0xFF9A9A9A, false);
		}
	}

	@Override
	public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
		if (!isMouseOverElement(mouseX, mouseY)) {
			return false;
		}
		if (rows.size() * ROW_HEIGHT <= getSize().height) {
			return true;
		}
		scrollOffset -= (int) (wheelDelta * ROW_HEIGHT);
		int maxScroll = rows.size() * ROW_HEIGHT - getSize().height;
		scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
		return true;
	}
}
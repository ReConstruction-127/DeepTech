package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.accessor;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.side.fluid.forge.FluidHelperImpl;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.accessor.capability.FixedFluidSource;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.accessor.capability.NetworkFluidSink;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 访问器网格控件:物品 / 流体列表共用.
 * <p>
 * 网格式展示(9 列 × 可见区高度行,可滚动 + 搜索过滤),数据由服务端
 * detectAndSendChanges 推送,客户端本地过滤排序渲染.
 * <p>
 * 交互(经 writeClientAction 通知服务端执行,物品/流体语义不同):
 * <ul>
 *   <li>物品:光标语义与箱子一致 —— 空光标单击 = 取一组到光标;带光标单击 = 光标物品全部存入;
 *       Shift+单击 = 取一组到背包(背包槽位 Shift+单击 = 该格整组存入网络,见 SNPlayerSlot)</li>
 *   <li>流体:与物品相同的光标语义 —— 光标拿空容器点流体格 = 灌满;光标拿满容器点任意格 = 倒入网络</li>
 * </ul>
 */
public class SNAccessorListWidget extends Widget {
	public enum Kind {
		ITEMS,
		FLUIDS
	}

	private static final int CELL_SIZE = 18;
	private static final int UPDATE_ID = 0;
	private static final int CLICK_ID = 1;

	/** 单行显示数据(图标 + 名称 + 总数) */
	private static class Row {
		private final ItemStack item;
		private final FluidStack fluid;
		private final String name;
		private final long amount;

		private Row(ItemStack item, FluidStack fluid, String name, long amount) {
			this.item = item;
			this.fluid = fluid;
			this.name = name;
			this.amount = amount;
		}
	}

	private final SNAccessorBlockEntity accessor;
	@Getter
	private final Kind kind;
	private final int cols;

	// 客户端数据(由服务端同步的全量条目)
	private final List<SNAccessorBlockEntity.ItemEntry> allItems = new ArrayList<>();
	private final List<SNAccessorBlockEntity.FluidEntry> allFluids = new ArrayList<>();
	private final List<Row> rows = new ArrayList<>();

	@Getter
	private String filter = "";
	private int rowOffset = 0;

	// 服务端变更检测
	private String lastSent = "";

	public SNAccessorListWidget(SNAccessorBlockEntity accessor, Kind kind, int cols, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.accessor = accessor;
		this.kind = kind;
		this.cols = Math.max(1, cols);
	}

	public void setFilter(String text) {
		filter = text == null ? "" : text;
		applyFilterAndSort();
	}

	@Override
	public void detectAndSendChanges() {
		if (isRemote()) {
			return;
		}
		accessor.refreshIfNeeded();

		CompoundTag tag = buildTag();
		String key = tag.toString();
		if (!key.equals(lastSent)) {
			lastSent = key;
			writeUpdateInfo(UPDATE_ID, buf -> buf.writeNbt(tag));
		}
	}

	private CompoundTag buildTag() {
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();

		if (kind == Kind.ITEMS) {
			for (SNAccessorBlockEntity.ItemEntry entry : accessor.getItemEntries()) {
				CompoundTag nbt = new CompoundTag();

				nbt.put("Stack", entry.stack().save(new CompoundTag()));
				nbt.putLong("Count", entry.count());
				list.add(nbt);
			}
		} else {
			for (SNAccessorBlockEntity.FluidEntry entry : accessor.getFluidEntries()) {
				CompoundTag c = new CompoundTag();
				c.put("Stack", entry.stack().writeToNBT(new CompoundTag()));
				c.putLong("Amount", entry.amount());
				list.add(c);
			}
		}
		tag.put("List", list);
		return tag;
	}

	@Override
	public void readUpdateInfo(int id, FriendlyByteBuf buf) {
		if (id != UPDATE_ID) {
			return;
		}
		CompoundTag tag = buf.readNbt();
		if (tag == null) {
			return;
		}
		if (kind == Kind.ITEMS) {
			allItems.clear();
			for (Tag nbt : tag.getList("List", Tag.TAG_COMPOUND)) {
				CompoundTag cc = (CompoundTag) nbt;

				ItemStack stack = ItemStack.of(cc.getCompound("Stack"));
				if (stack.isEmpty()) {
					continue;
				}
				allItems.add(new SNAccessorBlockEntity.ItemEntry(stack, cc.getLong("Count")));
			}
			applyFilterAndSort();
		} else {
			allFluids.clear();
			for (Tag c : tag.getList("List", Tag.TAG_COMPOUND)) {
				CompoundTag cc = (CompoundTag) c;
				net.minecraftforge.fluids.FluidStack forge = net.minecraftforge.fluids.FluidStack.loadFluidStackFromNBT(cc.getCompound("Stack"));
				if (forge.isEmpty()) {
					continue;
				}
				allFluids.add(new SNAccessorBlockEntity.FluidEntry(forge, cc.getLong("Amount")));
			}
			applyFilterAndSort();
		}
	}

	/**
	 * 应用搜索过滤并按显示名称排序(客户端,实时语言环境).
	 */
	private void applyFilterAndSort() {
		rows.clear();
		if (kind == Kind.ITEMS) {
			for (SNAccessorBlockEntity.ItemEntry entry : allItems) {
				String name = entry.stack().getHoverName().getString();
				if (matchesFilter(name, entry.stack().getItem().getDescriptionId(), idOf(entry.stack()))) {
					rows.add(new Row(entry.stack(), null, name, entry.count()));
				}
			}
		} else {
			for (SNAccessorBlockEntity.FluidEntry entry : allFluids) {
				FluidStack forge = entry.stack();
				String name = FluidHelperImpl.getDisplayName(FluidHelperImpl.toFluidStack(forge)).getString();
				if (matchesFilter(name, "", "")) {
					rows.add(new Row(null, forge, name, entry.amount()));
				}
			}
		}
		rows.sort(Comparator.comparing(row -> row.name));

		int maxRowOffset = Math.max(0, (rows.size() + cols - 1) / cols - getVisibleRows());
		rowOffset = Math.max(0, Math.min(rowOffset, maxRowOffset));
	}

	private static String idOf(ItemStack stack) {
		ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
		return id == null ? "" : id.getPath();
	}

	private boolean matchesFilter(String name, String descriptionId, String itemId) {
		if (filter.isEmpty()) {
			return true;
		}
		String query = filter.trim().toLowerCase(Locale.ROOT);
		if (query.isEmpty()) {
			return true;
		}
		if (name.toLowerCase(Locale.ROOT).contains(query)) {
			return true;
		}
		if (descriptionId.toLowerCase(Locale.ROOT).contains(query)) {
			return true;
		}
		return !itemId.isEmpty() && itemId.contains(query);
	}

	private int getVisibleRows() {
		return Math.max(1, getSize().height / CELL_SIZE);
	}

	private int getMaxRowOffset() {
		return Math.max(0, (rows.size() + cols - 1) / cols - getVisibleRows());
	}

	/** 网格左上角相对控件原点的偏移(居中) */
	private int getOffsetX() {
		return Math.max(0, (getSize().width - cols * CELL_SIZE) / 2);
	}

	private int getOffsetY() {
		return Math.max(0, (getSize().height - getVisibleRows() * CELL_SIZE) / 2);
	}

	// ============================================================
	//  渲染与交互(客户端)
	// ============================================================

	@Override
	public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = getPosition().x;
		int y = getPosition().y;
		int offsetX = getOffsetX();
		int offsetY = getOffsetY();
		int visibleRows = getVisibleRows();
		Font font = Minecraft.getInstance().font;

		// 鼠标悬停的格子
		int hoverCellIndex = -1;
		if (isMouseOverElement(mouseX, mouseY)) {
			int col = (int) ((mouseX - x - offsetX) / CELL_SIZE);
			int row = (int) ((mouseY - y - offsetY) / CELL_SIZE);
			if (col >= 0 && col < cols && row >= 0 && row < visibleRows) {
				int index = (rowOffset + row) * cols + col;
				if (index < rows.size()) {
					hoverCellIndex = index;
				}
			}
		}

		for (int row = 0; row < visibleRows; row++) {
			for (int col = 0; col < cols; col++) {
				int cellIndex = (rowOffset + row) * cols + col;
				int cellX = x + offsetX + col * CELL_SIZE;
				int cellY = y + offsetY + row * CELL_SIZE;

				// 空格子底色(箱子式)
				graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, 0x18000000);

				if (cellIndex >= rows.size()) {
					continue;
				}
				Row entry = rows.get(cellIndex);
				if (cellIndex == hoverCellIndex) {
					graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, 0x26FFFFFF);
				}

				// 图标(物品去掉原版数量字,由下方角标显示真实总量)
				if (kind == Kind.ITEMS) {
					ItemStack icon = entry.item;
					if (icon.getCount() != 1) {
						icon = icon.copy();
						icon.setCount(1);
					}
					graphics.renderItem(icon, cellX + 1, cellY + 1);
				} else if (entry.fluid != null) {
					DrawerHelper.drawFluidForGui(graphics, FluidHelperImpl.toFluidStack(entry.fluid), cellX + 1, cellY + 1, 16, 16);
				}

				// 数量角标(右上角,半倍字)
				if (entry.amount > 0) {
					String countText = entry.amount > 99999 ? "∞" : String.valueOf(entry.amount);
					PoseStack pose = graphics.pose();
					pose.pushPose();
					pose.translate(cellX, cellY, 0);
					pose.scale(0.5f, 0.5f, 1.0f);
					graphics.drawString(
							font,
							countText,
							((CELL_SIZE - 3) << 1) - font.width(countText),
							(CELL_SIZE - 9) << 1,
							0xFFFFFFFF,
							true
					);
					pose.popPose();
				}
			}
		}
	}

	@Override
	public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
		if (!isMouseOverElement(mouseX, mouseY)) {
			return false;
		}
		rowOffset -= (int) wheelDelta << 1;
		rowOffset = Math.max(0, Math.min(rowOffset, getMaxRowOffset()));
		return true;
	}

	@Override
	public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (!isMouseOverElement(mouseX, mouseY)) {
			return;
		}
		int col = (int) ((mouseX - getPosition().x - getOffsetX()) / CELL_SIZE);
		int row = (int) ((mouseY - getPosition().y - getOffsetY()) / CELL_SIZE);
		if (col < 0 || col >= cols || row < 0 || row >= getVisibleRows()) {
			return;
		}
		int index = (rowOffset + row) * cols + col;
		if (index >= rows.size()) {
			return;
		}
		Row entry = rows.get(index);
		List<Component> lines = new ArrayList<>();
		lines.add(Component.literal(entry.name));
		lines.add(kind == Kind.ITEMS
				? Component.translatable("gui.deep_tech.accessor_item_count", entry.amount)
				: Component.translatable("gui.deep_tech.accessor_fluid_amount", entry.amount));
		graphics.renderComponentTooltip(Minecraft.getInstance().font, lines, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0 || !isMouseOverElement(mouseX, mouseY)) {
			return false;
		}
		int col = (int) ((mouseX - getPosition().x - getOffsetX()) / CELL_SIZE);
		int row = (int) ((mouseY - getPosition().y - getOffsetY()) / CELL_SIZE);
		if (col < 0 || col >= cols || row < 0 || row >= getVisibleRows()) {
			return false;
		}
		int cellIndex = (rowOffset + row) * cols + col;

		CompoundTag identity;
		if (cellIndex < rows.size()) {
			Row target = rows.get(cellIndex);
			identity = kind == Kind.ITEMS ? target.item.serializeNBT() : target.fluid.writeToNBT(new CompoundTag());
		} else {
			identity = new CompoundTag();
		}

		int flags = 0;
		if (Screen.hasShiftDown()) {
			flags |= 1;
		}

		int actionFlags = flags;
		writeClientAction(CLICK_ID, (buf) -> {
			buf.writeNbt(identity);
			buf.writeVarInt(actionFlags);
		});
		return true;
	}

	@Override
	public void handleClientAction(int id, FriendlyByteBuf buffer) {
		if (id != CLICK_ID) {
			return;
		}
		Player player = getGui() == null ? null : getGui().entityPlayer;
		if (player == null || player.level().isClientSide()) {
			return;
		}
		CompoundTag tag = buffer.readNbt();
		if (tag == null) {
			return;
		}
		int flags = buffer.readVarInt();
		if (kind == Kind.ITEMS) {
			handleItemClick(player, ItemStack.of(tag), flags);
		} else {
			handleFluidClick(player, FluidStack.loadFluidStackFromNBT(tag), flags);
		}
		accessor.refreshIfNeeded();
		writeUpdateInfo(UPDATE_ID, buf -> buf.writeNbt(buildTag()));
	}

	/**
	 * 物品点击,与箱子完全一致的光标语义(服务端以容器光标栈为准):
	 * <ul>
	 *   <li>单击(空光标):把该物品取一组到光标上</li>
	 *   <li>单击(光标有物品):把光标上的物品全部存入网络,放不下的留在光标</li>
	 *   <li>Shift+单击:该物品取一组(最大堆叠)到背包</li>
	 * </ul>
	 */
	private void handleItemClick(Player player, ItemStack key, int flags) {
		boolean shift = (flags & 1) != 0;
		ModularUIContainer container = getGui() == null ? null : getGui().getModularUIContainer();
		ItemStack carried = container == null ? ItemStack.EMPTY : container.getCarried();

		if (!carried.isEmpty()) {
			// 光标携带物品:存入网络(像箱子放进任意格),放不下的留在光标
			int inserted = accessor.insertItem(carried.copy(), false);
			if (inserted > 0) {
				carried.shrink(inserted);
				if (carried.isEmpty()) {
					carried = ItemStack.EMPTY;
				}
				container.setCarried(carried);
			}
			return;
		}

		if (shift) {
			// Shift+单击:把该物品取一组(最大堆叠)到背包
			if (key.isEmpty()) {
				return;
			}
			ItemStack extracted = accessor.extractItem(key, key.getMaxStackSize(), false);
			if (extracted.isEmpty()) {
				return;
			}
			ItemStack leftover = ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(player.getInventory()), extracted, false);
			if (!leftover.isEmpty()) {
				player.drop(leftover, false);
			}
			return;
		}

		// 空光标单击:取一组到光标(像箱子)
		if (key.isEmpty()) {
			return;
		}
		ItemStack extracted = accessor.extractItem(key, key.getMaxStackSize(), false);
		if (!extracted.isEmpty()) {
			container.setCarried(extracted);
		}
	}

	/**
	 * 流体点击,与物品一致的光标语义(服务端以容器光标栈为准):
	 * <ul>
	 *   <li>光标是空容器(且仅 1 个)且格子有流体:从网络灌满该容器,留在光标</li>
	 *   <li>光标是有流体的容器(且仅 1 个):把容器流体全部倒入网络,空容器留在光标</li>
	 *   <li>光标是其他物品, 或容器成组(>1 个):无操作</li>
	 * </ul>
	 */
	private void handleFluidClick(Player player, FluidStack key, int flags) {
		ModularUIContainer container = getGui() == null ? null : getGui().getModularUIContainer();
		ItemStack carried = container == null ? ItemStack.EMPTY : container.getCarried();
		if (carried.isEmpty()) {
			return;
		}
		// 容器必须正好 1 个:成组的桶灌/倒会吞掉多余的桶(多份容器一律不响应)
		if (carried.getCount() != 1) {
			return;
		}

		// 1) 格子有流体:先试着把网络流体灌入光标上的容器(装不下的自动还回网络)
		if (!key.isEmpty()) {
			FluidStack drained = accessor.drain(new FluidStack(key.getFluid(), key.getAmount()), IFluidHandler.FluidAction.EXECUTE);
			if (!drained.isEmpty()) {
				FixedFluidSource source = new FixedFluidSource(drained);
				FluidActionResult filled = FluidUtil.tryFillContainer(carried, source, Integer.MAX_VALUE, player, true);
				if (!source.getFluid().isEmpty()) {
					accessor.fill(source.getFluid(), IFluidHandler.FluidAction.EXECUTE);
				}
				if (filled.isSuccess()) {
					container.setCarried(filled.getResult());
					return;
				}
			}
		}

		// 2) 灌不进去(容器已满/非容器):光标上若有流体则全部倒入网络
		FluidActionResult emptied = FluidUtil.tryEmptyContainer(carried, new NetworkFluidSink(accessor), Integer.MAX_VALUE, player, true);
		if (emptied.isSuccess()) {
			container.setCarried(emptied.getResult());
		}
	}
}
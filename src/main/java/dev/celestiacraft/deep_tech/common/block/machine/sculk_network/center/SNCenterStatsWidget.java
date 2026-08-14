package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center.SNCenterBlockEntity.ComponentCount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * 中枢状态控件:网络部件统计(单行 8 格:部件图标 + 数量角标,悬停提示),
 * 能量由 {@link dev.celestiacraft.deep_tech.api.gui.widget.EnergyBarWidget} 单独渲染。
 * <p>
 * 数据由服务端 detectAndSendChanges 推送(与访问器列表控件同一套 writeUpdateInfo / readUpdateInfo 机制),
 * 客户端本地渲染;部件数量来自中枢的 BFS 扫描结果(每秒更新一次)。
 */
public class SNCenterStatsWidget extends Widget {

	private static final int UPDATE_ID = 0;
	private static final int CELL_SIZE = 18;
	private static final int COLS = 8;
	private static final int VISIBLE_ROWS = 1;

	/** 部件网格左上角(相对控件原点) */
	private static final int GRID_X = 30;
	private static final int GRID_Y = 32;

	private final SNCenterBlockEntity center;

	// 客户端数据(由服务端同步)
	private final List<ComponentCount> components = new ArrayList<>();
	private int energyStored = 0;
	private boolean master = false;
	private int rowOffset = 0;

	// 服务端变更检测
	private String lastSent = "";

	public SNCenterStatsWidget(SNCenterBlockEntity center) {
		super(0, 0, 176, 126);
		this.center = center;
	}

	/** 客户端同步后的能量值(供 EnergyBarWidget 读取) */
	public int getSyncedEnergy() {
		return energyStored;
	}

	// ============================================================
	//  服务端:变更检测与推送
	// ============================================================

	@Override
	public void detectAndSendChanges() {
		if (isRemote()) {
			return;
		}
		CompoundTag tag = buildTag();
		String key = tag.toString();
		if (!key.equals(lastSent)) {
			lastSent = key;
			writeUpdateInfo(UPDATE_ID, buf -> buf.writeNbt(tag));
		}
	}

	private CompoundTag buildTag() {
		CompoundTag tag = new CompoundTag();
		net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();
		for (ComponentCount count : center.getComponentCounts()) {
			CompoundTag c = new CompoundTag();
			ResourceLocation id = ForgeRegistries.BLOCKS.getKey(count.block());
			c.putString("Id", id == null ? "" : id.toString());
			c.putInt("Count", count.count());
			list.add(c);
		}
		tag.put("Components", list);
		tag.putInt("Energy", center.getEnergyStored());
		tag.putBoolean("Master", center.isMaster());
		return tag;
	}

	// ============================================================
	//  客户端:接收数据
	// ============================================================

	@Override
	public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
		if (id != UPDATE_ID) {
			return;
		}
		CompoundTag tag = buffer.readNbt();
		if (tag == null) {
			return;
		}
		components.clear();
		for (var c : tag.getList("Components", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
			CompoundTag cc = (CompoundTag) c;
			String blockId = cc.getString("Id");
			if (blockId.isEmpty()) {
				continue;
			}
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
			if (block == null) {
				continue;
			}
			components.add(new ComponentCount(block, cc.getInt("Count")));
		}
		energyStored = tag.getInt("Energy");
		master = tag.getBoolean("Master");

		int maxDiff = Math.max(0, (components.size() + COLS - 1) / COLS - VISIBLE_ROWS);
		rowOffset = Math.max(0, Math.min(rowOffset, maxDiff));
	}

	/** 部件在网格中的索引(可能超出,留作未来更多部件滚动) */
	private int cellIndexAt(double mouseX, double mouseY) {
		int col = (int) ((mouseX - getPosition().x - GRID_X) / CELL_SIZE);
		int row = (int) ((mouseY - getPosition().y - GRID_Y) / CELL_SIZE);
		if (col < 0 || col >= COLS || row < 0 || row >= VISIBLE_ROWS) {
			return -1;
		}
		int index = (rowOffset + row) * COLS + col;
		return index < components.size() ? index : -1;
	}

	// ============================================================
	//  渲染与交互(客户端)
	// ============================================================

	@Override
	public void drawInBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = getPosition().x;
		int y = getPosition().y;
		var font = Minecraft.getInstance().font;

		// ---- 部件网格(单行 8 格) ----
		int hoverIndex = isMouseOverElement(mouseX, mouseY) ? cellIndexAt(mouseX, mouseY) : -1;
		for (int row = 0; row < VISIBLE_ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				int index = (rowOffset + row) * COLS + col;
				int cellX = x + GRID_X + col * CELL_SIZE;
				int cellY = y + GRID_Y + row * CELL_SIZE;

				graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, 0x18000000);
				if (index >= components.size()) {
					continue;
				}
				if (index == hoverIndex) {
					graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, 0x26FFFFFF);
				}

				// 部件方块图标
				Block block = components.get(index).block();
				ItemStack icon = block.asItem() == null ? ItemStack.EMPTY : new ItemStack(block.asItem());
				if (!icon.isEmpty()) {
					graphics.renderItem(icon, cellX + 1, cellY + 1);
				}

				// 数量角标(右上角,半倍字)
				int count = components.get(index).count();
				if (count > 0) {
					String countText = count > 99999 ? "∞" : String.valueOf(count);
					var pose = graphics.pose();
					pose.pushPose();
					pose.translate(cellX, cellY, 0);
					pose.scale(0.5f, 0.5f, 1.0f);
					graphics.drawString(font, countText, (CELL_SIZE - 3) * 2 - font.width(countText), (CELL_SIZE - 9) * 2, 0xFFFFFFFF, true);
					pose.popPose();
				}
			}
		}

		// ---- 汇总文字 ----
		long total = 0;
		for (ComponentCount cc : components) {
			total += cc.count();
		}
		graphics.drawString(font, Component.translatable("gui.deep_tech.center_component_total", total), x + 7, y + 92, 0xFF5D5F60, false);
		graphics.drawString(font, Component.translatable("gui.deep_tech.center_master",
				Component.translatable(master ? "gui.deep_tech.center_yes" : "gui.deep_tech.center_no")), x + 7, y + 104, 0xFF5D5F60, false);
	}

	@Override
	public void drawInForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (!isMouseOverElement(mouseX, mouseY)) {
			return;
		}
		int index = cellIndexAt(mouseX, mouseY);
		if (index < 0) {
			return;
		}
		ComponentCount cc = components.get(index);
		List<Component> lines = new ArrayList<>();
		lines.add(cc.block().getName());
		lines.add(Component.translatable("gui.deep_tech.center_component_count", cc.count()));
		graphics.renderComponentTooltip(Minecraft.getInstance().font, lines, mouseX, mouseY);
	}

	@Override
	public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
		if (!isMouseOverElement(mouseX, mouseY)) {
			return false;
		}
		rowOffset -= (int) wheelDelta;
		int maxDiff = Math.max(0, (components.size() + COLS - 1) / COLS - VISIBLE_ROWS);
		rowOffset = Math.max(0, Math.min(rowOffset, maxDiff));
		return true;
	}
}
package dev.celestiacraft.deep_tech.api.gui;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.api.block.machine.config.IMachineItemConfig;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import net.minecraftforge.items.ItemStackHandler;

/**
 * 根据机器的物品槽位配置(IMachineItemConfig)动态生成物品槽位 widget. 
 * <p>
 * 输入槽从 {@code inputStart} 开始, 输出槽从 {@code outputStart} 开始, 按固定间距向右排列;
 * 槽位数量为 0 时自动跳过, 不会创建任何 widget, 因此不会出现
 * {@code Slot 0 not in valid range - [0,0)} 这类越界崩溃. 
 * <p>
 * 使用示例:
 * <pre>{@code
 * MachineItemSlots.add(group, this, getInventory(), new Position(41, 38), new Position(97, 38));
 * }</pre>
 */
public class MachineItemSlots {
	/**
	 * 相邻槽位之间的标准间距(与 18x18 槽位纹理一致)
	 */
	public static final int SLOT_SPACING = 18;

	public static void add(
			WidgetGroup group,
			IMachineItemConfig config,
			ItemStackHandler handler,
			Position inputStart,
			Position outputStart
	) {
		add(group, config, handler, inputStart, outputStart, SLOT_SPACING);
	}

	/**
	 * 按配置生成所有物品槽位 widget. 
	 *
	 * @param group       槽位要加入的容器 widget
	 * @param config      机器物品槽位配置(数量与下标转换)
	 * @param handler     机器实际物品存储
	 * @param inputStart  第一个输入槽的位置
	 * @param outputStart 第一个输出槽的位置
	 * @param spacing     相邻槽位间距(默认 {@link #SLOT_SPACING})
	 */
	public static void add(
			WidgetGroup group,
			IMachineItemConfig config,
			ItemStackHandler handler,
			Position inputStart,
			Position outputStart,
			int spacing
	) {
		SimpleMachineInventory inventory = new SimpleMachineInventory(handler);
		for (int i = 0; i < config.getItemInputSlotCount(); i++) {
			group.addWidget(createSlot(
					inventory,
					config.getItemInputSlotIndex(i),
					inputStart.add(i * spacing, 0),
					true,
					true
			));
		}
		for (int i = 0; i < config.getItemOutputSlotCount(); i++) {
			group.addWidget(createSlot(
					inventory,
					config.getItemOutputSlotIndex(i),
					outputStart.add(i * spacing, 0),
					true,
					false
			));
		}
	}

	private static SlotWidget createSlot(
			SimpleMachineInventory container,
			int slotIndex,
			Position position,
			boolean canTakeItems,
			boolean canPutItems
	) {
		SlotWidget widget = new SlotWidget();
		widget.setContainerSlot(container, slotIndex);
		widget.setSelfPosition(position);
		widget.setBackground((ResourceTexture) null);
		widget.setCanTakeItems(canTakeItems);
		widget.setCanPutItems(canPutItems);
		return widget;
	}
}
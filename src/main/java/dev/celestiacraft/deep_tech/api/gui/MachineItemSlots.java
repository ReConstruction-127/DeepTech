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

	/**
	 * 在指定位置单独加一个槽位.
	 * <p>
	 * 用于槽位不是"输入一排 + 输出一排"的机器, 比如能量单元(两个槽上下排列).
	 *
	 * @param slotIndex    实际槽位下标
	 * @param position     槽位左上角位置
	 * @param canTakeItems 玩家能否从槽里取物
	 * @param canPutItems  玩家能否往槽里放物
	 * @return 创建出来的槽位 widget, 方便继续配置(底图默认为空, 由 GUI 贴图自己画框)
	 */
	public static SlotWidget addSlot(
			WidgetGroup group,
			ItemStackHandler handler,
			int slotIndex,
			Position position,
			boolean canTakeItems,
			boolean canPutItems
	) {
		SlotWidget widget = createSlot(
				new SimpleMachineInventory(handler),
				slotIndex,
				position,
				canTakeItems,
				canPutItems
		);
		group.addWidget(widget);
		return widget;
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
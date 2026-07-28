package dev.celestiacraft.deep_tech.common.recipe.crushing;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.gui.ProgressBarWidget;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.recipe.JEIProgressSupplier;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

@Getter
public class CrushingRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final Ingredient input;
	private final ItemStack output;
	private final int energyCost;
	private final int processingTime;

	public CrushingRecipe(
			ResourceLocation id,
			Ingredient input,
			ItemStack output,
			int energyCost,
			int processingTime
	) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.energyCost = energyCost;
		this.processingTime = processingTime;
	}

	@Override
	public boolean matches(Container container, @NotNull Level level) {
		return input.test(container.getItem(0));
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
		return output.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
		return output;
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return DTRecipes.CRUSHING.getSerializer();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return DTRecipes.CRUSHING.getRecipeType();
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(input);
		return list;
	}

	public ModularUI createModularUI(Player player) {
		ItemStackHandler handler = new ItemStackHandler(2);
		handler.setStackInSlot(0, input.getItems()[0].copy());
		handler.setStackInSlot(1, output.copy());
		SimpleMachineInventory container = new SimpleMachineInventory(handler);

		int width = 120;
		int height = 60;
		ModularUI ui = new ModularUI(width, height, null, player);

		WidgetGroup group = new WidgetGroup(0, 0, width, height);

		// 输入槽
		SlotWidget inputSlot = new SlotWidget();
		inputSlot.setContainerSlot(container, 0);
		inputSlot.setSelfPosition(new Position(10, 22));
		inputSlot.setBackground(SlotWidget.ITEM_SLOT_TEXTURE);
		inputSlot.setCanTakeItems(false);
		inputSlot.setCanPutItems(false);
		group.addWidget(inputSlot);

		// 输出槽
		SlotWidget outputSlot = new SlotWidget();
		outputSlot.setContainerSlot(container, 1);
		outputSlot.setSelfPosition(new Position(90, 22));
		outputSlot.setBackground(SlotWidget.ITEM_SLOT_TEXTURE);
		outputSlot.setCanTakeItems(false);
		outputSlot.setCanPutItems(false);
		group.addWidget(outputSlot);

		// 用 ProgressBarWidget + 动态 Supplier
		group.addWidget(new ProgressBarWidget(
				60, 28,                     // 居中位置
				16, 16,                     // 尺寸 16x16
				JEIProgressSupplier.INSTANCE::getProgress,
				() -> 100,
				new ResourceTexture(DeepTech.MODID + ":textures/gui/elements/progress_crusher_back.png"),
				new ResourceTexture(DeepTech.MODID + ":textures/gui/elements/progress_crusher_front.png")
		));

		// 能量和时间
		group.addWidget(new LabelWidget(8, 5, Component.literal("⚡ " + energyCost + " FE")));
		group.addWidget(new LabelWidget(8, 48, Component.literal("⏱ " + processingTime + " tick")));

		ui.widget(group);
		return ui;
	}
}
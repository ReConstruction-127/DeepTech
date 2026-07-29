package dev.celestiacraft.deep_tech.common.recipe.crushing;

import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
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

//	public ModularUI createModularUI(Player player) {
//		ItemStackHandler handler = new ItemStackHandler(2);
//		handler.setStackInSlot(0, input.getItems()[0].copy());
//		handler.setStackInSlot(1, output.copy());
//		SimpleMachineInventory container = new SimpleMachineInventory(handler);
//
//		int width = 120;
//		int height = 60;
//		ModularUI ui = new ModularUI(width, height, null, player);
//
//		WidgetGroup group = new WidgetGroup(0, 0, width, height);
//
//		// 输入槽
//		SlotWidget inputSlot = new SlotWidget();
//		inputSlot.setContainerSlot(container, 0);
//		inputSlot.setSelfPosition(new Position(10, 22));
//		inputSlot.setBackground(SlotWidget.ITEM_SLOT_TEXTURE);
//		inputSlot.setCanTakeItems(false);
//		inputSlot.setCanPutItems(false);
//		group.addWidget(inputSlot);
//
//		// 输出槽
//		SlotWidget outputSlot = new SlotWidget();
//		outputSlot.setContainerSlot(container, 1);
//		outputSlot.setSelfPosition(new Position(42, 22));
//		outputSlot.setBackground(SlotWidget.ITEM_SLOT_TEXTURE);
//		outputSlot.setCanTakeItems(false);
//		outputSlot.setCanPutItems(false);
//		group.addWidget(outputSlot);
//
//		ImageWidget progressImage = new ImageWidget();
//		progressImage.setSelfPosition(new Position(44, 27));
//		progressImage.setSize(16, 16);
//		progressImage.setImage(new ResourceTexture(
//				DeepTech.MODID + ":textures/gui/elements/jei/progressbar/crusher.png"
//		));
//		group.addWidget(progressImage);
//
//		ImageWidget iconImage = new ImageWidget();
//		iconImage.setSelfPosition(new Position(69, 20));
//		iconImage.setSize(32, 32);
//		iconImage.setImage(new ResourceTexture(
//				DeepTech.MODID + ":textures/gui/elements/jei/crusher.png"
//		));
//		group.addWidget(iconImage);
//
//		// 能量和时间
//		group.addWidget(new LabelWidget(8, 5, Component.literal("⚡ " + energyCost + " FE")));
//		group.addWidget(new LabelWidget(8, 48, Component.literal("⏱ " + processingTime + " tick")));
//
//		ui.widget(group);
//		return ui;
//	}
}
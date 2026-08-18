package dev.celestiacraft.deep_tech.compat.jei.handler;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import dev.celestiacraft.deep_tech.common.block.machine.alloy_furnace.AlloyFurnaceBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.assembler.AssemblerBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.processor.ProcessorBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_nursery.SculkNurseryBlockEntity;
import dev.celestiacraft.deep_tech.compat.jei.api.DTJeiRecipeType;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MachineGuiHandler implements IGuiContainerHandler<ModularUIGuiContainer> {
	public static final MachineGuiHandler INSTANCE = new MachineGuiHandler();
	private final Map<Class<?>, Function<ModularUIGuiContainer, Collection<IGuiClickableArea>>> handlers = new HashMap<>();

	private MachineGuiHandler() {
		register(CrusherBlockEntity.class, this::crusher);
		register(AlloyFurnaceBlockEntity.class, this::alloyFurnace);
		register(SculkFurnaceBlockEntity.class, this::sculkFurnace);
		register(SculkNurseryBlockEntity.class, this::sculkNursery);
		register(ProcessorBlockEntity.class, this::processor);
		register(AssemblerBlockEntity.class, this::assembler);
	}

	private <T> void register(Class<T> clazz, Function<ModularUIGuiContainer, Collection<IGuiClickableArea>> handler) {
		handlers.put(clazz, handler);
	}

	@Override
	public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull ModularUIGuiContainer screen, double mouseX, double mouseY) {
		for (Map.Entry<Class<?>, Function<ModularUIGuiContainer, Collection<IGuiClickableArea>>> entry : handlers.entrySet()) {
			if (entry.getKey().isInstance(screen.modularUI.holder)) {
				return entry.getValue().apply(screen);
			}
		}

		return List.of();
	}

	private Collection<IGuiClickableArea> crusher(ModularUIGuiContainer screen) {
		return List.of(IGuiClickableArea.createBasic(
				68,
				39,
				16,
				16,
				DTJeiRecipeType.CRUSHING
		));
	}

	private Collection<IGuiClickableArea> alloyFurnace(ModularUIGuiContainer screen) {
		return List.of(IGuiClickableArea.createBasic(
				74,
				39,
				16,
				16,
				DTJeiRecipeType.ALLOY
		));
	}

	private Collection<IGuiClickableArea> sculkFurnace(ModularUIGuiContainer screen) {
		return List.of(IGuiClickableArea.createBasic(
				68,
				40,
				14,
				14,
				RecipeTypes.SMELTING,
				RecipeTypes.BLASTING,
				RecipeTypes.SMOKING
		));
	}

	private Collection<IGuiClickableArea> sculkNursery(ModularUIGuiContainer screen) {
		return List.of(IGuiClickableArea.createBasic(
				88,
				32,
				16,
				16,
				DTJeiRecipeType.CULTIVATION
		));
	}

	private Collection<IGuiClickableArea> processor(ModularUIGuiContainer screen) {
		return List.of(IGuiClickableArea.createBasic(
				78,
				40,
				14,
				14,
				DTJeiRecipeType.PROCESSING
		));
	}

	private Collection<IGuiClickableArea> assembler(ModularUIGuiContainer screen) {
		return List.of(IGuiClickableArea.createBasic(
				148,
				59,
				14,
				22,
				DTJeiRecipeType.ASSEMBLING
		));
	}
}
package dev.celestiacraft.deep_tech.compat.jei.handler;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import dev.celestiacraft.deep_tech.common.block.machine.basic.alloy_furnace.AlloyFurnaceBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.assembler.AssemblerBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.basic.crusher.CrusherBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.basic.furnace.SculkFurnaceBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.bionic.advanced_crusher.AdvancedCrusherBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.bionic.advanced_sculk_furnace.AdvancedSculkFurnaceBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.processor.ProcessorBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_nursery.SculkNurseryBlockEntity;
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
		register(AdvancedSculkFurnaceBlockEntity.class, this::advancedSculkFurnace);
		register(AdvancedCrusherBlockEntity.class, this::advancedCrusher);
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

	/**
	 * 高级幽匿电炉的进度条和普通电炉在同一个位置(它暂时借用普通电炉的 GUI), 配方类型也一样.
	 */
	private Collection<IGuiClickableArea> advancedSculkFurnace(ModularUIGuiContainer screen) {
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

	/**
	 * 高级粉碎机的进度条和普通粉碎机在同一个位置(它暂时借用普通粉碎机的 GUI), 配方类型也一样.
	 */
	private Collection<IGuiClickableArea> advancedCrusher(ModularUIGuiContainer screen) {
		return List.of(IGuiClickableArea.createBasic(
				68,
				39,
				16,
				16,
				DTJeiRecipeType.CRUSHING
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
		// 和组装机 GUI 里进度条的位置/尺寸一致(AssemblerBlockEntity 里的 ProgressBarWidget)
		return List.of(IGuiClickableArea.createBasic(
				133,
				71,
				16,
				16,
				DTJeiRecipeType.ASSEMBLING
		));
	}
}
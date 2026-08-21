package dev.celestiacraft.deep_tech.compat.kubejs.api;

import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ArrayRecipeComponent;

/**
 * DeepTech 配方的 KubeJS 基类, 提供与 Java 侧构建器一致的追加式输入/输出方法.
 * <p>
 * 脚本先写 {@code event.recipes.deep_tech.xxx()} 再逐个链式追加, 所以这里不保留
 * 位置参数构造器; 必填字段校验统一放在各自的 {@code serialize()} 中.
 */
public abstract class DTRecipeJS extends RecipeJS {
	/**
	 * 链式构建的值在构造之后才逐个设置, 基类的空值检查会在这里误报;
	 * 因此跳过基类校验, 由 {@link #serialize()} 在写出时统一校验必填字段.
	 */
	@Override
	public void afterLoaded() {
	}

	protected RecipeJS addItem(RecipeKey<InputItem[]> key, Object from) {
		ArrayRecipeComponent<InputItem> array = cast(key);
		InputItem value = array.component().read(this, from);
		InputItem[] current = getValue(key);
		setValue(key, array.add(current == null ? array.emptyArray() : current, value));
		return this;
	}

	protected RecipeJS addItemOutput(RecipeKey<OutputItem[]> key, Object from) {
		ArrayRecipeComponent<OutputItem> array = cast(key);
		OutputItem value = array.component().read(this, from);
		OutputItem[] current = getValue(key);
		setValue(key, array.add(current == null ? array.emptyArray() : current, value));
		return this;
	}

	protected RecipeJS addFluidInput(RecipeKey<InputFluid[]> key, Object from) {
		ArrayRecipeComponent<InputFluid> array = cast(key);
		InputFluid value = array.component().read(this, from);
		InputFluid[] current = getValue(key);
		setValue(key, array.add(current == null ? array.emptyArray() : current, value));
		return this;
	}

	protected RecipeJS addFluidOutput(RecipeKey<OutputFluid[]> key, Object from) {
		ArrayRecipeComponent<OutputFluid> array = cast(key);
		OutputFluid value = array.component().read(this, from);
		OutputFluid[] current = getValue(key);
		setValue(key, array.add(current == null ? array.emptyArray() : current, value));
		return this;
	}

	protected static <T> ArrayRecipeComponent<T> cast(RecipeKey<T[]> key) {
		return (ArrayRecipeComponent<T>) key.component;
	}
}
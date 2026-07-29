package dev.celestiacraft.deep_tech.compat.kubejs;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.CurshingSchema;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class DeepTechKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		event.namespace(DeepTech.MODID)
				.register("crushing", CurshingSchema.SCHEMA);
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("DeepTech", DeepTech.class);
	}
}
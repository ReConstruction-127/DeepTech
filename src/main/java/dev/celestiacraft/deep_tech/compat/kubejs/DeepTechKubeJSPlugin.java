package dev.celestiacraft.deep_tech.compat.kubejs;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.AlloySchema;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.AssemblingSchema;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.CultivationSchema;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.CurshingSchema;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.HarvestSchema;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.InteractionSchema;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.ProcessorSchema;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class DeepTechKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		event.namespace(DeepTech.MODID)
				.register("alloy", AlloySchema.SCHEMA)
				.register("crushing", CurshingSchema.SCHEMA)
				.register("assembling", AssemblingSchema.SCHEMA)
				.register("cultivation", CultivationSchema.SCHEMA)
				.register("harvest", HarvestSchema.SCHEMA)
				.register("interaction", InteractionSchema.SCHEMA)
				.register("processing", ProcessorSchema.SCHEMA);
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("DeepTech", DeepTech.class);
	}
}
package dev.celestiacraft.deep_tech;

import com.tterrag.registrate.Registrate;
import dev.celestiacraft.deep_tech.common.register.*;
import dev.celestiacraft.deep_tech.config.CommonConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DeepTech.MODID)
public class DeepTech {
	public static final String MODID = "deep_tech";
	public static final String NAME = "Deep Tech";
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	public static final Registrate REGISTRATE = Registrate.create(MODID);

	public static ResourceLocation loadResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public static ResourceLocation loadGui(String path) {
		return loadResource("textures/gui/%s.png".formatted(path));
	}

	public static void registerLog(String registerType) {
		LOGGER.info("{} {} is Registered!", NAME, registerType);
	}

	public DeepTech(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		DTBlocks.register();
		DTItems.register();
//		DTMaterials.register();
		DTBlockEntities.register();
		DTRecipes.register(bus);
		DTFluids.register();
		// 创造模式标签页注册请确保一定在最下面
		DTCreativeTabs.register(bus);

		registerConfig(context);
	}

	private static void registerConfig(FMLJavaModLoadingContext context) {
		context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "nebula/deep_tech/common.toml");
	}
}
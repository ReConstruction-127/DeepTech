package dev.celestiacraft.deep_tech.api.register.fluid;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.texture.DTFluidTexture;
import dev.celestiacraft.libs.api.register.fluid.BasicFluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DTFluidBuilder<T extends ForgeFlowingFluid> {

	@FunctionalInterface
	public interface FluidTickHandler {
		void tick(Level level, BlockPos pos, FluidState state);
	}

	private final Registrate registrate;
	private final String name;
	private final FluidBuilder.FluidTypeFactory typeFactory;
	private final NonNullFunction<ForgeFlowingFluid.Properties, T> fluidFactory;

	private ResourceLocation flowingTexture;
	private ResourceLocation stillTexture;
	private int tintColor = 0xFFFFFFFF;
	private FluidBuilder<T, Registrate> builder;
	private boolean sourceConfigured;
	private FluidTickHandler tickHandler;

	private DTFluidBuilder(Registrate registrate, String name, FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<ForgeFlowingFluid.Properties, T> fluidFactory) {
		this.registrate = registrate;
		this.name = name;
		this.typeFactory = typeFactory;
		this.fluidFactory = fluidFactory;
	}

	public static DTFluidBuilder<ForgeFlowingFluid.Flowing> of(String name) {
		return of(DeepTech.REGISTRATE, name);
	}

	public static DTFluidBuilder<ForgeFlowingFluid.Flowing> of(Registrate registrate, String name) {
		return of(registrate, name, null);
	}

	public static DTFluidBuilder<ForgeFlowingFluid.Flowing> of(String name, FluidBuilder.FluidTypeFactory typeFactory) {
		return of(DeepTech.REGISTRATE, name, typeFactory);
	}

	public static DTFluidBuilder<ForgeFlowingFluid.Flowing> of(Registrate registrate, String name, FluidBuilder.FluidTypeFactory typeFactory) {
		return of(registrate, name, typeFactory, ForgeFlowingFluid.Flowing::new);
	}

	public static <T extends ForgeFlowingFluid> DTFluidBuilder<T> of(String name, FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<ForgeFlowingFluid.Properties, T> fluidFactory) {
		return of(DeepTech.REGISTRATE, name, typeFactory, fluidFactory);
	}

	public static <T extends ForgeFlowingFluid> DTFluidBuilder<T> of(Registrate registrate, String name, FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<ForgeFlowingFluid.Properties, T> fluidFactory) {
		return new DTFluidBuilder<>(registrate, name, typeFactory, fluidFactory);
	}

	public DTFluidBuilder<T> flowing(ResourceLocation texture) {
		checkMutable();
		flowingTexture = normalizeTexture(texture);
		return this;
	}

	public DTFluidBuilder<T> flowing(String texture) {
		return flowing(DeepTech.loadResource(texture));
	}

	public DTFluidBuilder<T> still(ResourceLocation texture) {
		checkMutable();
		stillTexture = normalizeTexture(texture);
		return this;
	}

	public DTFluidBuilder<T> still(String texture) {
		return still(DeepTech.loadResource(texture));
	}

	public DTFluidBuilder<T> textures(ResourceLocation flowingTexture, ResourceLocation stillTexture) {
		return flowing(flowingTexture).still(stillTexture);
	}

	public DTFluidBuilder<T> textures(String flowingTexture, String stillTexture) {
		return flowing(flowingTexture).still(stillTexture);
	}

	public DTFluidBuilder<T> textures(DTFluidTexture texture) {
		return flowing(texture.getFlowing()).still(texture.getStill());
	}

	public DTFluidBuilder<T> tint(int tintColor) {
		checkMutable();
		this.tintColor = tintColor;
		return this;
	}

	/**
	 * 给流体注册 tick 回调, 源液体和流动液体都会生效
	 * 使用该方法时不要再传自定义流体工厂, 直接用 {@link #of(String)}。
	 */
	public DTFluidBuilder<T> tick(FluidTickHandler handler) {
		checkMutable();
		tickHandler = handler;
		return this;
	}

	public DTFluidBuilder<T> properties(NonNullConsumer<FluidType.Properties> consumer) {
		builder().properties(consumer);
		return this;
	}

	public DTFluidBuilder<T> fluidProperties(NonNullConsumer<ForgeFlowingFluid.Properties> consumer) {
		builder().fluidProperties(consumer);
		return this;
	}

	public FluidBuilder<T, Registrate> builder() {
		if (builder == null) {
			checkTextures();
			FluidBuilder.FluidTypeFactory factory = typeFactory == null ? this::createBasicFluidType : typeFactory;
			if (tickHandler == null) {
				builder = registrate.fluid(name, stillTexture, flowingTexture, factory, fluidFactory);
			} else {
				FluidTickHandler handler = tickHandler;
				builder = registrate.fluid(name, stillTexture, flowingTexture, factory, properties -> createTickedFlowing(properties, handler));
			}
		}
		return builder;
	}

	private T createTickedFlowing(ForgeFlowingFluid.Properties properties, FluidTickHandler handler) {
		return (T) new ForgeFlowingFluid.Flowing(properties) {
			@Override
			public void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull FluidState state) {
				super.tick(level, pos, state);
				handler.tick(level, pos, state);
			}
		};
	}

	public FluidBuilder<T, Registrate> source() {
		if (tickHandler == null) {
			return source(ForgeFlowingFluid.Source::new);
		}
		FluidTickHandler handler = tickHandler;
		return source((properties) -> new ForgeFlowingFluid.Source(properties) {
			@Override
			public void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull FluidState state) {
				super.tick(level, pos, state);
				handler.tick(level, pos, state);
			}
		});
	}

	public FluidBuilder<T, Registrate> source(NonNullFunction<ForgeFlowingFluid.Properties, ? extends ForgeFlowingFluid> factory) {
		FluidBuilder<T, Registrate> fluid = builder();
		if (!sourceConfigured) {
			fluid.source(factory);
			sourceConfigured = true;
		}
		return fluid;
	}

	public BlockBuilder<LiquidBlock, FluidBuilder<T, Registrate>> block() {
		return builder().block();
	}

	public ItemBuilder<BucketItem, FluidBuilder<T, Registrate>> bucket() {
		return source().bucket();
	}

	public <I extends BucketItem> ItemBuilder<I, FluidBuilder<T, Registrate>> bucket(NonNullBiFunction<Supplier<? extends ForgeFlowingFluid>, Item.Properties, ? extends I> factory) {
		return source().bucket(factory);
	}

	public FluidBuilder<T, Registrate> noBucket() {
		return builder().noBucket();
	}

	public FluidEntry<T> register() {
		return source().register();
	}

	private void checkMutable() {
		if (builder != null) {
			throw new IllegalStateException("Fluid textures must be configured before creating the Registrate builder: " + name);
		}
	}

	private ResourceLocation normalizeTexture(ResourceLocation texture) {
		String path = texture.getPath();
		if (path.startsWith("textures/")) {
			path = path.substring("textures/".length());
		}
		if (path.endsWith(".png")) {
			path = path.substring(0, path.length() - ".png".length());
		}
		return ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), path);
	}

	private void checkTextures() {
		if (flowingTexture == null || stillTexture == null) {
			throw new IllegalStateException("Fluid textures must be configured before creating the Registrate builder: " + name);
		}
	}

	private BasicFluidType createBasicFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
		int color = tintColor;
		return new BasicFluidType(properties) {
			@Override
			public ResourceLocation getFlowingTexture() {
				return flowingTexture;
			}

			@Override
			public ResourceLocation getStillTexture() {
				return stillTexture;
			}

			@Override
			public int getTintColor() {
				return color;
			}
		};
	}
}

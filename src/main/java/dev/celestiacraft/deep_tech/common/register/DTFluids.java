package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.DeepTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DTFluids {
    // 流体类型注册
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, DeepTech.MODID);

    // 流体注册
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, DeepTech.MODID);

    public static final RegistryObject<FluidType> LIQUID_EXPERIENCE_TYPE =
            FLUID_TYPES.register(
                    "liquid_experience",
                    () -> new FluidType(
                            FluidType.Properties.create()
                                    .descriptionId(
                                            "fluid.deep_tech.liquid_experience"
                                    )
                                    .density(1000)
                                    .viscosity(1000)
                                    .temperature(300)
                    )
                    {
                        public ResourceLocation getStillTexture() {
                            // ✅ 指向你的自定义静态纹理
                            return new ResourceLocation(DeepTech.MODID, "fluid/liquid_experience_still");
                        }
                        public ResourceLocation getFlowingTexture() {
                            // ✅ 指向你的自定义流动纹理
                            return new ResourceLocation(DeepTech.MODID, "fluid/liquid_experience_flow");
                        }
                    }
            );

    public static final RegistryObject<ForgeFlowingFluid.Source> LIQUID_EXPERIENCE = FLUIDS.register(
            "liquid_experience",
            () -> new ForgeFlowingFluid.Source(DTFluids.LIQUID_EXPERIENCE_PROPERTIES)
    );

    public static final RegistryObject<ForgeFlowingFluid.Flowing> LIQUID_EXPERIENCE_FLOWING = FLUIDS.register(
            "flowing_liquid_experience",
            () -> new ForgeFlowingFluid.Flowing(DTFluids.LIQUID_EXPERIENCE_PROPERTIES)
    );

    // ✅ 流体属性
    public static final ForgeFlowingFluid.Properties LIQUID_EXPERIENCE_PROPERTIES =
            new ForgeFlowingFluid.Properties(
                    LIQUID_EXPERIENCE_TYPE,
                    LIQUID_EXPERIENCE,
                    LIQUID_EXPERIENCE_FLOWING
            )
                    .tickRate(10)
            // ✅ 如果需要桶，取消注释下面的行并创建对应的物品
            // .bucket(() -> DTItems.LIQUID_EXPERIENCE_BUCKET.get())
            ;

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
    }
}
package dev.celestiacraft.deep_tech.common.block.machine.plugins;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.api.block.machine.plugin.ISpeedPluginSource;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 加速插件: 贴在仿生(bionic)机器旁边的方块, 让那台机器的处理速度翻倍.
 * <p>
 * 一台机器最多吃两个(两个就是 x4), 具体规则见 {@code MachinePlugins}.
 * 它自己不做事, 只是让机器扫邻居的时候能认出来; 隔着一个扩展坞插也算,
 * 因为扩展坞会把它旁边的插件数量转给正对着的机器.
 */
public class SpeedPluginBlock extends BasicBlock implements ISpeedPluginSource {
	public SpeedPluginBlock(Properties properties) {
		super(MachineBlock.advancedProperties(properties)
				.requiresCorrectToolForDrops());
	}

	/**
	 * 自己就算一个加速插件.
	 * <p>
	 * 顺手把自己记进 {@code visited}: 同一个插件如果既贴着机器, 又贴着另一台转发的扩展坞,
	 * 只算一次, 不至于一个插件白拿双倍.
	 */
	@Override
	public int getSpeedPluginCount(@NotNull Level level, @NotNull BlockPos pos, @NotNull Set<BlockPos> visited) {
		return visited.add(pos.immutable()) ? 1 : 0;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			ModelFile model = provider.models().cubeAll(
					"block/machine/plugins/speed",
					provider.modLoc("block/machine/plugins/speed")
			);

			provider.simpleBlock(context.get(), model);
		};
	}
}

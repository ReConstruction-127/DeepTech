package dev.celestiacraft.deep_tech.common.block.machine.reactor;

import dev.celestiacraft.deep_tech.common.register.block.ReactorBlocks;
import dev.celestiacraft.deep_tech.tags.DeepTechBlockTags;
import dev.celestiacraft.libs.compat.patchouli.multiblock.StructureBuilder;
import net.minecraft.world.level.block.Blocks;
import vazkii.patchouli.api.IMultiblock;

/**
 * 幽匿反应堆的多方块结构定义 (5×5×5).
 * <p>
 * 同一份结构被两处使用, 因此必须保持一致:
 * <ul>
 *     <li>运行期校验: {@code MultiblockHandler} 拿着这里 build 出来的 {@link IMultiblock} 去比对世界;</li>
 *     <li>帕秋莉图鉴: {@code assets/deep_tech/patchouli_books/sculk_codex/*&#47;entries/sculk_reactor.json}
 *     里内联了一份逐字符相同的 pattern.</li>
 * </ul>
 * <p>
 * 字符含义:
 * <ul>
 *     <li>{@code 0} = 反应堆控制器, 同时也是结构锚点(帕秋莉用 {@code 0} 定位结构);
 *     <b>必须显式映射</b>, 因为帕秋莉里 {@code 0} 默认是空气, 不映射会导致结构永远校验不通过</li>
 *     <li>{@code 1} = 外壳, 走 {@link DeepTechBlockTags#REACTOR_CASING} 标签, 于是扩展坞可以替换外壳</li>
 *     <li>{@code 2} = 玻璃</li>
 *     <li>{@code 4} = 幽匿催发体</li>
 *     <li>{@code C} = 生长腔, 走 {@link DeepTechBlockTags#REACTOR_CAVITY} 标签(空气或幽匿块),
 *     否则长满幽匿块之后结构就会失效</li>
 * </ul>
 * <p>
 * 层序遵循帕秋莉约定: <b>y 从顶到底</b>, 所以数组第 0 层是最顶层, 最后一层是最底层;
 * 层内是 {@code pattern[y][x].charAt(z)}(x 西→东, z 北→南)。
 * 注意 {@code StructureBuilder} 预置的 {@code ' '} 是 anyMatcher 而不是空气, 所以本结构里不使用空格。
 */
public final class ReactorStructure {
	/**
	 * 生长腔格数: 第 3、4 层内部 3×3×2 = 18 格, 填满即爆炸
	 */
	public static final int CAVITY_SIZE = 18;

	public static final String[][] PATTERN = {
			{ "22222", "22222", "22222", "22222", "22222" },
			{ "22222", "2CCC2", "2CCC2", "2CCC2", "22222" },
			{ "22222", "2CCC2", "2CCC2", "2CCC2", "22222" },
			{ "22222", "24442", "24442", "24442", "22222" },
			{ "11111", "11111", "11111", "11111", "11011" }
	};

	/**
	 * 结构半边长: 5×5×5 以中心为原点占 ±2 格
	 */
	public static final int HALF_SIZE = 2;

	private static IMultiblock instance;

	private ReactorStructure() {
	}

	/**
	 * 懒构建结构: 不能在静态块里直接 build, 否则会提前触发 {@link ReactorBlocks} 的类初始化。
	 */
	public static IMultiblock structure() {
		if (instance == null) {
			instance = StructureBuilder.create(PATTERN)
					.define('0', builder -> builder.block(ReactorBlocks.REACTOR_CONTROLLER.get()))
					.define('1', builder -> builder.tag(DeepTechBlockTags.REACTOR_CASING))
					.define('2', builder -> builder.block(Blocks.TINTED_GLASS))
					.define('4', builder -> builder.block(Blocks.SCULK_CATALYST))
					.define('C', builder -> builder.tag(DeepTechBlockTags.REACTOR_CAVITY))
					.build();
		}

		return instance;
	}
}

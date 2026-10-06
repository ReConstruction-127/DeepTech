package dev.celestiacraft.deep_tech.common.block.machine.reactor;

import dev.celestiacraft.deep_tech.tags.DeepTechBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 反应堆过载时向外蔓延 / 扩散幽匿的工具.
 *
 * <h3>两个阶段用的是两套完全不同的逻辑</h3>
 * <ul>
 *     <li><b>外围蔓延(20 次)</b>: 只在<b>紧贴结构表面的那一层</b>操作, 而且只认空气。
 *     先放<b>幽匿脉络</b>; 某个位置已经有脉络又被选中, 就升级成<b>幽匿块</b>;
 *     已经是幽匿块的位置不再作为候选。脉络的六面属性按邻居算出, 保证朝向正确。
 *     这一步不向外扩散, 只是贴着结构表面铺一层。</li>
 *     <li><b>连续扩散</b>: 面贴面地长出去 —— 每个幽匿块挑一个合法的面把邻居变成幽匿块,
 *     新块与旧块下一轮继续各自扩散, 直到六面都不满足条件。</li>
 * </ul>
 *
 * <h3>覆盖规则</h3>
 * 硬度 != -1, 且不在 {@link DeepTechBlockTags#REACTOR_INERT} 里(该标签包含反应堆外壳、控制器、
 * 能量接收器、全部 bionic 机器, 以及原版幽匿系方块 —— <b>幽匿脉络除外</b>, 它是要被扩散覆盖掉的装饰)。
 * 空气天然满足硬度条件, 所以也能往空气里蔓延。
 * 覆盖一个带方块实体的方块之前, 会先把里面的物品正常掉出来。
 */
public final class ReactorMeltdown {
	/**
	 * 紧贴结构表面那一层的切比雪夫距离
	 */
	private static final int SURFACE_SHELL = ReactorStructure.HALF_SIZE + 1;

	private ReactorMeltdown() {
	}

	/**
	 * 是不是幽匿系方块(急停时用它清理结构内部, 催发体也算在内)
	 */
	public static boolean isSculkFamily(BlockState state) {
		return state.is(Blocks.SCULK)
				|| state.is(Blocks.SCULK_VEIN)
				|| state.is(Blocks.SCULK_CATALYST)
				|| state.is(Blocks.SCULK_SHRIEKER)
				|| state.is(Blocks.SCULK_SENSOR)
				|| state.is(Blocks.CALIBRATED_SCULK_SENSOR);
	}

	/**
	 * 这一格能不能被覆盖
	 */
	public static boolean canCover(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.is(DeepTechBlockTags.REACTOR_INERT)) {
			return false;
		}

		// 硬度 -1 = 不可破坏(基岩/屏障等)
		return state.getDestroySpeed(level, pos) != -1.0F;
	}

	// ------------------------------------------------------------------
	// 外围蔓延: 贴着结构表面铺脉络 / 幽匿块
	// ------------------------------------------------------------------

	/**
	 * 结构表面的候选格: 与 5×5×5 结构面贴面的那一层, 且当前是空气或已有幽匿脉络。
	 */
	public static List<BlockPos> surfaceCandidates(Level level, BlockPos center) {
		List<BlockPos> candidates = new ArrayList<>();

		for (BlockPos pos : BlockPos.betweenClosed(
				center.offset(-SURFACE_SHELL, -SURFACE_SHELL, -SURFACE_SHELL),
				center.offset(SURFACE_SHELL, SURFACE_SHELL, SURFACE_SHELL))) {

			if (chebyshev(pos, center) != SURFACE_SHELL) {
				continue;
			}

			BlockState state = level.getBlockState(pos);

			// 空气待铺装饰; 已有装饰的还能被升级成幽匿块; 已经是幽匿块的不可再选
			if (state.isAir() || isSurfaceDecoration(state)) {
				candidates.add(pos.immutable());
			}
		}

		return candidates;
	}

	/**
	 * 在过载阶段(<b>还不是完全失控</b>)贴结构表面放一次: 空气 → 普通幽匿脉络; 已有装饰 → 升级成幽匿块。
	 * <p>
	 * 这一步<b>不做装饰替换、也不生成效果云</b> —— 那两件事只发生在完全失控的扩散阶段。
	 *
	 * @return 这一格最终变成了什么所在的坐标(脉络或幽匿块), 没放成返回 null
	 */
	@Nullable
	public static BlockPos placeOnSurface(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.isAir()) {
			BlockState vein = veinStateFor(level, pos);

			// 六个面都贴不上(比如悬空)就不放, 免得放了立刻掉
			if (!vein.canSurvive(level, pos)) {
				return null;
			}

			level.setBlockAndUpdate(pos, vein);
			return pos.immutable();
		}

		if (isSurfaceDecoration(state)) {
			level.setBlockAndUpdate(pos, Blocks.SCULK.defaultBlockState());
			return pos.immutable();
		}

		return null;
	}

	/**
	 * 铺一个表面装饰(只在完全失控的扩散阶段用), 四种形态按概率:
	 * <b>97% 幽匿脉络 / 1% 幽匿感测体 / 1% 幽匿尖啸体 / 1% 幽匿催发体</b>。
	 * <p>
	 * 只有目标是空气才会放; 脉络还要算出六面朝向(朝着能贴住的邻居), 贴不上就不放。
	 *
	 * @return 装饰所在坐标, 没放成返回 null
	 */
	@Nullable
	public static BlockPos placeDecoration(Level level, BlockPos pos) {
		if (!level.getBlockState(pos).isAir()) {
			return null;
		}

		int roll = level.random.nextInt(100);
		BlockState state;

		if (roll < 97) {
			state = veinStateFor(level, pos);

			// 六个面都贴不上(比如悬空)就不放, 免得放了立刻掉
			if (!state.canSurvive(level, pos)) {
				return null;
			}
		} else if (roll < 98) {
			state = Blocks.SCULK_SENSOR.defaultBlockState();
		} else if (roll < 99) {
			state = Blocks.SCULK_SHRIEKER.defaultBlockState();
		} else {
			state = Blocks.SCULK_CATALYST.defaultBlockState();
		}

		level.setBlockAndUpdate(pos, state);
		return pos.immutable();
	}

	/**
	 * 给一个幽匿块挂表面装饰: 优先挂在<b>正上方</b>, 上方不行就在紧邻的空气格里随机找。
	 *
	 * @return 装饰所在坐标, 没挂成返回 null
	 */
	@Nullable
	public static BlockPos decorateAround(Level level, BlockPos sculkPos) {
		BlockPos placed = placeDecoration(level, sculkPos.above());

		if (placed != null) {
			return placed;
		}

		Direction[] directions = Direction.values();
		int start = level.random.nextInt(directions.length);

		for (int i = 0; i < directions.length; i++) {
			placed = placeDecoration(level, sculkPos.relative(directions[(start + i) % directions.length]));

			if (placed != null) {
				return placed;
			}
		}

		return null;
	}

	/**
	 * 是不是"表面装饰"那几种方块(用来判断能不能被升级成幽匿块)
	 */
	public static boolean isSurfaceDecoration(BlockState state) {
		return state.is(Blocks.SCULK_VEIN)
				|| state.is(Blocks.SCULK_SENSOR)
				|| state.is(Blocks.SCULK_SHRIEKER)
				|| state.is(Blocks.SCULK_CATALYST);
	}

	/**
	 * 算幽匿脉络的六面属性: 朝着"能贴住的邻居"那个面置 true, 这样脉络朝向不会出错。
	 */
	private static BlockState veinStateFor(Level level, BlockPos pos) {
		BlockState vein = Blocks.SCULK_VEIN.defaultBlockState();

		for (Direction direction : Direction.values()) {
			BlockPos neighbour = pos.relative(direction);

			if (MultifaceBlock.canAttachTo(level, direction, neighbour, level.getBlockState(neighbour))) {
				vein = vein.setValue(MultifaceBlock.getFaceProperty(direction), true);
			}
		}

		return vein;
	}

	// ------------------------------------------------------------------
	// 连续扩散: 面贴面蔓延
	// ------------------------------------------------------------------

	/**
	 * 从这一格幽匿块挑一个合法的面扩散一次.
	 * <p>
	 * 方向池不是固定权重, 而是每次现算:
	 * <ul>
	 *     <li>水平方向 3~4 份、垂直方向 1~2 份 —— 权重随机, 所以不会压出平整的上下平面;</li>
	 *     <li>再往池子里多塞几份 {@code heading}(这一块"来时的方向"), 让它倾向于顺着原走向继续爬,
	 *     长成枝蔓而不是一层层规则地往外糊。</li>
	 * </ul>
	 * <b>没有半径边界</b>: 只有惰性方块、硬度 -1 的方块, 以及没加载的区块能挡住它。
	 *
	 * @param heading 这一块是从哪个方向长过来的, 可为 null
	 * @return 新长出来的幽匿块坐标, 没长出来返回 null
	 */
	public static BlockPos growOneFace(Level level, BlockPos from, @Nullable Direction heading) {
		for (Direction direction : shuffledFloodDirections(level.random, heading)) {
			BlockPos target = from.relative(direction);

			if (!level.hasChunkAt(target) || !canCover(level, target)) {
				continue;
			}

			dropContents(level, target);
			level.setBlockAndUpdate(target, Blocks.SCULK.defaultBlockState());
			return target.immutable();
		}

		return null;
	}

	/**
	 * 现算一张乱序方向表当扩散方向池.
	 * <p>
	 * 水平方向 3~4 份、垂直方向 1~2 份(每次随机), 再额外塞入若干份 {@code heading},
	 * 于是既保留"横向铺开"的倾向, 又不会长出平整的上下平面, 还会顺着来向爬出枝蔓。
	 */
	private static Direction[] shuffledFloodDirections(RandomSource random, @Nullable Direction heading) {
		List<Direction> pool = new ArrayList<>();
		int horizontalWeight = 3 + random.nextInt(2);
		int verticalWeight = 1 + random.nextInt(2);

		for (Direction direction : Direction.values()) {
			int weight = direction.getAxis() == Direction.Axis.Y ? verticalWeight : horizontalWeight;

			for (int i = 0; i < weight; i++) {
				pool.add(direction);
			}
		}

		if (heading != null) {
			for (int i = 0; i < 4; i++) {
				pool.add(heading);
			}
		}

		Direction[] array = pool.toArray(new Direction[0]);

		for (int i = array.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			Direction swap = array[i];
			array[i] = array[j];
			array[j] = swap;
		}

		return array;
	}

	private static int chebyshev(BlockPos pos, BlockPos center) {
		return Math.max(
				Math.abs(pos.getX() - center.getX()),
				Math.max(Math.abs(pos.getY() - center.getY()), Math.abs(pos.getZ() - center.getZ()))
		);
	}

	// ------------------------------------------------------------------
	// 内容物掉落
	// ------------------------------------------------------------------

	/**
	 * 把这一格上的方块实体里的东西掉出来, 再让调用方替换掉方块.
	 * <p>
	 * 优先走原版 {@link Container}(箱子、桶、熔炉这类, 语义最准); 不是 Container 的方块实体
	 * (很多模组机器只暴露物品能力)再退回 Forge 的物品能力。两条路互斥, 避免同一份内容物掉两次。
	 */
	private static void dropContents(Level level, BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity == null) {
			return;
		}

		if (blockEntity instanceof Container container) {
			Containers.dropContents(level, pos, container);
			container.clearContent();
			return;
		}

		IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);

		if (handler == null) {
			return;
		}

		for (int slot = 0; slot < handler.getSlots(); slot++) {
			ItemStack stack = handler.getStackInSlot(slot);

			if (!stack.isEmpty()) {
				Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack.copy());
			}
		}
	}
}

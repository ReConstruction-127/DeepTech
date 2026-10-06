package dev.celestiacraft.deep_tech.common.block.machine.reactor;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.block.machine.plugin.MachinePlugins;
import dev.celestiacraft.deep_tech.api.gui.MachineItemSlots;
import dev.celestiacraft.deep_tech.api.gui.widget.EnergyBarWidget;
import dev.celestiacraft.deep_tech.api.gui.widget.ProportionalTankWidget;
import dev.celestiacraft.deep_tech.api.fluid.SingleTankFluidTransfer;
import dev.celestiacraft.deep_tech.common.register.DTEffects;
import dev.celestiacraft.deep_tech.common.register.DTFluids;
import dev.celestiacraft.deep_tech.common.register.block.ReactorBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.ReactorConfig;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.ITickableBlockEntity;
import dev.celestiacraft.libs.compat.patchouli.multiblock.IMultiblockProvider;
import dev.celestiacraft.libs.compat.patchouli.multiblock.MultiblockHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 反应堆控制器.
 * <p>
 * 结构校验交给 nebula_libs 的 {@link MultiblockHandler}(内部用帕秋莉的 {@code IMultiblock.simulate}),
 * 结构定义在 {@link ReactorStructure}。
 *
 * <h3>正常工作</h3>
 * 结构成立时每 {@code GROW_INTERVAL}(默认 5 tick)消耗 {@code MB_PER_SCULK}(默认 100 mB)
 * 幽匿培养液, 在生长腔里<b>随机放 1-3 个</b>幽匿块。
 * 培养液只从<b>第一格输入罐</b>取, 那一格也只接受幽匿培养液; 其余两个输入罐反过来不收培养液(见 {@link #canFillFluid}).
 * <b>贴在控制器上的加速插件(最多 2 个, 见 {@link MachinePlugins})会缩短这个间隔</b> ——
 * 注意它只加快正常工作, 失控阶段的节奏不受影响。
 * <p>
 * <b>只有机器自己把生长腔填满才会进入过载</b>: 玩家手动往空腔里塞满幽匿块不会触发爆炸,
 * 机器不工作(结构不成立 / 没有培养液)时拆掉反应堆也不会爆炸。
 * <p>
 * <b>但机器正在运行时拆掉结构会直接进入失控</b> —— 判定依据是"这台机器自己长出过幽匿块,
 * 而且腔里现在还有": 手动塞满的、以及被红石急停清空过的结构, 拆掉都是安全的。
 *
 * <h3>过载序列</h3>
 * <ol>
 *     <li>{@code STAGE_SPREADING}: 每 {@code SPREAD_INTERVAL} 在<b>紧贴结构表面的一层</b>操作,
 *     每次 3-5 格: 空气铺<b>普通幽匿脉络</b>(这一步不做装饰替换, 也不生成效果云),
 *     已有装饰的位置再被选中就升级成幽匿块。共 {@code SPREAD_OPERATIONS}(默认 20)次。
 *     <br>这一步里如果机器内部空间被重新清空(腔里的幽匿块被挖光), 会<b>中止过载并把计数器清零重置</b>。</li>
 *     <li>{@code STAGE_DISPERSING}: <b>扩散格数 = 进入扩散时第一格储罐里的培养液量</b>(1 mB = 1 格:
 *     装 8000 就一共长 8000 格, 装 1 就只有 1 格)。先记下这个量并<b>作废机器内全部流体</b>,
 *     然后面贴面连续扩散 —— 每个幽匿块挑一个合法的面把邻居变成幽匿块, 新块与旧块下一轮继续各自扩散,
 *     直到额度用完或者六面都不满足条件。<b>没有半径边界</b>: 只有惰性方块、硬度 -1 的方块和未加载区块能挡住它。
 *     每 {@code DISPERSAL_INTERVAL}(默认 10 tick)蔓延一步, 每步最多
 *     {@code DISPERSAL_STEP_LIMIT}(默认 512)格。
 *     <br><b>野蛮生长, 而不是糊成一块实心团</b>: 每个幽匿块只有 {@code SPREAD_BUDGET_MIN}~{@code SPREAD_BUDGET_MAX}
 *     次扩散额度, 用完就退出活跃集 —— 长出来的是一丛"会死的枝蔓"(自避 + 断路, 带大量空洞),
 *     而不是把可达区域填满的实心团(后者外表面必然又平又滑)。
 *     方向池每次现算(水平 3~4 份、垂直 1~2 份), 并额外偏向"来时的方向"让它顺着原走向爬;
 *     每步还有 30% 概率"先不长", 各枝蔓推进不同步。于是既没有平滑的面, 也没有平整的棱角。
 *     <br><b>装饰只发生在这个阶段</b>: 每个长出来的幽匿块挂一个表面装饰
 *     (97% 脉络 / 1% 感测体 / 1% 尖啸体 / 1% 催发体),
 *     其中<b>只有幽匿脉络</b>有 {@code CLOUD_CHANCE}(默认 0.001%)的概率生成持续
 *     {@code CLOUD_DURATION}(默认 6000 tick = 5 分钟)的感染效果云。</li>
 *     <li>扩散结束 → <b>两次爆炸</b>: 先在机器正中央, 再在控制器处, 威力为 {@code EXPLOSION_POWER}(默认 4, 与 TNT 一致)。</li>
 * </ol>
 *
 * <h3>拆掉控制器</h3>
 * 机器<b>热</b>的时候(正在失控, 或者正在运行而且腔里还有自己长出来的幽匿块)被挖掉控制器,
 * 会立刻引爆: 威力固定与 TNT 一致, 同样先在机器正中央、再在控制器处各爆一次。
 * 冷的机器(没运转过 / 已被红石急停清空)拆掉什么都不会发生。
 *
 * <h3>红石 = 紧急急停</h3>
 * 收到红石信号时: 作废内部全部物品 / 流体 / 能量, 清除结构内所有幽匿系方块(含催发体),
 * 并中止正在进行的过载 —— 这是唯一的"刹车"。
 */
public class ReactorControllerBlockEntity extends MachineBlockEntity<ReactorControllerBlockEntity>
		implements ITickableBlockEntity<ReactorControllerBlockEntity>, IUIHolder.BlockEntityUI, IMultiblockProvider {

	public static final int ITEM_INPUT_SLOTS = 2;
	public static final int ITEM_OUTPUT_SLOTS = 1;
	public static final int FLUID_INPUT_TANKS = 3;
	public static final int FLUID_OUTPUT_TANKS = 2;
	public static final int TANK_CAPACITY = 8000;

	/**
	 * 结构半边长: 5×5×5 以中心为原点占 ±2 格
	 */
	public static final int STRUCTURE_HALF_SIZE = ReactorStructure.HALF_SIZE;

	private static final int STAGE_IDLE = 0;
	private static final int STAGE_SPREADING = 1;
	private static final int STAGE_DISPERSING = 2;

	/**
	 * 拆控制器引爆用的固定威力: 与 TNT 一致
	 */
	private static final float TNT_POWER = 4.0F;

	/**
	 * 效果云自身的半径(格)
	 */
	private static final int CLOUD_RADIUS_BLOCKS = 3;

	/**
	 * 扩散每一步里"这一步先不长"的概率(百分比).
	 * <p>
	 * 让各条枝蔓的推进不同步, 外形才不会是平滑的一层壳。
	 */
	private static final int SPREAD_SKIP_CHANCE = 30;

	/**
	 * 每个幽匿块还能再扩散几次(在这个区间里随机).
	 * <p>
	 * 这是"野蛮生长"的关键: 每块长够次数就退出, 于是整体是一丛会死的枝蔓,
	 * 而不是把可达区域填满的实心团 —— 后者外表面必然又平又滑。
	 */
	private static final int SPREAD_BUDGET_MIN = 2;
	private static final int SPREAD_BUDGET_MAX = 3;

	/**
	 * 扩散开始时原地扫描幽匿块的半径 —— 只是找种子, 与洪水范围无关
	 */
	private static final int SEED_SCAN_RADIUS = 8;

	/**
	 * 距下次生长 / 蔓延 / 扩散还剩多少 tick
	 */
	private int growCooldown = 0;
	/**
	 * 过载阶段
	 */
	private int overloadStage = STAGE_IDLE;
	/**
	 * 外围蔓延已进行的次数
	 */
	private int spreadCount = 0;
	/**
	 * 这台机器是否真的运转过(自己长出过幽匿块).
	 * <p>
	 * 用来区分两种"结构被拆": <b>正在运行时被拆</b>要进入失控, 而
	 * <b>没运转过(玩家手动塞满 / 已被红石急停清空)</b>拆掉是安全的。
	 */
	private boolean hasRun = false;
	/**
	 * 扩散阶段还剩几格.
	 * <p>
	 * 进入扩散时等于<b>第一格储罐里的培养液量(mB)</b>, 1 mB 长 1 格:
	 * 装 8000 就一共长 8000 格, 装 1 就只有 1 格。
	 */
	private int dispersalsLeft = 0;
	/**
	 * 正在引爆: 防止爆炸拆掉控制器时 onRemove 又触发一次引爆
	 */
	private boolean detonating = false;
	/**
	 * 结构中心的缓存: 结构有效时算一次; 结构被拆之后还要靠它定位失控中心
	 */
	@Nullable
	private BlockPos structureCenter;
	/**
	 * 结构中心: 过载开始时算一次并固定下来, 之后即使结构被破坏也继续按这个中心蔓延
	 */
	@Nullable
	private BlockPos overloadCenter;

	/**
	 * 扩散阶段的活跃幽匿块: 坐标 -> 这一块还能再扩散几次 + 它是从哪个方向长过来的.
	 * <p>
	 * 只放内存: 存档重载后在 {@link #onLoad()} 里按世界里的幽匿块重建。
	 */
	private final Map<BlockPos, Frontier> activeSculk = new HashMap<>();

	/**
	 * 活跃幽匿块的扩散状态
	 *
	 * @param budget  还能再扩散几次
	 * @param heading 来时的方向, 用来倾向于顺着原走向继续爬
	 */
	private record Frontier(int budget, @Nullable Direction heading) {
		Frontier spent() {
			return new Frontier(budget - 1, heading);
		}
	}

	/**
	 * 懒创建: 不能在字段初始化时 build, 那一刻 {@code BlockEntity.level} 还没赋值
	 * (原版是构造完成之后才 setLevel), 交给第一次用到时再建。
	 */
	@Nullable
	private MultiblockHandler multiblock;

	public ReactorControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ------------------------------------------------------------------
	// 多方块
	// ------------------------------------------------------------------

	@Override
	public MultiblockHandler getMultiblockHandler() {
		if (multiblock == null) {
			multiblock = MultiblockHandler
					.builder(this, ReactorStructure::structure)
					.translationKey("multiblock.deep_tech.sculk_reactor")
					.build();
		}

		return multiblock;
	}

	@Override
	public BlockEntity getBlockEntity() {
		return this;
	}

	/** 内部统一入口, 省得每处都写 getMultiblockHandler() */
	private MultiblockHandler multiblock() {
		return getMultiblockHandler();
	}

	// ------------------------------------------------------------------
	// 槽位 / 储罐 / 能量
	// ------------------------------------------------------------------

	@Override
	public int getItemInputSlotCount() {
		return ITEM_INPUT_SLOTS;
	}

	@Override
	public int getItemOutputSlotCount() {
		return ITEM_OUTPUT_SLOTS;
	}

	@Override
	public int getFluidInputTankCount() {
		return FLUID_INPUT_TANKS;
	}

	@Override
	public int getFluidOutputTankCount() {
		return FLUID_OUTPUT_TANKS;
	}

	@Override
	public int getMachineTankCapacity(int tank) {
		return TANK_CAPACITY;
	}

	@Override
	public int getMachineMaxEnergy() {
		return ReactorConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return ReactorConfig.MAX_RECEIVE.get();
	}

	/**
	 * 控制器是耗电方, 不对外输出
	 */
	@Override
	public int getMaxExtract() {
		return 0;
	}

	/**
	 * 输入罐的液体限制:
	 * <ul>
	 *     <li><b>第一格输入罐只允许幽匿培养液</b> —— 扩散格数就是它的液量;</li>
	 *     <li>其余两个输入罐反过来, <b>不允许装入幽匿培养液</b>;</li>
	 *     <li>输出罐不接受任何液体。</li>
	 * </ul>
	 */
	@Override
	public boolean canFillFluid(int tank, FluidStack stack) {
		if (!isFluidInputTank(tank)) {
			return false;
		}

		boolean culture = stack.getFluid().isSame(DTFluids.SCULK_CULTURE.getSource());

		if (tank == getFluidInputTankIndex(0)) {
			return culture;
		}

		return !culture;
	}

	// ------------------------------------------------------------------
	// tick
	// ------------------------------------------------------------------

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, ReactorControllerBlockEntity entity) {
		// 红石信号 = 紧急急停, 优先级最高
		if (level.hasNeighborSignal(pos)) {
			emergencyStop(level, pos, state);
			return;
		}

		// 过载一旦开始就自己跑完(失控反应), 不再依赖结构是否仍然完整
		if (overloadStage != STAGE_IDLE) {
			overloadTick(level, pos, state);
			return;
		}

		boolean valid = multiblock().isValid();

		if (!valid) {
			// 正在运行的机器被拆掉 => 直接进入失控
			BlockPos center = structureCenter != null ? structureCenter : pos;

			if (hasRun && countCavitySculk(level, center) > 0) {
				startOverload(level, pos, state);
				return;
			}

			// 没运转过, 或者已被红石急停清空: 拆了就是安全的
			updateLit(level, pos, state, false);
			growCooldown = 0;
			return;
		}

		if (structureCenter == null) {
			structureCenter = computeStructureCenter(pos);
		}

		updateLit(level, pos, state, true);

		if (++growCooldown < growInterval(level, pos)) {
			return;
		}
		growCooldown = 0;

		grow(level, pos, state);
	}

	/**
	 * 正常工作的间隔: 受加速插件影响(0 个 = 1 倍, 1 个 = 2 倍, 2 个 = 4 倍)。
	 * <p>
	 * 失控阶段不调用这里, 所以插件不会加快失控。
	 */
	private int growInterval(Level level, BlockPos pos) {
		int multiplier = MachinePlugins.getSpeedMultiplier(level, pos);
		return Math.max(1, ReactorConfig.GROW_INTERVAL.get() / multiplier);
	}

	// ------------------------------------------------------------------
	// 正常生长
	// ------------------------------------------------------------------

	/**
	 * 一次生长周期: 消耗 100 mB 培养液, 在生长腔里随机放 1-3 个幽匿块。
	 * <p>
	 * 关键: 只有<b>本次放置把空腔填满</b>才进入过载, 所以手动填满空腔不会爆炸。
	 */
	private void grow(Level level, BlockPos pos, BlockState state) {
		List<BlockPos> air = new ArrayList<>(multiblock().findFilterBlock(BlockState::isAir));

		if (air.isEmpty()) {
			// 空腔已经不剩空气: 可能是玩家手动塞满的, 机器没有在生长 —— 什么都不做
			return;
		}

		if (!drainCulture(ReactorConfig.MB_PER_SCULK.get())) {
			return;
		}

		int min = ReactorConfig.SCULK_PER_CYCLE_MIN.get();
		int max = Math.max(min, ReactorConfig.SCULK_PER_CYCLE_MAX.get());
		int count = min + level.random.nextInt(max - min + 1);

		for (int i = 0; i < count && !air.isEmpty(); i++) {
			BlockPos target = air.remove(level.random.nextInt(air.size()));
			level.setBlockAndUpdate(target, Blocks.SCULK.defaultBlockState());
		}

		hasRun = true;
		markDirty();

		if (air.isEmpty()) {
			startOverload(level, pos, state);
		}
	}

	/**
	 * 从<b>第一格输入罐</b>里扣幽匿培养液 —— 这台机器只用第一格输入罐。
	 * <p>
	 * 输入罐默认不允许抽取(见 {@code IMachineFluidConfig#canDrainFluid}), 但这里是机器自己消耗,
	 * 所以 {@code drainTank} 的最后一个参数传 false 跳过策略检查。
	 */
	private boolean drainCulture(int mb) {
		int tank = getFluidInputTankIndex(0);
		FluidStack stored = getFluidHandler().getFluidInTank(tank);
		FluidStack wanted = new FluidStack(DTFluids.SCULK_CULTURE.getSource(), mb);

		if (stored.getAmount() < mb || !stored.isFluidEqual(wanted)) {
			return false;
		}

		getFluidHandler().drainTank(tank, mb, IFluidHandler.FluidAction.EXECUTE, false);
		return true;
	}

	// ------------------------------------------------------------------
	// 过载序列
	// ------------------------------------------------------------------

	private void startOverload(Level level, BlockPos pos, BlockState state) {
		overloadStage = STAGE_SPREADING;
		spreadCount = 0;
		overloadCenter = structureCenter != null ? structureCenter : computeStructureCenter(pos);
		growCooldown = 0;

		updateLit(level, pos, state, true);
		markDirty();
		sync();
	}

	private void overloadTick(Level level, BlockPos pos, BlockState state) {
		BlockPos center = overloadCenter != null ? overloadCenter : pos;

		if (overloadStage == STAGE_SPREADING) {
			if (++growCooldown < ReactorConfig.SPREAD_INTERVAL.get()) {
				return;
			}
			growCooldown = 0;
			spreadOnce(level, pos, state, center);
			return;
		}

		if (++growCooldown < ReactorConfig.DISPERSAL_INTERVAL.get()) {
			return;
		}
		growCooldown = 0;
		dispersalTick(level, pos, center);
	}

	/**
	 * 外围蔓延一次: 在紧贴结构表面的那一层覆盖 3-5 格 —— 空气铺装饰, 已有装饰的升级成幽匿块。
	 */
	private void spreadOnce(Level level, BlockPos pos, BlockState state, BlockPos center) {
		// 机器内部空间被重新清空 => 停止过载, 计数器重置
		if (countCavitySculk(level, center) == 0) {
			abortOverload(level, pos, state);
			return;
		}

		List<BlockPos> candidates = ReactorMeltdown.surfaceCandidates(level, center);
		int min = ReactorConfig.SPREAD_MIN.get();
		int max = Math.max(min, ReactorConfig.SPREAD_MAX.get());
		int count = min + level.random.nextInt(max - min + 1);

		for (int i = 0; i < count && !candidates.isEmpty(); i++) {
			// 过载阶段只铺普通脉络 / 把已有装饰升级成幽匿块: 不做装饰替换, 也不生成效果云
			ReactorMeltdown.placeOnSurface(level, candidates.remove(level.random.nextInt(candidates.size())));
		}

		spreadCount++;

		if (spreadCount >= ReactorConfig.SPREAD_OPERATIONS.get()) {
			startDispersal(level, pos, state, center);
		}

		markDirty();
		sync();
	}

	/**
	 * 进入扩散阶段: 先作废机器内存储的全部流体, 再把世界里的幽匿块设为扩散种子。
	 * <p>
	 * 扩散没有总量上限(不再按液体容量除算), 一直长到六面都无处可长为止。
	 */
	private void startDispersal(Level level, BlockPos pos, BlockState state, BlockPos center) {
		overloadStage = STAGE_DISPERSING;

		// 扩散格数 = 第一格储罐里的培养液量(1 mB = 1 格), 所以要在倒掉之前先记下来
		dispersalsLeft = getFluidHandler().getFluidInTank(getFluidInputTankIndex(0)).getAmount();

		// 清空机器内存储的流体
		for (int tank = 0; tank < getFluidHandler().getTanks(); tank++) {
			getFluidHandler().drainTank(tank, Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE, false);
		}

		seedActiveSculk(level, center);

		markDirty();
		sync();
	}

	/**
	 * 把扩散半径内已有的幽匿块作为扩散种子(生长腔里的 + 外围蔓延出来的)。
	 */
	private void seedActiveSculk(Level level, BlockPos center) {
		activeSculk.clear();

		int radius = SEED_SCAN_RADIUS;

		for (BlockPos cell : BlockPos.betweenClosed(
				center.offset(-radius, -radius, -radius),
				center.offset(radius, radius, radius))) {
			if (level.getBlockState(cell).is(Blocks.SCULK)) {
				activeSculk.put(cell.immutable(), newFrontier(level, null));
			}
		}
	}

	/**
	 * 给一个新长出来的幽匿块发一张额度卡: 随机 2~3 次扩散机会, 并记住它是从哪个方向长过来的。
	 */
	private static Frontier newFrontier(Level level, @Nullable Direction heading) {
		int budget = SPREAD_BUDGET_MIN + level.random.nextInt(SPREAD_BUDGET_MAX - SPREAD_BUDGET_MIN + 1);
		return new Frontier(budget, heading);
	}

	/**
	 * {@code from} 朝哪个方向一步能到 {@code to} —— 用来记住枝蔓的走向
	 */
	@Nullable
	private static Direction directionOf(BlockPos from, BlockPos to) {
		for (Direction direction : Direction.values()) {
			if (from.relative(direction).equals(to)) {
				return direction;
			}
		}

		return null;
	}

	/**
	 * 扩散一步: 每个活跃幽匿块挑一个合法的面把邻居变成幽匿块(面贴面, 不隔空);
	 * 新块加入活跃集并挂上装饰, 旧块只要还有合法面就继续参与; 六面都不行了就移出。
	 * <p>
	 * 单步处理的格数受 {@code DISPERSAL_STEP_LIMIT} 限制, 把工作量摊到多步, 免得一次铺几千格卡顿。
	 */
	private void dispersalTick(Level level, BlockPos pos, BlockPos center) {
		if (activeSculk.isEmpty() || dispersalsLeft <= 0) {
			finishMeltdown(level, pos, center);
			return;
		}

		int limit = ReactorConfig.DISPERSAL_STEP_LIMIT.get();
		int processed = 0;

		Map<BlockPos, Frontier> grown = new HashMap<>();
		Iterator<Map.Entry<BlockPos, Frontier>> iterator = activeSculk.entrySet().iterator();

		while (iterator.hasNext() && processed < limit && dispersalsLeft > 0) {
			Map.Entry<BlockPos, Frontier> entry = iterator.next();
			BlockPos from = entry.getKey();
			Frontier frontier = entry.getValue();
			processed++;

			// 洪水也走走停停: 每步有一定概率先不长, 让各条枝蔓的推进不同步
			if (level.random.nextInt(100) < SPREAD_SKIP_CHANCE) {
				continue;
			}

			BlockPos created = ReactorMeltdown.growOneFace(level, from, frontier.heading());

			if (created == null) {
				iterator.remove();
				continue;
			}

			grown.put(created, newFrontier(level, directionOf(from, created)));

			// 每长一格扣 1 mB 额度(额度 = 第一格储罐的培养液量)
			dispersalsLeft--;

			// 每个幽匿块上都要挂一个表面装饰
			decorate(level, created);

			// 这一块的额度用完就退出活跃集: 枝蔓长到头就"死", 不会把周围填满
			if (frontier.budget() <= 1) {
				iterator.remove();
			} else {
				entry.setValue(frontier.spent());
			}
		}

		activeSculk.putAll(grown);
		markDirty();

		if (activeSculk.isEmpty() || dispersalsLeft <= 0) {
			finishMeltdown(level, pos, center);
		}
	}

	/**
	 * 给一个幽匿块挂表面装饰; 只有挂成幽匿脉络时才可能生成感染效果云。
	 */
	private void decorate(Level level, BlockPos sculkPos) {
		BlockPos decoration = ReactorMeltdown.decorateAround(level, sculkPos);

		if (decoration != null) {
			maybeSpawnCloud(level, decoration);
		}
	}

	/**
	 * 只有幽匿脉络会生成效果云: 按 {@code CLOUD_CHANCE}(默认 25%)的概率, 在这一格生成一朵
	 * 存在 {@code CLOUD_DURATION}(默认 5 分钟)、带感染效果的区域效果云。
	 */
	private void maybeSpawnCloud(Level level, BlockPos pos) {
		if (!level.getBlockState(pos).is(Blocks.SCULK_VEIN)) {
			return;
		}

		if (level.random.nextDouble() * 100.0D >= ReactorConfig.CLOUD_CHANCE.get()) {
			return;
		}

		AreaEffectCloud cloud = new AreaEffectCloud(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		cloud.setRadius(CLOUD_RADIUS_BLOCKS);
		cloud.setDuration(ReactorConfig.CLOUD_DURATION.get());
		cloud.setWaitTime(10);
		cloud.addEffect(new MobEffectInstance(
				DTEffects.INFECTION.get(),
				ReactorConfig.INFECTION_DURATION.get(),
				0
		));

		level.addFreshEntity(cloud);
	}

	/**
	 * 失控收尾: 先在机器正中央爆一次, 再在控制器处爆一次, 威力为 {@code EXPLOSION_POWER}(默认 4, 与 TNT 一致)。
	 */
	private void finishMeltdown(Level level, BlockPos pos, BlockPos center) {
		float power = ReactorConfig.EXPLOSION_POWER.get();

		overloadStage = STAGE_IDLE;
		spreadCount = 0;
		dispersalsLeft = 0;
		activeSculk.clear();
		overloadCenter = null;
		hasRun = false;
		detonating = true;

		markDirty();
		sync();

		level.explode(null,
				center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D,
				power, Level.ExplosionInteraction.BLOCK);

		level.explode(null,
				pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
				power, Level.ExplosionInteraction.BLOCK);
	}

	/**
	 * 控制器被挖掉时的处理.
	 * <p>
	 * 这台机器<b>热</b>的时候(正在失控, 或者正在运行 —— 自己长过幽匿块而且腔里还有)被拆,
	 * 会直接引爆: 威力固定与 TNT 一致, 先在机器正中央一次, 再在控制器处一次。
	 * 冷的机器(没运转过 / 已被红石急停清空)拆掉什么都不会发生。
	 */
	public void detonateOnBreak() {
		if (level == null || level.isClientSide() || detonating) {
			return;
		}

		BlockPos center = overloadCenter != null ? overloadCenter
				: (structureCenter != null ? structureCenter : worldPosition);

		boolean hot = overloadStage != STAGE_IDLE
				|| (hasRun && countCavitySculk(level, center) > 0);

		if (!hot) {
			return;
		}

		detonating = true;
		overloadStage = STAGE_IDLE;
		dispersalsLeft = 0;
		activeSculk.clear();
		overloadCenter = null;
		hasRun = false;

		level.explode(null,
				center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D,
				TNT_POWER, Level.ExplosionInteraction.BLOCK);

		level.explode(null,
				worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D,
				TNT_POWER, Level.ExplosionInteraction.BLOCK);
	}

	/**
	 * 中止过载: 计数器重置, 下次要重新数满 20 次。
	 */
	private void abortOverload(Level level, BlockPos pos, BlockState state) {
		overloadStage = STAGE_IDLE;
		spreadCount = 0;
		dispersalsLeft = 0;
		activeSculk.clear();
		overloadCenter = null;

		updateLit(level, pos, state, multiblock().isValid());
		markDirty();
		sync();
	}

	/**
	 * 结构内部还剩几个幽匿块 —— 只数 {@code minecraft:sculk}, 不含催发体,
	 * 所以"腔里的幽匿块被挖光"能被准确识别。
	 */
	private int countCavitySculk(Level level, BlockPos center) {
		int count = 0;

		for (BlockPos cell : BlockPos.betweenClosed(
				center.offset(-STRUCTURE_HALF_SIZE, -STRUCTURE_HALF_SIZE, -STRUCTURE_HALF_SIZE),
				center.offset(STRUCTURE_HALF_SIZE, STRUCTURE_HALF_SIZE, STRUCTURE_HALF_SIZE))) {
			if (level.getBlockState(cell).is(Blocks.SCULK)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * 结构中心: 取结构全部格子坐标的平均值。
	 * <p>
	 * 过载期间结构可能被蔓延破坏, 所以这类查询只在过载开始时做一次, 之后复用存下来的坐标。
	 */
	private BlockPos computeStructureCenter(BlockPos fallback) {
		List<BlockPos> cells = multiblock().findFilterBlock(state -> true);

		if (cells.isEmpty()) {
			return fallback;
		}

		long sumX = 0;
		long sumY = 0;
		long sumZ = 0;

		for (BlockPos cell : cells) {
			sumX += cell.getX();
			sumY += cell.getY();
			sumZ += cell.getZ();
		}

		return new BlockPos(
				(int) (sumX / cells.size()),
				(int) (sumY / cells.size()),
				(int) (sumZ / cells.size())
		);
	}

	// ------------------------------------------------------------------
	// 紧急急停(红石)
	// ------------------------------------------------------------------

	/**
	 * 紧急急停: 作废内部储存 + 清除结构内全部幽匿系方块(含催发体) + 中止过载。
	 */
	private void emergencyStop(Level level, BlockPos pos, BlockState state) {
		boolean changed = false;

		if (overloadStage != STAGE_IDLE) {
			overloadStage = STAGE_IDLE;
			spreadCount = 0;
			dispersalsLeft = 0;
			activeSculk.clear();
			overloadCenter = null;
			changed = true;
		}

		// 急停之后这台机器算"没运转过": 此后拆掉受损的结构不会再引发失控
		if (hasRun) {
			hasRun = false;
			changed = true;
		}

		structureCenter = null;
		growCooldown = 0;

		// 1) 作废内部物品
		for (int slot = 0; slot < getItemHandler().getSlots(); slot++) {
			if (!getItemHandler().getStackInSlot(slot).isEmpty()) {
				getItemHandler().setStackInSlot(slot, ItemStack.EMPTY);
				changed = true;
			}
		}

		// 2) 作废内部流体
		for (int tank = 0; tank < getFluidHandler().getTanks(); tank++) {
			if (!getFluidHandler().getFluidInTank(tank).isEmpty()) {
				getFluidHandler().drainTank(tank, Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE, false);
				changed = true;
			}
		}

		// 3) 作废内部能量
		if (getEnergyStored() != 0) {
			setEnergy(0);
			changed = true;
		}

		// 4) 清除结构内所有幽匿系方块(含催发体)
		BlockPos center = overloadCenter != null ? overloadCenter : computeStructureCenter(pos);
		int cleared = multiblock().destroyFilter(ReactorMeltdown::isSculkFamily, false);

		for (BlockPos cell : BlockPos.betweenClosed(
				center.offset(-STRUCTURE_HALF_SIZE, -STRUCTURE_HALF_SIZE, -STRUCTURE_HALF_SIZE),
				center.offset(STRUCTURE_HALF_SIZE, STRUCTURE_HALF_SIZE, STRUCTURE_HALF_SIZE))) {
			if (ReactorMeltdown.isSculkFamily(level.getBlockState(cell))) {
				level.destroyBlock(cell, false);
				cleared++;
			}
		}

		if (cleared > 0) {
			changed = true;
		}

		updateLit(level, pos, state, false);

		if (changed) {
			markDirty();
			sync();
		}
	}

	// ------------------------------------------------------------------
	// 生命周期 / NBT
	// ------------------------------------------------------------------

	@Override
	public void onLoad() {
		super.onLoad();

		// 存档重载时活跃集是空的, 按世界里的幽匿块重建, 否则扩散阶段会立刻判定"无处可扩散"
		if (level != null && !level.isClientSide() && overloadStage == STAGE_DISPERSING && activeSculk.isEmpty()) {
			seedActiveSculk(level, overloadCenter != null ? overloadCenter : worldPosition);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("OverloadStage", overloadStage);
		tag.putInt("SpreadCount", spreadCount);
		tag.putBoolean("HasRun", hasRun);
		tag.putInt("DispersalsLeft", dispersalsLeft);

		if (structureCenter != null) {
			tag.putInt("StructureCenterX", structureCenter.getX());
			tag.putInt("StructureCenterY", structureCenter.getY());
			tag.putInt("StructureCenterZ", structureCenter.getZ());
		}

		if (overloadCenter != null) {
			tag.putInt("OverloadCenterX", overloadCenter.getX());
			tag.putInt("OverloadCenterY", overloadCenter.getY());
			tag.putInt("OverloadCenterZ", overloadCenter.getZ());
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		overloadStage = tag.getInt("OverloadStage");
		spreadCount = tag.getInt("SpreadCount");
		hasRun = tag.getBoolean("HasRun");
		dispersalsLeft = tag.getInt("DispersalsLeft");

		if (tag.contains("StructureCenterX")) {
			structureCenter = new BlockPos(
					tag.getInt("StructureCenterX"),
					tag.getInt("StructureCenterY"),
					tag.getInt("StructureCenterZ")
			);
		}

		if (tag.contains("OverloadCenterX")) {
			overloadCenter = new BlockPos(
					tag.getInt("OverloadCenterX"),
					tag.getInt("OverloadCenterY"),
					tag.getInt("OverloadCenterZ")
			);
		}
	}

	// ------------------------------------------------------------------
	// 工具
	// ------------------------------------------------------------------

	private void updateLit(Level level, BlockPos pos, BlockState state, boolean lit) {
		if (state.hasProperty(BasicBlock.LIT) && state.getValue(BasicBlock.LIT) != lit) {
			level.setBlock(pos, state.setValue(BasicBlock.LIT, lit), Block.UPDATE_ALL);
		}
	}

	// ------------------------------------------------------------------
	// GUI: 3 个物品槽 + 5 个流体罐, 一行排开
	// ------------------------------------------------------------------

	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);

		group.addWidget(new LabelWidget(8, 6, ReactorBlocks.REACTOR_CONTROLLER.get().getName()));

		group.addWidget(new EnergyBarWidget(8, 20, this::getEnergyStored, getMaxEnergyStored()));

		// 2 输入槽 + 1 输出槽
		MachineItemSlots.add(group, this, getItemHandler(), new Position(62, 20), new Position(116, 20));

		// 前 3 个是输入罐, 后 2 个是输出罐.
		// 用项目通用的比例填充式流体槽: 支持拿着桶点击槽位灌入/抽取.
		// LDLib TankWidget 的桶点击是对整个 IFluidTransfer 做 fill/drain(不区分罐索引),
		// 所以每个槽位必须绑定一个只暴露该罐的 SingleTankFluidTransfer, 点击才精确命中对应罐.
		for (int tank = 0; tank < getMaxMachineTank(); tank++) {
			int index = tank;
			group.addWidget(new ProportionalTankWidget(
					new SingleTankFluidTransfer(getFluidHandler().getTankHandler(index)),
					0,
					30 + tank * 22,
					44,
					18,
					52,
					true,
					true
			).setBackground(new ResourceTexture(DeepTech.loadGui("elements/tank_back"))));
		}

		addPlayerInventory(group, player);
		return group;
	}
}

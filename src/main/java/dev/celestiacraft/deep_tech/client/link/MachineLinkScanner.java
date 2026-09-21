package dev.celestiacraft.deep_tech.client.link;

import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.center.SNCenterBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port.SNHelper;
import dev.celestiacraft.deep_tech.common.block.machine.other.resonance_node.ResonanceNodeBlockEntity;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.client.MachineLinkConfig;
import dev.celestiacraft.deep_tech.tags.DeepTechItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MachineLinkScanner {
	private static final double BOUNDS_PADDING = 0.5D;
	private static final int RESCAN_INTERVAL = 20;
	private static final int FADE_TICKS = 4;
	private static final List<Link> LINKS = new ArrayList<>();
	private static final List<Node> NODES = new ArrayList<>();
	private static final Map<BlockPos, Node> NODE_LOOKUP = new LinkedHashMap<>();
	private static int rescanCooldown = 0;
	private static int fadeTicks = 0;
	private static int previousFadeTicks = 0;

	public record Link(Vec3 from, Vec3 to, int color, AABB bounds) {
		public Link(Vec3 from, Vec3 to, int color) {
			this(from, to, color, new AABB(from, to).inflate(BOUNDS_PADDING));
		}
	}

	public record Node(Vec3 at, int color, boolean hub, AABB bounds) {
		public Node(Vec3 at, int color, boolean hub) {
			this(at, color, hub, new AABB(at, at).inflate(BOUNDS_PADDING));
		}
	}

	public static List<Link> links() {
		return LINKS;
	}

	public static List<Node> nodes() {
		return NODES;
	}

	public static float fade(float partialTick) {
		return Mth.lerp(partialTick, previousFadeTicks, fadeTicks) / FADE_TICKS;
	}

	public static void clientTick() {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		Player player = minecraft.player;
		if (level == null || player == null) {
			reset();
			return;
		}

		previousFadeTicks = fadeTicks;
		boolean active = MachineLinkConfig.ENABLED.get() && holdsWrench(player);
		fadeTicks = active
				? Math.min(fadeTicks + 1, FADE_TICKS)
				: Math.max(fadeTicks - 1, 0);

		if (fadeTicks == 0) {
			if (!LINKS.isEmpty() || !NODES.isEmpty()) {
				LINKS.clear();
				NODES.clear();
			}
			rescanCooldown = 0;
			return;
		}
		if (!active) {
			return;
		}
		if (rescanCooldown > 0) {
			rescanCooldown--;
			return;
		}
		rescanCooldown = RESCAN_INTERVAL;
		rescan(level, player.blockPosition());
	}

	private static void reset() {
		LINKS.clear();
		NODES.clear();
		rescanCooldown = 0;
		fadeTicks = 0;
		previousFadeTicks = 0;
	}

	private static boolean holdsWrench(Player player) {
		return player.getMainHandItem().is(DeepTechItemTags.WRENCH)
				|| player.getOffhandItem().is(DeepTechItemTags.WRENCH);
	}

	private static void rescan(ClientLevel level, BlockPos origin) {
		LINKS.clear();
		NODES.clear();
		NODE_LOOKUP.clear();
		int range = MachineLinkConfig.SCAN_RANGE.get();
		int maxLinks = MachineLinkConfig.MAX_LINKS.get();
		List<BlockPos> centers = new ArrayList<>();
		List<BlockPos> resonanceNodes = new ArrayList<>();
		collectMachines(level, origin, range, centers, resonanceNodes);
		Set<BlockPos> scannedCenters = new HashSet<>();
		for (BlockPos center : centers) {
			if (scannedCenters.contains(center)) {
				continue;
			}
			addNetworkLinks(level, center, scannedCenters, maxLinks);
		}
		addResonanceLinks(resonanceNodes, maxLinks);
		NODES.addAll(NODE_LOOKUP.values());
		NODE_LOOKUP.clear();
	}

	private static void collectMachines(ClientLevel level, BlockPos origin, int range, List<BlockPos> centers, List<BlockPos> resonanceNodes) {
		int minChunkX = SectionPos.blockToSectionCoord(origin.getX() - range);
		int maxChunkX = SectionPos.blockToSectionCoord(origin.getX() + range);
		int minChunkZ = SectionPos.blockToSectionCoord(origin.getZ() - range);
		int maxChunkZ = SectionPos.blockToSectionCoord(origin.getZ() + range);
		int rangeSquared = range * range;

		for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
			for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
				if (chunk == null) {
					continue;
				}
				for (BlockEntity entity : chunk.getBlockEntities().values()) {
					BlockPos pos = entity.getBlockPos();
					if (pos.distSqr(origin) > rangeSquared) {
						continue;
					}
					if (entity instanceof SNCenterBlockEntity) {
						centers.add(pos);
					} else if (entity instanceof ResonanceNodeBlockEntity) {
						resonanceNodes.add(pos);
					}
				}
			}
		}
	}

	private static void addNetworkLinks(ClientLevel level, BlockPos center, Set<BlockPos> scannedCenters, int maxLinks) {
		Vec3 hub = Vec3.atCenterOf(center);
		putNode(center, hub, MachineLinkColors.HUB, true);
		SNHelper.collectNetwork(level, center, (pos, block) -> {
			if (block == MachineBlocks.SN_CENTER.get()) {
				scannedCenters.add(pos.immutable());
				return;
			}
			if (LINKS.size() >= maxLinks) {
				return;
			}
			int color = MachineLinkColors.ofComponent(block);
			if (color == 0) {
				return;
			}
			Vec3 target = Vec3.atCenterOf(pos);
			LINKS.add(new Link(hub, target, color));
			putNode(pos.immutable(), target, color, false);
		});
	}

	private static void addResonanceLinks(List<BlockPos> resonanceNodes, int maxLinks) {
		int count = resonanceNodes.size();
		if (count == 0) {
			return;
		}
		boolean[] visited = new boolean[count];
		Queue<Integer> queue = new ArrayDeque<>();
		for (int root = 0; root < count; root++) {
			if (visited[root]) {
				continue;
			}
			visited[root] = true;
			queue.add(root);
			while (!queue.isEmpty()) {
				int current = queue.poll();
				BlockPos currentPos = resonanceNodes.get(current);
				Vec3 currentCenter = Vec3.atCenterOf(currentPos);
				putNode(currentPos, currentCenter, MachineLinkColors.RESONANCE_NODE, false);
				for (int other = 0; other < count; other++) {
					if (visited[other]) {
						continue;
					}
					BlockPos otherPos = resonanceNodes.get(other);
					if (!withinResonanceRange(currentPos, otherPos)) {
						continue;
					}
					visited[other] = true;
					queue.add(other);
					if (LINKS.size() < maxLinks) {
						LINKS.add(new Link(currentCenter, Vec3.atCenterOf(otherPos), MachineLinkColors.RESONANCE_NODE));
					}
				}
			}
		}
	}

	private static boolean withinResonanceRange(BlockPos from, BlockPos to) {
		return Math.abs(from.getX() - to.getX()) <= ResonanceNodeBlockEntity.LINK_RANGE
				&& Math.abs(from.getY() - to.getY()) <= ResonanceNodeBlockEntity.LINK_RANGE
				&& Math.abs(from.getZ() - to.getZ()) <= ResonanceNodeBlockEntity.LINK_RANGE;
	}

	private static void putNode(BlockPos pos, Vec3 at, int color, boolean hub) {
		Node existing = NODE_LOOKUP.get(pos);
		if (existing != null && (existing.hub() || !hub)) {
			return;
		}
		NODE_LOOKUP.put(pos, new Node(at, color, hub));
	}
}
package dev.celestiacraft.deep_tech.common.block.machine.resonance_node;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.tags.DeepTechItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResonanceNodeClientHelper {

    private final ResonanceNodeBlockEntity entity;
    private List<BlockPos> cachedNodes = new ArrayList<>();
    private int scanCooldown = 0;
    private int particleTickCounter = 0;

    private static final int RANGE = 16;
    private static final int PARTICLE_COOLDOWN = 10;   // ✅ 从 5 改为 10
    private static final int PLAYER_CHECK_RANGE = 16;
    private static final int SCAN_INTERVAL = 10;       // ✅ 从 5 改为 10
    private static final int MAX_VISIBLE_CONNECTIONS = 20; // ✅ 限制最多显示 20 条连接
    private static final int MAX_PARTICLES_PER_CONNECTION = 8; // ✅ 减少每连接粒子数

    public ResonanceNodeClientHelper(ResonanceNodeBlockEntity entity) {
        this.entity = entity;
    }

    public void tick(Level level) {
        if (level == null) return;

        if (!hasNearbyPlayerWithWrench(level)) return;

        particleTickCounter++;
        if (particleTickCounter % PARTICLE_COOLDOWN != 0) return;

        // 定期刷新节点列表
        if (particleTickCounter % 20 == 0) {
            scanNetwork(level);
        }

        // 移除已失效的节点
        validateCachedNodes(level);

        // ✅ 限制显示的连接数量
        int connectionsShown = 0;
        for (BlockPos targetPos : cachedNodes) {
            if (connectionsShown >= MAX_VISIBLE_CONNECTIONS) break;
            spawnConnectionParticles(level, targetPos);
            connectionsShown++;
        }
    }

    private void validateCachedNodes(Level level) {
        Iterator<BlockPos> iterator = cachedNodes.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof ResonanceNodeBlockEntity)) {
                iterator.remove();
            }
        }
    }

    private boolean hasNearbyPlayerWithWrench(Level level) {
        AABB rangeBox = new AABB(
                entity.getBlockPos().getX() - PLAYER_CHECK_RANGE,
                entity.getBlockPos().getY() - PLAYER_CHECK_RANGE,
                entity.getBlockPos().getZ() - PLAYER_CHECK_RANGE,
                entity.getBlockPos().getX() + PLAYER_CHECK_RANGE + 1,
                entity.getBlockPos().getY() + PLAYER_CHECK_RANGE + 1,
                entity.getBlockPos().getZ() + PLAYER_CHECK_RANGE + 1
        );

        List<Player> players = level.getEntitiesOfClass(Player.class, rangeBox);
        for (Player player : players) {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            if (!mainHand.isEmpty() && mainHand.is(DeepTechItemTags.WRENCH)) {
                return true;
            }
            if (!offHand.isEmpty() && offHand.is(DeepTechItemTags.WRENCH)) {
                return true;
            }
        }
        return false;
    }

    private void scanNetwork(Level level) {
        if (scanCooldown-- > 0) return;
        scanCooldown = SCAN_INTERVAL;

        cachedNodes.clear();

        AABB rangeBox = new AABB(
                entity.getBlockPos().getX() - RANGE,
                entity.getBlockPos().getY() - RANGE,
                entity.getBlockPos().getZ() - RANGE,
                entity.getBlockPos().getX() + RANGE + 1,
                entity.getBlockPos().getY() + RANGE + 1,
                entity.getBlockPos().getZ() + RANGE + 1
        );

        for (BlockPos pos : BlockPos.betweenClosed(
                (int) rangeBox.minX, (int) rangeBox.minY, (int) rangeBox.minZ,
                (int) rangeBox.maxX, (int) rangeBox.maxY, (int) rangeBox.maxZ
        )) {
            if (pos.equals(entity.getBlockPos())) continue;

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ResonanceNodeBlockEntity) {
                cachedNodes.add(pos.immutable());
            }
        }

        if (cachedNodes.size() > 100) {
            cachedNodes = cachedNodes.subList(0, 100);
        }

        // ✅ 注释掉 debug 日志，避免刷屏
        // DeepTech.LOGGER.debug("ResonanceNode client at {} found {} nodes", entity.getBlockPos(), cachedNodes.size());
    }

    private void spawnConnectionParticles(Level level, BlockPos targetPos) {
        if (level == null) return;

        RandomSource rand = level.getRandom();

        Vector3f start = new Vector3f(
                entity.getBlockPos().getX() + 0.5f,
                entity.getBlockPos().getY() + 0.7f,
                entity.getBlockPos().getZ() + 0.5f
        );
        Vector3f end = new Vector3f(
                targetPos.getX() + 0.5f,
                targetPos.getY() + 0.7f,
                targetPos.getZ() + 0.5f
        );

        DustParticleOptions purple = new DustParticleOptions(
                new Vector3f(0.6f, 0.2f, 0.8f), 0.8f
        );
        DustParticleOptions cyan = new DustParticleOptions(
                new Vector3f(0.0f, 0.8f, 0.8f), 0.8f
        );

        DustParticleOptions particle = rand.nextBoolean() ? purple : cyan;

        // ✅ 减少每连接粒子数
        int count = 4 + rand.nextInt(5); // 4-8 个（原 12-22）

        for (int i = 0; i < count; i++) {
            float progress = (float) i / count;

            float ox = (rand.nextFloat() - 0.5f) * 0.3f;
            float oy = (rand.nextFloat() - 0.5f) * 0.3f;
            float oz = (rand.nextFloat() - 0.5f) * 0.3f;

            float x = start.x() + (end.x() - start.x()) * progress + ox;
            float y = start.y() + (end.y() - start.y()) * progress + oy;
            float z = start.z() + (end.z() - start.z()) * progress + oz;

            float speed = 0.015f;
            float vx = (end.x() - start.x()) * speed + (rand.nextFloat() - 0.5f) * 0.01f;
            float vy = (end.y() - start.y()) * speed + (rand.nextFloat() - 0.5f) * 0.01f;
            float vz = (end.z() - start.z()) * speed + (rand.nextFloat() - 0.5f) * 0.01f;

            level.addParticle(particle, x, y, z, vx, vy, vz);
        }
    }
}
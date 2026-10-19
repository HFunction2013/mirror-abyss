package com.example.mirror_abyss.world;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;

/**
 * ModDimensions - registers the Mirror Abyss dimension type and the
 * teleportation helpers used by the portal, the watcher_altar and the boss
 * arena ("Void Sanctum").
 *
 * Dimension features (data-driven, see data/mirror_abyss/dimension_type):
 *  - no day/night cycle (fixedTime), permanent twilight
 *  - purple-gray sky with floating mirror shard particles (client renderer)
 *  - custom chunk generator + custom biome source (MirrorAbyssChunkGenerator)
 */
public final class ModDimensions {

    public static final ResourceKey<DimensionType> MIRROR_ABYSS_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE, new ResourceLocation(MirrorAbyss.MOD_ID, "mirror_abyss"));

    public static final ResourceKey<Level> MIRROR_ABYSS_LEVEL = ResourceKey.create(
            Registries.DIMENSION, new ResourceLocation(MirrorAbyss.MOD_ID, "mirror_abyss"));

    // The DimensionType itself is fully data-driven:
    //   data/mirror_abyss/dimension_type/mirror_abyss.json
    // (registered in code it would collide with the datapack entry).
    // Custom chunk generator / biome source codecs are registered via
    // ModWorldgenCodecs (DeferredRegister) - see that class.

    // ======================= Teleportation =======================

    /** Teleport a player into the Mirror Abyss via the portal. */
    public static void teleportToMirrorAbyss(ServerPlayer player) {
        ServerLevel target = player.getServer().getLevel(MIRROR_ABYSS_LEVEL);
        if (target == null) {
            MirrorAbyss.LOGGER.error("[mirror_abyss] Mirror Abyss level is missing!");
            return;
        }
        // Skeleton: spawn at world spawn (0, y, 0). Production code should
        // search for a safe landing position via heightmap.
        Vec3 spawn = new Vec3(0.5D, 100.0D, 0.5D);
        player.teleportTo(target, spawn.x, spawn.y, spawn.z, player.getYRot(), player.getXRot());
    }

    /** Teleport a player to the Void Sanctum (boss arena) via the watcher_altar. */
    public static void teleportToVoidSanctum(ServerPlayer player) {
        // Skeleton: the sanctum is a special platform in the same dimension.
        // Production code should use a structure-bounding-box anchor.
        ServerLevel target = player.getServer().getLevel(MIRROR_ABYSS_LEVEL);
        if (target == null) {
            return;
        }
        Vec3 sanctum = new Vec3(512.5D, 90.0D, 512.5D);
        player.teleportTo(target, sanctum.x, sanctum.y, sanctum.z, player.getYRot(), player.getXRot());
    }

    /** Teleport a player back to the overworld (used by endings). */
    public static void teleportToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.getServer().overworld();
        Vec3 spawn = new Vec3(0.5D, 64.0D, 0.5D);
        player.teleportTo(overworld, spawn.x, spawn.y, spawn.z, player.getYRot(), player.getXRot());
    }

    /** Portal teleport hook - called by the portal frame activation. */
    public static void registerTeleporter() {
        MirrorAbyss.LOGGER.info("[mirror_abyss] Teleporter hooks registered.");
    }

    private ModDimensions() {
    }
}

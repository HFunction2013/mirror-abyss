package com.example.mirror_abyss.entity;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.entity.Monster.CrackSpider;
import com.example.mirror_abyss.entity.Monster.MirageWalker;
import com.example.mirror_abyss.entity.Monster.VoidDevourer;
import com.example.mirror_abyss.entity.boss.MirrorEater;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Entity type registry for all mirror_abyss mobs.
 * Registration names are snake_case and must match:
 *   - assets/mirror_abyss/lang/<lang>.json (entity.mirror_abyss.<name>)
 *   - data/mirror_abyss/loot_tables/entities/<name>.json
 *   - spawn_rule JSONs in data/mirror_abyss/<namespace>/spawn_rules (optional)
 */
public final class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MirrorAbyss.MOD_ID);

    /** mirage_walker : translucent mirror fragment humanoid. */
    public static final RegistryObject<EntityType<MirageWalker>> MIRAGE_WALKER =
            ENTITY_TYPES.register("mirage_walker",
                    () -> EntityType.Builder.of(MirageWalker::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("mirage_walker"));

    /** void_devourer : floating black orb with a purple vortex. */
    public static final RegistryObject<EntityType<VoidDevourer>> VOID_DEVOURER =
            ENTITY_TYPES.register("void_devourer",
                    () -> EntityType.Builder.of(VoidDevourer::new, MobCategory.MONSTER)
                            .sized(0.9F, 0.9F)
                            .clientTrackingRange(8)
                            .build("void_devourer"));

    /** crack_spider : rock and purple fissure spider, climbs walls. */
    public static final RegistryObject<EntityType<CrackSpider>> CRACK_SPIDER =
            ENTITY_TYPES.register("crack_spider",
                    () -> EntityType.Builder.of(CrackSpider::new, MobCategory.MONSTER)
                            .sized(1.4F, 0.9F)
                            .clientTrackingRange(8)
                            .build("crack_spider"));

    /** mirror_eater : three-phase boss of the Void Sanctum. */
    public static final RegistryObject<EntityType<MirrorEater>> MIRROR_EATER =
            ENTITY_TYPES.register("mirror_eater",
                    () -> EntityType.Builder.of(MirrorEater::new, MobCategory.MONSTER)
                            .sized(2.0F, 3.5F)
                            .fireImmune()
                            .clientTrackingRange(16)
                            .build("mirror_eater"));

    /** void_bolt : projectile fired by void_devourer. */
    public static final RegistryObject<EntityType<VoidBolt>> VOID_BOLT =
            ENTITY_TYPES.register("void_bolt",
                    () -> EntityType.Builder.<VoidBolt>of(VoidBolt::new, MobCategory.MISC)
                            .sized(0.35F, 0.35F)
                            .clientTrackingRange(8)
                            .build("void_bolt"));

    /**
     * Attribute registration - hook on EntityAttributeCreationEvent.
     */
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(MIRAGE_WALKER.get(), MirageWalker.createAttributes().build());
        event.put(VOID_DEVOURER.get(), VoidDevourer.createAttributes().build());
        event.put(CRACK_SPIDER.get(), CrackSpider.createAttributes().build());
        event.put(MIRROR_EATER.get(), MirrorEater.createBossAttributes().build());
    }

    /**
     * Spawn placement registration - hook on SpawnPlacementRegisterEvent.
     * All mirror mobs spawn on solid ground in the Mirror Abyss biomes
     * (spawn lists are defined in the biome JSONs).
     */
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        // Monster.spawnRestriction for ground mobs (placeholder values).
        // event.register(MIRAGE_WALKER.get(), SpawnPlacements.Type.ON_GROUND,
        //         Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules,
        //         SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    /** Static fallback used from FMLCommonSetupEvent (kept for compatibility). */
    public static void registerSpawnPlacementsStatic() {
        // No-op in skeleton: see registerSpawnPlacements for the event-driven version.
    }

    private ModEntities() {
    }
}

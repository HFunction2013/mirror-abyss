package com.example.mirror_abyss.world;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * ModBiomes - the three Mirror Abyss biomes.
 *
 *  1. mirror_plains      (破碎镜原) - main twilight plains, cracked_stone ground
 *  2. cracked_badlands   (裂隙岩地) - harsh fissure terrain, void_floor patches
 *  3. void_ruins         (虚空遗迹) - watcher ruins biome, watcher_brick
 *
 * Each biome also carries mob spawn entries and a couple of features
 * (mirror shard clusters / abyss ore). Full biome JSONs are data-driven in
 * data/mirror_abyss/worldgen/biome/*.json; this class registers the biome
 * holders in code so they can be referenced by the custom biome source.
 */
public final class ModBiomes {

    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(ForgeRegistries.Keys.BIOMES, MirrorAbyss.MOD_ID);

    public static final ResourceKey<Biome> MIRROR_PLAINS = key("mirror_plains");
    public static final ResourceKey<Biome> CRACKED_BADLANDS = key("cracked_badlands");
    public static final ResourceKey<Biome> VOID_RUINS = key("void_ruins");

    public static final RegistryObject<Biome> MIRROR_PLAINS_BIOME = BIOMES.register("mirror_plains",
            () -> buildBiome(0.1F, 0.0F));
    public static final RegistryObject<Biome> CRACKED_BADLANDS_BIOME = BIOMES.register("cracked_badlands",
            () -> buildBiome(-0.3F, 0.15F));
    public static final RegistryObject<Biome> VOID_RUINS_BIOME = BIOMES.register("void_ruins",
            () -> buildBiome(0.0F, -0.1F));

    private static ResourceKey<Biome> key(String name) {
        return ResourceKey.create(Registries.BIOME, new ResourceLocation(MirrorAbyss.MOD_ID, name));
    }

    /**
     * Skeleton biome builder - tune climate/sky values per biome in
     * production. The real values live in the data-driven biome JSONs.
     */
    private static Biome buildBiome(float temperature, float downfall) {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        net.minecraft.world.entity.MobCategory monster = net.minecraft.world.entity.MobCategory.MONSTER;
        spawns.addSpawn(monster,
                new MobSpawnSettings.SpawnerData(com.example.mirror_abyss.entity.ModEntities.MIRAGE_WALKER.get(), 100, 1, 3));
        spawns.addSpawn(monster,
                new MobSpawnSettings.SpawnerData(com.example.mirror_abyss.entity.ModEntities.VOID_DEVOURER.get(), 60, 1, 2));
        spawns.addSpawn(monster,
                new MobSpawnSettings.SpawnerData(com.example.mirror_abyss.entity.ModEntities.CRACK_SPIDER.get(), 80, 1, 2));

        // 1.20.1 Builder requires HolderGetter args; they are only used when
        // addFeature() is called. We add no features here (all tuning lives in
        // the data-driven biome JSONs), so passing null is safe.
        BiomeGenerationSettings.Builder gen =
                new BiomeGenerationSettings.Builder(null, null);
        // Features are added via the data-driven biome JSONs (cleaner for tuning).

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(temperature)
                .downfall(downfall)
                .specialEffects(new net.minecraft.world.level.biome.BiomeSpecialEffects.Builder()
                        .skyColor(0x8a7fb8)      // permanent purple-gray twilight sky
                        .fogColor(0x3b2a4a)
                        .waterColor(0x2a1640)
                        .waterFogColor(0x1a0e2a)
                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    private ModBiomes() {
    }
}

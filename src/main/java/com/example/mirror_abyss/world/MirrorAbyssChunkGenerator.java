package com.example.mirror_abyss.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;

/**
 * MirrorAbyssChunkGenerator - custom chunk generator for the Mirror Abyss.
 *
 * Extends NoiseBasedChunkGenerator and pairs a custom biome source with the
 * "mirror_abyss:mirror_abyss_noise_settings" noise settings datapack entry.
 * The codec is registered into BuiltInRegistries.CHUNK_GENERATOR so the
 * dimension JSON can use "type": "mirror_abyss:mirror_abyss".
 */
public class MirrorAbyssChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<MirrorAbyssChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(MirrorAbyssChunkGenerator::getBiomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings))
                    .apply(instance, MirrorAbyssChunkGenerator::new));

    public MirrorAbyssChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    @Override
    protected Codec<? extends NoiseBasedChunkGenerator> codec() {
        return CODEC;
    }

    /** Skeleton hook: override buildSurface to add void fissures / mirror crust. */
    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager,
                             RandomState randomState, ChunkAccess chunk) {
        super.buildSurface(level, structureManager, randomState, chunk);
        // TODO(feature): sprinkle void_floor and mirror_shard_block clusters here.
    }
}

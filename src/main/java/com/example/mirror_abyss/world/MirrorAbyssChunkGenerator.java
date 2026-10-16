package com.example.mirror_abyss.world;

import com.example.mirror_abyss.MirrorAbyss;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

/**
 * MirrorAbyssChunkGenerator - custom chunk generator for the Mirror Abyss.
 *
 * Extends NoiseBasedChunkGenerator and pairs a custom biome source with the
 * "mirror_abyss:mirror_abyss_noise_settings" noise settings datapack entry.
 * The codec is registered into BuiltinRegistries.CHUNK_GENERATOR so the
 * dimension JSON can use "type": "mirror_abyss:mirror_abyss".
 */
public class MirrorAbyssChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<MirrorAbyssChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(MirrorAbyssChunkGenerator::getBiomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::getSettings))
                    .apply(instance, MirrorAbyssChunkGenerator::new));

    public MirrorAbyssChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    @Override
    protected Codec<? extends NoiseBasedChunkGenerator> codec() {
        return CODEC;
    }

    /** Registers the codec under "mirror_abyss:mirror_abyss". */
    public static void registerCodec() {
        Registry.register(BuiltinRegistries.CHUNK_GENERATOR,
                new ResourceLocation(MirrorAbyss.MOD_ID, "mirror_abyss"), CODEC);
    }

    /** Skeleton hook: override fillFromNoise to add void fissures / mirror crust. */
    @Override
    public void buildSurface(net.minecraft.world.level.levelgen.RandomState randomState,
                             net.minecraft.world.level.chunk.ChunkAccess chunk,
                             net.minecraft.core.Holder<Biome> biome, int x, int z) {
        super.buildSurface(randomState, chunk, biome, x, z);
        // TODO(feature): sprinkle void_floor and mirror_shard_block clusters here.
    }

    @SuppressWarnings("unused")
    private static DensityFunction unusedDensityHook() {
        return DensityFunctions.constant(0.0D);
    }
}

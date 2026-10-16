package com.example.mirror_abyss.world;

import com.example.mirror_abyss.MirrorAbyss;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import java.util.stream.Stream;

/**
 * MirrorAbyssBiomeSource - custom biome source for the Mirror Abyss.
 *
 * Picks one of the three registered biomes deterministically from the
 * world position (simple hash-based selection; replace with noise-based
 * MultiNoise logic for real terrain blending).
 *
 * Codec is registered into BuiltinRegistries.BIOME_SOURCE so the dimension
 * JSON can reference "mirror_abyss:mirror_abyss_biome_source".
 */
public class MirrorAbyssBiomeSource extends BiomeSource {

    public static final Codec<MirrorAbyssBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(BiomeSource.HOLDER_CODEC)
                            .fieldOf("biomes")
                            .forGetter(source -> source.biomes))
                    .apply(instance, MirrorAbyssBiomeSource::new));

    private final List<Holder<Biome>> biomes;

    public MirrorAbyssBiomeSource(List<Holder<Biome>> biomes) {
        super(biomes);
        this.biomes = biomes;
    }

    /** Default instance referencing the three mod biomes. */
    public static MirrorAbyssBiomeSource defaultInstance() {
        Registry<Biome> registry = BuiltinRegistries.BIOME;
        return new MirrorAbyssBiomeSource(List.of(
                registry.getHolderOrThrow(ModBiomes.MIRROR_PLAINS),
                registry.getHolderOrThrow(ModBiomes.CRACKED_BADLANDS),
                registry.getHolderOrThrow(ModBiomes.VOID_RUINS)));
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return biomes.stream();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        // Simple deterministic pick: hash the column position.
        int hash = (x * 7349 + z * 9403) & 0x7FFFFFFF;
        return biomes.get(hash % biomes.size());
    }

    /** Registers the codec under "mirror_abyss:mirror_abyss_biome_source". */
    public static void registerCodec() {
        Registry.register(BuiltinRegistries.BIOME_SOURCE,
                new ResourceLocation(MirrorAbyss.MOD_ID, "mirror_abyss_biome_source"), CODEC);
    }

    /** Sample debug helper - not used by generation. */
    @SuppressWarnings("unused")
    private static BlockPos sample() {
        return BlockPos.ZERO;
    }
}

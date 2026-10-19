package com.example.mirror_abyss.world;

import com.example.mirror_abyss.MirrorAbyss;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
 * Codec is registered into BuiltInRegistries.BIOME_SOURCE so the dimension
 * JSON can reference "mirror_abyss:mirror_abyss_biome_source".
 */
public class MirrorAbyssBiomeSource extends BiomeSource {

    public static final Codec<MirrorAbyssBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryCodecs.homogeneousList(Registries.BIOME)
                            .fieldOf("biomes")
                            .forGetter(source -> source.biomes))
                    .apply(instance, MirrorAbyssBiomeSource::new));

    private final HolderSet<Biome> biomes;

    public MirrorAbyssBiomeSource(HolderSet<Biome> biomes) {
        super();
        this.biomes = biomes;
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
        List<Holder<Biome>> list = biomes.stream().toList();
        return list.get(hash % list.size());
    }

    /** Registers the codec under "mirror_abyss:mirror_abyss_biome_source". */
    public static void registerCodec() {
        Registry.register(BuiltInRegistries.BIOME_SOURCE,
                new ResourceLocation(MirrorAbyss.MOD_ID, "mirror_abyss_biome_source"), CODEC);
    }
}

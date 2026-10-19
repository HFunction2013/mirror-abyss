package com.example.mirror_abyss.world;

import com.example.mirror_abyss.MirrorAbyss;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;

/**
 * ModWorldgenCodecs - registers the custom chunk generator and biome source
 * codecs through Forge's DeferredRegister, so they are injected into the
 * vanilla worldgen registries BEFORE those registries freeze.
 *
 * Manually calling Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ...)
 * during mod construction fails with:
 *   "Registry is already frozen (trying to add key ...)"
 * DeferredRegister handles the timing correctly.
 */
public final class ModWorldgenCodecs {

    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, MirrorAbyss.MOD_ID);

    public static final DeferredRegister<Codec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, MirrorAbyss.MOD_ID);

    static {
        CHUNK_GENERATORS.register("mirror_abyss", () -> MirrorAbyssChunkGenerator.CODEC);
        BIOME_SOURCES.register("mirror_abyss_biome_source", () -> MirrorAbyssBiomeSource.CODEC);
    }

    private ModWorldgenCodecs() {
    }
}

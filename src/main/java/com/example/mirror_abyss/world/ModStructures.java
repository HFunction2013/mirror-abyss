package com.example.mirror_abyss.world;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/**
 * ModStructures - structure type registry.
 *
 * Three procedurally generated structures (no .nbt templates needed for the
 * skeleton; each builds its room from a block palette at generation time):
 *
 *   1. watchers_ruin   (守望者遗迹) - watcher_brick ruin platform
 *   2. mirror_hallway  (镜之回廊)   - mirror_shard_block corridor
 *   3. void_sanctum    (虚空圣殿)   - boss arena (watcher_brick + void_floor)
 */
public final class ModStructures {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(ForgeRegistries.STRUCTURE_TYPES, MirrorAbyss.MOD_ID);

    public static final RegistryObject<StructureType<MirrorRuinsStructure>> WATCHERS_RUIN =
            STRUCTURE_TYPES.register("watchers_ruin",
                    () -> () -> MirrorRuinsStructure.codec(
                            List.of(ModBlocks.WATCHER_BRICK.get().defaultBlockState(),
                                    ModBlocks.CRACKED_STONE.get().defaultBlockState()),
                            8, 4, 8));

    public static final RegistryObject<StructureType<MirrorRuinsStructure>> MIRROR_HALLWAY =
            STRUCTURE_TYPES.register("mirror_hallway",
                    () -> () -> MirrorRuinsStructure.codec(
                            List.of(ModBlocks.MIRROR_SHARD_BLOCK.get().defaultBlockState(),
                                    ModBlocks.CRACKED_STONE.get().defaultBlockState()),
                            12, 4, 4));

    public static final RegistryObject<StructureType<MirrorRuinsStructure>> VOID_SANCTUM =
            STRUCTURE_TYPES.register("void_sanctum",
                    () -> () -> MirrorRuinsStructure.codec(
                            List.of(ModBlocks.WATCHER_BRICK.get().defaultBlockState(),
                                    ModBlocks.VOID_FLOOR.get().defaultBlockState()),
                            16, 3, 16));

    private ModStructures() {
    }
}

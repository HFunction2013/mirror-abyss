package com.example.mirror_abyss.world;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * ModStructures - structure type registry.
 *
 * A single shared structure type "mirror_ruins" backs the three data-driven
 * structure entries (watchers_ruin / mirror_hallway / void_sanctum). Each
 * entry JSON supplies its own palette, box size and spawn_boss flag, so no
 * separate StructureType registration is needed per building.
 */
public final class ModStructures {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(ForgeRegistries.STRUCTURE_TYPES, MirrorAbyss.MOD_ID);

    /** Shared structure type; JSON "type" must be "mirror_abyss:mirror_ruins". */
    public static final RegistryObject<StructureType<MirrorRuinsStructure>> MIRROR_RUINS =
            STRUCTURE_TYPES.register("mirror_ruins", () -> () -> MirrorRuinsStructure.CODEC);

    private ModStructures() {
    }
}

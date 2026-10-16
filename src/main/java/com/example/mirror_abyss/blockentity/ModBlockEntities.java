package com.example.mirror_abyss.blockentity;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Block entity type registry.
 * Names are snake_case and must match the block they belong to.
 */
public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MirrorAbyss.MOD_ID);

    public static final RegistryObject<BlockEntityType<VoidStabilizerBE>> VOID_STABILIZER =
            BLOCK_ENTITY_TYPES.register("void_stabilizer",
                    () -> BlockEntityType.Builder.of(VoidStabilizerBE::new, ModBlocks.VOID_STABILIZER.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<FinalMirrorBE>> FINAL_MIRROR =
            BLOCK_ENTITY_TYPES.register("final_mirror",
                    () -> BlockEntityType.Builder.of(FinalMirrorBE::new, ModBlocks.FINAL_MIRROR.get())
                            .build(null));

    /** Generic helper used by subclasses. */
    protected static <T extends BlockEntity> BlockEntityType<T> typeOf(BlockEntityType.BlockEntitySupplier<T> supplier, Block... blocks) {
        return BlockEntityType.Builder.of(supplier, blocks).build(null);
    }

    private ModBlockEntities() {
    }
}

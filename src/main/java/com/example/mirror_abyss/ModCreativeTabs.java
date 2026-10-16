package com.example.mirror_abyss;

import com.example.mirror_abyss.block.ModBlocks;
import com.example.mirror_abyss.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Creative tab for all mirror_abyss content.
 * Registered via DeferredRegister<CreativeModeTab> (Forge 1.20.1).
 */
public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(CreativeModeTab.class, MirrorAbyss.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MIRROR_ABYSS_TAB = CREATIVE_TABS.register(
            "mirror_abyss",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mirror_abyss"))
                    .icon(() -> new ItemStack(ModItems.FINAL_MIRROR_ITEM.get()))
                    .displayItems((params, output) -> {
                        // Blocks
                        output.accept(ModBlocks.CRACKED_STONE.get());
                        output.accept(ModBlocks.WATCHER_BRICK.get());
                        output.accept(ModBlocks.ABYSS_ORE.get());
                        output.accept(ModBlocks.MIRROR_SHARD_BLOCK.get());
                        output.accept(ModBlocks.REFLECTIVE_LAMP.get());
                        output.accept(ModBlocks.VOID_STABILIZER.get());
                        output.accept(ModBlocks.WATCHER_ALTAR.get());
                        output.accept(ModBlocks.MIRROR_PORTAL_FRAME.get());
                        output.accept(ModBlocks.VOID_FLOOR.get());
                        output.accept(ModBlocks.FINAL_MIRROR.get());
                        // Items
                        output.accept(ModItems.MIRROR_SHARD.get());
                        output.accept(ModItems.VOID_ESSENCE.get());
                        output.accept(ModItems.VOID_EYE.get());
                        output.accept(ModItems.WATCHER_RELIC.get());
                        output.accept(ModItems.CRACKED_SILK.get());
                        output.accept(ModItems.VOID_CORE.get());
                        output.accept(ModItems.FINAL_MIRROR_ITEM.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}

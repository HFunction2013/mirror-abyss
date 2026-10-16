package com.example.mirror_abyss.item;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * All mirror_abyss items (plain items + block items).
 *
 * Registration names are snake_case and must match:
 *   - assets/mirror_abyss/models/item/<name>.json
 *   - assets/mirror_abyss/textures/item/<name>.png
 *   - loot tables / recipes references
 */
public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MirrorAbyss.MOD_ID);

    // ===================== Plain items =====================

    /** mirror_shard : triangular mirror fragment, dropped by abyss_ore & mirage_walker. */
    public static final RegistryObject<Item> MIRROR_SHARD = ITEMS.register("mirror_shard",
            () -> new Item(new Item.Properties()));

    /** void_essence : purple-black fluid orb, dropped by void_devourer. */
    public static final RegistryObject<Item> VOID_ESSENCE = ITEMS.register("void_essence",
            () -> new Item(new Item.Properties()));

    /** void_eye : pupil-shaped void relic, rare drop, used on the watcher_altar. */
    public static final RegistryObject<Item> VOID_EYE = ITEMS.register("void_eye",
            () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    /** watcher_relic : bronze amulet of the extinct civilization. */
    public static final RegistryObject<Item> WATCHER_RELIC = ITEMS.register("watcher_relic",
            () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    /** cracked_silk : thread dropped by crack_spider. */
    public static final RegistryObject<Item> CRACKED_SILK = ITEMS.register("cracked_silk",
            () -> new Item(new Item.Properties()));

    /** void_core : boss material, dropped by the mirror_eater. */
    public static final RegistryObject<Item> VOID_CORE = ITEMS.register("void_core",
            () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));

    /** final_mirror_item : the Final Mirror as a carried item, boss drop. */
    public static final RegistryObject<Item> FINAL_MIRROR_ITEM = ITEMS.register("final_mirror_item",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.EPIC)));

    // ===================== Block items =====================

    public static final RegistryObject<Item> CRACKED_STONE_ITEM = ITEMS.register("cracked_stone",
            () -> new BlockItem(ModBlocks.CRACKED_STONE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MIRROR_SHARD_BLOCK_ITEM = ITEMS.register("mirror_shard_block",
            () -> new BlockItem(ModBlocks.MIRROR_SHARD_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> VOID_STABILIZER_ITEM = ITEMS.register("void_stabilizer",
            () -> new BlockItem(ModBlocks.VOID_STABILIZER.get(), new Item.Properties()));
    public static final RegistryObject<Item> WATCHER_ALTAR_ITEM = ITEMS.register("watcher_altar",
            () -> new BlockItem(ModBlocks.WATCHER_ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> FINAL_MIRROR_ITEM_BLOCK = ITEMS.register("final_mirror",
            () -> new BlockItem(ModBlocks.FINAL_MIRROR.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MIRROR_PORTAL_FRAME_ITEM = ITEMS.register("mirror_portal_frame",
            () -> new BlockItem(ModBlocks.MIRROR_PORTAL_FRAME.get(), new Item.Properties()));
    public static final RegistryObject<Item> VOID_FLOOR_ITEM = ITEMS.register("void_floor",
            () -> new BlockItem(ModBlocks.VOID_FLOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> WATCHER_BRICK_ITEM = ITEMS.register("watcher_brick",
            () -> new BlockItem(ModBlocks.WATCHER_BRICK.get(), new Item.Properties()));
    public static final RegistryObject<Item> REFLECTIVE_LAMP_ITEM = ITEMS.register("reflective_lamp",
            () -> new BlockItem(ModBlocks.REFLECTIVE_LAMP.get(), new Item.Properties()));
    public static final RegistryObject<Item> ABYSS_ORE_ITEM = ITEMS.register("abyss_ore",
            () -> new BlockItem(ModBlocks.ABYSS_ORE.get(), new Item.Properties()));

    private ModItems() {
    }
}

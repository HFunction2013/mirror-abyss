package com.example.mirror_abyss.block;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * All mirror_abyss blocks.
 *
 * Registration names are snake_case and must match:
 *   - assets/mirror_abyss/blockstates/<name>.json
 *   - assets/mirror_abyss/models/block/<name>.json
 *   - assets/mirror_abyss/models/item/<name>.json (block item model)
 *   - data/mirror_abyss/loot_tables/blocks/<name>.json
 */
public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MirrorAbyss.MOD_ID);

    // ---- 1. cracked_stone : main ground block of the Mirror Abyss ----
    public static final RegistryObject<Block> CRACKED_STONE = BLOCKS.register("cracked_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .mapColor(MapColor.COLOR_BLACK)));

    // ---- 2. mirror_shard_block : reflective puzzle block (emits a light beam) ----
    public static final RegistryObject<Block> MIRROR_SHARD_BLOCK = BLOCKS.register("mirror_shard_block",
            () -> new MirrorShardBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .noOcclusion()
                    .strength(2.0f, 6.0f)
                    .lightLevel(state -> 7)));

    // ---- 3. void_stabilizer : suppresses void fissures, interrupts boss charge ----
    public static final RegistryObject<Block> VOID_STABILIZER = BLOCKS.register("void_stabilizer",
            () -> new VoidStabilizerBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0f, 1200.0f)
                    .lightLevel(state -> 8)));

    // ---- 4. watcher_altar : accepts a void_eye, teleports to the Void Sanctum ----
    public static final RegistryObject<Block> WATCHER_ALTAR = BLOCKS.register("watcher_altar",
            () -> new WatcherAltarBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(4.0f, 20.0f)
                    .lightLevel(state -> state.getValue(WatcherAltarBlock.ACTIVATED) ? 9 : 4)));

    // ---- 5. final_mirror : story core block, opens the ending GUI ----
    public static final RegistryObject<Block> FINAL_MIRROR = BLOCKS.register("final_mirror",
            () -> new FinalMirrorBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(-1.0f, 3600000.0f)
                    .noOcclusion()
                    .lightLevel(state -> 11)));

    // ---- 6. mirror_portal_frame : non-solid frame, activated with mirror shards ----
    public static final RegistryObject<Block> MIRROR_PORTAL_FRAME = BLOCKS.register("mirror_portal_frame",
            () -> new MirrorPortalFrameBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(5.0f, 1200.0f)
                    .noOcclusion()));

    // ---- 7. void_floor : dangerous block, deals void damage on contact ----
    public static final RegistryObject<Block> VOID_FLOOR = BLOCKS.register("void_floor",
            () -> new VoidFloorBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(1.2f, 6.0f)
                    .lightLevel(state -> 3)));

    // ---- 8. watcher_brick : decorative ancient civilization brick ----
    public static final RegistryObject<Block> WATCHER_BRICK = BLOCKS.register("watcher_brick",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(3.0f, 18.0f)));

    // ---- 9. reflective_lamp : puzzle light source, emits a straight beam, light 12 ----
    public static final RegistryObject<Block> REFLECTIVE_LAMP = BLOCKS.register("reflective_lamp",
            () -> new ReflectiveLampBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.0f, 8.0f)
                    .lightLevel(state -> 12)));

    // ---- 10. abyss_ore : deep overworld ore, drops mirror shards ----
    public static final RegistryObject<Block> ABYSS_ORE = BLOCKS.register("abyss_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(4.0f, 12.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 4)));

    private ModBlocks() {
    }
}

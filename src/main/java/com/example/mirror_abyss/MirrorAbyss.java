package com.example.mirror_abyss;

import com.example.mirror_abyss.advancement.ModAdvancements;
import com.example.mirror_abyss.block.ModBlocks;
import com.example.mirror_abyss.blockentity.ModBlockEntities;
import com.example.mirror_abyss.effect.ModEffects;
import com.example.mirror_abyss.entity.ModEntities;
import com.example.mirror_abyss.gui.ModMenuTypes;
import com.example.mirror_abyss.item.ModItems;
import com.example.mirror_abyss.networking.ModNetwork;
import com.example.mirror_abyss.sound.ModSounds;
import com.example.mirror_abyss.world.ModBiomes;
import com.example.mirror_abyss.world.ModDimensions;
import com.example.mirror_abyss.world.ModWorldgenCodecs;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * MirrorAbyss - main mod entry point.
 * Echoes of the Abyss: The Final Mirror (mirror_abyss)
 *
 * Boot order:
 *   1. Register every DeferredRegister on the mod event bus.
 *   2. Hook common setup (networking, structure registration, custom codecs).
 *   3. Register game event listeners (loot injection, advancement triggers).
 */
@Mod(MirrorAbyss.MOD_ID)
public class MirrorAbyss {

    /** Mod id, must match mods.toml, every asset path and every datapack namespace. */
    public static final String MOD_ID = "mirror_abyss";

    /** Shared logger. */
    public static final Logger LOGGER = LogUtils.getLogger();

    public MirrorAbyss() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        // ---- Registry batches (blocks, items, entities, block entities, menus, effects) ----
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
        ModMenuTypes.MENU_TYPES.register(modBus);
        ModEffects.MOB_EFFECTS.register(modBus);
        ModCreativeTabs.CREATIVE_TABS.register(modBus);
        ModSounds.SOUND_EVENTS.register(modBus);

        // ---- Worldgen batches (biomes; dimension type is fully data-driven JSON) ----
        ModBiomes.BIOMES.register(modBus);
        // Custom chunk generator / biome source codecs - must go through
        // DeferredRegister (manual Registry.register would hit "registry frozen").
        ModWorldgenCodecs.CHUNK_GENERATORS.register(modBus);
        ModWorldgenCodecs.BIOME_SOURCES.register(modBus);

        // ---- Lifecycle listeners ----
        modBus.addListener(this::commonSetup);
        modBus.addListener(ModEntities::registerAttributes);
        modBus.addListener(ModEntities::registerSpawnPlacements);

        // ---- Game event listeners ----
        forgeBus.register(this);

        // Force static init of advancement triggers / networking.
        ModAdvancements.init();
        ModNetwork.register(); // SimpleChannel (ending choice packet)
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModDimensions.registerTeleporter();
            LOGGER.info("[mirror_abyss] Common setup finished.");
        });
    }
}

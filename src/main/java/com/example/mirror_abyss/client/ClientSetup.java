package com.example.mirror_abyss.client;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.client.model.CrackSpiderModel;
import com.example.mirror_abyss.client.model.MirageWalkerModel;
import com.example.mirror_abyss.client.model.MirrorEaterModel;
import com.example.mirror_abyss.client.model.VoidBoltModel;
import com.example.mirror_abyss.client.model.VoidDevourerModel;
import com.example.mirror_abyss.client.renderer.CrackSpiderRenderer;
import com.example.mirror_abyss.client.renderer.MirageWalkerRenderer;
import com.example.mirror_abyss.client.renderer.MirrorEaterRenderer;
import com.example.mirror_abyss.client.renderer.VoidBoltRenderer;
import com.example.mirror_abyss.client.renderer.VoidDevourerRenderer;
import com.example.mirror_abyss.entity.ModEntities;
import com.example.mirror_abyss.gui.FinalMirrorScreen;
import com.example.mirror_abyss.gui.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * ClientSetup - all client-side registrations.
 *
 * Loaded only on the client thanks to @Mod.EventBusSubscriber(Dist.CLIENT).
 * Registers layer definitions, entity renderers and the final mirror menu
 * screen.
 */
@Mod.EventBusSubscriber(modid = MirrorAbyss.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientSetup {

    public static final ModelLayerLocation MIRAGE_WALKER_LAYER = layer("mirage_walker");
    public static final ModelLayerLocation VOID_DEVOURER_LAYER = layer("void_devourer");
    public static final ModelLayerLocation CRACK_SPIDER_LAYER = layer("crack_spider");
    public static final ModelLayerLocation MIRROR_EATER_LAYER = layer("mirror_eater");
    public static final ModelLayerLocation VOID_BOLT_LAYER = layer("void_bolt");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(MirrorAbyss.MOD_ID, name), "main");
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MIRAGE_WALKER_LAYER, MirageWalkerModel::createBodyLayer);
        event.registerLayerDefinition(VOID_DEVOURER_LAYER, VoidDevourerModel::createBodyLayer);
        event.registerLayerDefinition(CRACK_SPIDER_LAYER, CrackSpiderModel::createBodyLayer);
        event.registerLayerDefinition(MIRROR_EATER_LAYER, MirrorEaterModel::createBodyLayer);
        event.registerLayerDefinition(VOID_BOLT_LAYER, VoidBoltModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MIRAGE_WALKER.get(), MirageWalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.VOID_DEVOURER.get(), VoidDevourerRenderer::new);
        event.registerEntityRenderer(ModEntities.CRACK_SPIDER.get(), CrackSpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.MIRROR_EATER.get(), MirrorEaterRenderer::new);
        event.registerEntityRenderer(ModEntities.VOID_BOLT.get(), VoidBoltRenderer::new);
    }

    /** Menu screens are registered inside FMLClientSetupEvent (1.20.1 has no
     *  RegisterMenuScreensEvent - MenuScreens.register is the vanilla way). */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MenuScreens.register(ModMenuTypes.FINAL_MIRROR_MENU.get(), FinalMirrorScreen::new));
    }

    /** Register the Mirror Abyss dimension special effects (sky/fog renderer).
     *  This class is @EventBusSubscriber(Dist.CLIENT), so this only ever runs
     *  on the client - the server never loads MirrorAbyssEffects. The key must
     *  match the "effects" field of the dimension_type JSON. */
    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(new ResourceLocation(MirrorAbyss.MOD_ID, "mirror_abyss"),
                new MirrorAbyssEffects());
    }

    private ClientSetup() {
    }
}

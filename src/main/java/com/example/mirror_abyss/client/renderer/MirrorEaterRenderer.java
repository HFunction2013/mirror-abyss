package com.example.mirror_abyss.client.renderer;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.client.ClientSetup;
import com.example.mirror_abyss.client.model.MirrorEaterModel;
import com.example.mirror_abyss.entity.boss.MirrorEater;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * MirrorEaterRenderer - boss renderer with a boss-bar sized shadow.
 */
public class MirrorEaterRenderer extends MobRenderer<MirrorEater, EntityModel<MirrorEater>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MirrorAbyss.MOD_ID, "textures/entity/mirror_eater.png");

    @SuppressWarnings("unchecked")
    public MirrorEaterRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel<MirrorEater>) (EntityModel<?>) new MirrorEaterModel(
                context.bakeLayer(ClientSetup.MIRROR_EATER_LAYER)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(MirrorEater entity) {
        return TEXTURE;
    }
}

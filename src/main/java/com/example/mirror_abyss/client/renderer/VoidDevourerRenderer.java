package com.example.mirror_abyss.client.renderer;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.client.ClientSetup;
import com.example.mirror_abyss.client.model.VoidDevourerModel;
import com.example.mirror_abyss.entity.Monster.VoidDevourer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * VoidDevourerRenderer - translucent orb with a purple vortex.
 */
public class VoidDevourerRenderer extends MobRenderer<VoidDevourer, EntityModel<VoidDevourer>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MirrorAbyss.MOD_ID, "textures/entity/void_devourer.png");

    @SuppressWarnings("unchecked")
    public VoidDevourerRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel<VoidDevourer>) (EntityModel<?>) new VoidDevourerModel(
                context.bakeLayer(ClientSetup.VOID_DEVOURER_LAYER)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(VoidDevourer entity) {
        return TEXTURE;
    }

    @Override
    public RenderType getRenderType(VoidDevourer entity, boolean bodyVisible,
                                    boolean translucent, boolean showOutline) {
        return RenderType.entityTranslucent(TEXTURE);
    }
}

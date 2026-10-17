package com.example.mirror_abyss.client.renderer;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.client.ClientSetup;
import com.example.mirror_abyss.client.model.MirageWalkerModel;
import com.example.mirror_abyss.entity.Monster.MirageWalker;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * MirageWalkerRenderer - renders the walker with a translucent render type
 * (mirror fragments body).
 */
public class MirageWalkerRenderer extends MobRenderer<MirageWalker, EntityModel<MirageWalker>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MirrorAbyss.MOD_ID, "textures/entity/mirage_walker.png");

    @SuppressWarnings("unchecked")
    public MirageWalkerRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel<MirageWalker>) (EntityModel<?>) new MirageWalkerModel(
                context.bakeLayer(ClientSetup.MIRAGE_WALKER_LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(MirageWalker entity) {
        return TEXTURE;
    }

    @Override
    public RenderType getRenderType(MirageWalker entity, boolean bodyVisible,
                                    boolean translucent, boolean showOutline) {
        return RenderType.entityTranslucent(TEXTURE);
    }
}

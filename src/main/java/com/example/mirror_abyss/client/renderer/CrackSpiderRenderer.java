package com.example.mirror_abyss.client.renderer;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.client.ClientSetup;
import com.example.mirror_abyss.client.model.CrackSpiderModel;
import com.example.mirror_abyss.entity.Monster.CrackSpider;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * CrackSpiderRenderer - opaque rock spider.
 */
public class CrackSpiderRenderer extends MobRenderer<CrackSpider, EntityModel<CrackSpider>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MirrorAbyss.MOD_ID, "textures/entity/crack_spider.png");

    @SuppressWarnings("unchecked")
    public CrackSpiderRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel<CrackSpider>) (EntityModel<?>) new CrackSpiderModel(
                context.bakeLayer(ClientSetup.CRACK_SPIDER_LAYER)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(CrackSpider entity) {
        return TEXTURE;
    }
}

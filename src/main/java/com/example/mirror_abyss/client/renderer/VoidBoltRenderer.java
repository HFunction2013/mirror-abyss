package com.example.mirror_abyss.client.renderer;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.client.ClientSetup;
import com.example.mirror_abyss.client.model.VoidBoltModel;
import com.example.mirror_abyss.entity.VoidBolt;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * VoidBoltRenderer - renders the void bolt as a small glowing purple cube.
 */
public class VoidBoltRenderer extends EntityRenderer<VoidBolt> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MirrorAbyss.MOD_ID, "textures/entity/void_bolt.png");

    private final VoidBoltModel model;

    public VoidBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new VoidBoltModel(context.bakeLayer(ClientSetup.VOID_BOLT_LAYER));
    }

    @Override
    public void render(VoidBolt entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight) {
        super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
        poseStack.pushPose();
        poseStack.translate(0.0D, -0.15D, 0.0D);
        poseStack.scale(0.4F, 0.4F, 0.4F);
        VertexConsumer buffer = buffers.getBuffer(RenderType.entityTranslucent(TEXTURE));
        this.model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 0.85F);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(VoidBolt entity) {
        return TEXTURE;
    }
}

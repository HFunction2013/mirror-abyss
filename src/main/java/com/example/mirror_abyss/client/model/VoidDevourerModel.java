package com.example.mirror_abyss.client.model;

import com.example.mirror_abyss.entity.Monster.VoidDevourer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * VoidDevourerModel - floating black orb with a purple vortex core.
 */
public class VoidDevourerModel extends EntityModel<VoidDevourer> {

    private final ModelPart core;
    private final ModelPart vortex;

    public VoidDevourerModel(ModelPart root) {
        this.core = root.getChild("core");
        this.vortex = root.getChild("vortex");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-7.0F, -7.0F, -7.0F, 14.0F, 14.0F, 14.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F));
        part.addOrReplaceChild("vortex", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(VoidDevourer entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // Slow rotation of the vortex core.
        this.vortex.yRot = ageInTicks * 0.05F;
        this.vortex.xRot = (float) Math.sin(ageInTicks * 0.02F) * 0.2F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.core.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.vortex.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

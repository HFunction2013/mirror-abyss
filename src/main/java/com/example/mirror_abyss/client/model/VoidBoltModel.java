package com.example.mirror_abyss.client.model;

import com.example.mirror_abyss.entity.VoidBolt;
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
 * VoidBoltModel - small purple orb projectile.
 */
public class VoidBoltModel extends EntityModel<VoidBolt> {

    private final ModelPart cube;

    public VoidBoltModel(ModelPart root) {
        this.cube = root.getChild("cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(VoidBolt entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.cube.yRot = ageInTicks * 0.2F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.cube.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

package com.example.mirror_abyss.client.model;

import com.example.mirror_abyss.entity.Monster.MirageWalker;
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
 * MirageWalkerModel - translucent humanoid built from mirror fragment cubes.
 * Placeholder geometry; every cube maps the whole 16x16 texture.
 */
public class MirageWalkerModel extends EntityModel<MirageWalker> {

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public MirageWalkerModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0F, -5.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 13.0F, 0.0F));
        part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.5F, 0.0F, -1.5F, 5.0F, 6.0F, 3.0F),
                PartPose.offset(0.0F, 13.0F, 0.0F));
        part.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.5F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offset(-3.0F, 14.0F, 0.0F));
        part.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-0.5F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offset(3.0F, 14.0F, 0.0F));
        part.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.5F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offset(-1.0F, 19.0F, 0.0F));
        part.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-0.5F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offset(1.0F, 19.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(MirageWalker entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // Walk cycle placeholder: swing arms and legs.
        this.rightLeg.xRot = (float) Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = (float) Math.cos(limbSwing * 0.6662F + Math.PI) * 1.4F * limbSwingAmount;
        this.rightArm.xRot = this.leftLeg.xRot;
        this.leftArm.xRot = this.rightLeg.xRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

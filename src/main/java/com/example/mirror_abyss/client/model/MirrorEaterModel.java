package com.example.mirror_abyss.client.model;

import com.example.mirror_abyss.entity.boss.MirrorEater;
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
 * MirrorEaterModel - large boss: dark body, bright mirror chest, void pupil head.
 */
public class MirrorEaterModel extends EntityModel<MirrorEater> {

    private final ModelPart head;
    private final ModelPart chestMirror;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public MirrorEaterModel(ModelPart root) {
        this.head = root.getChild("head");
        this.chestMirror = root.getChild("chest_mirror");
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
                        .addBox(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 4.0F, 0.0F));
        part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-6.0F, 0.0F, -3.0F, 12.0F, 14.0F, 6.0F),
                PartPose.offset(0.0F, 4.0F, 0.0F));
        part.addOrReplaceChild("chest_mirror", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-3.0F, 3.0F, -3.9F, 6.0F, 8.0F, 1.0F),
                PartPose.offset(0.0F, 4.0F, 0.0F));
        part.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-3.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-7.0F, 6.0F, 0.0F));
        part.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(7.0F, 6.0F, 0.0F));
        part.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-3.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F),
                PartPose.offset(-3.0F, 18.0F, 0.0F));
        part.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F),
                PartPose.offset(3.0F, 18.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(MirrorEater entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
        this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
        // Slow "absorbing" pulse while charging.
        if (entity.getEntityData().get(MirrorEater.CHARGING_ACCESSOR)) {
            float pulse = 1.0F + 0.08F * (float) Math.sin(ageInTicks * 0.3F);
            this.chestMirror.xScale = pulse;
            this.chestMirror.yScale = pulse;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.chestMirror.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

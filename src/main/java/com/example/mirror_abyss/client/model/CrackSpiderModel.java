package com.example.mirror_abyss.client.model;

import com.example.mirror_abyss.entity.Monster.CrackSpider;
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
 * CrackSpiderModel - rock body with purple fissure limbs.
 */
public class CrackSpiderModel extends EntityModel<CrackSpider> {

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart leg5;
    private final ModelPart leg6;
    private final ModelPart leg7;

    public CrackSpiderModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leg0 = root.getChild("leg0");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
        this.leg5 = root.getChild("leg5");
        this.leg6 = root.getChild("leg6");
        this.leg7 = root.getChild("leg7");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 14.0F, -4.0F));
        part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -3.0F, -3.0F, 8.0F, 6.0F, 8.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        // 8 legs (4 pairs), single cube each.
        float[][] legs = {
                {-4.0F, -1.0F, -4.0F}, {4.0F, -1.0F, -4.0F},
                {-4.0F, -1.0F, -1.0F}, {4.0F, -1.0F, -1.0F},
                {-4.0F, -1.0F, 2.0F}, {4.0F, -1.0F, 2.0F},
                {-4.0F, -1.0F, 4.0F}, {4.0F, -1.0F, 4.0F}
        };
        for (int i = 0; i < 8; i++) {
            float[] o = legs[i];
            float x = o[0] < 0 ? -1.0F : 0.0F;
            part.addOrReplaceChild("leg" + i, CubeListBuilder.create().texOffs(0, 0)
                            .addBox(x, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                    PartPose.offset(o[0], 14.0F + o[1], o[2]));
        }

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(CrackSpider entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // Walk cycle: alternate leg swing.
        float swing = (float) Math.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
        this.leg0.zRot = swing;
        this.leg3.zRot = swing;
        this.leg4.zRot = -swing;
        this.leg7.zRot = -swing;
        this.leg1.zRot = -swing;
        this.leg2.zRot = swing;
        this.leg5.zRot = swing;
        this.leg6.zRot = -swing;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        for (int i = 0; i < 8; i++) {
            this.leg(i).render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    private ModelPart leg(int i) {
        return switch (i) {
            case 0 -> leg0;
            case 1 -> leg1;
            case 2 -> leg2;
            case 3 -> leg3;
            case 4 -> leg4;
            case 5 -> leg5;
            case 6 -> leg6;
            default -> leg7;
        };
    }
}

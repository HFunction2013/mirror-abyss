package com.example.mirror_abyss.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

/**
 * MirrorAbyssEffects - DimensionSpecialEffects for the Mirror Abyss.
 *
 * Renders a permanent-twilight sky: a deep purple dome fading to a pale
 * purple-gray horizon (the "purple-gray gradient" from the design doc).
 *
 * Registration (1.20.1 Forge):
 *   ClientSetup#registerDimensionEffects handles the
 *   RegisterDimensionSpecialEffectsEvent and maps "mirror_abyss:mirror_abyss"
 *   (the dimension_type JSON "effects" field) to this instance.
 *
 * The sky itself is drawn through the Forge extension hook
 * IForgeDimensionSpecialEffects#renderSky - returning true fully suppresses
 * the vanilla sky renderer for this dimension.
 *
 * Floating mirror-shard particles are spawned separately by ClientParticles
 * so they keep existing while the sky box is only drawn per frame.
 */
@OnlyIn(Dist.CLIENT)
public class MirrorAbyssEffects extends DimensionSpecialEffects {

    public MirrorAbyssEffects() {
        // cloudHeight 128, hasGround true, SkyType.END (no sun/moon), no bright
        // lightmap, constant ambient light for the twilight feel.
        super(128.0F, true, SkyType.END, false, true);
    }

    /** No sunrise/sunset tint - the dimension is stuck in dusk. */
    @Override
    @Nullable
    public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        return null;
    }

    /** Keep the twilight fog color independent of brightness changes. */
    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return fogColor;
    }

    /** Light fog - the whole dimension feels hazy. */
    @Override
    public boolean isFoggyAt(int x, int z) {
        return true;
    }

    /**
     * Custom sky: a 6-face gradient box around the camera.
     * ceiling = near-black purple (#1a1228), top = deep twilight purple
     * (#2b203a), bottom = pale purple-gray (#847cb0), floor = void black.
     *
     * @return true - the vanilla sky renderer is fully replaced.
     */
    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick,
                             PoseStack poseStack, Camera camera,
                             Matrix4f projectionMatrix, boolean isFoggy,
                             Runnable setupFog) {
        // Vanilla would run setupFog before drawing its own sky; we take over,
        // so run it ourselves to leave the fog state consistent.
        setupFog.run();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Vec3 cam = camera.getPosition();
        double r = 800.0D;
        int topR = 43, topG = 32, topB = 58, a = 255;      // #2b203a
        int botR = 132, botG = 124, botB = 172;            // #847cb0
        int ceilR = 26, ceilG = 18, ceilB = 40;            // #1a1228

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Ceiling (top face).
        quad(builder, cam.x - r, cam.y + r, cam.z - r, cam.x + r, cam.y + r, cam.z - r,
                cam.x + r, cam.y + r, cam.z + r, cam.x - r, cam.y + r, cam.z + r,
                ceilR, ceilG, ceilB, a);

        // Four side faces with top->bottom gradient.
        side(builder, cam.x - r, cam.y + r, cam.z - r, cam.x + r, cam.z - r, cam.y - r,
                topR, topG, topB, botR, botG, botB, a); // north
        side(builder, cam.x + r, cam.y + r, cam.z - r, cam.x + r, cam.z + r, cam.y - r,
                topR, topG, topB, botR, botG, botB, a); // east
        side(builder, cam.x + r, cam.y + r, cam.z + r, cam.x - r, cam.z + r, cam.y - r,
                topR, topG, topB, botR, botG, botB, a); // south
        side(builder, cam.x - r, cam.y + r, cam.z + r, cam.x - r, cam.z - r, cam.y - r,
                topR, topG, topB, botR, botG, botB, a); // west

        // Floor (near-black, gazing into the void).
        quad(builder, cam.x - r, cam.y - r, cam.z - r, cam.x + r, cam.y - r, cam.z - r,
                cam.x + r, cam.y - r, cam.z + r, cam.x - r, cam.y - r, cam.z + r,
                10, 8, 16, a);

        tesselator.end();
        RenderSystem.disableBlend();
        return true; // suppress vanilla sky rendering
    }

    /** Emit one vertex with the given RGBA color. */
    private static void vertex(BufferBuilder builder, double x, double y, double z,
                               int r, int g, int b, int a) {
        builder.vertex(x, y, z).color(r, g, b, a).endVertex();
    }

    /** A flat quad given by four corners. */
    private static void quad(BufferBuilder builder,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             double x3, double y3, double z3,
                             double x4, double y4, double z4,
                             int r, int g, int b, int a) {
        vertex(builder, x1, y1, z1, r, g, b, a);
        vertex(builder, x2, y2, z2, r, g, b, a);
        vertex(builder, x3, y3, z3, r, g, b, a);
        vertex(builder, x4, y4, z4, r, g, b, a);
    }

    /** A vertical side: two top corners with the top color, two bottom with the bottom color. */
    private static void side(BufferBuilder builder,
                             double x1, double yTop, double z1,
                             double x2, double z2, double yBottom,
                             int topR, int topG, int topB,
                             int botR, int botG, int botB, int a) {
        vertex(builder, x1, yTop, z1, topR, topG, topB, a);
        vertex(builder, x2, yTop, z2, topR, topG, topB, a);
        vertex(builder, x2, yBottom, z2, botR, botG, botB, a);
        vertex(builder, x1, yBottom, z1, botR, botG, botB, a);
    }
}

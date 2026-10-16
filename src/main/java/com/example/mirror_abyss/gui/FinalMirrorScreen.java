package com.example.mirror_abyss.gui;

import com.example.mirror_abyss.MirrorAbyss;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * FinalMirrorScreen - the ending selection GUI.
 *
 * Two buttons:
 *   - "Seal the Void"  -> peace ending
 *   - "Fuse Reality"   -> chaos ending
 *
 * Production implementation sends a custom login-agnostic packet
 * (e.g. SimpleChannel#sendToServer) with the choice; the server then
 * writes the ending into the FinalMirrorBE and triggers the finale.
 * The skeleton keeps the button layout and event hooks.
 */
public class FinalMirrorScreen extends AbstractContainerScreen<FinalMirrorMenu> {

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(MirrorAbyss.MOD_ID, "textures/gui/final_mirror.png");

    public FinalMirrorScreen(FinalMirrorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 110;
    }

    @Override
    protected void init() {
        super.init();
        int cx = this.leftPos + this.imageWidth / 2;

        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.mirror_abyss.seal_void"),
                        btn -> onEndingChosen(1))
                .bounds(cx - 82, this.topPos + 60, 78, 20)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.mirror_abyss.fuse_reality"),
                        btn -> onEndingChosen(2))
                .bounds(cx + 4, this.topPos + 60, 78, 20)
                .build());
    }

    private void onEndingChosen(int ending) {
        // TODO(packet): channel.sendToServer(new FinalMirrorChoicePacket(ending));
        MirrorAbyss.LOGGER.info("[mirror_abyss] Player chose ending {}", ending);
        this.onClose();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 176, 110);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.leftPos + this.imageWidth / 2,
                this.topPos + 12, 0xFFFFFF);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}

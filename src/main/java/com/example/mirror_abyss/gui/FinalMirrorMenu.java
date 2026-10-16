package com.example.mirror_abyss.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

/**
 * FinalMirrorMenu - container menu without slots.
 *
 * The screen sends the chosen ending to the server through a simple
 * custom payload (skeleton: see the packet comment in FinalMirrorScreen).
 */
public class FinalMirrorMenu extends AbstractContainerMenu {

    public FinalMirrorMenu(int id) {
        super(ModMenuTypes.FINAL_MIRROR_MENU.get(), id);
    }

    @Nullable
    public static FinalMirrorMenu fromNetwork(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf buf) {
        return new FinalMirrorMenu(id);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

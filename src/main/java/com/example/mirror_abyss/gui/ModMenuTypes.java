package com.example.mirror_abyss.gui;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Menu type registry for the FinalMirror ending GUI.
 */
public final class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MirrorAbyss.MOD_ID);

    /** Menu with no inventory slots; used only to carry the ending choice. */
    public static final RegistryObject<MenuType<FinalMirrorMenu>> FINAL_MIRROR_MENU =
            MENU_TYPES.register("final_mirror_menu",
                    () -> IForgeMenuType.create((id, inv, data) -> new FinalMirrorMenu(id)));

    private ModMenuTypes() {
    }
}

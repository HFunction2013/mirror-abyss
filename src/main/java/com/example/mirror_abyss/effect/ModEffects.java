package com.example.mirror_abyss.effect;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Custom mob effects.
 */
public final class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MirrorAbyss.MOD_ID);

    /**
     * void_erosion - applied by void bolts from the void_devourer.
     * Deals magic damage over time and tints the screen purple.
     */
    public static final RegistryObject<MobEffect> VOID_EROSION = MOB_EFFECTS.register("void_erosion",
            () -> new VoidErosionEffect(MobEffectCategory.HARMFUL, 0x5a2bd6));

    private ModEffects() {
    }
}

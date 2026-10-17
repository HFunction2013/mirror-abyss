package com.example.mirror_abyss.sound;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * ModSounds - sound event registry for mirror_abyss.
 *
 * Every entry also exists in assets/mirror_abyss/sounds.json, where each event
 * falls back to a vanilla sound event (type "event") so the skeleton works
 * without shipping .ogg files. Replace with real recordings later.
 *
 * Subtitle keys are added to both lang files:
 *   subtitles.mirror_abyss.<name>
 */
public final class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MirrorAbyss.MOD_ID);

    /** Sparse dimension ambient loop (see ClientParticles). */
    public static final RegistryObject<SoundEvent> MIRROR_AMBIENT =
            SOUND_EVENTS.register("mirror_ambient",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MirrorAbyss.MOD_ID, "mirror_ambient")));

    /** Portal frame activation. */
    public static final RegistryObject<SoundEvent> PORTAL_ACTIVATE =
            SOUND_EVENTS.register("portal_activate",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MirrorAbyss.MOD_ID, "portal_activate")));

    /** Boss phase transition roar. */
    public static final RegistryObject<SoundEvent> BOSS_ROAR =
            SOUND_EVENTS.register("boss_roar",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MirrorAbyss.MOD_ID, "boss_roar")));

    /** Boss void-annihilation charge (phase 3). */
    public static final RegistryObject<SoundEvent> BOSS_CHARGE =
            SOUND_EVENTS.register("boss_charge",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MirrorAbyss.MOD_ID, "boss_charge")));

    /** VoidDevourer ranged attack launch. */
    public static final RegistryObject<SoundEvent> VOID_BOLT_LAUNCH =
            SOUND_EVENTS.register("void_bolt_launch",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MirrorAbyss.MOD_ID, "void_bolt_launch")));

    /** Ending A - sealing the void. */
    public static final RegistryObject<SoundEvent> ENDING_SEAL =
            SOUND_EVENTS.register("ending_seal",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MirrorAbyss.MOD_ID, "ending_seal")));

    /** Ending B - fusing reality. */
    public static final RegistryObject<SoundEvent> ENDING_FUSE =
            SOUND_EVENTS.register("ending_fuse",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MirrorAbyss.MOD_ID, "ending_fuse")));

    private ModSounds() {
    }
}

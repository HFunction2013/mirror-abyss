package com.example.mirror_abyss.block;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * VoidTouch - shared "dangerous Mirror Abyss block" helper (issue #2).
 *
 * A living entity that steps on a VoidTouch block has a 30% chance to take
 * 2 HP of void damage. A per-entity cooldown (0.5s) prevents the same step
 * from re-proccing on consecutive ticks, and a per-entity check means the
 * roll happens once per block entered, not once per tick.
 *
 * The damage uses the data-driven damage type mirror_abyss:void_touch
 * (data/mirror_abyss/damage_type/void_touch.json) so the death message reads
 * "was consumed by the void" instead of a generic magic death.
 */
public final class VoidTouch {

    /** Data-driven damage type key, registered via datapack JSON. */
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(
            Registries.DAMAGE_TYPE, new ResourceLocation(MirrorAbyss.MOD_ID, "void_touch"));

    /** Chance to trigger per stepOn event (issue #2: 30%). */
    public static final float CHANCE = 0.30F;

    /** Damage dealt, in half-hearts. */
    public static final float DAMAGE = 2.0F;

    /** Per-entity cooldown in ticks (0.5s) so one step cannot proc repeatedly. */
    public static final int COOLDOWN_TICKS = 10;

    /** Forge persistent-data key holding the game time the cooldown expires. */
    private static final String COOLDOWN_KEY = "mirror_abyss_void_touch_cooldown";

    private VoidTouch() {
    }

    /**
     * Rolls the 30% chance and applies void damage to a living entity while
     * respecting the per-entity cooldown. Safe to call from any side.
     */
    public static void tryApply(Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof LivingEntity living)) {
            return;
        }
        CompoundTag data = living.getPersistentData();
        long now = level.getGameTime();
        if (now < data.getLong(COOLDOWN_KEY)) {
            return; // still cooling down from the last proc
        }
        if (level.random.nextFloat() < CHANCE) {
            // 1.20.1: resolve the data-driven damage type from the registry
            // and build a DamageSource from its Holder.
            Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
            Holder<DamageType> holder = registry.getHolderOrThrow(DAMAGE_TYPE);
            living.hurt(new DamageSource(holder), DAMAGE);
            data.putLong(COOLDOWN_KEY, now + COOLDOWN_TICKS);
        }
    }
}

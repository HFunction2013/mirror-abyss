package com.example.mirror_abyss.entity;

import com.example.mirror_abyss.world.ModDimensions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * MirrorMob - base class for every Mirror Abyss monster.
 *
 * Issue #1: creatures of the Mirror Abyss are fully immune to damage while
 * they are inside their own dimension (ModDimensions.MIRROR_ABYSS_LEVEL).
 * Outside that dimension (overworld, nether, end) they take damage normally,
 * so they can still be lured out and killed if the player wants their drops.
 *
 * The MirrorEater boss deliberately does NOT extend this class: it must stay
 * killable inside the Void Sanctum or the main quest (final_mirror endings)
 * would be impossible to finish.
 */
public abstract class MirrorMob extends Monster {

    protected MirrorMob(EntityType<? extends MirrorMob> type, Level level) {
        super(type, level);
    }

    /** True while this mob is inside the Mirror Abyss dimension. */
    protected boolean inOwnDimension() {
        return this.level().dimension() == ModDimensions.MIRROR_ABYSS_LEVEL;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (inOwnDimension()) {
            return false; // fully immune inside the Mirror Abyss
        }
        return super.hurt(source, amount);
    }
}

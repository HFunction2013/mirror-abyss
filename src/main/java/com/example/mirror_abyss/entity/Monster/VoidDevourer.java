package com.example.mirror_abyss.entity.Monster;

import com.example.mirror_abyss.effect.ModEffects;
import com.example.mirror_abyss.entity.ModEntities;
import com.example.mirror_abyss.entity.VoidBolt;
import com.example.mirror_abyss.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * VoidDevourer - floating black orb with a purple vortex.
 *
 * Behavior:
 *  - Hovers above the ground (no gravity override in skeleton; spawn with
 *    NoGravity flag in final implementation).
 *  - Fires void_bolt projectiles at the player (RangedAttackGoal).
 *  - The bolt applies "void_erosion" on hit.
 *
 * Drops: void_essence (1-2), void_eye (rare ~4%), XP.
 */
public class VoidDevourer extends Monster implements RangedAttackMob {

    public VoidDevourer(EntityType<? extends VoidDevourer> type, Level level) {
        super(type, level);
        this.xpReward = 12;
        this.setNoGravity(true); // floating orb
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 0.9D, 30, 24.0F));
        this.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.RandomStrollGoal(this, 0.5D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        // Launch a void bolt from slightly above the orb toward the target.
        Vec3 spawn = this.position().add(0.0D, 0.6D, 0.0D);
        VoidBolt bolt = new VoidBolt(this.level(), this, spawn.x, spawn.y, spawn.z);
        Vec3 dir = target.getEyePosition().subtract(spawn).normalize().scale(1.6D);
        bolt.setDeltaMovement(dir);
        this.playSound(ModSounds.VOID_BOLT_LAUNCH.get(), 1.0F, 1.1F);
        this.level().addFreshEntity(bolt);
    }

    @Override
    public void tick() {
        super.tick();
        // Self-defense aura: melee attackers get a short void_erosion.
        if (!this.level().isClientSide && this.tickCount % 40 == 0) {
            this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(1.5D))
                    .forEach(p -> p.addEffect(new MobEffectInstance(ModEffects.VOID_EROSION.get(), 60, 0)));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDERMAN_HURT;
    }
}

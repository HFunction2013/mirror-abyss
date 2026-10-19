package com.example.mirror_abyss.entity.Monster;

import com.example.mirror_abyss.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * MirageWalker - translucent humanoid made of mirror fragments.
 *
 * Behavior:
 *  - Melee attacker with a dash (LeapAtTargetGoal).
 *  - Neutral trick: while the player HOLDS a mirror_shard AND stands still
 *    (velocity near zero), the walker's target is cleared and it does not
 *    attack. Implemented by the custom MirrorShardAwareTargetGoal.
 *  - Rendered translucent via a RenderType (see client-side TODO).
 *
 * Drops: mirror_shard (1-3), XP.
 */
public class MirageWalker extends Monster {

    public MirageWalker(EntityType<? extends MirageWalker> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Custom target goal: ignores players that hold a shard and stand still.
        this.targetSelector.addGoal(2, new MirrorShardAwareTargetGoal(this, Player.class));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        // Always "hold" a shard visually - fragments embedded in the body.
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.MIRROR_SHARD.get()));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Briefly shatter into pieces: take reduced damage from projectiles.
        if (source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.Projectile) {
            amount *= 0.5F;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GLASS_HIT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GLASS_BREAK;
    }

    /**
     * Custom target goal - re-evaluates the target every tick.
     * If the player holds mirror_shard and is stationary, the target is
     * dropped so the walker wanders off (does not attack).
     */
    public static class MirrorShardAwareTargetGoal extends NearestAttackableTargetGoal<Player> {

        public MirrorShardAwareTargetGoal(MirageWalker mob, Class<Player> targetClass) {
            super(mob, targetClass, true);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !playerHoldsShardAndStandsStill();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !playerHoldsShardAndStandsStill();
        }

        private boolean playerHoldsShardAndStandsStill() {
            LivingEntity target = this.target;
            if (!(target instanceof Player player)) {
                return false;
            }
            boolean holdsShard = !player.getMainHandItem().isEmpty()
                    && player.getMainHandItem().is(ModItems.MIRROR_SHARD.get());
            Vec3 vel = player.getDeltaMovement();
            boolean standingStill = vel.lengthSqr() < 0.003D && player.onGround();
            return holdsShard && standingStill;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        // Skeleton: persist any phase flags here later.
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }
}

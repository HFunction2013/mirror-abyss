package com.example.mirror_abyss.entity.Monster;

import com.example.mirror_abyss.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * CrackSpider - rock and purple fissure spider.
 *
 * Behavior:
 *  - Climbable navigation (like vanilla Spider), leaves small void_floor
 *    cracks under itself every few seconds.
 *
 * Drops: cracked_silk (1-2), spider_eye (rare), XP.
 */
public class CrackSpider extends Monster {

    private int crackCooldown = 0;

    public CrackSpider(EntityType<? extends CrackSpider> type, Level level) {
        super(type, level);
        this.xpReward = 7;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.RandomStrollGoal(this, 0.8D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean onClimbable() {
        // Climb walls like a vanilla spider.
        return this.horizontalCollision;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        // Drop a small void_floor crack under the spider every 5 seconds.
        if (--crackCooldown <= 0) {
            crackCooldown = 100;
            BlockPos below = this.blockPosition().below();
            BlockState current = this.level().getBlockState(below);
            if (current.isAir()) {
                this.level().setBlock(below, ModBlocks.VOID_FLOOR.get().defaultBlockState(), 3);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("crack_cooldown", crackCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        crackCooldown = tag.getInt("crack_cooldown");
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SPIDER_HURT;
    }
}

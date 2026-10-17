package com.example.mirror_abyss.entity;

import com.example.mirror_abyss.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * VoidBolt - projectile fired by the void_devourer.
 * Applies void_erosion and explodes into purple particles on impact.
 */
public class VoidBolt extends AbstractHurtingProjectile {

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(VoidBolt.class, EntityDataSerializers.FLOAT);

    public VoidBolt(EntityType<? extends VoidBolt> type, Level level) {
        super(type, level);
        this.entityData.set(DAMAGE, 4.0F);
    }

    public VoidBolt(Level level, LivingEntity owner, double x, double y, double z) {
        super(ModEntities.VOID_BOLT.get(), owner, x, y, z, level);
        this.entityData.set(DAMAGE, 4.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DAMAGE, 4.0F);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            Entity target = result.getEntity();
            LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
            if (target instanceof LivingEntity living && living != owner) {
                living.hurt(this.damageSources().mobProjectile(this, owner),
                        this.entityData.get(DAMAGE));
                living.addEffect(new MobEffectInstance(ModEffects.VOID_EROSION.get(), 100, 0));
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide && result.getType() != HitResult.Type.MISS) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            // Purple vortex trail.
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("bolt_damage", this.entityData.get(DAMAGE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DAMAGE, tag.getFloat("bolt_damage"));
    }

    @Override
    protected float getInertia() {
        return 0.99F;
    }
}

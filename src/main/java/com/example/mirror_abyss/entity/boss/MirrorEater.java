package com.example.mirror_abyss.entity.boss;

import com.example.mirror_abyss.block.VoidStabilizerBlock;
import com.example.mirror_abyss.entity.ModEntities;
import com.example.mirror_abyss.entity.Monster.MirageWalker;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * MirrorEater - the three-phase boss of the Void Sanctum.
 *
 * Phase 1 (health > 66%): melee + periodically summons mirage_walker adds;
 *   projectiles are reflected back at the attacker.
 * Phase 2 (health 33%..66%): scatters mirror shard projectiles, deals area
 *   damage, teleports the player around the arena.
 * Phase 3 (health < 33%): absorbs void, charges "void annihilation" - a
 *   telegraphed attack that MUST be interrupted by a fully charged
 *   void_stabilizer nearby, otherwise it deals massive damage.
 *
 * Drops: final_mirror_item, void_core, XP.
 */
public class MirrorEater extends Monster {

    public static final int PHASE_ONE = 1;
    public static final int PHASE_TWO = 2;
    public static final int PHASE_THREE = 3;

    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(MirrorEater.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHARGING =
            SynchedEntityData.defineId(MirrorEater.class, EntityDataSerializers.BOOLEAN);

    /** Public accessor for client-side animation (charging pulse). */
    public static final EntityDataAccessor<Boolean> CHARGING_ACCESSOR = CHARGING;

    private final ServerBossEvent bossEvent =
            (ServerBossEvent) new ServerBossEvent(Component.translatable("entity.mirror_abyss.mirror_eater"),
                    BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);

    private int actionCooldown = 60;
    private int chargeProgress = 0;
    private static final int CHARGE_TIME = 100; // ticks to full "void annihilation"

    public MirrorEater(EntityType<? extends MirrorEater> type, Level level) {
        super(type, level);
        this.xpReward = 300;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createBossAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.ATTACK_DAMAGE, 14.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 8.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, PHASE_ONE);
        this.entityData.define(CHARGING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.5D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ======================= Phase state machine =======================

    private int currentPhase() {
        float hp = this.getHealth() / this.getMaxHealth();
        if (hp > 0.66F) return PHASE_ONE;
        if (hp > 0.33F) return PHASE_TWO;
        return PHASE_THREE;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }

        // Sync boss bar.
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        // Phase transition.
        int phase = currentPhase();
        if (phase != this.entityData.get(PHASE)) {
            this.entityData.set(PHASE, phase);
            onPhaseEnter(phase);
        }

        if (--actionCooldown > 0) {
            return;
        }
        actionCooldown = 80 + this.random.nextInt(40);

        switch (this.entityData.get(PHASE)) {
            case PHASE_ONE -> phaseOneAction();
            case PHASE_TWO -> phaseTwoAction();
            case PHASE_THREE -> phaseThreeAction();
        }
    }

    private void onPhaseEnter(int phase) {
        switch (phase) {
            case PHASE_ONE -> broadcast(Component.translatable("boss.mirror_abyss.phase1"));
            case PHASE_TWO -> broadcast(Component.translatable("boss.mirror_abyss.phase2"));
            case PHASE_THREE -> broadcast(Component.translatable("boss.mirror_abyss.phase3"));
        }
    }

    /** Phase 1: summon two mirage_walkers around the boss. */
    private void phaseOneAction() {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 2; i++) {
                MirageWalker walker = ModEntities.MIRAGE_WALKER.get().create(serverLevel);
                if (walker != null) {
                    Vec3 spawn = this.position().add(this.random.nextDouble() * 3.0D - 1.5D, 0.5D,
                            this.random.nextDouble() * 3.0D - 1.5D);
                    walker.moveTo(spawn);
                    walker.setTarget(this.getTarget());
                    serverLevel.addFreshEntity(walker);
                }
            }
        }
    }

    /** Phase 2: teleport the player + area damage + mirror shard scatter. */
    private void phaseTwoAction() {
        if (this.getTarget() instanceof ServerPlayer player) {
            // Teleport the player to a random spot in the arena (skeleton: +-8 blocks).
            Vec3 tp = this.position().add(this.random.nextDouble() * 16.0D - 8.0D, 0.0D,
                    this.random.nextDouble() * 16.0D - 8.0D);
            player.teleportTo(tp.x, tp.y + 1.0D, tp.z);
            // Area damage around the boss.
            this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(6.0D))
                    .forEach(p -> p.hurt(this.damageSources().mobAttack(this), 6.0F));
            broadcast(Component.translatable("boss.mirror_abyss.teleport"));
        }
    }

    /**
     * Phase 3: charge "void annihilation".
     * If a fully charged void_stabilizer is within the arena, the charge is
     * interrupted; otherwise it resolves into a massive AoE.
     */
    private void phaseThreeAction() {
        this.entityData.set(CHARGING, true);
        this.chargeProgress = 0;
        broadcast(Component.translatable("boss.mirror_abyss.charge"));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !this.entityData.get(CHARGING)) {
            return;
        }
        chargeProgress++;
        if (chargeProgress >= CHARGE_TIME) {
            this.entityData.set(CHARGING, false);
            resolveAnnihilation();
        } else if (chargeProgress % 20 == 0) {
            // Interrupt check: a charged stabilizer within 10 blocks cancels the charge.
            if (VoidStabilizerBlock.isStabilized(this.level(), this.blockPosition())) {
                this.entityData.set(CHARGING, false);
                broadcast(Component.translatable("boss.mirror_abyss.interrupted"));
            }
        }
    }

    private void resolveAnnihilation() {
        // Huge AoE on everyone in the arena (skeleton value; tune in production).
        this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(24.0D))
                .forEach(p -> p.hurt(this.damageSources().mobAttack(this), 30.0F));
        broadcast(Component.translatable("boss.mirror_abyss.annihilation"));
    }

    /** Projectile reflection - phase 1 & 2: reflect projectiles back. */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.isProjectile() && this.entityData.get(PHASE) != PHASE_THREE) {
            // Reflect: deal a fraction of the damage back to the attacker.
            if (source.getEntity() instanceof LivingEntity attacker) {
                attacker.hurt(this.damageSources().thorns(this), amount * 0.5F);
            }
            return false; // projectile is consumed/reflected
        }
        return super.hurt(source, amount);
    }

    // ======================= Boss bar management =======================

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    private void broadcast(Component message) {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.players().forEach(p -> p.displayClientMessage(message, true));
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.bossEvent.removeAllPlayers();
        // Drops (final_mirror_item + void_core) are handled by the entity loot
        // table data/mirror_abyss/loot_tables/entities/mirror_eater.json.
        broadcast(Component.translatable("boss.mirror_abyss.death"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("mirror_phase", this.entityData.get(PHASE));
        tag.putBoolean("charging", this.entityData.get(CHARGING));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(PHASE, tag.getInt("mirror_phase"));
        this.entityData.set(CHARGING, tag.getBoolean("charging"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDER_DRAGON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDER_DRAGON_DEATH;
    }
}

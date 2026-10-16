package com.example.mirror_abyss.blockentity;

import com.example.mirror_abyss.block.VoidStabilizerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * VoidStabilizerBE - server-side block entity.
 *
 * Responsibilities:
 *  - Track charge level (0..1). A charged stabilizer suppresses void damage
 *    and can interrupt the MirrorEater "void annihilation" charge.
 *  - Tick a soft pulsing light level via blockstate updates (skeleton).
 */
public class VoidStabilizerBE extends BlockEntity {

    /** Current charge, 0.0 (off) .. 1.0 (fully charged). */
    private float charge = 0.0F;
    private int chargeTicks = 0;

    public VoidStabilizerBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOID_STABILIZER.get(), pos, state);
    }

    public void tickServer(Level level, BlockPos pos, BlockState state) {
        // Charge slowly over time while the chunk is loaded.
        if (chargeTicks++ % 20 == 0 && charge < 1.0F) {
            charge = Math.min(1.0F, charge + 0.05F);
            setChanged();
        }
    }

    public float getCharge() {
        return charge;
    }

    public boolean isFullyCharged() {
        return charge >= 1.0F;
    }

    /** Consume charge when the boss attack is interrupted. */
    public void discharge() {
        charge = 0.0F;
        setChanged();
    }

    /**
     * Static helper used by VoidFloorBlock and the boss AI:
     * returns true when a fully charged stabilizer exists within SUPPRESS_RADIUS.
     */
    public static boolean isProtected(Level level, BlockPos pos) {
        double radius = VoidStabilizerBlock.SUPPRESS_RADIUS;
        double radiusSq = radius * radius;
        BlockPos.betweenClosedStream(
                        pos.offset((int) -radius, (int) -radius, (int) -radius),
                        pos.offset((int) radius, (int) radius, (int) radius)))
                .forEach(p -> {
                    BlockEntity be = level.getBlockEntity(p);
                    if (be instanceof VoidStabilizerBE stabilizer
                            && stabilizer.isFullyCharged()
                            && p.distSqr(pos) <= radiusSq) {
                        protectedFound = true;
                    }
                });
        return protectedFound;
    }

    // Mutable flag used by isProtected (simplified skeleton; replace with a
    // proper loop in production code).
    private static boolean protectedFound = false;
}

package com.example.mirror_abyss.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * VoidFloorBlock - dangerous ground block.
 *
 * Any living entity standing on it takes void damage per tick, unless a
 * void_stabilizer is protecting the area (VoidStabilizerBlock#isStabilized).
 */
public class VoidFloorBlock extends Block {

    public static final float VOID_DAMAGE_PER_TICK = 2.0F; // 1 heart per tick

    public VoidFloorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof LivingEntity living) {
            if (VoidStabilizerBlock.isStabilized(level, pos)) {
                return; // stabilizer suppresses the fissure
            }
            living.hurt(level.damageSources().magic(), VOID_DAMAGE_PER_TICK);
        }
        super.stepOn(level, pos, state, entity);
    }
}

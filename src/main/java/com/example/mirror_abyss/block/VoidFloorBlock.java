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
 * Living entities standing on it have a 30% chance (VoidTouch) to take void
 * damage, unless a void_stabilizer is protecting the area
 * (VoidStabilizerBlock#isStabilized).
 */
public class VoidFloorBlock extends Block {

    public VoidFloorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof LivingEntity) {
            if (VoidStabilizerBlock.isStabilized(level, pos)) {
                return; // stabilizer suppresses the fissure
            }
            VoidTouch.tryApply(level, pos, entity);
        }
        super.stepOn(level, pos, state, entity);
    }
}

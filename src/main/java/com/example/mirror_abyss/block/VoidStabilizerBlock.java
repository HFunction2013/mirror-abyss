package com.example.mirror_abyss.block;

import com.example.mirror_abyss.blockentity.ModBlockEntities;
import com.example.mirror_abyss.blockentity.VoidStabilizerBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * VoidStabilizerBlock - functional block with a block entity.
 *
 * Functions:
 *  - Suppresses void_fissure damage in a radius (see VoidStabilizerBE#isProtected).
 *  - During the MirrorEater phase-3 charge ("void annihilation"), a charged
 *    stabilizer nearby interrupts the boss attack.
 */
public class VoidStabilizerBlock extends BaseEntityBlock {

    public static final double SUPPRESS_RADIUS = 8.0D;

    public VoidStabilizerBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoidStabilizerBE(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : (lvl, pos, st, be) -> ((VoidStabilizerBE) be).tickServer(lvl, pos, st);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL; // custom JSON model (base + core)
    }

    /**
     * Queried by void_floor damage logic and by the MirrorEater AI.
     */
    public static boolean isStabilized(Level level, BlockPos pos) {
        return VoidStabilizerBE.isProtected(level, pos);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}

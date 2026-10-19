package com.example.mirror_abyss.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * MirrorShardBlock - reflective puzzle block.
 *
 * Design:
 *  - When lit (a beam from a reflective_lamp reaches it and it faces the beam),
 *    the block sets REFLECTING=true and re-emits a beam along the reflected
 *    direction (code-side puzzle logic - no rendering needed for the skeleton).
 *  - The vanilla-style light beam is implemented by the puzzle manager
 *    (see ReflectiveLampBlock#tickBeams for the scan algorithm).
 */
public class MirrorShardBlock extends Block {

    /** True while the block is reflecting a light beam. */
    public static final BooleanProperty REFLECTING = BooleanProperty.create("reflecting");

    private static final VoxelShape SHAPE = Shapes.box(0.02D, 0.02D, 0.02D, 0.98D, 0.98D, 0.98D);

    public MirrorShardBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(REFLECTING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REFLECTING);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    /**
     * Puzzle hook: called by the beam scanner when a beam enters this block.
     * @return the direction the beam leaves this block, or null if absorbed.
     */
    public Direction reflectBeam(Level level, BlockPos pos, Direction incoming) {
        level.setBlock(pos, level.getBlockState(pos).setValue(REFLECTING, true), 3);
        level.scheduleTick(pos, this, 40); // turn off the glow after a while
        // Mirror reflection: vertical beams are sent back, horizontal beams turn 90 deg (placeholder rule).
        return incoming.getAxis() == Direction.Axis.Y ? incoming.getOpposite() : incoming.getClockWise();
    }

    @Override
    public void tick(BlockState state, net.minecraft.server.level.ServerLevel level,
                     BlockPos pos, net.minecraft.util.RandomSource random) {
        if (state.getValue(REFLECTING)) {
            level.setBlock(pos, state.setValue(REFLECTING, false), 3);
        }
    }
}

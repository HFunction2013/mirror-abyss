package com.example.mirror_abyss.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * MirrorPortalFrameBlock - non-solid portal frame.
 *
 * Build a 4x5 (width x height) rectangle of frames, then right-click the
 * bottom frame with mirror_shard to activate. The interior is filled with
 * a custom portal overlay (dimension transition handled in ModDimensions).
 *
 * Activation check scans the rectangle relative to the clicked block:
 *   - width = 4 along the horizontal axis perpendicular to the facing
 *   - height = 5
 */
public class MirrorPortalFrameBlock extends Block {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    private static final VoxelShape FRAME_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);

    public MirrorPortalFrameBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return FRAME_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty(); // non-solid: players can step through once active
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(com.example.mirror_abyss.item.ModItems.MIRROR_SHARD.get())) {
            return InteractionResult.PASS;
        }
        Direction facing = player.getDirection(); // used to derive the portal plane
        if (tryActivatePortal((ServerLevel) level, pos, facing)) {
            held.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /**
     * Skeleton validation: expects a filled 4x5 frame rectangle of
     * mirror_portal_frame blocks with an empty interior.
     */
    private boolean tryActivatePortal(ServerLevel level, BlockPos clicked, Direction facing) {
        Direction widthDir = facing.getClockWise();
        boolean anyActivated = false;
        for (int w = 0; w < 4; w++) {
            for (int h = 0; h < 5; h++) {
                BlockPos framePos = clicked.offset(widthDir.getStepX() * w, h, widthDir.getStepZ() * w);
                BlockState frameState = level.getBlockState(framePos);
                if (frameState.is(this)) {
                    level.setBlock(framePos, frameState.setValue(ACTIVATED, true), 3);
                    anyActivated = true;
                }
            }
        }
        return anyActivated;
    }
}

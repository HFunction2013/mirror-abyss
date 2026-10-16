package com.example.mirror_abyss.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * WatcherAltarBlock - functional altar.
 *
 * Function:
 *  - Right-click with a void_eye while standing on the altar teleports the
 *    player to the Void Sanctum (boss arena) and consumes the eye.
 *  - ACTIVATED state lights up the runes (model animation handled via blockstate).
 */
public class WatcherAltarBlock extends Block {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public WatcherAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(com.example.mirror_abyss.item.ModItems.VOID_EYE.get())) {
            return InteractionResult.PASS;
        }
        // Consume one eye and teleport the player to the Void Sanctum.
        held.shrink(1);
        level.setBlock(pos, state.setValue(ACTIVATED, true), 3);
        com.example.mirror_abyss.world.ModDimensions.teleportToVoidSanctum(player);
        // Reset after a short delay so the altar can be reused.
        level.scheduleTick(pos, this, 60);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (state.getValue(ACTIVATED)) {
            level.setBlock(pos, state.setValue(ACTIVATED, false), 3);
        }
    }

    /** Safe landing offset above the altar. */
    public static Vec3 sanctumSpawnOffset() {
        return new Vec3(0.5D, 2.0D, 0.5D);
    }
}

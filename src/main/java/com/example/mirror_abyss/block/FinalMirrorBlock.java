package com.example.mirror_abyss.block;

import com.example.mirror_abyss.blockentity.FinalMirrorBE;
import com.example.mirror_abyss.blockentity.ModBlockEntities;
import com.example.mirror_abyss.gui.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * FinalMirrorBlock - story core block.
 *
 * Right-clicking opens the FinalMirrorScreen (a menu with no inventory slots).
 * The screen lets the player choose one of two endings:
 *   - Seal the Void  (peace ending)
 *   - Fuse Reality  (chaos ending)
 */
public class FinalMirrorBlock extends BaseEntityBlock {

    public FinalMirrorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FinalMirrorBE(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(getMenuProvider(state, pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private MenuProvider getMenuProvider(BlockState state, BlockPos pos) {
        return new SimpleMenuProvider((id, inv, player) ->
                ModMenuTypes.FINAL_MIRROR_MENU.get().create(id, inv),
                Component.translatable("container.mirror_abyss.final_mirror"));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}

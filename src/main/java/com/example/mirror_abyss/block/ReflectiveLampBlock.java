package com.example.mirror_abyss.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * ReflectiveLampBlock - puzzle light source.
 *
 * Emits a straight light beam along its facing axis. The beam scanner
 * (skeleton: tickBeams) walks block-by-block; when a beam hits a
 * mirror_shard_block it is reflected, and when it hits a target
 * (e.g. an unlit watcher_altar) the puzzle is solved and the
 * light_refraction advancement triggers.
 */
public class ReflectiveLampBlock extends Block {

    /** True while the lamp is lit (its beam is active). */
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    /** Maximum beam length in blocks. */
    public static final int BEAM_RANGE = 24;

    public ReflectiveLampBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        scanBeam(level, pos, state.getValue(LIT));
    }

    /**
     * Skeleton beam scanner - walks along +X from the lamp until the range
     * limit, a mirror, or a puzzle target is reached.
     */
    private void scanBeam(ServerLevel level, BlockPos start, boolean lit) {
        if (!lit) {
            return;
        }
        BlockPos cursor = start.offset(Direction.EAST.getStepX(), 0, 0);
        for (int i = 0; i < BEAM_RANGE; i++) {
            BlockState hit = level.getBlockState(cursor);
            if (hit.is(com.example.mirror_abyss.block.ModBlocks.MIRROR_SHARD_BLOCK.get())) {
                hit.getBlock(); // reflection handled by MirrorShardBlock#reflectBeam (hook point)
                return;
            }
            if (hit.is(com.example.mirror_abyss.block.ModBlocks.WATCHER_ALTAR.get())) {
                // Puzzle solved: light the altar and grant the advancement.
                level.setBlock(cursor, hit.setValue(WatcherAltarBlock.ACTIVATED, true), 3);
                com.example.mirror_abyss.advancement.ModAdvancements.grantLightRefraction(level, start);
                return;
            }
            cursor = cursor.offset(Direction.EAST.getStepX(), 0, 0);
        }
    }
}

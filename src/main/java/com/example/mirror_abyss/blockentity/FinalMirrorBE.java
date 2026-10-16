package com.example.mirror_abyss.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * FinalMirrorBE - stores the chosen ending state of this world's mirror.
 *
 * Values:
 *   - 0 = undecided
 *   - 1 = seal (peace ending)
 *   - 2 = fuse (chaos ending)
 * The value is persisted in NBT and read by the story progression code.
 */
public class FinalMirrorBE extends BlockEntity {

    private int ending = 0;

    public FinalMirrorBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FINAL_MIRROR.get(), pos, state);
    }

    public int getEnding() {
        return ending;
    }

    public void setEnding(int ending) {
        this.ending = ending;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ending", ending);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ending = tag.getInt("ending");
    }
}

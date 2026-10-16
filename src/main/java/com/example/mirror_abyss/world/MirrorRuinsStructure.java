package com.example.mirror_abyss.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;
import java.util.Optional;

/**
 * MirrorRuinsStructure - simple procedural structure.
 *
 * Generates a rectangular room/platform out of the configured block palette.
 * No .nbt templates are required for the skeleton; swap for Jigsaw-based
 * generation (with Structure Block exported templates) for fancy buildings.
 */
public class MirrorRuinsStructure extends Structure {

    private final List<BlockState> palette;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    public MirrorRuinsStructure(StructureSettings settings, List<BlockState> palette,
                                int sizeX, int sizeY, int sizeZ) {
        super(settings);
        this.palette = palette;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public static Codec<MirrorRuinsStructure> codec(List<BlockState> palette,
                                                    int sizeX, int sizeY, int sizeZ) {
        return RecordCodecBuilder.create(instance -> instance.group(
                        StructureSettings.CODEC.forGetter(Structure::settings),
                        Codec.list(BlockState.CODEC).fieldOf("palette").forGetter(s -> s.palette),
                        Codec.INT.fieldOf("size_x").forGetter(s -> s.sizeX),
                        Codec.INT.fieldOf("size_y").forGetter(s -> s.sizeY),
                        Codec.INT.fieldOf("size_z").forGetter(s -> s.sizeZ))
                .apply(instance,
                        (settings, pal, x, y, z) -> new MirrorRuinsStructure(settings, pal, x, y, z)));
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, heightmapType(context), builder -> generatePieces(builder, context));
    }

    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        builder.addPiece(new MirrorRuinsPiece(
                this, chunkPos.getMinBlockX() + 4, chunkPos.getMinBlockZ() + 4,
                palette, sizeX, sizeY, sizeZ));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.VOID_SANCTUM.get(); // placeholder - overridden per registration in practice
    }

    /**
     * The actual generated piece: fills a box with the palette blocks.
     */
    public static class MirrorRuinsPiece extends StructurePiece {

        private final List<BlockState> palette;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;

        public MirrorRuinsPiece(Structure owner, int startX, int startZ,
                                List<BlockState> palette, int sizeX, int sizeY, int sizeZ) {
            super(owner.getPieceType(), 0,
                    new BoundingBox(startX, owner.getSettings().terrainAdjustment() == null ? 64 : 64,
                            startZ, startX + sizeX, 64 + sizeY, startZ + sizeZ));
            this.palette = palette;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
        }

        public MirrorRuinsPiece(CompoundTag tag) {
            super(net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType.UNKNOWN, tag);
            this.palette = List.of();
            this.sizeX = 0;
            this.sizeY = 0;
            this.sizeZ = 0;
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            // Skeleton: persist nothing yet.
        }

        @Override
        public void addChildren(StructurePiece parent, StructurePieceAccessor accessor, RandomSource random) {
            // No children in the skeleton.
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                                RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos anchor) {
            // Fill the room box with palette blocks (skeleton, one layer).
            BoundingBox bb = this.boundingBox;
            for (int x = bb.minX(); x <= bb.maxX(); x++) {
                for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
                    for (int y = bb.minY(); y <= bb.minY() + 1; y++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (box.isInside(pos)) {
                            BlockState state = palette.get((x + z + y) % palette.size());
                            level.setBlock(pos, state, 2);
                        }
                    }
                }
            }
        }
    }
}

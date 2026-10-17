package com.example.mirror_abyss.world;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.entity.ModEntities;
import com.example.mirror_abyss.entity.boss.MirrorEater;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;
import java.util.Optional;

/**
 * MirrorRuinsStructure - single shared procedural structure type.
 *
 * The three structures (watchers_ruin / mirror_hallway / void_sanctum) are
 * three data-driven entries that share this type; each JSON provides its own
 * palette, box size and the spawn_boss flag. The void_sanctum entry sets
 * spawn_boss=true so the MirrorEater spawns when the arena generates.
 *
 * No .nbt templates are required; swap for Jigsaw + Structure-Block-exported
 * templates for fancy buildings later.
 */
public class MirrorRuinsStructure extends Structure {

    /** Registered piece type, needed to save/load the piece. */
    public static final StructurePieceType PIECE_TYPE = StructurePieceType.setPieceId(
            MirrorRuinsPiece::new, new ResourceLocation(MirrorAbyss.MOD_ID, "mirror_ruins_piece"));

    public static final Codec<MirrorRuinsStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StructureSettings.CODEC.forGetter(Structure::settings),
            Codec.list(BlockState.CODEC).fieldOf("palette").forGetter(s -> s.palette),
            Codec.INT.fieldOf("size_x").forGetter(s -> s.sizeX),
            Codec.INT.fieldOf("size_y").forGetter(s -> s.sizeY),
            Codec.INT.fieldOf("size_z").forGetter(s -> s.sizeZ),
            Codec.BOOL.optionalFieldOf("spawn_boss", false).forGetter(s -> s.spawnBoss))
            .apply(instance, MirrorRuinsStructure::new));

    private final List<BlockState> palette;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final boolean spawnBoss;

    public MirrorRuinsStructure(StructureSettings settings, List<BlockState> palette,
                                int sizeX, int sizeY, int sizeZ, boolean spawnBoss) {
        super(settings);
        this.palette = palette;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.spawnBoss = spawnBoss;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, heightmapType(context), builder -> generatePieces(builder, context));
    }

    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        builder.addPiece(new MirrorRuinsPiece(
                chunkPos.getMinBlockX() + 4, chunkPos.getMinBlockZ() + 4,
                palette, sizeX, sizeY, sizeZ, spawnBoss));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.MIRROR_RUINS.get();
    }

    /**
     * The generated piece: fills a box with the palette blocks and optionally
     * spawns the MirrorEater boss (void_sanctum).
     */
    public static class MirrorRuinsPiece extends StructurePiece {

        private final List<BlockState> palette;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;
        private final boolean spawnBoss;

        public MirrorRuinsPiece(int startX, int startZ, List<BlockState> palette,
                                int sizeX, int sizeY, int sizeZ, boolean spawnBoss) {
            super(PIECE_TYPE, 0, new BoundingBox(startX, 64, startZ,
                    startX + sizeX, 64 + sizeY, startZ + sizeZ));
            this.palette = palette;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
            this.spawnBoss = spawnBoss;
        }

        public MirrorRuinsPiece(CompoundTag tag, net.minecraft.core.RegistryAccess registryAccess) {
            super(PIECE_TYPE, tag);
            this.palette = List.of();
            this.sizeX = 0;
            this.sizeY = 0;
            this.sizeZ = 0;
            this.spawnBoss = false;
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            // Skeleton: only the box + type are persisted by the base class.
        }

        @Override
        public void addChildren(StructurePiece parent, StructurePieceAccessor accessor, RandomSource random) {
            // No children in the skeleton.
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                                RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos anchor) {
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

            // Void Sanctum: summon the MirrorEater boss at the arena center.
            if (spawnBoss && level instanceof ServerLevel serverLevel) {
                BlockPos center = new BlockPos((bb.minX() + bb.maxX()) / 2, bb.maxY() + 1,
                        (bb.minZ() + bb.maxZ()) / 2);
                MirrorEater boss = ModEntities.MIRROR_EATER.get().create(serverLevel);
                if (boss != null) {
                    boss.moveTo(center.getX() + 0.5D, center.getY(), center.getZ() + 0.5D, 0.0F, 0.0F);
                    serverLevel.addFreshEntity(boss);
                }
            }
        }
    }
}

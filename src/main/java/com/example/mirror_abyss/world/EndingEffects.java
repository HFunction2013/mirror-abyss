package com.example.mirror_abyss.world;

import com.example.mirror_abyss.advancement.ModAdvancements;
import com.example.mirror_abyss.block.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * EndingEffects - the two Final Mirror endings.
 *
 *   ending 1 (seal / peace): broadcasts the sealing, rewards the player with
 *     a short beacon-grade buff. In production, extend this to suppress all
 *     void_floor damage across the Mirror Abyss (world-level flag).
 *
 *   ending 2 (fuse / chaos): broadcasts the fusion and spawns a ring of
 *     void_floor patches around the player in the overworld - reality is
 *     leaking. In production, extend with a scheduled "void spread" event.
 */
public final class EndingEffects {

    public static final int SEAL = 1;
    public static final int FUSE = 2;

    private EndingEffects() {
    }

    public static void apply(ServerPlayer player, int ending) {
        ServerLevel level = player.serverLevel();

        if (ending == SEAL) {
            broadcast(level, "end.mirror_abyss.sealed", ChatFormatting.AQUA);
            // Skeleton reward: saturation + regeneration so the player survives the void.
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 400, 1));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        } else if (ending == FUSE) {
            broadcast(level, "end.mirror_abyss.fused", ChatFormatting.DARK_PURPLE);
            spawnVoidPatches(level, player.blockPosition());
        } else {
            return; // invalid choice - do nothing
        }

        // Fire the final_decision advancement trigger.
        ModAdvancements.grantFinalDecision(player);
    }

    /** Broadcast a translatable message to all players on the server. */
    private static void broadcast(ServerLevel level, String key, ChatFormatting color) {
        Component message = Component.translatable(key).withStyle(color);
        level.players().forEach(p -> p.displayClientMessage(message, true));
    }

    /** Chaos ending: sprinkle void_floor patches around the player. */
    private static void spawnVoidPatches(ServerLevel level, BlockPos origin) {
        RandomSource random = level.random;
        for (int i = 0; i < 16; i++) {
            BlockPos pos = origin.offset(
                    random.nextInt(21) - 10,
                    random.nextInt(5) - 2,
                    random.nextInt(21) - 10);
            if (level.getBlockState(pos).isAir() && !level.getBlockState(pos.below()).isAir()) {
                level.setBlock(pos, ModBlocks.VOID_FLOOR.get().defaultBlockState(), 3);
            }
        }
    }
}

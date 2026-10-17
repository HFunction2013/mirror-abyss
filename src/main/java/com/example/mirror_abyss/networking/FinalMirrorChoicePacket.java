package com.example.mirror_abyss.networking;

import com.example.mirror_abyss.blockentity.FinalMirrorBE;
import com.example.mirror_abyss.world.EndingEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * FinalMirrorChoicePacket - client -> server.
 *
 * Carries the chosen ending (1 = seal / peace, 2 = fuse / chaos).
 * Server handler:
 *   1. persists the choice into the nearest FinalMirrorBE,
 *   2. applies the ending effects,
 *   3. fires the final_decision advancement trigger.
 */
public class FinalMirrorChoicePacket {

    /** 1 = seal the void (peace), 2 = fuse reality (chaos). */
    private final int ending;

    public FinalMirrorChoicePacket(int ending) {
        this.ending = ending;
    }

    public static void encode(FinalMirrorChoicePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.ending);
    }

    public static FinalMirrorChoicePacket decode(FriendlyByteBuf buf) {
        return new FinalMirrorChoicePacket(buf.readInt());
    }

    public static void handle(FinalMirrorChoicePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                return;
            }
            // Persist the ending on the nearest FinalMirror block entity (within 10 blocks).
            BlockPos center = player.blockPosition();
            BlockPos.betweenClosedStream(center.offset(-10, -5, -10), center.offset(10, 5, 10))
                    .forEach(pos -> {
                        BlockEntity be = player.level().getBlockEntity(pos);
                        if (be instanceof FinalMirrorBE finalMirrorBE) {
                            finalMirrorBE.setEnding(packet.ending);
                        }
                    });
            // Apply the world-level ending effects + advancement.
            EndingEffects.apply(player, packet.ending);
        });
        ctx.setPacketHandled(true);
    }

    public int getEnding() {
        return ending;
    }
}

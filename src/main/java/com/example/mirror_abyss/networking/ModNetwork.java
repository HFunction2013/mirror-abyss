package com.example.mirror_abyss.networking;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simplechan.SimpleChannel;

/**
 * ModNetwork - SimpleChannel for client-server packets.
 *
 * Packets:
 *   id 0: FinalMirrorChoicePacket - the player's ending choice.
 */
public final class ModNetwork {

    public static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MirrorAbyss.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int nextId = 0;

    private ModNetwork() {
    }

    /** Must be called during mod construction (both sides). */
    public static void register() {
        CHANNEL.registerMessage(nextId++, FinalMirrorChoicePacket.class,
                FinalMirrorChoicePacket::encode,
                FinalMirrorChoicePacket::decode,
                FinalMirrorChoicePacket::handle);
        MirrorAbyss.LOGGER.info("[mirror_abyss] Networking registered ({} packet(s)).", nextId);
    }
}

package com.example.mirror_abyss.client;

import com.example.mirror_abyss.MirrorAbyss;
import com.example.mirror_abyss.sound.ModSounds;
import com.example.mirror_abyss.world.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * ClientParticles - ambient effects for the Mirror Abyss dimension.
 *
 * While the player is inside the dimension:
 *  - floating mirror-shard particles (purple PORTAL wisps + bright END_ROD
 *    sparkles) drift around the player;
 *  - a sparse ambient sound loop plays (see assets/mirror_abyss/sounds.json).
 */
@Mod.EventBusSubscriber(modid = MirrorAbyss.MOD_ID, value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientParticles {

    private ClientParticles() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        if (mc.level.dimension() != ModDimensions.MIRROR_ABYSS_LEVEL) {
            return;
        }
        Level level = mc.level;
        Player player = mc.player;

        // Purple wisp - the dominant "broken reflection" particle.
        if (level.random.nextInt(5) == 0) {
            Vec3 p = player.position();
            double x = p.x + (level.random.nextDouble() - 0.5D) * 14.0D;
            double y = p.y + level.random.nextDouble() * 10.0D - 2.0D;
            double z = p.z + (level.random.nextDouble() - 0.5D) * 14.0D;
            level.addParticle(ParticleTypes.PORTAL, x, y, z,
                    (level.random.nextDouble() - 0.5D) * 0.08D,
                    level.random.nextDouble() * 0.04D,
                    (level.random.nextDouble() - 0.5D) * 0.08D);
        }

        // Bright sparkle - mirror shard glitter.
        if (level.random.nextInt(14) == 0) {
            Vec3 p = player.position();
            double x = p.x + (level.random.nextDouble() - 0.5D) * 10.0D;
            double y = p.y + level.random.nextDouble() * 8.0D - 1.0D;
            double z = p.z + (level.random.nextDouble() - 0.5D) * 10.0D;
            level.addParticle(ParticleTypes.END_ROD, x, y, z,
                    0.0D, -0.01D, 0.0D);
        }

        // Sparse ambient sound loop (about once per 30s per player).
        if (level.random.nextInt(600) == 0) {
            player.playSound(ModSounds.MIRROR_AMBIENT.get(), 0.5F, 1.0F);
        }
    }
}

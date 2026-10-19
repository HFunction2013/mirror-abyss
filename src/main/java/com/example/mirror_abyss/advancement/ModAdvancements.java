package com.example.mirror_abyss.advancement;

import com.example.mirror_abyss.MirrorAbyss;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * ModAdvancements - custom advancement criteria triggers.
 *
 * The 7 advancement trees themselves are data-driven JSONs under
 * data/mirror_abyss/advancements/ (see that folder). Two of them need a
 * custom trigger registered in code (1.20.1 has no vanilla CustomTrigger,
 * so both use the shared MirrorTrigger below):
 *
 *   - light_refraction : fired when a mirror-shard light puzzle is solved
 *   - final_decision   : fired when an ending is chosen at the final mirror
 */
public final class ModAdvancements {

    public static final ResourceLocation LIGHT_REFRACTION_ID =
            new ResourceLocation(MirrorAbyss.MOD_ID, "light_refraction");
    public static final ResourceLocation FINAL_DECISION_ID =
            new ResourceLocation(MirrorAbyss.MOD_ID, "final_decision");

    /** Shared no-predicate trigger, one instance per criterion id. */
    public static final MirrorTrigger LIGHT_REFRACTION = new MirrorTrigger(LIGHT_REFRACTION_ID);
    public static final MirrorTrigger FINAL_DECISION = new MirrorTrigger(FINAL_DECISION_ID);

    private ModAdvancements() {
    }

    /** Must be called during mod construction so triggers are registered before datapacks load. */
    public static void init() {
        CriteriaTriggers.register(LIGHT_REFRACTION);
        CriteriaTriggers.register(FINAL_DECISION);
    }

    /** Fire the light_refraction trigger for the nearest player (puzzle solver). */
    public static void grantLightRefraction(ServerLevel level, net.minecraft.core.BlockPos pos) {
        net.minecraft.world.entity.player.Player nearest =
                level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0D, false);
        if (nearest instanceof ServerPlayer serverPlayer) {
            LIGHT_REFRACTION.trigger(serverPlayer);
        }
    }

    /** Fire the final_decision trigger for a specific player. */
    public static void grantFinalDecision(ServerPlayer player) {
        FINAL_DECISION.trigger(player);
    }

    /**
     * Predicate-free criterion trigger: any player that fires it matches.
     */
    public static class MirrorTrigger extends SimpleCriterionTrigger<MirrorTrigger.TriggerInstance> {

        private final ResourceLocation id;

        public MirrorTrigger(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        protected TriggerInstance createInstance(JsonObject json,
                                                 ContextAwarePredicate player,
                                                 DeserializationContext context) {
            return new TriggerInstance(id, player);
        }

        /** Fire the trigger for one player. */
        public void trigger(ServerPlayer player) {
            this.trigger(player, instance -> instance.matches());
        }

        public static class TriggerInstance extends AbstractCriterionTriggerInstance {

            public TriggerInstance(ResourceLocation criterion, ContextAwarePredicate player) {
                super(criterion, player);
            }

            /** Always matches - the trigger is purely a "was fired" marker. */
            public boolean matches() {
                return true;
            }

            @Override
            public JsonObject serializeToJson(SerializationContext context) {
                return super.serializeToJson(context);
            }
        }
    }
}

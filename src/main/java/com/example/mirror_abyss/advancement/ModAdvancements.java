package com.example.mirror_abyss.advancement;

import com.example.mirror_abyss.MirrorAbyss;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.CustomTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * ModAdvancements - custom advancement criteria triggers.
 *
 * The 7 advancement trees themselves are data-driven JSONs under
 * data/mirror_abyss/advancements/ (see that folder). Two of them need a
 * custom trigger registered in code:
 *
 *   - light_refraction : fired when a mirror-shard light puzzle is solved
 *   - final_decision   : fired when an ending is chosen at the final mirror
 */
public final class ModAdvancements {

    public static final ResourceLocation LIGHT_REFRACTION_ID =
            new ResourceLocation(MirrorAbyss.MOD_ID, "light_refraction");
    public static final ResourceLocation FINAL_DECISION_ID =
            new ResourceLocation(MirrorAbyss.MOD_ID, "final_decision");

    /** Custom criterion trigger instance (vanilla CustomTrigger = criteria without predicates). */
    public static final CustomTrigger LIGHT_REFRACTION = new CustomTrigger(LIGHT_REFRACTION_ID);
    public static final CustomTrigger FINAL_DECISION = new CustomTrigger(FINAL_DECISION_ID);

    private ModAdvancements() {
    }

    /** Must be called during mod construction so triggers are registered before datapacks load. */
    public static void init() {
        // CriteriaTriggers#register(CriterionTrigger) returns the same instance.
        CriteriaTriggers.register(LIGHT_REFRACTION);
        CriteriaTriggers.register(FINAL_DECISION);
    }

    /** Fire the light_refraction trigger for the nearest player (puzzle solver). */
    public static void grantLightRefraction(ServerLevel level, net.minecraft.core.BlockPos pos) {
        ServerPlayer nearest = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0D, false);
        if (nearest != null) {
            LIGHT_REFRACTION.trigger(nearest);
        }
    }

    /** Fire the final_decision trigger for a specific player. */
    public static void grantFinalDecision(ServerPlayer player) {
        FINAL_DECISION.trigger(player);
    }

    /** Skeleton only: SimpleCriterionTrigger subclass template if predicates are needed later. */
    @SuppressWarnings("unused")
    private static class ExamplePredicateTrigger extends SimpleCriterionTrigger<SimpleCriterionTrigger.TriggerInstance> {
        private final ResourceLocation id;

        ExamplePredicateTrigger(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        protected SimpleCriterionTrigger.TriggerInstance createInstance(
                com.google.gson.JsonObject json,
                net.minecraft.advancements.critereon.ContextAwarePredicates player) {
            return new SimpleCriterionTrigger.TriggerInstance(id, player);
        }
    }
}

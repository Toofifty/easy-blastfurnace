package com.toofifty.easyblastfurnace.utils;

import com.toofifty.easyblastfurnace.methods.Method;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.util.RSTimeUnit;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

@Slf4j
public class StaminaHelper {

    @Inject
    BlastFurnaceState state;

    @Inject
    Client client;

    @Inject
    MethodHandler methodHandler;

    private Instant staminaEndTime;

    private double lossRateMultiplier;

    private boolean haveLogged;

    public double getEnergyNeededForNextRun()
    {
        // 18 ticks or 10800 milliseconds minimum in one run.
        // On a metal ore/gold/hybrid trip: 9 ticks with ore, 5 ticks without, 4 ticks with bars.
        Method method = methodHandler.getMethod();
        boolean isCoalRunNext = state.getFurnace().isCoalRunNext(method.coalPer());
        int weight = client.getWeight() - getInventoryOresAndBarsWeight();
        int nextOreWeight = (int) Math.round(getWeightOfNextOresOrBarsInInventory(false)) + weight;
        int nextBarWeight = (int) Math.round(getWeightOfNextOresOrBarsInInventory(true)) + weight;
        int ticksSpentIdle = getTicksSpentIdle(isCoalRunNext, method.getName());
        double energyRecovered = getMinimumEnergyRecovered(ticksSpentIdle);

        calculateStaminaDuration(ticksSpentIdle);

        if (!state.getBank().isOpen()) haveLogged = false;

        if (isCoalRunNext) {
            if (state.getBank().isOpen() && !haveLogged) {
                haveLogged = true;
                log.debug("COAL | nextOreWeight: " + nextOreWeight);
                log.debug("staminaDuration: " + Duration.of(10L * client.getVarbitValue(Varbits.STAMINA_EFFECT), RSTimeUnit.GAME_TICKS).toMillis() + " | staminaEndTime: " + (staminaEndTime != null ? Duration.between(Instant.now(), staminaEndTime).toMillis() : 0) + " | lossRateMultiplier: " + lossRateMultiplier);
                log.debug( "Energy: " + client.getEnergy() + " | minEnergyRecovered: " + energyRecovered + " | Energy needed: " + (getLossRate(weight) * 9 + getLossRate(nextOreWeight) * 9 - energyRecovered));
                log.debug("--------------------------------------------------------------------");
            }
            return Math.round(getLossRate(weight) * 9 + getLossRate(nextOreWeight) * 9 - energyRecovered);
        } else {
            if (state.getBank().isOpen() && !haveLogged) {
                haveLogged = true;
                log.debug("BARS | nextOreWeight: " + nextOreWeight + " | nextBarWeight: " + nextBarWeight);
                log.debug("staminaDuration: " + Duration.of(10L * client.getVarbitValue(Varbits.STAMINA_EFFECT), RSTimeUnit.GAME_TICKS).toMillis() + " | staminaEndTime: " + (staminaEndTime != null ? Duration.between(Instant.now(), staminaEndTime).toMillis() : 0) + " | lossRateMultiplier: " + lossRateMultiplier);
                log.debug( "Energy: " + client.getEnergy() + " | energyRecovered: " + energyRecovered + " | Energy needed: " + (getLossRate(nextOreWeight) * 9 + getLossRate(weight) * 5 + getLossRate(nextBarWeight) * 4 - energyRecovered));
                log.debug("--------------------------------------------------------------------");
            }
            return Math.round(getLossRate(nextOreWeight) * 9 + getLossRate(weight) * 5 + getLossRate(nextBarWeight) * 4 - energyRecovered);
        }
    }

    private double getLossRate(int weight)
    {
        return ((Math.min(Math.max(weight, 0), 64) / 100.0) + 0.64) * lossRateMultiplier;
    }

    private double getMinimumEnergyRecovered(int ticksSpentIdle)
    {
        double energyRecoveryPerSecond = ((48 + client.getBoostedSkillLevel(Skill.AGILITY)) / 360.0);

        int boost = 0;

        for (Graceful graceful : Graceful.values()) {
            if (state.getEquipment().equipped(graceful.items)) boost += graceful.boost;
        }

        if (boost == 20) boost += 10; // full graceful bonus

        energyRecoveryPerSecond *= 1.0 + boost / 100.0;

        return energyRecoveryPerSecond * 0.6 * ticksSpentIdle;
    }

    private int getTicksSpentIdle(boolean coalRun, String methodName)
    {
        int ticksSpentIdle = methodName.equals("Gold bars") ? 0 : 2; // 2 ticks to unload coal bag (0 if no coal bag)

        if (!coalRun || methodName.contains("Gold")) {
            ticksSpentIdle++; // 1 tick spent collecting bars
        }

        if (!methodName.equals("Gold bars") && state.getCoalBag().getMaxCoal() > 27) {
            ticksSpentIdle = ticksSpentIdle + 2; // 2 more ticks to unload coal bag
        }

        return ticksSpentIdle;
    }

    private double getWeightOfNextOresOrBarsInInventory(boolean getBars)
    {
        Method method = methodHandler.getMethod();
        String ore = method.getName().toUpperCase().replace("GOLD + ", "").replace("STEEL", "IRON").replace(" BARS", "_ORE");
        String bars = ore.replace("IRON", "STEEL").replace("_ORE", "_BAR");
        double coalRunWeight = getBars ? BarsOres.GOLD_BAR.getWeight() : BarsOres.COAL.getWeight();
        int freeSlots = state.getInventory().getFreeSlots(true);

        if (state.getFurnace().isCoalRunNext(method.coalPer())) {
            freeSlots = Math.min(freeSlots, state.getBank().getQuantity(method.getName().contains("Gold") ? ItemID.GOLD_ORE : ItemID.COAL));
            return coalRunWeight * freeSlots;
        } else {
            freeSlots = Math.min(freeSlots, state.getBank().getQuantity(BarsOres.valueOf(ore).getItemID()));
            return BarsOres.valueOf(getBars ? bars : ore).getWeight() * freeSlots;
        }
    }

    private int getInventoryOresAndBarsWeight() {
        double weight = 0;
        weight += state.getInventory().getQuantity(ItemID.IRON_BAR, ItemID.STEEL_BAR, ItemID.RUNITE_BAR, ItemID.GOLD_BAR) * BarsOres.GOLD_BAR.getWeight();
        weight += state.getInventory().getQuantity(ItemID.IRON_ORE, ItemID.COAL, ItemID.RUNITE_ORE, ItemID.GOLD_ORE) * BarsOres.COAL.getWeight();
        weight += state.getInventory().getQuantity(ItemID.ADAMANTITE_BAR) * BarsOres.ADAMANTITE_BAR.getWeight();
        weight += state.getInventory().getQuantity(ItemID.ADAMANTITE_ORE) * BarsOres.ADAMANTITE_ORE.getWeight();
        weight += state.getInventory().getQuantity(ItemID.MITHRIL_BAR) * BarsOres.MITHRIL_BAR.getWeight();
        weight += state.getInventory().getQuantity(ItemID.MITHRIL_ORE) * BarsOres.MITHRIL_ORE.getWeight();
        return (int) Math.round(weight);
    }

    private void calculateStaminaDuration(int ticksSpentIdle)
    {
        Duration staminaDuration = Duration.of(10L * client.getVarbitValue(Varbits.STAMINA_EFFECT), RSTimeUnit.GAME_TICKS);
        lossRateMultiplier = staminaDuration.isZero() ? 1 : 0.3; // Stamina effect reduces energy depletion to 30%
        int timeForNextRun = 10800 + ticksSpentIdle * 600; // max time for one run is 13800ms

        // This is so we can get an accurate stamina timer value for the last 13800ms (staminaDuration changes in steps of 6000ms)
        if (staminaEndTime == null && !staminaDuration.isZero() && staminaDuration.toMillis() <= 18000) {
            staminaEndTime = Instant.now().plus(staminaDuration);
        } else if (staminaDuration.isZero() || staminaDuration.toMillis() > 18000) {
            staminaEndTime = null;
        }

        // The closer our stamina potion is to finishing, the less overall lossRate reduction it needs for the run.
        long lastMillis = staminaEndTime != null ? Duration.between(Instant.now(), staminaEndTime).toMillis() : 0;
        if (staminaEndTime != null && lastMillis >= 0 && lastMillis <= timeForNextRun) {
            lossRateMultiplier = 1 - (0.7 * lastMillis / timeForNextRun); // max 1, min 0.3
        }

        // todo: && runEnergyPlugin.getRingOfEnduranceCharges() >= 500 once Runelite accepts this PR: https://github.com/runelite/runelite/pull/15621.
        if (state.getEquipment().equipped(ItemID.RING_OF_ENDURANCE)) {
            lossRateMultiplier *= 0.85; // Ring of Endurance passive effect reduces energy depletion to 85%
        }
    }
}

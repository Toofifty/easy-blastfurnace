package com.toofifty.easyblastfurnace.utils;

import com.toofifty.easyblastfurnace.methods.Method;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.RSTimeUnit;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

@Slf4j
public class StaminaHelper {

    @Inject
    private BlastFurnaceState state;

    @Inject
    private Client client;

    @Inject
    private MethodHandler methodHandler;

    @Inject
    private ConfigManager configManager;

    private Instant staminaEndTime;

    private double lossRateMultiplier;

    public double getEnergyNeededForNextRun()
    {
        // 18 ticks or 10800 milliseconds minimum in one run.
        // On a metal ore/gold/hybrid trip: 9 ticks with ore, 5 ticks without, 4 ticks with bars.
        Method method = methodHandler.getMethod();
        int coalPer = CoalPer.getValueFromString(method.getName());
        boolean isCoalRunNext = state.getFurnace().isCoalRunNext(coalPer);
        int weight = client.getWeight() - getInventoryOresAndBarsWeight();
        int nextOreWeight = (int) Math.round(getWeightOfNextOresOrBarsInInventory(false, coalPer)) + weight;
        int nextBarWeight = (int) Math.round(getWeightOfNextOresOrBarsInInventory(true, coalPer)) + weight;
        int ticksSpentIdle = getTicksSpentIdle(isCoalRunNext, method.getName());
        double energyRecovered = getMinimumEnergyRecovered(ticksSpentIdle);

        calculateStaminaDuration(ticksSpentIdle);

        if (isCoalRunNext) {
            return Math.round(getLossRate(weight) * 9 + getLossRate(nextOreWeight) * 9 - energyRecovered);
        } else {
            return Math.round(getLossRate(nextOreWeight) * 9 + getLossRate(weight) * 5 + getLossRate(nextBarWeight) * 4 - energyRecovered);
        }
    }

    private double getLossRate(int weight)
    {
		final int effectiveWeight = Math.min(Math.max(weight, 0), 64);

		int energyUnitsLost = (int) ((60 + (67 * effectiveWeight / 64.0)) * (1 - client.getBoostedSkillLevel(Skill.AGILITY) / 300.0));

		return energyUnitsLost / 100.0 * lossRateMultiplier;
    }

    private double getMinimumEnergyRecovered(int ticksSpentIdle)
    {
		double energyRecoveryPerSecond = (25 + client.getBoostedSkillLevel(Skill.AGILITY) / 6.0) / 100;
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
        int ticksSpentIdle = 4; // 4 ticks for banking
        boolean goldBars = methodName.equals("Gold bars");
		boolean silverBars =  methodName.equals("Silver bars");

        if (!goldBars && !silverBars) ticksSpentIdle += 2; // 2 ticks to unload coal bag
        if (!goldBars && !silverBars && state.getCoalBag().getMaxCoal() > 27) ticksSpentIdle += 2; // 2 more ticks to unload coal bag
        if (!coalRun || methodName.contains("Gold") || methodName.contains("Silver")) ticksSpentIdle++; // 1 tick spent collecting bars

        return ticksSpentIdle;
    }

    private double getWeightOfNextOresOrBarsInInventory(boolean getBars, int coalPer)
    {
        Method method = methodHandler.getMethod();
        String ore = method.getName().toUpperCase().replace("GOLD + ", "").replace("SILVER + ", "").replace("STEEL", "IRON").replace(" BARS", "_ORE");
        String bars = ore.replace("IRON", "STEEL").replace("_ORE", "_BAR");
        double coalRunWeight = getBars ? BarsOres.GOLD_BAR.getWeight() : BarsOres.COAL.getWeight();
        int freeSlots = state.getInventory().getFreeSlotsIncludingOresAndBars();

        if (state.getFurnace().isCoalRunNext(coalPer)) {
            freeSlots = Math.min(freeSlots, state.getBank().getQuantity(method.getName().contains("Gold") ? ItemID.GOLD_ORE : method.getName().contains("Silver") ? ItemID.SILVER_ORE : ItemID.COAL));
            return coalRunWeight * freeSlots;
        } else {
            freeSlots = Math.min(freeSlots, state.getBank().getQuantity(BarsOres.valueOf(ore).getItemID()));
            return BarsOres.valueOf(getBars ? bars : ore).getWeight() * freeSlots;
        }
    }

    private int getInventoryOresAndBarsWeight() {
        double weight = 0;
        weight += state.getInventory().getQuantity(ItemID.IRON_BAR, ItemID.SILVER_BAR, ItemID.STEEL_BAR, ItemID.RUNITE_BAR, ItemID.GOLD_BAR) * BarsOres.GOLD_BAR.getWeight();
        weight += state.getInventory().getQuantity(ItemID.IRON_ORE, ItemID.SILVER_ORE, ItemID.COAL, ItemID.RUNITE_ORE, ItemID.GOLD_ORE) * BarsOres.COAL.getWeight();
        weight += state.getInventory().getQuantity(ItemID.ADAMANTITE_BAR) * BarsOres.ADAMANTITE_BAR.getWeight();
        weight += state.getInventory().getQuantity(ItemID.ADAMANTITE_ORE) * BarsOres.ADAMANTITE_ORE.getWeight();
        weight += state.getInventory().getQuantity(ItemID.MITHRIL_BAR) * BarsOres.MITHRIL_BAR.getWeight();
        weight += state.getInventory().getQuantity(ItemID.MITHRIL_ORE) * BarsOres.MITHRIL_ORE.getWeight();
        return (int) Math.round(weight);
    }

    private void calculateStaminaDuration(int ticksSpentIdle)
    {
        Duration staminaDuration = Duration.of(10L * client.getVarbitValue(VarbitID.STAMINA_DURATION), RSTimeUnit.GAME_TICKS);
        double baseDrain = isWearingSufficientlyChargedRingOfEndurance() ? 0.85 : 1; // ROE reduces energy depletion to 85% when no stamina potion is active
        lossRateMultiplier = staminaDuration.isZero() ? baseDrain : 0.3; // Stamina effect reduces energy depletion to 30%
        int timeForNextRun = 10800 + ticksSpentIdle * 600;

        // This is so we can get an accurate stamina timer value for the last seconds of potion (staminaDuration changes in steps of 6000ms)
        if (staminaEndTime == null && !staminaDuration.isZero() && staminaDuration.toMillis() <= 18000) {
            staminaEndTime = Instant.now().plus(staminaDuration);
        } else if (staminaDuration.isZero() || staminaDuration.toMillis() > 18000) {
            staminaEndTime = null;
        }

        // The closer our stamina potion is to finishing, the less overall lossRate reduction it provides for the run.
        long lastMillis = staminaEndTime != null ? Duration.between(Instant.now(), staminaEndTime).toMillis() : 0;
        if (staminaEndTime != null && lastMillis >= 0 && lastMillis <= timeForNextRun) {
            lossRateMultiplier = baseDrain - ((baseDrain - 0.3) * lastMillis / timeForNextRun); // max baseDrain, min 0.3
        }
    }

    private boolean isWearingSufficientlyChargedRingOfEndurance()
    {
        Integer charges = configManager.getRSProfileConfiguration("runenergy", "ringOfEnduranceCharges", Integer.class);
        return (charges != null && charges >= 500 && state.getEquipment().equipped(ItemID.RING_OF_ENDURANCE));
    }
}

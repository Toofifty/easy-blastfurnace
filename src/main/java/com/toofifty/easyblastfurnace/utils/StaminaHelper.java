package com.toofifty.easyblastfurnace.utils;

import com.toofifty.easyblastfurnace.methods.Method;
import com.toofifty.easyblastfurnace.state.BankState;
import com.toofifty.easyblastfurnace.state.EquipmentState;
import com.toofifty.easyblastfurnace.state.FurnaceState;
import com.toofifty.easyblastfurnace.state.InventoryState;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.util.RSTimeUnit;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
@Slf4j
public class StaminaHelper {

    @Inject
    BankState bank;
    @Inject
    Client client;

    @Inject
    EquipmentState equipment;

    @Inject
    InventoryState inventory;

    @Inject
    MethodHandler methodHandler;

    @Inject
    FurnaceState furnace;

    private Instant staminaEndTime;

    public double getEnergyNeededForNextRun()
    {
        // 18 ticks or 10800 milliseconds minimum in one run.
        // On a metal ore/gold/hybrid trip: 9 ticks with ore, 5 ticks without, 4 ticks with bars.
        Method method = methodHandler.getMethod();
        int weight = client.getWeight() - getInventoryBarWeight() - getInventoryOreWeight();
        int nextOreWeight = (int) Math.round(getWeightOfNextOresInInventory()) + weight;
        int nextBarWeight = (int) Math.round(getWeightOfNextBarsInInventory()) + weight;
        int staminaPotionEffectVarb = client.getVarbitValue(Varbits.STAMINA_EFFECT);
        final Duration staminaDuration = Duration.of(10L * staminaPotionEffectVarb, RSTimeUnit.GAME_TICKS);

        log.info("nextOreWeight: " + nextOreWeight + " | nextBarWeight: " + nextBarWeight);

        // This is so we can have an accurate count for the stamina timer during the potion's last 12 seconds.
        if (staminaEndTime == null && !staminaDuration.isZero() && staminaDuration.toMillis() <= 12000) {
            staminaEndTime = Instant.now().plus(staminaDuration);
        } else if (staminaDuration.isZero() || staminaDuration.toMillis() > 12000) {
            staminaEndTime = null;
        }

        if (furnace.isCoalRun(method.coalPer())) {
            if (inventory.getFreeSlots(true) > 26) log.info("COAL: energy: " + client.getEnergy() + " | energyNeeded: " + (getLossRate(weight) * 9 + getLossRate(nextOreWeight) * 9));
            return Math.round(getLossRate(weight) * 9 + getLossRate(nextOreWeight) * 9);
        } else {
            if (inventory.getFreeSlots(true) > 26) log.info("BARS: energy: " + client.getEnergy() + " | energyNeeded: " + (getLossRate(nextOreWeight) * 9 + getLossRate(weight) * 5 + getLossRate(nextBarWeight) * 4));
            return Math.round(getLossRate(nextOreWeight) * 9 + getLossRate(weight) * 5 + getLossRate(nextBarWeight) * 4);
        }
    }

    private double getLossRate(int weight)
    {
        double multiplier = 0.3; // Stamina effect reduces energy depletion to 30%

        // The closer our stamina potion is to finishing, the less overall lossRate reduction it needs for the run.
        long lastMillis = staminaEndTime != null ? Duration.between(Instant.now(), staminaEndTime).toMillis() : 0;
        if (lastMillis <= 10800) {
            multiplier = 1 - (0.7 * Math.sqrt(lastMillis / 10800d)); // max 1, min 0.3
        }

        // todo: && runEnergyPlugin.getRingOfEnduranceCharges() once Runelite accepts this PR: https://github.com/runelite/runelite/pull/15621.
        if (equipment.equipped(ItemID.RING_OF_ENDURANCE)) {
            multiplier *= 0.85; // Ring of Endurance passive effect reduces energy depletion to 85%
        }

        return ((Math.min(Math.max(weight, 0), 64) / 100.0) + 0.64) * multiplier;
    }

    private double getWeightOfNextOresInInventory()
    {
        Method method = methodHandler.getMethod();
        String ore = method.getName().toUpperCase().replace("GOLD + ", "").replace("STEEL", "IRON").replace(" BARS", "_ORE");
        boolean isCoalRun = furnace.isCoalRun(method.coalPer());
        int oreSlots = inventory.getFreeSlots(true);

        // Adjust free slots based on ore availability in bank
        if (isCoalRun) {
            oreSlots = Math.min(oreSlots, bank.getQuantity(method.getName().contains("Gold") ? ItemID.GOLD_ORE : ItemID.COAL));
            return BarsOres.COAL.getWeight() * oreSlots;
        } else {
            oreSlots = Math.min(oreSlots, bank.getQuantity(BarsOres.valueOf(ore).getItemID()));
            return BarsOres.valueOf(ore).getWeight() * oreSlots;
        }
    }

    private double getWeightOfNextBarsInInventory()
    {
        Method method = methodHandler.getMethod();
        String bar = method.getName().toUpperCase().replace("GOLD + ", "").replace(" BARS", "_BAR");
        boolean isCoalRun = furnace.isCoalRun(method.coalPer());
        int barSlots = inventory.getFreeSlots(true);

        // Adjust free slots based on ore availability in bank
        if (isCoalRun && method.getName().contains("Gold")) {
            barSlots = Math.min(barSlots, bank.getQuantity(ItemID.GOLD_ORE));
            return BarsOres.GOLD_BAR.getWeight() * barSlots;
        } else if (isCoalRun) {
            return 0;
        } else {
            barSlots = Math.min(barSlots, bank.getQuantity(BarsOres.valueOf(bar).getItemID()));
            return BarsOres.valueOf(bar).getWeight() * barSlots;
        }
    }

    private int getInventoryBarWeight() {
        double weight = 0;
        weight += inventory.getQuantity(ItemID.STEEL_BAR, ItemID.RUNITE_BAR, ItemID.GOLD_BAR) * BarsOres.GOLD_BAR.getWeight();
        weight += inventory.getQuantity(ItemID.ADAMANTITE_BAR, ItemID.IRON_BAR) * BarsOres.IRON_BAR.getWeight();
        weight += inventory.getQuantity(ItemID.MITHRIL_BAR) * BarsOres.MITHRIL_BAR.getWeight();
        return (int) Math.round(weight);
    }

    private int getInventoryOreWeight() {
        double weight = 0;
        weight += inventory.getQuantity(ItemID.IRON_ORE, ItemID.COAL, ItemID.RUNITE_ORE, ItemID.GOLD_ORE) * BarsOres.COAL.getWeight();
        weight += inventory.getQuantity(ItemID.ADAMANTITE_ORE) * BarsOres.ADAMANTITE_ORE.getWeight();
        weight += inventory.getQuantity(ItemID.MITHRIL_ORE) * BarsOres.MITHRIL_ORE.getWeight();
        return (int) Math.round(weight);
    }
}

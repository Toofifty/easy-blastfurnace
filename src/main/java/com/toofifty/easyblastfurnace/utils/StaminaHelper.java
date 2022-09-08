package com.toofifty.easyblastfurnace.utils;

import com.toofifty.easyblastfurnace.methods.Method;
import com.toofifty.easyblastfurnace.state.BankState;
import com.toofifty.easyblastfurnace.state.EquipmentState;
import com.toofifty.easyblastfurnace.state.FurnaceState;
import com.toofifty.easyblastfurnace.state.InventoryState;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.util.RSTimeUnit;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

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

    public double getEnergyNeeded() {
        // Calculate the amount of energy lost every tick for the next run ahead of time.
        // On a coal trip: 9 ticks with ore, 9 ticks without. 18 ticks or 10800 milliseconds.
        // On a metal ore/gold/hybrid trip: 9 ticks with ore, 5 ticks without, 4 ticks with bars.
        Method method = methodHandler.getMethod();
        int inventoryBarsWeight = (int) Math.round(getWeightOfBarsInInventory());
        int nextOreWeight = (int) Math.round(getWeightOfNextOresInInventory());
        int staminaPotionEffectVarb = client.getVarbitValue(Varbits.STAMINA_EFFECT);
        final int effectiveWeight = client.getWeight() - inventoryBarsWeight;
        double lossRate = (Math.min(Math.max(effectiveWeight, 0), 64) / 100.0) + 0.64; // energy lost each tick
        double lossRateOres = (Math.min(Math.max(effectiveWeight + nextOreWeight, 0), 64) / 100.0) + 0.64;
        double lossRateBars = (Math.min(Math.max(effectiveWeight + inventoryBarsWeight, 0), 64) / 100.0) + 0.64;
        final Duration staminaDuration = Duration.of(10L * staminaPotionEffectVarb, RSTimeUnit.GAME_TICKS);
        double offset = staminaDuration.isZero() ? 1 : 0.3; // Stamina effect reduces energy depletion to 30%

        // This is so we can have an accurate count for the stamina timer during the potion's last 12 seconds.
        if (staminaEndTime == null && !staminaDuration.isZero() && staminaDuration.toMillis() <= 12000) {
            staminaEndTime = Instant.now().plus(staminaDuration);
        } else if (staminaDuration.isZero() || staminaDuration.toMillis() > 12000) {
            staminaEndTime = null;
        }

        // The closer our stamina potion is to finishing, the less lossRate reduction it will give.
        long lastMillis = staminaEndTime != null ? Duration.between(Instant.now(), staminaEndTime).toMillis() : 0;
        if (lastMillis > 0 && lastMillis <= 10800) {
            offset = (0.7 - (0.7 * lastMillis / 10800)) + 0.3;
        }

        // todo: && runEnergyPlugin.getRingOfEnduranceCharges() once Runelite accepts this PR: https://github.com/runelite/runelite/pull/15621.
        if (equipment.equipped(ItemID.RING_OF_ENDURANCE)) {
            lossRate *= 0.85; // Ring of Endurance passive effect reduces energy depletion to 85%
            lossRateOres *= 0.85;
            lossRateBars *= 0.85;
        }

        lossRate *= offset;
        lossRateOres *= offset;
        lossRateBars *= offset;

        if (furnace.isCoalRun(method.coalPer())) {
            return Math.ceil(lossRate * 9 + lossRateOres * 9);
        } else {
            return Math.ceil(lossRateOres * 9 + lossRate * 5 + lossRateBars * 4);
        }
    }

    private double getWeightOfNextOresInInventory()
    {
        Method method = methodHandler.getMethod();
        String ores = method.getName().toLowerCase().replace(" bars", "");
        boolean isCoalRun = furnace.isCoalRun(method.coalPer());
        double coalWeight = BarsOres.COAL.getWeight();
        int oreSlots = inventory.getFreeSlots(true);
        double weight = 0;

        // Adjust free slots based on ore availability in bank
        if (isCoalRun) {
            oreSlots = Math.min(oreSlots, bank.getQuantity(ores.equals("gold") ? ItemID.GOLD_ORE : ItemID.COAL));
        }

        // Get correct weight and number of ores.
        switch(ores) {
            case "gold":
            case "steel":
            case "runite":
            case "gold + runite":
                int itemId = ores.equals("runite") ? ItemID.RUNITE_ORE : ores.equals("steel") ? ItemID.IRON_ORE : ItemID.GOLD_ORE;
                oreSlots = Math.min(oreSlots, bank.getQuantity(itemId));
                weight = (coalWeight * oreSlots); // Ores are the same weight as coal, so coalWeight is always right.
                break;
            case "mithril":
            case "gold + mithril":
                if (!isCoalRun) oreSlots = Math.min(oreSlots, bank.getQuantity(ItemID.MITHRIL_ORE));
                weight = (isCoalRun ? coalWeight : BarsOres.MITHRIL_ORE.getWeight()) * oreSlots;
                break;
            case "adamantite":
            case "gold + adamantite":
                if (!isCoalRun) oreSlots = Math.min(oreSlots, bank.getQuantity(ItemID.ADAMANTITE_ORE));
                weight = (isCoalRun ? coalWeight : BarsOres.ADAMANTITE_ORE.getWeight()) * oreSlots;
                break;
        }
        return weight;
    }

    private double getWeightOfBarsInInventory() {
        double weight = 0;
        weight += inventory.getQuantity(ItemID.STEEL_BAR, ItemID.RUNITE_BAR, ItemID.GOLD_BAR) * BarsOres.GOLD_BAR.getWeight();
        weight += inventory.getQuantity(ItemID.ADAMANTITE_BAR, ItemID.IRON_BAR) * BarsOres.IRON_BAR.getWeight();
        weight += inventory.getQuantity(ItemID.MITHRIL_BAR) * BarsOres.MITHRIL_BAR.getWeight();
        return weight;
    }
}

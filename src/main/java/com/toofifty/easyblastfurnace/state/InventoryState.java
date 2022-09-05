package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.methods.Method;
import com.toofifty.easyblastfurnace.utils.BarsOres;
import com.toofifty.easyblastfurnace.utils.MethodHandler;
import net.runelite.api.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
public class InventoryState
{
    @Inject
    private Client client;

    @Inject
    private FurnaceState furnace;

    @Inject
    private MethodHandler methodHandler;

    @Inject
    private BankState bank;

    private ItemContainer inventory;

    private Item[] previousInventory = new Item[]{};

    private void load()
    {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory != null) {
            this.inventory = inventory;
        }
    }

    private int getPreviousQuantity(int itemId)
    {
        Optional<Item> item = Arrays.stream(previousInventory).filter(i -> i.getId() == itemId).findFirst();

        return item.map(Item::getQuantity).orElse(0);
    }

    public void update()
    {
        load();

        if (inventory != null) {
            previousInventory = inventory.getItems().clone();
        }
    }

    public int getChange(int itemId)
    {
        return getQuantity(itemId) - getPreviousQuantity(itemId);
    }

    public boolean hasChanged(int itemId)
    {
        return getChange(itemId) != 0;
    }

    public int getQuantity(int ...itemIds)
    {
        load();
        int total = 0;

        for (int itemId : itemIds) {
            total += inventory.count(itemId);
        }

        return total;
    }

    public boolean has(int ...itemIds) { return getQuantity(itemIds) > 0; }

    public int getFreeSlots(boolean ignoreBars)
    {
        load();

        int freeSlots = 28;
        int[] bars = new int[]{
            ItemID.IRON_BAR, ItemID.STEEL_BAR, ItemID.MITHRIL_BAR,
            ItemID.ADAMANTITE_BAR, ItemID.RUNITE_BAR, ItemID.GOLD_BAR
        };

        for (Item item : inventory.getItems()) {
            if (ignoreBars && IntStream.of(bars).noneMatch(id -> id == item.getId()) && item.getQuantity() > 0) {
                freeSlots--;
            } else if (!ignoreBars && item.getQuantity() > 0) {
                freeSlots--;
            }
        }
        return freeSlots;
    }

    public boolean hasFreeSlots()
    {
        return getFreeSlots(false) > 0;
    }

    public double getWeightOfNextOresInInventory()
    {
        Method method = methodHandler.getMethod();
        String ores = method.getName().toLowerCase().replace(" bars", "");
        boolean isCoalRun = furnace.isCoalRun(method.coalPer());
        double coalWeight = BarsOres.COAL.getWeight();
        int oreSlots = getFreeSlots(true);
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

    public double getWeightOfBarsInInventory() {
        double weight = 0;
        weight += getQuantity(ItemID.STEEL_BAR, ItemID.RUNITE_BAR, ItemID.GOLD_BAR) * BarsOres.GOLD_BAR.getWeight();
        weight += getQuantity(ItemID.ADAMANTITE_BAR, ItemID.IRON_BAR) * BarsOres.IRON_BAR.getWeight();
        weight += getQuantity(ItemID.MITHRIL_BAR) * BarsOres.MITHRIL_BAR.getWeight();
        return weight;
    }
}

package com.toofifty.easyblastfurnace.state;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class InventoryState
{
    @Inject
    private Client client;

    private ItemContainer inventory;

    private Item[] previousInventory = new Item[]{};

    private void load()
    {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory != null) {
            this.inventory = inventory;
        }
    }

    private int getPreviousQuantity(int ...itemIds)
    {
        int total = 0;

        for (int itemId : itemIds) {
            Optional<Item> item = Arrays.stream(previousInventory).filter(i -> i.getId() == itemId).findFirst();
            total += item.map(Item::getQuantity).orElse(0);
        }

        return total;
    }

    public void update()
    {
        load();

        if (inventory != null) {
            previousInventory = inventory.getItems().clone();
        }
    }

    public int getChange(int ...itemIds)
    {
        if (inventory == null) return 0;

        int totalChange = 0;

        for (int itemId : itemIds) {
            totalChange += getQuantity(itemId) - getPreviousQuantity(itemId);
        }

        return totalChange;
    }

    public boolean hasChanged(int ...itemIds)
    {
        return getChange(itemIds) != 0;
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

    public boolean has(int ...itemIds) {
        return getQuantity(itemIds) > 0;
    }

    public int getFreeSlots()
    {
        load();

        int freeSlots = 28;
        for (Item item : inventory.getItems()) {
            if (item.getQuantity() > 0) {
                freeSlots--;
            }
        }
        return freeSlots;
    }


    public int getFreeSlotsIncludingOresAndBars()
    {
        load();

        int freeSlots = 28;
        int[] barsAndOres = new int[]{
            ItemID.IRON_BAR, ItemID.MITHRIL_BAR, ItemID.ADAMANTITE_BAR, ItemID.RUNITE_BAR, ItemID.GOLD_BAR, ItemID.STEEL_BAR,
            ItemID.IRON_ORE, ItemID.MITHRIL_ORE, ItemID.ADAMANTITE_ORE, ItemID.RUNITE_ORE, ItemID.GOLD_ORE, ItemID.COAL
        };

        for (Item item : inventory.getItems()) {
            if (IntStream.of(barsAndOres).noneMatch(id -> id == item.getId()) && item.getQuantity() > 0) {
                freeSlots--;
            }
        }
        return freeSlots;
    }

    public boolean hasFreeSlots()
    {
        return getFreeSlots() > 0;
    }
}

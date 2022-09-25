package com.toofifty.easyblastfurnace.state;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;

import javax.inject.Inject;
public class EquipmentState
{
    @Inject
    private Client client;

    private ItemContainer equipment;

    private void load()
    {
        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
        if (equipment != null) {
            this.equipment = equipment;
        }
    }

    public boolean equipped(int ...itemIds)
    {
        load();
        if (equipment == null) return false;

        int total = 0;
        if (equipment == null) return false;

        for (int itemId : itemIds) {
            total += equipment.count(itemId);
        }

        return total > 0;
    }

    public boolean hasGoldsmithEffect()
    {
        return equipped(ItemID.GOLDSMITH_GAUNTLETS, ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, ItemID.MAX_CAPE);
    }
}

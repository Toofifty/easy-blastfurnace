package com.toofifty.easyblastfurnace.state;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;

public class BankState
{
    @Inject
    private Client client;

    private ItemContainer bank;

    private void load()
    {
        ItemContainer bank = client.getItemContainer(InventoryID.BANK);
        if (bank != null) {
            this.bank = bank;
        }
    }

    public boolean isOpen()
    {
        Widget bankContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
        return bankContainer != null && !bankContainer.isHidden();
    }

    public int getQuantity(int ...itemIds)
    {
        load();
        int total = 0;

        if (bank == null) return 0;

        for (int itemId : itemIds) {
            total += bank.count(itemId);
        }

        return total;
    }

    public boolean has(int ...itemIds) { return getQuantity(itemIds) > 0; }
}

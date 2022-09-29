package com.toofifty.easyblastfurnace.state;

import lombok.Getter;
import net.runelite.api.ItemID;

import javax.inject.Inject;

public class CoalBagState
{
    private static final int MIN_COAL = 0;

    @Inject
    private InventoryState inventory;

    @Inject
    private BankState bank;

    @Getter
    private int coal;

    @Getter
    private int maxCoal = 27;

    @Getter
    private int oreOntoConveyorCount = 0;

    public void setMaxCoal(int quantity)
    {
        maxCoal = quantity;
    }

    public void setCoal(int quantity)
    {
        coal = Math.min(Math.max(quantity, MIN_COAL), maxCoal);
    }

    public boolean isEmpty()
    {
        return coal == MIN_COAL;
    }

    public boolean isFull()
    {
        return coal == maxCoal;
    }

    public void empty()
    {
        if (bank.isOpen()) {
            setCoal(MIN_COAL);
            return;
        }

        oreOntoConveyorCount = 0;
        setCoal(coal - inventory.getFreeSlots(false));
    }

    public void fill()
    {
        if (bank.isOpen()) {
            setCoal(maxCoal);
            return;
        }
        oreOntoConveyorCount = 0;
        setCoal(coal + inventory.getQuantity(ItemID.COAL));
    }

    public void oreOntoConveyor(int ...override)
    {
        if (override.length > 0) {
            oreOntoConveyorCount = override[0];
        } else {
            oreOntoConveyorCount++;
        }
    }
}

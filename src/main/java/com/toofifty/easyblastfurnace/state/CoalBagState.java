package com.toofifty.easyblastfurnace.state;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.gameval.ItemID;

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

    @Setter
	@Getter
    private int maxCoal = 27;

    public boolean recentlyEmptiedCoalBag = false;

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
        recentlyEmptiedCoalBag = true;
        setCoal(coal - inventory.getFreeSlots());
    }

    public void fill()
    {
        if (bank.isOpen()) {
            setCoal(maxCoal);
            return;
        }
        setCoal(coal + inventory.getQuantity(ItemID.COAL));
    }
}

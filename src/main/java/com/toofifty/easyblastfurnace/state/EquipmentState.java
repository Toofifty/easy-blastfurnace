package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.utils.Equipment;
import net.runelite.api.Client;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.ItemContainer;

import javax.inject.Inject;

public class EquipmentState
{
    @Inject
    private Client client;

	@Inject
	private EasyBlastFurnaceConfig config;

    private ItemContainer equipment;

    private void load()
    {
        ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
        if (equipment != null) {
            this.equipment = equipment;
        }
    }

    public boolean equipped(int ...itemIds)
    {
        load();
        if (equipment == null) return false;

        for (int itemId : itemIds) {
            if (equipment.count(itemId) > 0) return true;
        }

        return false;
    }

    public boolean hasGoldsmithEffect()
    {
        return equipped(Equipment.merge(Equipment.GOLDSMITH.items, Equipment.MAX_CAPE.items, Equipment.SMITHING_CAPE.items));
    }

    public boolean hasIceGlovesEffect()
    {
        return equipped(Equipment.ICE_GLOVES.items);
    }
}

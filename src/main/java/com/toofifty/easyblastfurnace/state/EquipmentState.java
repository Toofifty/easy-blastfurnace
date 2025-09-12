package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import net.runelite.api.Client;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;

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
        int total = 0;
        if (equipment == null) return false;

        for (int itemId : itemIds) {
            total += equipment.count(itemId);
        }

        return total > 0;
    }

    public boolean hasGoldsmithEffect()
    {
        return equipped(ItemID.GAUNTLETS_OF_GOLDSMITHING, ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_TRIMMED, ItemID.SKILLCAPE_MAX);
    }

    public boolean hasIceGlovesEffect()
    {
        return equipped(ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE);
    }
}

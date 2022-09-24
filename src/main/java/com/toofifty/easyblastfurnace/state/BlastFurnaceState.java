package com.toofifty.easyblastfurnace.state;

import lombok.Getter;
import net.runelite.api.ItemID;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class BlastFurnaceState
{
    @Inject
    private CoalBagState coalBag;

    @Inject
    private InventoryState inventory;

    @Inject
    private EquipmentState equipment;

    @Inject
    private PlayerState player;

    @Inject
    private FurnaceState furnace;

    @Inject
    private BankState bank;

    public void update()
    {
        if (player.isAtConveyorBelt() &&
            (inventory.hasChanged(ItemID.GOLD_ORE) ||
                inventory.hasChanged(ItemID.IRON_ORE) ||
                inventory.hasChanged(ItemID.MITHRIL_ORE) ||
                inventory.hasChanged(ItemID.ADAMANTITE_ORE) ||
                inventory.hasChanged(ItemID.RUNITE_ORE))) {
            player.hasLoadedOres(true);
        }

        if (furnace.has(ItemID.GOLD_BAR, ItemID.STEEL_BAR, ItemID.MITHRIL_BAR, ItemID.ADAMANTITE_BAR, ItemID.RUNITE_BAR)) {
            player.hasLoadedOres(false);
        }

        if (equipment.equipped(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, ItemID.MAX_CAPE)) {
            coalBag.setMaxCoal(36);
        } else {
            coalBag.setMaxCoal(27);
        }

        inventory.update();
        furnace.update();
    }
}

package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
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

    @Inject
    private EasyBlastFurnaceConfig config;

    public void update()
    {
        int invChange = inventory.getChange(ItemID.GOLD_ORE, ItemID.IRON_ORE, ItemID.MITHRIL_ORE, ItemID.ADAMANTITE_ORE, ItemID.RUNITE_ORE);

        if (player.isAtConveyorBelt() && invChange > 0) {
            furnace.setOresOnConveyorBelt(invChange);
            player.hasOreOnConveyor(true);
        }

        if (equipment.equipped(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, ItemID.MAX_CAPE)) {
            coalBag.setMaxCoal(36);
        } else {
            coalBag.setMaxCoal(27);
        }

        if (coalBag.getOreOntoConveyorCount() > 0 && bank.isOpen() && inventory.hasChanged(ItemID.COAL) && inventory.has(ItemID.COAL)) {
            coalBag.oreOntoConveyor(0);
        }

        inventory.update();
        furnace.update();
    }
}

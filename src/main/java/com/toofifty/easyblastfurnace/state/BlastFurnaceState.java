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
        if (player.isAtConveyorBelt() &&
            (inventory.hasChanged(ItemID.GOLD_ORE) ||
                inventory.hasChanged(ItemID.IRON_ORE) ||
                inventory.hasChanged(ItemID.MITHRIL_ORE) ||
                inventory.hasChanged(ItemID.ADAMANTITE_ORE) ||
                inventory.hasChanged(ItemID.RUNITE_ORE))) {
            player.hasLoadedOres(true);
        }

		if (config.tickPerfectMethod()) {
			if (furnace.hasInventory(ItemID.GOLD_BAR, ItemID.STEEL_BAR, ItemID.MITHRIL_BAR, ItemID.ADAMANTITE_BAR, ItemID.RUNITE_BAR)) {
				player.hasLoadedOres(false);
			}
		}
		else if (!config.tickPerfectMethod()) {
			if (furnace.has(ItemID.GOLD_BAR, ItemID.STEEL_BAR, ItemID.MITHRIL_BAR, ItemID.ADAMANTITE_BAR, ItemID.RUNITE_BAR)) {
				player.hasLoadedOres(false);
			}
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

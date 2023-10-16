package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
@Slf4j
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

    private int lastPositiveChange = 0;
    public void update()
    {
        int invChange = inventory.getChange(ItemID.GOLD_ORE, ItemID.IRON_ORE, ItemID.MITHRIL_ORE, ItemID.ADAMANTITE_ORE, ItemID.RUNITE_ORE);

        if (invChange > 0) {
            lastPositiveChange = invChange;
        }

        if (player.isAtConveyorBelt() && invChange == -1) { // invChange is always -1 when adding ores to the conveyor belt.
            furnace.setOresOnConveyorBelt(lastPositiveChange);
            lastPositiveChange = 0;
            player.hasOreOnConveyor(true);
        }

        if (equipment.equipped(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, ItemID.MAX_CAPE)) {
            coalBag.setMaxCoal(36);
        } else {
            coalBag.setMaxCoal(27);
        }

        inventory.update();
        furnace.update();
        log.info("Gold ore in furnace: " + furnace.getQuantity(ItemID.GOLD_ORE));
        log.info("coal in furnace: " + furnace.getQuantity(ItemID.COAL));
        log.info("adamantite ore in furnace: " + furnace.getQuantity(ItemID.ADAMANTITE_ORE));
    }
}

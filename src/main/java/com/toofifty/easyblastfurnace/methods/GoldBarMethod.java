package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Equipment;
import com.toofifty.easyblastfurnace.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;

@Slf4j
public class GoldBarMethod extends Method
{
    private MethodStep[] checkPrerequisite(BlastFurnaceState state, boolean hasGoldsmithEquipment)
    {
        // ensure player has both ice gloves & goldsmith gauntlets either in inventory or equipped

        if (!state.getInventory().has(Equipment.ICE_GLOVES.items) && !state.getEquipment().hasIceGlovesEffect()) {
            return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
        }

        if (state.getBank().has(Equipment.MAX_CAPE.items) &&
            !state.getInventory().has(Equipment.MAX_CAPE.items) &&
            !state.getEquipment().equipped(Equipment.MAX_CAPE.items)) {
            return state.getBank().isOpen() ? withdrawMaxCape : openBank;
        }

        if (state.getInventory().has(Equipment.MAX_CAPE.items) &&
            !state.getEquipment().equipped(Equipment.MAX_CAPE.items)) {
            return equipMaxCape;
        }

        if (state.getBank().has(Equipment.SMITHING_CAPE.items) &&
            !state.getInventory().has(Equipment.SMITHING_CAPE.items) &&
            !state.getEquipment().equipped(Equipment.merge(Equipment.SMITHING_CAPE.items, Equipment.MAX_CAPE.items))) {
            return state.getBank().isOpen() ? withdrawSmithingCape : openBank;
        }

        if (state.getInventory().has(Equipment.SMITHING_CAPE.items) &&
            !state.getEquipment().equipped(Equipment.merge(Equipment.SMITHING_CAPE.items, Equipment.MAX_CAPE.items))) {
            return equipSmithingCape;
        }

        if (hasGoldsmithEquipment && !state.getInventory().has(Equipment.GOLDSMITH.items) && !state.getEquipment().hasGoldsmithEffect()) {
            return state.getBank().isOpen() ? withdrawGoldsmithGauntlets : openBank;
        }

        return null;
    }

    @Override
    public MethodStep[] next(BlastFurnaceState state)
    {
        boolean hasGoldsmithEquipment = Equipment.hasGoldsmithEquipment(state);
        MethodStep[] prerequisite = checkPrerequisite(state, hasGoldsmithEquipment);
        if (prerequisite != null) return prerequisite;
        boolean oreOnConveyor = state.getPlayer().hasOreOnConveyor();
        boolean furnaceHasBar = state.getFurnace().has(ItemID.GOLD_BAR);
        boolean furnaceHasOre = state.getFurnace().has(ItemID.GOLD_ORE);
        boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();
        boolean atBarDispenser = state.getPlayer().isAtBarDispenser();
        boolean atConveyorBelt = state.getPlayer().isAtConveyorBelt();
        boolean useDepositInventory = state.getConfig().useDepositInventory();

        if (state.getBank().isOpen()) {
            if (furnaceHasOre && furnaceHasBar || (!tickPerfectMethod && furnaceHasBar)) {
                if (state.getInventory().has(ItemID.GOLD_BAR, ItemID.GOLD_ORE)) {
                    return useDepositInventory ? depositInventory : depositBarsAndOres;
                }
                return collectBars;
            }

            if (state.getInventory().has(ItemID.GOLD_BAR)) {
                return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
            }

            if (hasGoldsmithEquipment && tickPerfectMethod && !state.getEquipment().hasGoldsmithEffect() && !state.getEquipment().hasIceGlovesEffect()) {
                return equipGoldsmithGauntlets;
            }

            if (hasGoldsmithEquipment && !tickPerfectMethod && !state.getEquipment().hasGoldsmithEffect()) {
                return equipGoldsmithGauntlets;
            }

            if (!state.getInventory().has(ItemID.GOLD_ORE)) {
                return withdrawGoldOre;
            }
        }

        if (tickPerfectMethod && state.getInventory().has(ItemID.GOLD_ORE)) {
            if (furnaceHasBar) {
                return putOntoConveyorBelt;
            } else {
                return hasGoldsmithEquipment ? putOntoConveyorBeltAndEquipGoldsmithGauntlets : putOntoConveyorBelt;
            }
        }

        if (tickPerfectMethod && (oreOnConveyor || furnaceHasOre) && furnaceHasBar) {
            if (atConveyorBelt) {
                return goToDispenser;
            }

            if (!atBarDispenser) {
                return goToDispenserAndEquipIceOrSmithsGloves;
            }

            return hasGoldsmithEquipment ? collectBarsAndEquipGoldsmithGauntlets : collectBars;
        }

        if (!tickPerfectMethod && state.getInventory().has(ItemID.GOLD_ORE)) {
            if (hasGoldsmithEquipment && !state.getEquipment().hasGoldsmithEffect()) {
                return equipGoldsmithGauntlets;
            }
            return putOntoConveyorBelt;
        }

		if (!tickPerfectMethod && (oreOnConveyor || furnaceHasOre)) {
			return waitForGoldBars;
		}

        if (!tickPerfectMethod && furnaceHasBar) {
            if (!state.getEquipment().hasIceGlovesEffect()) {
                return equipIceOrSmithsGloves;
            }
            return collectBars;
        }

        return openBank;
    }

    @Override
    public String getName()
    {
        return Strings.GOLD;
    }
}

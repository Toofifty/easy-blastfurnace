package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.BankItemStep;
import com.toofifty.easyblastfurnace.steps.ItemStep;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public class DrinkPotionMethod extends Method
{
    // Stamina
    private final MethodStep[] withdrawStaminaPotion1 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_STAMINA_POTION, ItemID.STAMINA_POTION1) };
    private final MethodStep[] withdrawStaminaPotion2 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_STAMINA_POTION, ItemID.STAMINA_POTION2) };
    private final MethodStep[] withdrawStaminaPotion3 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_STAMINA_POTION, ItemID.STAMINA_POTION3) };
    private final MethodStep[] withdrawStaminaPotion4 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_STAMINA_POTION, ItemID.STAMINA_POTION4) };

    private final MethodStep[] drinkStaminaPotion1 = new MethodStep[] { new ItemStep(Strings.DRINK_STAMINA_POTION, ItemID.STAMINA_POTION1) };
    private final MethodStep[] drinkStaminaPotion2 = new MethodStep[] { new ItemStep(Strings.DRINK_STAMINA_POTION, ItemID.STAMINA_POTION2) };
    private final MethodStep[] drinkStaminaPotion3 = new MethodStep[] { new ItemStep(Strings.DRINK_STAMINA_POTION, ItemID.STAMINA_POTION3) };
    private final MethodStep[] drinkStaminaPotion4 = new MethodStep[] { new ItemStep(Strings.DRINK_STAMINA_POTION, ItemID.STAMINA_POTION4) };
    private final MethodStep[] getMoreStaminaPotions = new MethodStep[] { new ItemStep(Strings.GET_MORE_STAMINA_POTIONS, ItemID.COAL_BAG_12019) };


    // Super Energy
    private final MethodStep[] withdrawSuperEnergyPotion1 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_SUPER_ENERGY_POTION, ItemID.SUPER_ENERGY1) };
    private final MethodStep[] withdrawSuperEnergyPotion2 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_SUPER_ENERGY_POTION, ItemID.SUPER_ENERGY2) };
    private final MethodStep[] withdrawSuperEnergyPotion3 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_SUPER_ENERGY_POTION, ItemID.SUPER_ENERGY3) };
    private final MethodStep[] withdrawSuperEnergyPotion4 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_SUPER_ENERGY_POTION, ItemID.SUPER_ENERGY4) };

    private final MethodStep[] drinkSuperEnergyPotion1 = new MethodStep[] { new ItemStep(Strings.DRINK_SUPER_ENERGY_POTION, ItemID.SUPER_ENERGY1) };
    private final MethodStep[] drinkSuperEnergyPotion2 = new MethodStep[] { new ItemStep(Strings.DRINK_SUPER_ENERGY_POTION, ItemID.SUPER_ENERGY2) };
    private final MethodStep[] drinkSuperEnergyPotion3 = new MethodStep[] { new ItemStep(Strings.DRINK_SUPER_ENERGY_POTION, ItemID.SUPER_ENERGY3) };
    private final MethodStep[] drinkSuperEnergyPotion4 = new MethodStep[] { new ItemStep(Strings.DRINK_SUPER_ENERGY_POTION, ItemID.SUPER_ENERGY4) };
    private final MethodStep[] getMoreSuperEnergyPotions = new MethodStep[] { new ItemStep(Strings.GET_MORE_SUPER_ENERGY_POTIONS, ItemID.COAL_BAG_12019) };

    // Energy
    private final MethodStep[] withdrawEnergyPotion1 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_ENERGY_POTION, ItemID.ENERGY_POTION1) };
    private final MethodStep[] withdrawEnergyPotion2 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_ENERGY_POTION, ItemID.ENERGY_POTION2) };
    private final MethodStep[] withdrawEnergyPotion3 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_ENERGY_POTION, ItemID.ENERGY_POTION3) };
    private final MethodStep[] withdrawEnergyPotion4 = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_ENERGY_POTION, ItemID.ENERGY_POTION4) };

    private final MethodStep[] drinkEnergyPotion1 = new MethodStep[] { new ItemStep(Strings.DRINK_ENERGY_POTION, ItemID.ENERGY_POTION1) };
    private final MethodStep[] drinkEnergyPotion2 = new MethodStep[] { new ItemStep(Strings.DRINK_ENERGY_POTION, ItemID.ENERGY_POTION2) };
    private final MethodStep[] drinkEnergyPotion3 = new MethodStep[] { new ItemStep(Strings.DRINK_ENERGY_POTION, ItemID.ENERGY_POTION3) };
    private final MethodStep[] drinkEnergyPotion4 = new MethodStep[] { new ItemStep(Strings.DRINK_ENERGY_POTION, ItemID.ENERGY_POTION4) };
    private final MethodStep[] getMoreEnergyPotions = new MethodStep[] { new ItemStep(Strings.GET_MORE_ENERGY_POTIONS, ItemID.COAL_BAG_12019) };

    @Override
    public MethodStep[] next(BlastFurnaceState state)
    {
        switch(state.getConfig().potionOverlayMode()) {
            case SUPER_ENERGY: return GetSuperEnergyStep(state);
            case ENERGY: return GetEnergyStep(state);
            default: return GetStaminaStep(state);
        }
    }

    private MethodStep[] GetStaminaStep(BlastFurnaceState state) {
        if (state.getPlayer().hasEnoughEnergy() &&
                (state.getInventory().has(ItemID.VIAL, ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4))) {
            return state.getConfig().useDepositInventory() ? depositInventory : depositStaminaPotions;
        }

        if (!state.getBank().isOpen() || state.getPlayer().hasEnoughEnergy()) return null;

        if (!state.getInventory().has(ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4) &&
                !state.getInventory().hasFreeSlots()) {
            return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
        }

        if (state.getInventory().has(ItemID.STAMINA_POTION1)) {
            return drinkStaminaPotion1;
        }

        if (state.getInventory().has(ItemID.STAMINA_POTION2)) {
            return drinkStaminaPotion2;
        }

        if (state.getInventory().has(ItemID.STAMINA_POTION3)) {
            return drinkStaminaPotion3;
        }

        if (state.getInventory().has(ItemID.STAMINA_POTION4)) {
            return drinkStaminaPotion4;
        }

        if (state.getBank().has(ItemID.STAMINA_POTION1)) {
            return withdrawStaminaPotion1;
        }

        if (state.getBank().has(ItemID.STAMINA_POTION2)) {
            return withdrawStaminaPotion2;
        }

        if (state.getBank().has(ItemID.STAMINA_POTION3)) {
            return withdrawStaminaPotion3;
        }

        if (state.getBank().has(ItemID.STAMINA_POTION4)) {
            return withdrawStaminaPotion4;
        }

        return getMoreStaminaPotions;
    }

    private MethodStep[] GetSuperEnergyStep(BlastFurnaceState state) {
        if (state.getPlayer().hasEnoughEnergy() &&
                (state.getInventory().has(ItemID.VIAL, ItemID.SUPER_ENERGY1, ItemID.SUPER_ENERGY2, ItemID.SUPER_ENERGY3, ItemID.SUPER_ENERGY4))) {
            return state.getConfig().useDepositInventory() ? depositInventory : depositSuperEnergyPotions;
        }

        if (!state.getBank().isOpen() || state.getPlayer().hasEnoughEnergy()) return null;

        if (!state.getInventory().has(ItemID.SUPER_ENERGY1, ItemID.SUPER_ENERGY2, ItemID.SUPER_ENERGY3, ItemID.SUPER_ENERGY4) &&
                !state.getInventory().hasFreeSlots()) {
            return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
        }


        if (state.getInventory().has(ItemID.SUPER_ENERGY4)) {
            return drinkSuperEnergyPotion4;
        }

        if (state.getInventory().has(ItemID.SUPER_ENERGY3)) {
            return drinkSuperEnergyPotion3;
        }

        if (state.getInventory().has(ItemID.SUPER_ENERGY2)) {
            return drinkSuperEnergyPotion2;
        }

        if (state.getInventory().has(ItemID.SUPER_ENERGY1)) {
            return drinkSuperEnergyPotion1;
        }


        if (state.getBank().has(ItemID.SUPER_ENERGY1)) {
            return withdrawSuperEnergyPotion1;
        }

        if (state.getBank().has(ItemID.SUPER_ENERGY2)) {
            return withdrawSuperEnergyPotion2;
        }

        if (state.getBank().has(ItemID.SUPER_ENERGY3)) {
            return withdrawSuperEnergyPotion3;
        }

        if (state.getBank().has(ItemID.SUPER_ENERGY4)) {
            return withdrawSuperEnergyPotion4;
        }

        return getMoreSuperEnergyPotions;
    }

    private MethodStep[] GetEnergyStep(BlastFurnaceState state) {
        if (state.getPlayer().hasEnoughEnergy() &&
                (state.getInventory().has(ItemID.VIAL, ItemID.ENERGY_POTION1, ItemID.ENERGY_POTION2, ItemID.ENERGY_POTION3, ItemID.ENERGY_POTION4))) {
            return state.getConfig().useDepositInventory() ? depositInventory : depositEnergyPotions;
        }

        if (!state.getBank().isOpen() || state.getPlayer().hasEnoughEnergy()) return null;

        if (!state.getInventory().has(ItemID.ENERGY_POTION1, ItemID.ENERGY_POTION2, ItemID.ENERGY_POTION3, ItemID.ENERGY_POTION4) &&
                !state.getInventory().hasFreeSlots()) {
            return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
        }

        if (state.getInventory().has(ItemID.ENERGY_POTION4)) {
            return drinkEnergyPotion4;
        }

        if (state.getInventory().has(ItemID.ENERGY_POTION3)) {
            return drinkEnergyPotion3;
        }

        if (state.getInventory().has(ItemID.ENERGY_POTION2)) {
            return drinkEnergyPotion2;
        }

        if (state.getInventory().has(ItemID.ENERGY_POTION1)) {
            return drinkEnergyPotion1;
        }

        if (state.getBank().has(ItemID.ENERGY_POTION1)) {
            return withdrawEnergyPotion1;
        }

        if (state.getBank().has(ItemID.ENERGY_POTION2)) {
            return withdrawEnergyPotion2;
        }

        if (state.getBank().has(ItemID.ENERGY_POTION3)) {
            return withdrawEnergyPotion3;
        }

        if (state.getBank().has(ItemID.ENERGY_POTION4)) {
            return withdrawEnergyPotion4;
        }

        return getMoreEnergyPotions;
    }

    @Override
    public String getName()
    {
        return Strings.DRINK_STAMINA;
    }
}

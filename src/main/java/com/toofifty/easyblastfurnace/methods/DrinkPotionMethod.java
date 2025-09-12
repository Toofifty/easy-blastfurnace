package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.BankItemStep;
import com.toofifty.easyblastfurnace.steps.ItemStep;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Potion;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;

public class DrinkPotionMethod extends Method
{

	// Stamina
	private final MethodStep[] drinkStaminaPotion = new MethodStep[] { new ItemStep(Strings.DRINK_STAMINA_POTION, ItemID._1DOSESTAMINA, ItemID._2DOSESTAMINA, ItemID._3DOSESTAMINA, ItemID._4DOSESTAMINA) };
	private final MethodStep[] getMoreStaminaPotions = new MethodStep[] { new ItemStep(Strings.GET_MORE_STAMINA_POTIONS, ItemID.COAL_BAG) };

	// Super Energy
	private final MethodStep[] drinkSuperEnergyPotion = new MethodStep[] { new ItemStep(Strings.DRINK_SUPER_ENERGY_POTION, ItemID._1DOSE2ENERGY, ItemID._2DOSE2ENERGY, ItemID._3DOSE2ENERGY, ItemID._4DOSE2ENERGY) };
	private final MethodStep[] getMoreSuperEnergyPotions = new MethodStep[] { new ItemStep(Strings.GET_MORE_SUPER_ENERGY_POTIONS, ItemID.COAL_BAG) };

	// Energy
	private final MethodStep[] drinkEnergyPotion = new MethodStep[] { new ItemStep(Strings.DRINK_ENERGY_POTION, ItemID._1DOSE1ENERGY, ItemID._2DOSE1ENERGY, ItemID._3DOSE1ENERGY, ItemID._4DOSE1ENERGY) };
	private final MethodStep[] getMoreEnergyPotions = new MethodStep[] { new ItemStep(Strings.GET_MORE_ENERGY_POTIONS, ItemID.COAL_BAG) };

	// Strange fruit
	private final MethodStep[] eatStrangeFruit = new MethodStep[] { new ItemStep(Strings.EAT_STRANGE_FRUIT, ItemID.MACRO_TRIFFIDFRUIT) };
	private final MethodStep[] getMoreStrangeFruit = new MethodStep[] { new ItemStep(Strings.GET_MORE_STRANGE_FRUIT, ItemID.COAL_BAG) };

    @Override
    public MethodStep[] next(BlastFurnaceState state)
    {
		switch(state.getConfig().potionOverlayMode()) {
			case SUPER_ENERGY: return getSuperEnergyStep(state);
			case ENERGY: return getEnergyStep(state);
			case STRANGE_FRUIT: return getStrangeFruitStep(state);
			default: return getStaminaStep(state);
		}
    }

	private MethodStep[] getStaminaStep(BlastFurnaceState state) {
		int[] itemIds = new int[]{ItemID._1DOSESTAMINA, ItemID._2DOSESTAMINA, ItemID._3DOSESTAMINA, ItemID._4DOSESTAMINA};
		return getMethodStep(state, itemIds, drinkStaminaPotion, Strings.WITHDRAW_STAMINA_POTION, depositStaminaPotions, getMoreStaminaPotions);
	}

	private MethodStep[] getEnergyStep(BlastFurnaceState state) {
		int[] itemIds = new int[]{ItemID._1DOSE1ENERGY, ItemID._2DOSE1ENERGY, ItemID._3DOSE1ENERGY, ItemID._4DOSE1ENERGY};
		return getMethodStep(state, itemIds, drinkEnergyPotion, Strings.WITHDRAW_ENERGY_POTION, depositEnergyPotions, getMoreEnergyPotions);
	}

	private MethodStep[] getSuperEnergyStep(BlastFurnaceState state) {
		int[] itemIds = new int[]{ItemID._1DOSE2ENERGY, ItemID._2DOSE2ENERGY, ItemID._3DOSE2ENERGY, ItemID._4DOSE2ENERGY};
		return getMethodStep(state, itemIds, drinkSuperEnergyPotion, Strings.WITHDRAW_SUPER_ENERGY_POTION, depositSuperEnergyPotions, getMoreSuperEnergyPotions);
	}

	private MethodStep[] getStrangeFruitStep(BlastFurnaceState state) {
		int[] itemIds = new int[]{ItemID.MACRO_TRIFFIDFRUIT};
		return getMethodStep(state, itemIds, eatStrangeFruit, Strings.WITHDRAW_STRANGE_FRUIT, depositStrangeFruit, getMoreStrangeFruit);
	}

	private MethodStep[] getMethodStep(BlastFurnaceState state, int[] itemIds, MethodStep[] consumeStep, String withdrawStep, MethodStep depositStep[], MethodStep[] getMoreStep)
	{
		boolean hasDosesInInventory = Arrays.stream(itemIds).anyMatch(id -> state.getInventory().has(id));

		if (state.getPlayer().hasEnoughEnergy() && (state.getInventory().has(ItemID.VIAL_EMPTY) || hasDosesInInventory)) {
			return state.getConfig().useDepositInventory() ? depositInventory : depositStep;
		}

		if (!state.getBank().isOpen() || state.getPlayer().hasEnoughEnergy()) return null;

		if (!hasDosesInInventory && !state.getInventory().hasFreeSlots()) {
			return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
		}

		Integer bankItemId = Arrays.stream(itemIds).filter(state.getBank()::has)
				.boxed()
				.findFirst()
				.orElse(null);
		Potion potionStoragePotion = state.getBank().getPotionStoragePotion(itemIds);

		if (hasDosesInInventory) {
			return consumeStep;
		}

		if (bankItemId != null) {
			return withdrawFromBank(withdrawStep, bankItemId);
		}

		if (potionStoragePotion != null) {
			return withdrawFromBank(withdrawStep + Strings.WITHDRAW_FROM_POTION_STORAGE, potionStoragePotion.itemId);
		}

		return getMoreStep;
	}

	private MethodStep[] withdrawFromBank(String stepText, int itemId)
	{
		return new MethodStep[] { new BankItemStep(stepText, itemId) };
	}

    @Override
    public String getName()
    {
        return Strings.DRINK_STAMINA;
    }
}

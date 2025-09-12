package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.utils.Potion;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.ScriptID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Objects;

public class BankState
{
    @Inject
    private Client client;

	@Getter
	private Potion[] potions;

    private ItemContainer bank;

    private void load()
    {
        ItemContainer bank = client.getItemContainer(InventoryID.BANK);
        if (bank != null) {
            this.bank = bank;
        }
    }

    public boolean isOpen()
    {
        Widget bankContainer = client.getWidget(InterfaceID.Bankmain.UNIVERSE);
        return bankContainer != null && !bankContainer.isHidden();
    }

    public int getQuantity(int ...itemIds)
    {
        load();
        int total = 0;

        if (bank == null) return 0;

        for (int itemId : itemIds) {
            total += bank.count(itemId);
        }

        return total;
    }

    public boolean has(int ...itemIds) {
        load();
        if (bank == null) return false;
        for (int itemId : itemIds) {
            if (bank.count(itemId) > 0) return true;
        }
        return false;
    }

	public void updatePotionStorage() {
		EnumComposition potionStorePotions = client.getEnum(EnumID.POTIONSTORE_POTIONS);
		potions = new Potion[potionStorePotions.size()];
		int potionsIdx = 0;
		for (EnumComposition e : new EnumComposition[]{potionStorePotions})
		{
			for (int potionEnumId : e.getIntVals())
			{
				EnumComposition potionEnum = client.getEnum(potionEnumId);
				client.runScript(ScriptID.POTIONSTORE_DOSES, potionEnumId);
				int doses = client.getIntStack()[0];
				client.runScript(ScriptID.POTIONSTORE_WITHDRAW_DOSES, potionEnumId);
				int withdrawDoses = client.getIntStack()[0];

				if (doses > 0 && withdrawDoses > 0)
				{
					Potion p = new Potion();
					p.potionEnum = potionEnum;
					p.itemId = potionEnum.getIntValue(withdrawDoses);
					p.doses = doses;
					p.withdrawDoses = withdrawDoses;
					potions[potionsIdx] = p;
				}
				++potionsIdx;
			}
		}
	}

	public Potion potionStorageSearch(int itemId) {
		Potion potion = null;
		for (Potion potionStoragePotion : potions) {
			if (potionStoragePotion != null && itemId == potionStoragePotion.itemId) {
				potion = potionStoragePotion;
			}
		}

		return potion;
	}

	public Potion getPotionStoragePotion(int ...itemIds) {
		if (potions == null) return null;
		return Arrays.stream(itemIds)
				.mapToObj(this::potionStorageSearch) // call single‑id method
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}
}

package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.utils.BarsOres;
import net.runelite.api.Client;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FurnaceState
{
    @Inject
    private Client client;

    private final Map<Integer, Integer> previousQuantity = new HashMap<>();

    public void update()
    {
        for (BarsOres varbit : BarsOres.values()) {
            previousQuantity.put(varbit.getItemID(), getQuantity(new int[]{varbit.getItemID()}));
        }
    }

    public int getChange(int itemId)
    {
        return getQuantity(new int[]{itemId}) - previousQuantity.getOrDefault(itemId, 0);
    }

    public int getQuantity(int[] itemIds)
    {
        int total = 0;

        for (int itemId : itemIds) {
            Optional<BarsOres> varbit = Arrays.stream(BarsOres.values()).filter(e -> e.getItemID() == itemId).findFirst();
            assert varbit.isPresent();
            total += client.getVarbitValue(varbit.get().getVarbit());
        }

        return total;
    }

    public boolean has(int[] itemIds) { return getQuantity(itemIds) > 0; }
}

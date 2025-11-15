package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.utils.BarsOres;

import com.toofifty.easyblastfurnace.utils.MethodHandler;
import com.toofifty.easyblastfurnace.utils.Strings;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

public class FurnaceState
{
    @Inject
    private Client client;

    @Inject
    private EasyBlastFurnaceConfig config;

	@Inject
	private MethodHandler methodHandler;

    private final Map<Integer, Integer> previousQuantity = new HashMap<>();

    @Getter
    @Setter
    public int oresOnConveyorBelt = 0;

    public void update()
    {
        for (BarsOres varbit : BarsOres.values()) {
            previousQuantity.put(varbit.getItemID(), getQuantity(varbit.getItemID()));
        }
    }

    public int getChange(int itemId)
    {
        return getQuantity(itemId) - previousQuantity.getOrDefault(itemId, 0);
    }


    public int getCoalOffset()
    {
		boolean isSteelMethod = Objects.equals(methodHandler.getMethod().getName(), Strings.STEEL);
		if (isSteelMethod || config.addCoalBuffer()) {
            return 0;
        }
        return 1;
    }

    public int getQuantity(int ...itemIds)
    {
        int total = 0;

        for (int itemId : itemIds) {
            Optional<BarsOres> varbit = Arrays.stream(BarsOres.values()).filter(e -> e.getItemID() == itemId).findFirst();
            assert varbit.isPresent();
            total += client.getVarbitValue(varbit.get().getVarbit());
        }

        return total;
    }

    public boolean has(int ...itemIds)
    {
        return getQuantity(itemIds) > 0;
    }

    public boolean isCoalRunNext(int coalPer)
    {
        int coalInFurnace = getQuantity(ItemID.COAL);
        return coalInFurnace < 27 * (coalPer - getCoalOffset());
    }
}

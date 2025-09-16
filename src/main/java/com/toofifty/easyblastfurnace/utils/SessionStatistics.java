package com.toofifty.easyblastfurnace.utils;

import com.toofifty.easyblastfurnace.methods.*;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
@Slf4j
public class SessionStatistics
{
    @Inject
    private Client client;

    @Inject
    private MethodHandler methodHandler;

    @Inject
    private BlastFurnaceState state;

    @Getter
    private int staminaDoses = 0;

    private Instant timeStarted;

    @Getter
    private int barsPerHour = 0;

    private ItemContainer cachedBank;

    private final Map<Integer, Integer> outputs = new HashMap<>();

    public void clear()
    {
        outputs.clear();
        staminaDoses = 0;
        timeStarted = null;
    }

    public void drinkStamina()
    {
        if (state.getEquipment().equipped(ItemID.RING_OF_ENDURANCE)) {
            staminaDoses += 2;
        } else {
            staminaDoses++;
        }
    }

    public int getTotalActionsDone()
    {
        int actions = 0;
        for (int itemId : outputs.keySet()) {
            actions += outputs.getOrDefault(itemId, 0);
        }
        calculateBarsPerHour(actions);
        return actions;
    }

    public double getTotalXpGained()
    {
        double xp = 0;
        for (int itemId : outputs.keySet()) {
            int quantity = outputs.getOrDefault(itemId, 0);
			if (!Equipment.hasGoldsmithEquipment(state)) {
				xp += Objects.requireNonNull(XpRecord.get(itemId)).getXp() * quantity;
			} else {
				xp += Objects.requireNonNull(XpRecord.get(itemId)).getGauntletXp() * quantity;
			}
        }
        return xp;
    }

    private int getActionsBanked(int itemId)
    {
        return getActionsBanked(XpRecord.get(itemId));
    }

    private ItemContainer getBank()
    {
        ItemContainer bank = client.getItemContainer(InventoryID.BANK);

        if (bank != null) {
            return cachedBank = bank;
        }

        return cachedBank;
    }

    private int getActionsBanked(XpRecord xpRecord)
    {
        ItemContainer bank = getBank();
        if (bank == null) return 0;

        int ores = bank.count(xpRecord.getOreId());

        if (xpRecord.getCoalPer() == 0) {
            return ores;
        }

        int coal = bank.count(ItemID.COAL);

        return Math.min(ores, coal / xpRecord.getCoalPer());
    }

    private double getXpBanked(int itemId)
    {
        return getXpBanked(XpRecord.get(itemId));
    }

    private double getXpBanked(XpRecord xpRecord)
    {
		if (!Equipment.hasGoldsmithEquipment(state)) {
			return getActionsBanked(xpRecord) * xpRecord.getXp();
		} else {
			return getActionsBanked(xpRecord) * xpRecord.getGauntletXp();
		}
    }

    public int getTotalActionsBanked()
    {
        Method method = methodHandler.getMethod();

        if (method instanceof GoldHybridMethod) {
            return getActionsBanked(ItemID.GOLD_ORE) +
                getActionsBanked(((GoldHybridMethod) method).oreItem());
        }

		if (method instanceof SilverHybridMethod) {
			return getActionsBanked(ItemID.SILVER_ORE) +
				getActionsBanked(((SilverHybridMethod) method).oreItem());
		}


		if (method instanceof MetalBarMethod) {
            return getActionsBanked(((MetalBarMethod) method).oreItem());
        }

        if (method instanceof GoldBarMethod) {
            return getActionsBanked(ItemID.GOLD_ORE);
        }

		if (method instanceof SilverBarMethod) {
			return getActionsBanked(ItemID.SILVER_ORE);
		}

        return 0;
    }

    public double getTotalXpBanked()
    {
        Method method = methodHandler.getMethod();

        if (method instanceof GoldHybridMethod) {
            return getXpBanked(ItemID.GOLD_ORE) +
                getXpBanked(((GoldHybridMethod) method).oreItem());
        }

		if (method instanceof SilverHybridMethod) {
			return getXpBanked(ItemID.SILVER_ORE) +
					getXpBanked(((SilverHybridMethod) method).oreItem());
		}

        if (method instanceof MetalBarMethod) {
            return getXpBanked(((MetalBarMethod) method).oreItem());
        }

        if (method instanceof GoldBarMethod) {
            return getXpBanked(ItemID.GOLD_ORE);
        }

		if (method instanceof SilverBarMethod) {
			return getXpBanked(ItemID.SILVER_ORE);
		}

        return 0;
    }

    public void onFurnaceUpdate()
    {
        int[] bars = new int[]{
            ItemID.GOLD_BAR, ItemID.SILVER_BAR, ItemID.STEEL_BAR, ItemID.MITHRIL_BAR, ItemID.ADAMANTITE_BAR, ItemID.RUNITE_BAR
        };

        int[] ores = new int[]{
            ItemID.GOLD_ORE, ItemID.SILVER_ORE, ItemID.IRON_ORE, ItemID.MITHRIL_ORE, ItemID.ADAMANTITE_ORE, ItemID.RUNITE_ORE
        };

        for (int oreId : ores) {
            int diff = state.getFurnace().getChange(oreId);
            if (diff > 0) {
                state.getFurnace().setOresOnConveyorBelt(Math.max(state.getFurnace().getOresOnConveyorBelt() - diff, 0));
                if (state.getFurnace().getOresOnConveyorBelt() == 0) {
                    state.getPlayer().hasOreOnConveyor(false);
                }
            }
        }

        for (int barId : bars) {
            int diff = state.getFurnace().getChange(barId);
            if (diff > 0) {
                int totalBars = outputs.getOrDefault(barId, 0) + diff;
                outputs.put(barId, totalBars);
            }
        }
    }

    private void calculateBarsPerHour(int totalBars)
    {
        Instant now = Instant.now();

        if (timeStarted == null) timeStarted = now;

        Duration timeSinceStart = Duration.between(timeStarted, now);
        double hours = (double) (timeSinceStart.toMillis() / 1000L) / 3600;

        if (!timeSinceStart.isNegative() && hours != 0)
        {
            barsPerHour = (int) Math.floor(totalBars / hours);
        }
    }
}

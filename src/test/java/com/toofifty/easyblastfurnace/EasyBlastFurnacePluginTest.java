package com.toofifty.easyblastfurnace;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.utils.BarsOres;
import com.toofifty.easyblastfurnace.utils.CoalPer;
import com.toofifty.easyblastfurnace.utils.MethodHandler;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.google.inject.testing.fieldbinder.Bind;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EasyBlastFurnacePluginTest {

    @Inject
    EasyBlastFurnacePlugin easyBlastFurnacePlugin;

    @Inject
    private MethodHandler methodHandler;

    @Inject
    private BlastFurnaceState state;

    @Mock
    @Bind
    private Client client;

    @Mock
    @Bind
    private GameObject patchObject;

    @Mock
    @Bind
    private RuneLiteConfig runeLiteConfig;

    @Mock
    @Bind
    private ConfigManager configManager;

    @Mock
    @Bind
    private ItemManager itemManager;

    @Mock
    @Bind
    private EasyBlastFurnaceConfig easyBlastFurnaceConfig;

    private final ItemContainer bankContainer = mock(ItemContainer.class);
    private final ItemContainer inventoryContainer = mock(ItemContainer.class);
    private final ItemContainer equipmentContainer = mock(ItemContainer.class);
    private final Player localPlayer = mock(Player.class);
    private final Widget bankWidget = mock(Widget.class);
    private final VarbitChanged blastFurnaceChange = new VarbitChanged();
    private final ItemContainerChanged event = new ItemContainerChanged(InventoryID.INVENTORY.getId(), inventoryContainer);
    private final MenuOptionClicked menuOptionClicked = mock(MenuOptionClicked.class);

    @Before
    public void before()
    {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        GameObjectSpawned gameObjectSpawned = new GameObjectSpawned();

        when(client.getWorld()).thenReturn(358);

        when(patchObject.getId()).thenReturn(ObjectID.BANK_CHEST_26707);
        gameObjectSpawned.setGameObject(patchObject);
        easyBlastFurnacePlugin.onGameObjectSpawned(gameObjectSpawned);
        when(patchObject.getId()).thenReturn(ObjectID.CONVEYOR_BELT);
        gameObjectSpawned.setGameObject(patchObject);
        easyBlastFurnacePlugin.onGameObjectSpawned(gameObjectSpawned);
        when(patchObject.getId()).thenReturn(ObjectID.BAR_DISPENSER);
        gameObjectSpawned.setGameObject(patchObject);
        easyBlastFurnacePlugin.onGameObjectSpawned(gameObjectSpawned);

        when(client.getItemContainer(InventoryID.BANK)).thenReturn(bankContainer);
        when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inventoryContainer);
        when(client.getItemContainer(InventoryID.EQUIPMENT)).thenReturn(equipmentContainer);

        when(client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER)).thenReturn(bankWidget);

        when(client.getLocalPlayer()).thenReturn(localPlayer);
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1949, 4967, 0));

        assertTrue(easyBlastFurnacePlugin.isEnabled());
    }

    @Test
    public void steelBarMethod()
    {
        metalMethods(ItemID.IRON_ORE, ItemID.STEEL_BAR, BarsOres.STEEL_BAR.name(), Strings.STEEL.name());
        assertEquals(Strings.WITHDRAWIRONORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void mithrilBarMethod()
    {
        metalMethods(ItemID.MITHRIL_ORE, ItemID.MITHRIL_BAR, BarsOres.MITHRIL_BAR.name(), Strings.MITHRIL.name());
        assertEquals(Strings.WITHDRAWMITHRILORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void adamantiteBarMethod()
    {
        metalMethods(ItemID.ADAMANTITE_ORE, ItemID.ADAMANTITE_BAR, BarsOres.ADAMANTITE_BAR.name(), Strings.ADAMANTITE.name());
        assertEquals(Strings.WITHDRAWADAMANTITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void runiteBarMethod()
    {
        metalMethods(ItemID.RUNITE_ORE, ItemID.RUNITE_BAR, BarsOres.RUNITE_BAR.name(), Strings.RUNITE.name());
        assertEquals(Strings.WITHDRAWRUNITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void mithrilHybridMethod()
    {
        metalMethods(ItemID.MITHRIL_ORE, ItemID.MITHRIL_BAR, BarsOres.MITHRIL_BAR.name(), Strings.MITHRILHYBRID.name());
        assertEquals(Strings.WITHDRAWMITHRILORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void adamantiteHybridMethod()
    {
        metalMethods(ItemID.ADAMANTITE_ORE, ItemID.ADAMANTITE_BAR, BarsOres.ADAMANTITE_BAR.name(), Strings.ADAMANTITEHYBRID.name());
        assertEquals(Strings.WITHDRAWADAMANTITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void runiteHybridMethod()
    {
        metalMethods(ItemID.RUNITE_ORE, ItemID.RUNITE_BAR, BarsOres.RUNITE_BAR.name(), Strings.RUNITEHYBRID.name());
        assertEquals(Strings.WITHDRAWRUNITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void goldBarMethod()
    {
        // Get Method - Inventory: 27 gold ore
        when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ItemID.GOLD_ORE, 27)});
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(27);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.GOLD.getTxt(), methodHandler.getMethod().getName());
        assertEquals(Strings.WITHDRAWICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        goldPrerequisites();

        ontoConveyor();

        // Ore onto conveyor done - Inventory: 0 gold ore, ice gloves, Equipment: goldsmith gauntlets
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1942, 4967, 0));
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(0);
        state.update();
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WAITFORBARS.getTxt(), methodHandler.getStep().getTooltip());

        // BF makes bars
        when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(0);
        when(client.getVarbitValue(BarsOres.GOLD_BAR.getVarbit())).thenReturn(27);
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.EQUIPICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        // Collect Bars - Inventory: 0 gold ore, goldsmith gauntlets, Equipment: ice gloves
        when(inventoryContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.COLLECTBARS.getTxt(), methodHandler.getStep().getTooltip());

        depositBars(ItemID.GOLD_BAR, BarsOres.GOLD_BAR.name(), 27);

        // Deposit bars done - Inventory: 0 gold bars, goldsmith gauntlets, Equipment: ice gloves
        when(inventoryContainer.count(ItemID.GOLD_BAR)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWGOLDORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    private void metalMethods(int ore, int bar, String barName, String methodName)
    {
        boolean isHybrid = methodName.contains("HYBRID");
        int oreCount = isHybrid ? 26 : 27;

        if (isHybrid) {
            when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ore, 1), new Item(ItemID.GOLD_ORE, oreCount)});
            when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(oreCount);
        } else {
            when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ore, oreCount)});
        }
        // Get Method - Inventory: 26 ore.
        when(inventoryContainer.count(ore)).thenReturn(oreCount);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.valueOf(methodName).getTxt(), methodHandler.getMethod().getName());
        assertEquals(Strings.WITHDRAWCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequesite - Inventory: 27 ore, full coal bag
        when(inventoryContainer.count(ItemID.OPEN_COAL_BAG)).thenReturn(1);
        state.getCoalBag().setCoal(27);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        if (isHybrid) {
            goldPrerequisites();
        } else {
            // Prerequesite - Inventory: 27 ore, full coal bag
            when(inventoryContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
            easyBlastFurnacePlugin.onItemContainerChanged(event);
            assertEquals(Strings.EQUIPICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());
        }

        ontoConveyor();

        // Empty coal bag onto conveyor - Inventory: 26 ore, full coal bag
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1942, 4967, 0));
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(0);
        when(inventoryContainer.count(ore)).thenReturn(0);
        state.update();
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EMPTYCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Wait for bars - Inventory: 0 ore, empty coal bag
        when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ItemID.OPEN_COAL_BAG, 1)});
        when(menuOptionClicked.getMenuOption()).thenReturn(Strings.EMPTY.getTxt());
        when(bankWidget.isHidden()).thenReturn(true);
        easyBlastFurnacePlugin.onMenuOptionClicked(menuOptionClicked);
        assertEquals(0, state.getCoalBag().getCoal());
        assertEquals(Strings.WAITFORBARS.getTxt(), methodHandler.getStep().getTooltip());

        when(client.getVarbitValue(BarsOres.valueOf(barName).getVarbit())).thenReturn(oreCount);
        when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
        if (isHybrid) {
            when(inventoryContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
            when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(0);
        }
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);

        if (isHybrid) {
            // Equip Ice gloves - Inventory: 0 bars, empty coal bag
            assertEquals(Strings.EQUIPICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        } else {
            // Collect bars - Inventory: 0 bars, empty coal bag
            assertEquals(Strings.COLLECTBARS.getTxt(), methodHandler.getStep().getTooltip());
        }

        depositBars(bar, barName, oreCount);

        // Fill coal bag - Inventory: 0 bars, empty coal bag, Equipment: ice gloves
        when(inventoryContainer.count(bar)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.FILLCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Withdraw ore - Inventory: 0 bars, full coal bag, Equipment: ice gloves
        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(0);
        when(menuOptionClicked.getMenuOption()).thenReturn(Strings.FILL.getTxt());
        easyBlastFurnacePlugin.onMenuOptionClicked(menuOptionClicked);
        assertEquals(state.getCoalBag().getMaxCoal(), state.getCoalBag().getCoal());

        // Withdraw ore - Inventory: 0 bars, full coal bag, Equipment: ice gloves
        int coalNeeded = oreCount * (CoalPer.valueOf(barName.replace("_BAR", "").replace("STEEL", "IRON")).getValue() - state.getFurnace().getCoalOffset());

        if (isHybrid) {
            assertEquals(Strings.WITHDRAWGOLDORE.getTxt(), methodHandler.getStep().getTooltip());
        } else if (coalNeeded > 27) {
            assertEquals(Strings.WITHDRAWCOAL.getTxt(), methodHandler.getStep().getTooltip());
        }

        if (coalNeeded > 27) {
            // Put ore onto conveyor - Inventory: 27 coal, full coal bag, Equipment: ice gloves
            when(inventoryContainer.count(ItemID.COAL)).thenReturn(27);
            easyBlastFurnacePlugin.onItemContainerChanged(event);
            assertEquals(Strings.PUTONTOCONVEYORBELT.getTxt(), methodHandler.getStep().getTooltip());
        }

        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(coalNeeded);
        when(inventoryContainer.count(ItemID.COAL)).thenReturn(0);
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(state.getCoalBag().getMaxCoal(), state.getCoalBag().getCoal());
    }

    private void goldPrerequisites()
    {
        // Prerequisites - Inventory: 27 gold ore and ice gloves, Bank: Smithing cape
        when(state.getInventory().getQuantity(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I)).thenReturn(1);
        when(bankContainer.count(ItemID.SMITHING_CAPE)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWSMITHINGCAPE.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequisites - Inventory: 27 gold ore, ice gloves, smithing cape
        when(inventoryContainer.count(ItemID.SMITHING_CAPE)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EQUIPSMITHINGCAPE.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequisites - Inventory: 27 gold ore, ice gloves
        when(bankContainer.count(ItemID.SMITHING_CAPE)).thenReturn(0);
        when(inventoryContainer.count(ItemID.SMITHING_CAPE)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWGOLDSMITHGAUNTLETS.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequisites - Inventory: 27 gold ore, ice gloves, goldsmith gauntlets
        when(inventoryContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EQUIPGOLDSMITHGAUNTLETS.getTxt(), methodHandler.getStep().getTooltip());
    }

    private void ontoConveyor()
    {
        // Put onto conveyor - Inventory: 26 ore, full coal bag
        when(inventoryContainer.count(ItemID.ICE_GLOVES)).thenReturn(0);
        when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
        when(equipmentContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.PUTONTOCONVEYORBELT.getTxt(), methodHandler.getStep().getTooltip());
    }

    private void depositBars(int bar, String barName, int oreCount)
    {
        // Open bank - Inventory: 26 bars, empty coal bag
        when(client.getVarbitValue(BarsOres.valueOf(barName).getVarbit())).thenReturn(0);
        when(inventoryContainer.count(bar)).thenReturn(oreCount);
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1949, 4967, 0));
        when(bankWidget.isHidden()).thenReturn(true);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.OPENBANK.getTxt(), methodHandler.getStep().getTooltip());

        // deposit inventory
        when(bankWidget.isHidden()).thenReturn(false);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.DEPOSITINVENTORY.getTxt(), methodHandler.getStep().getTooltip());
    }
}

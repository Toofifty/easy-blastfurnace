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
        metalBarMethod(ItemID.IRON_ORE, ItemID.STEEL_BAR, BarsOres.STEEL_BAR.name(), Strings.STEEL.name());
        assertEquals(Strings.WITHDRAWIRONORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void mithrilBarMethod()
    {
        metalBarMethod(ItemID.MITHRIL_ORE, ItemID.MITHRIL_BAR, BarsOres.MITHRIL_BAR.name(), Strings.MITHRIL.name());
        assertEquals(Strings.WITHDRAWMITHRILORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void adamantiteBarMethod()
    {
        metalBarMethod(ItemID.ADAMANTITE_ORE, ItemID.ADAMANTITE_BAR, BarsOres.ADAMANTITE_BAR.name(), Strings.ADAMANTITE.name());
        assertEquals(Strings.WITHDRAWADAMANTITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void runiteBarMethod()
    {
        metalBarMethod(ItemID.RUNITE_ORE, ItemID.RUNITE_BAR, BarsOres.RUNITE_BAR.name(), Strings.RUNITE.name());
        assertEquals(Strings.WITHDRAWRUNITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void mithrilHybridMethod()
    {
        goldHybridMethod(ItemID.MITHRIL_ORE, ItemID.MITHRIL_BAR, BarsOres.MITHRIL_BAR.name(), Strings.MITHRILHYBRID.name());
        assertEquals(Strings.WITHDRAWMITHRILORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void adamantiteHybridMethod()
    {
        goldHybridMethod(ItemID.ADAMANTITE_ORE, ItemID.ADAMANTITE_BAR, BarsOres.ADAMANTITE_BAR.name(), Strings.ADAMANTITEHYBRID.name());
        assertEquals(Strings.WITHDRAWADAMANTITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void runiteHybridMethod()
    {
        goldHybridMethod(ItemID.RUNITE_ORE, ItemID.RUNITE_BAR, BarsOres.RUNITE_BAR.name(), Strings.RUNITEHYBRID.name());
        assertEquals(Strings.WITHDRAWRUNITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void goldBarMethod()
    {
        // Get Method - Inventory: 27 gold ore
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(27);
        when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ItemID.GOLD_ORE, 27)});
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.GOLD.getTxt(), methodHandler.getMethod().getName());
        assertEquals(Strings.WITHDRAWICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        goldPrerequisites();

        // Ore onto conveyor - Inventory: 27 gold ore, ice gloves, Equipment: goldsmith gauntlets
        when(inventoryContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(0);
        when(equipmentContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.PUTONTOCONVEYORBELT.getTxt(), methodHandler.getStep().getTooltip());

        // Ore onto conveyor done - Inventory: 0 gold ore, ice gloves, Equipment: goldsmith gauntlets
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1942, 4967, 0));
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(0);
        state.update();
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertTrue(state.getPlayer().hasLoadedOres());

        // BF makes bars
        when(client.getVarbitValue(BarsOres.GOLD_BAR.getVarbit())).thenReturn(27);
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.EQUIPICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        // Collect Bars - Inventory: 0 gold ore, goldsmith gauntlets, Equipment: ice gloves
        when(inventoryContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.COLLECTBARS.getTxt(), methodHandler.getStep().getTooltip());

        // Deposit bars - Inventory: 27 gold bars, goldsmith gauntlets, Equipment: ice gloves
        when(client.getVarbitValue(BarsOres.GOLD_BAR.getVarbit())).thenReturn(0);
        when(inventoryContainer.count(ItemID.GOLD_BAR)).thenReturn(27);
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1949, 4967, 0));
        when(bankWidget.isHidden()).thenReturn(true);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.OPENBANK.getTxt(), methodHandler.getStep().getTooltip());

        // Deposit bars (bank opened)
        when(bankWidget.isHidden()).thenReturn(false);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.DEPOSITINVENTORY.getTxt(), methodHandler.getStep().getTooltip());

        // Deposit bars done - Inventory: 0 gold bars, goldsmith gauntlets, Equipment: ice gloves
        when(inventoryContainer.count(ItemID.GOLD_BAR)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWGOLDORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    private void metalBarMethod(int ore, int bar, String barName, String methodName)
    {
        // Get Method - Inventory: 27 ore
        when(inventoryContainer.count(ore)).thenReturn(27);
        when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ore, 27)});
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.valueOf(methodName).getTxt(), methodHandler.getMethod().getName());
        assertEquals(Strings.WITHDRAWCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequesite - Inventory: 27 ore, full coal bag
        when(inventoryContainer.count(ItemID.OPEN_COAL_BAG)).thenReturn(1);
        state.getCoalBag().setCoal(27);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequesite - Inventory: 27 ore, coal bag, ice gloves
        when(inventoryContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EQUIPICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        // Ore onto conveyor - Inventory: 27 ore, coal bag, Equipment: ice gloves
        when(inventoryContainer.count(ItemID.ICE_GLOVES)).thenReturn(0);
        when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.PUTONTOCONVEYORBELT.getTxt(), methodHandler.getStep().getTooltip());

        // Empty coal bag onto conveyor - Inventory: 0 ore, coal bag, Equipment: ice gloves
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1942, 4967, 0));
        when(inventoryContainer.count(ore)).thenReturn(0);
        state.update();
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EMPTYCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Ore onto conveyor done - Inventory: 0 ore, empty coal bag, Equipment: ice gloves
        when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ItemID.OPEN_COAL_BAG, 1)});
        when(menuOptionClicked.getMenuOption()).thenReturn(Strings.EMPTY.getTxt());
        when(bankWidget.isHidden()).thenReturn(true);
        easyBlastFurnacePlugin.onMenuOptionClicked(menuOptionClicked);
        assertEquals(0, state.getCoalBag().getCoal());
        assertTrue(state.getPlayer().hasLoadedOres());

        // Collect Bars - Inventory: 27 bars, empty coal bag, Equipment: ice gloves
        when(client.getVarbitValue(BarsOres.valueOf(barName).getVarbit())).thenReturn(27);
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.COLLECTBARS.getTxt(), methodHandler.getStep().getTooltip());

        // Deposit Inventory - Inventory: 27 bars, empty coal bag, Equipment: ice gloves
        when(client.getVarbitValue(BarsOres.valueOf(barName).getVarbit())).thenReturn(0);
        when(inventoryContainer.count(bar)).thenReturn(27);
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1949, 4967, 0));
        when(bankWidget.isHidden()).thenReturn(true);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.OPENBANK.getTxt(), methodHandler.getStep().getTooltip());

        when(bankWidget.isHidden()).thenReturn(false);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.DEPOSITINVENTORY.getTxt(), methodHandler.getStep().getTooltip());

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
        int coalNeeded = 27 * (CoalPer.valueOf(barName.replace("_BAR", "").replace("STEEL", "IRON")).getValue() - state.getFurnace().getCoalOffset());

        if (coalNeeded > 27) {
            assertEquals(Strings.WITHDRAWCOAL.getTxt(), methodHandler.getStep().getTooltip());

            // Put ore onto conveyor - Inventory: 27 coal, full coal bag, Equipment: ice gloves
            when(inventoryContainer.count(ItemID.COAL)).thenReturn(27);
            easyBlastFurnacePlugin.onItemContainerChanged(event);
            assertEquals(Strings.PUTONTOCONVEYORBELT.getTxt(), methodHandler.getStep().getTooltip());
        }

        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(coalNeeded);
        when(inventoryContainer.count(ItemID.COAL)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(state.getCoalBag().getMaxCoal(), state.getCoalBag().getCoal());
    }

    private void goldHybridMethod(int ore, int bar, String barName, String methodName)
    {
        // Get Method - Inventory: 26 ore.
        when(inventoryContainer.count(ore)).thenReturn(1);
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(26);
        when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ore, 1), new Item(ItemID.GOLD_ORE, 26)});
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.valueOf(methodName).getTxt(), methodHandler.getMethod().getName());
        assertEquals(Strings.WITHDRAWCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        when(inventoryContainer.count(ItemID.OPEN_COAL_BAG)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        goldPrerequisites();

        // Put onto conveyor - Inventory: 26 ore, ice gloves Equipment: goldsmith gauntlets
        when(equipmentContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.PUTONTOCONVEYORBELT.getTxt(), methodHandler.getStep().getTooltip());

        // Empty coal bag onto conveyor - Inventory: 26 ore, ice gloves Equipment: goldsmith gauntlets
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1942, 4967, 0));
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(0);
        when(inventoryContainer.count(ore)).thenReturn(0);
        state.getCoalBag().setCoal(27);
        state.update();
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EMPTYCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Wait for bars -  Inventory: 0 bars, empty coal bag, ice gloves, Equipment: goldsmith gauntlets
        state.getCoalBag().setCoal(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WAITFORBARS.getTxt(), methodHandler.getStep().getTooltip());

        // Equip Ice gloves - Inventory: 0 bars, empty coal bag, ice gloves, Equipment: goldsmith gauntlets
        when(client.getVarbitValue(BarsOres.valueOf(barName).getVarbit())).thenReturn(27);
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.EQUIPICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        // Collect bars - Inventory: 0 bars, empty coal bag, goldsmith gauntlets, Equipment: ice gloves
        when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.COLLECTBARS.getTxt(), methodHandler.getStep().getTooltip());

        // Deposit Inventory - Inventory: 26 bars, empty coal bag, goldsmith gauntlets, Equipment: ice gloves
        when(client.getVarbitValue(BarsOres.valueOf(barName).getVarbit())).thenReturn(0);
        when(inventoryContainer.count(bar)).thenReturn(27);
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1949, 4967, 0));
        when(bankWidget.isHidden()).thenReturn(true);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.OPENBANK.getTxt(), methodHandler.getStep().getTooltip());

        when(bankWidget.isHidden()).thenReturn(false);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.DEPOSITINVENTORY.getTxt(), methodHandler.getStep().getTooltip());

        // Fill coal bag - Inventory: 0 bars, empty coal bag, Equipment: ice gloves
        when(inventoryContainer.count(bar)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.FILLCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Withdraw ore - Inventory: 0 bars, full coal bag, Equipment: ice gloves
        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(0);
        when(menuOptionClicked.getMenuOption()).thenReturn(Strings.FILL.getTxt());
        easyBlastFurnacePlugin.onMenuOptionClicked(menuOptionClicked);
        assertEquals(state.getCoalBag().getMaxCoal(), state.getCoalBag().getCoal());
        assertEquals(Strings.WITHDRAWGOLDORE.getTxt(), methodHandler.getStep().getTooltip());

        // Withdraw ore - Inventory: 0 bars, full coal bag, Equipment: ice gloves
        int coalNeeded = 26 * (CoalPer.valueOf(barName.replace("_BAR", "").replace("STEEL", "IRON")).getValue() - state.getFurnace().getCoalOffset());

        // Put ore onto conveyor - Inventory: 27 coal, full coal bag, Equipment: ice gloves
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(27);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.PUTONTOCONVEYORBELT.getTxt(), methodHandler.getStep().getTooltip());

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
}

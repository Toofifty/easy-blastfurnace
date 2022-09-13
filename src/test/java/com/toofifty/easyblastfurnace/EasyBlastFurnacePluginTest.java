package com.toofifty.easyblastfurnace;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.utils.BarsOres;
import com.toofifty.easyblastfurnace.utils.MethodHandler;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
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
    public void goldBarMethod()
    {
        // Get Method - Inventory: 26 gold
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(27);
        when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ItemID.GOLD_ORE, 27)});
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.GOLD.getTxt(), methodHandler.getMethod().getName());
        assertEquals(Strings.WITHDRAWICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequisites - Inventory: 27 gold and ice gloves, Bank: Smithing cape
        when(state.getInventory().getQuantity(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I)).thenReturn(1);
        when(bankContainer.count(ItemID.SMITHING_CAPE)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWSMITHINGCAPE.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequisites - Inventory: 27 gold, ice gloves, smithing cape
        when(inventoryContainer.count(ItemID.SMITHING_CAPE)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EQUIPSMITHINGCAPE.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequisites - Inventory: 27 gold, ice gloves
        when(bankContainer.count(ItemID.SMITHING_CAPE)).thenReturn(0);
        when(inventoryContainer.count(ItemID.SMITHING_CAPE)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWGOLDSMITHGAUNTLETS.getTxt(), methodHandler.getStep().getTooltip());

        // Prerequisites - Inventory: 27 gold, ice gloves, goldsmith gauntlets
        when(inventoryContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EQUIPGOLDSMITHGAUNTLETS.getTxt(), methodHandler.getStep().getTooltip());

        // Ore into BF - Inventory: 27 gold, ice gloves, Equipment: goldsmith gauntlets
        when(inventoryContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(0);
        when(equipmentContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.PUTONTOCONVEYORBELT.getTxt(), methodHandler.getStep().getTooltip());

        // Ore into BF done - Inventory: 0 gold, ice gloves, Equipment: goldsmith gauntlets
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1942, 4967, 0));
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(0);
        state.update();
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertTrue(state.getPlayer().hasLoadedOres());

        // BF makes bars
        when(client.getVarbitValue(BarsOres.GOLD_BAR.getVarbit())).thenReturn(27);
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.EQUIPICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        // Collect Bars - Inventory: 0 gold, goldsmith gauntlets, Equipment: ice gloves
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
}

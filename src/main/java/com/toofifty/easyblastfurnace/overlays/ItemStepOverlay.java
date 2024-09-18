package com.toofifty.easyblastfurnace.overlays;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.config.HighlightOverlayTextSetting;
import com.toofifty.easyblastfurnace.config.ItemOverlaySetting;
import com.toofifty.easyblastfurnace.steps.ItemStep;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.MethodHandler;
import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.TextComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

@Singleton
public class ItemStepOverlay extends WidgetItemOverlay
{
    @Inject
    Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private EasyBlastFurnaceConfig config;

    @Inject
    private MethodHandler methodHandler;

    public static boolean itemInBank = true;
    public static WidgetItem currentWidgetItem;

    ItemStepOverlay()
    {
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        if (config.itemOverlayMode() == ItemOverlaySetting.NONE) return;

        MethodStep[] steps = methodHandler.getSteps();
        int finalItemId = itemId;

        if (steps == null) return;

        for (MethodStep step : steps) {
            if (!(step instanceof ItemStep)) continue;

            // This ensures we only highlight one item, i.e. the first ore in an inventory full of ores.
            if (currentWidgetItem != null) {
                widgetItem = currentWidgetItem;
                itemId = widgetItem.getWidget().getItemId();
            } else if (Arrays.stream(((ItemStep) step).getItemIds()).noneMatch(id -> id == finalItemId)) {
                continue;
            }

            currentWidgetItem = widgetItem;

            Color color = config.itemOverlayColor();

            Rectangle bounds = widgetItem.getCanvasBounds();

            if (config.itemOverlayMode() == ItemOverlaySetting.OUTLINE) {
                BufferedImage outline = itemManager.getItemOutline(itemId, widgetItem.getQuantity(), color);
                ImageComponent imageComponent = new ImageComponent(outline);
                imageComponent.setPreferredLocation(new Point(bounds.x, bounds.y));
                imageComponent.render(graphics);
            } else {
                graphics.setColor(color);
                graphics.draw(bounds);
            }

            if (config.itemOverlayTextMode() == HighlightOverlayTextSetting.NONE) continue;

            TextComponent textComponent = new TextComponent();
            textComponent.setColor(color);
            textComponent.setText(step.getTooltip());

            FontMetrics fontMetrics = graphics.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(step.getTooltip());
            int textHeight = fontMetrics.getHeight();

            if (config.itemOverlayTextMode() == HighlightOverlayTextSetting.BELOW) {
                textComponent.setPosition(new Point(
                        bounds.x + bounds.width / 2 - textWidth / 2,
                        bounds.y + bounds.height + textHeight
                ));
            } else {
                textComponent.setPosition(new Point(
                        bounds.x + bounds.width / 2 - textWidth / 2,
                        bounds.y - textHeight / 2
                ));
            }

            textComponent.render(graphics);
        }
    }
}

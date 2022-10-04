package com.toofifty.easyblastfurnace.overlays;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.config.HighlightOverlayTextSetting;
import com.toofifty.easyblastfurnace.config.ItemOverlaySetting;
import com.toofifty.easyblastfurnace.steps.BankItemStep;
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
public class BankItemStepOverlay extends WidgetItemOverlay
{
    @Inject
    Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private EasyBlastFurnaceConfig config;

    @Inject
    private MethodHandler methodHandler;

    BankItemStepOverlay()
    {
        showOnBank();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        if (config.itemOverlayMode() == ItemOverlaySetting.NONE) return;

        MethodStep step = methodHandler.getStep();

        if (step == null) return;
        if (!(step instanceof BankItemStep)) return;
        if (Arrays.stream(((BankItemStep) step).getItemIds()).noneMatch(id -> id == itemId)) return;

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

        if (config.itemOverlayTextMode() == HighlightOverlayTextSetting.NONE) return;

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

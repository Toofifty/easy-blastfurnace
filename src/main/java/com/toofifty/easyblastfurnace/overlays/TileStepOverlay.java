package com.toofifty.easyblastfurnace.overlays;

import com.google.inject.Inject;
import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.config.HighlightOverlayTextSetting;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.steps.TileStep;
import com.toofifty.easyblastfurnace.utils.MethodHandler;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TextComponent;

import java.awt.*;

public class TileStepOverlay extends Overlay
{
    @Inject
    private Client client;

    @Inject
    private EasyBlastFurnaceConfig config;

    @Inject
    private MethodHandler methodHandler;

    TileStepOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showObjectOverlays()) return null;

        MethodStep[] steps = methodHandler.getSteps();

        if (steps == null) return null;

        for (MethodStep step : steps) {
            if (!(step instanceof TileStep)) continue;

            Color color = config.objectOverlayColor();

            LocalPoint localPoint = LocalPoint.fromWorld(client, ((TileStep) step).getWorldPoint());
            if (localPoint == null) continue;

            Polygon polygon = Perspective.getCanvasTilePoly(client, localPoint);

            graphics.setColor(color);
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getBlue(), color.getGreen(), 20));
            graphics.fill(polygon);

            if (config.objectOverlayTextMode() == HighlightOverlayTextSetting.NONE) continue;

            TextComponent textComponent = new TextComponent();
            Rectangle bounds = polygon.getBounds();

            FontMetrics fontMetrics = graphics.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(step.getTooltip());
            int textHeight = fontMetrics.getHeight();

            if (config.objectOverlayTextMode() == HighlightOverlayTextSetting.ABOVE) {
                textComponent.setPosition(new Point(
                        bounds.x + bounds.width / 2 - textWidth / 2,
                        bounds.y - textHeight
                ));
            } else {
                textComponent.setPosition(new Point(
                        bounds.x + bounds.width / 2 - textWidth / 2,
                        bounds.y + bounds.height
                ));
            }

            textComponent.setColor(color);
            textComponent.setText(step.getTooltip());

            textComponent.render(graphics);
        }

        return null;
    }
}

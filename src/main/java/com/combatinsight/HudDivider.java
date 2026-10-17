package com.combatinsight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

/** A layout-aware rule, aligned to the same content bounds as the value rows. */
final class HudDivider implements LayoutableRenderableEntity
{
    private final Rectangle bounds = new Rectangle();
    private Point location = new Point();
    private Dimension size = new Dimension();

    @Override public Rectangle getBounds() { return bounds; }
    @Override public void setPreferredLocation(Point point) { location = point; }
    @Override public void setPreferredSize(Dimension dimension) { size = dimension; }
    @Override public Dimension render(Graphics2D graphics)
    {
        Color previous = graphics.getColor();
        graphics.setColor(new Color(180, 190, 200, 65));
        graphics.drawLine(location.x, location.y + 3, location.x + Math.max(0, size.width - 1), location.y + 3);
        graphics.setColor(previous);
        bounds.setBounds(location.x, location.y, size.width, 7);
        return bounds.getSize();
    }
}

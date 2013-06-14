/*
 * Copyright (C) 2013 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package zhyi.zse.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import javax.swing.border.AbstractBorder;

/**
 * This border implements a drop shadow.
 *
 * @author Zhao Yi
 */
@SuppressWarnings("serial")
public class ShadowBorder extends AbstractBorder {
    private static final Color DARK = new Color(0, 0, 0, 80);
    private static final Color BRIGHT = new Color(0, 0, 0, 2);
    private static final float[] RADIAL_FRACTIONS = {0.0F, 1.0F};
    private static final Color[] RADIAL_COLORS = {DARK.brighter(), BRIGHT.brighter()};

    private int thickness;
    private int offset;
    private Insets insets;

    /**
     * Constructs a new shadow border.
     *
     * @param thickness The thickness of the shadow.
     * @param offset The offset to the owning component.
     */
    public ShadowBorder(int thickness, int offset) {
        this.thickness = thickness;
        this.offset = offset;
        insets = new Insets(0, 0, thickness, thickness);
    }

    @Override
    public void paintBorder(Component c, Graphics g,
            int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;

        // Paint the upper right corner.
        int ox = width - thickness;
        int oy = offset;
        g2.setPaint(new RadialGradientPaint(ox, oy + thickness, thickness,
                RADIAL_FRACTIONS, RADIAL_COLORS));
        g2.fillRect(ox, oy, thickness, thickness);

        // Paint the right side.
        oy = offset + thickness;
        g2.setPaint(new GradientPaint(
                ox, oy, DARK, ox + thickness, oy, BRIGHT));
        g2.fillRect(ox, oy, thickness, height - offset - 2 * thickness);

        // Paint the lower right corner.
        oy = height - thickness;
        g2.setPaint(new RadialGradientPaint(ox, oy, thickness,
                RADIAL_FRACTIONS, RADIAL_COLORS));
        g2.fillRect(ox, oy, thickness, thickness);

        // Paint the bottom side.
        ox = offset + thickness;
        g2.setPaint(new GradientPaint(
                ox, oy, DARK, ox, oy + thickness, BRIGHT));
        g2.fillRect(ox, oy, width - offset - 2 * thickness, thickness);

        // Lowerleft
        ox = offset;
        g2.setPaint(new RadialGradientPaint(ox + thickness, oy, thickness,
                RADIAL_FRACTIONS, RADIAL_COLORS));
        g2.fillRect(ox, oy, thickness, thickness);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = 0;
        insets.left = 0;
        insets.bottom = thickness;
        insets.right = thickness;
        return insets;
    }
}

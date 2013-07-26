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
import java.awt.RenderingHints;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

/**
 * This class implements a drop shadow.
 * <p>
 * To get the best visual effect, it is recommended to make the background of
 * the background of the owning component transparent.
 *
 * @author Zhao Yi
 */
public class ShadowBorder implements Border {
    private static final Color DEFAULT_DARK = new Color(0, 0, 0, 80);
    private static final Color DEFAULT_BRIGHT = new Color(0, 0, 0, 2);
    private static final float[] RADIAL_FRACTIONS = {0.0F, 1.0F};

    private int thickness;
    private int offset;
    private Insets insets;
    private Color dark;
    private Color bright;
    private Color[] radialColors;

    /**
     * Constructs a new shadow border.
     *
     * @param thickness The shadow's thickness.
     * @param offset The shadow's offset to the top and left of the component.
     */
    public ShadowBorder(int thickness, int offset) {
        this(thickness, offset, DEFAULT_DARK, DEFAULT_BRIGHT);
    }

    /**
     * Constructs a new shadow border.
     *
     * @param thickness The shadow's thickness.
     * @param offset The shadow's offset to the top and the left edges
     *        of the component.
     * @param dark The color that is nearest to the component edge.
     * @param bright The color that is farthest from the component edge.
     */
    public ShadowBorder(int thickness, int offset, Color dark, Color bright) {
        this.thickness = thickness;
        this.offset = offset;
        insets = new Insets(0, 0, thickness, thickness);
        this.dark = dark;
        this.bright = bright;
        radialColors = new Color[] {dark.brighter(), bright.brighter()};
    }

    @Override
    public void paintBorder(Component c, Graphics g,
            int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the upper right corner.
        int ox = width - thickness;
        int oy = offset;
        g2.setPaint(new RadialGradientPaint(ox, oy + thickness,
                thickness, RADIAL_FRACTIONS, radialColors));
        g2.fillRect(ox, oy, thickness, thickness);

        // Paint the right side.
        oy = offset + thickness;
        g2.setPaint(new GradientPaint(
                ox, oy, dark, ox + thickness, oy, bright));
        g2.fillRect(ox, oy, thickness, height - offset - 2 * thickness);

        // Paint the lower right corner.
        oy = height - thickness;
        g2.setPaint(new RadialGradientPaint(ox, oy,
                thickness, RADIAL_FRACTIONS, radialColors));
        g2.fillRect(ox, oy, thickness, thickness);

        // Paint the bottom side.
        ox = offset + thickness;
        g2.setPaint(new GradientPaint(
                ox, oy, dark, ox, oy + thickness, bright));
        g2.fillRect(ox, oy, width - offset - 2 * thickness, thickness);

        // Lowerleft
        ox = offset;
        g2.setPaint(new RadialGradientPaint(ox + thickness, oy,
                thickness, RADIAL_FRACTIONS, radialColors));
        g2.fillRect(ox, oy, thickness, thickness);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * Returns the shadow's thickness.
     *
     * @return The shadow's thickness.
     */
    public int getThickness() {
        return thickness;
    }

    /**
     * Returns the shadow's offset to the top and the left edges of the component.
     *
     * @return The shadow's offset.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the color that is nearest to the component edge. The default value
     * is {@code "rgba(0, 0, 0, 80)"}.
     *
     * @return The dark color.
     */
    Color getDark() {
        return dark;
    }

    /**
     * Returns the color that is farthest from the component edge. The default
     * value is {@code "rgba(0, 0, 0, 2)"}.
     *
     * @return The bright color.
     */
     Color getBright() {
        return bright;
    }

    /**
     * A subclass of {@link ShadowBorder} that implements {@link UIResource}.
     */
    public static class ShadowBorderUIResource
            extends ShadowBorder implements UIResource {
        /**
         * Constructs a new shadow border.
         *
         * @param thickness The shadow's thickness.
         * @param offset The shadow's offset to the top and left of the component.
         */
        public ShadowBorderUIResource(int thickness, int offset) {
            super(thickness, offset);
        }

        /**
         * Constructs a new shadow border.
         *
         * @param thickness The shadow's thickness.
         * @param offset The shadow's offset to the top and the left edges
         *        of the component.
         * @param dark The color that is nearest to the component edge.
         * @param bright The color that is farthest from the component edge.
         */
        public ShadowBorderUIResource(int thickness, int offset,
                Color dark, Color bright) {
            super(thickness, offset, dark, bright);
        }
    }
}

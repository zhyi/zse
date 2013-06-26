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
package zhyi.zse.swing.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToolTipUI;
import zhyi.zse.swing.AeroToolTipBorder;

/**
 * The tool tip UI for Windows Aero look and feel.
 *
 * @author Zhao Yi
 */
public class AeroToolTipUI extends BasicToolTipUI {
    private static final AeroToolTipUI SHARED_INSTANCE = new AeroToolTipUI();
    private static final Color TOP_COLOR = Color.WHITE;
    private static final Color BOTTOM_COLOR = new Color(228, 229, 240);

    /**
     * Creates a UI for the specified component.
     *
     * @param c The component for which the UI is created.
     *
     * @return The created UI.
     */
    public static ComponentUI createUI(JComponent c) {
        return SHARED_INSTANCE;
    }

    @Override
    public void update(Graphics g, JComponent c) {
        if (c.getBorder() instanceof AeroToolTipBorder) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension size = c.getSize();
            Insets insets = c.getInsets();
            Color background = c.getBackground();
            if (background instanceof UIResource) {
                g2.setPaint(new GradientPaint(insets.left, insets.top, TOP_COLOR,
                        insets.left, size.height - insets.bottom, BOTTOM_COLOR));
            } else {
                g2.setPaint(background);
            }
            g2.fill(new RoundRectangle2D.Double(
                        0, 0, size.width - 1, size.height - 1, 6, 6));
            super.paint(g, c);
        } else {
            super.update(g, c);
        }
    }
}

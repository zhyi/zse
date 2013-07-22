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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

/**
 * This class implements the Windows Aero styled tool tip border.
 *
 * @author Zhao Yi
 */
public class AeroToolTipBorder implements Border {
    private static final Insets INSETS = new Insets(3, 3, 5, 3);
    private static final Color BORDER_COLOR = new Color(118, 118, 118);

    @Override
    public void paintBorder(Component c, Graphics g,
            int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BORDER_COLOR);
        g2.draw(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, 4, 4));
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return INSETS;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * A subclass of {@link AeroToolTipBorder} that implements {@link UIResource}.
     */
    public static class AeroToolTipBorderUIResource
            extends AeroToolTipBorder implements UIResource {
    }
}

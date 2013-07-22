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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import zhyi.zse.swing.plaf.AeroToolTipBorder;

/**
 * This popup factory adds shadow borders for any popup components.
 *
 * @author Zhao Yi
 */
public class ShadowPopupFactory extends PopupFactory {
    private static final ShadowBorder POPUP_SHADOW_BORDER = new ShadowBorder(4, 4);
    private static final JPanel PATCH = new JPanel();

    @Override
    public Popup getPopup(Component owner, Component contents, int x, int y) {
        Popup popup = super.getPopup(owner, contents, x, y);
        // If the popup component is contained in a heavy weight window,
        // make that window's background transparent.
        Window popupWindow = SwingUtilities.getWindowAncestor(contents);
        if (popupWindow != null) {
            popupWindow.setBackground(new Color(0, 0, 0, 0));
        }
        Container parent = contents.getParent();
        if (parent instanceof JComponent) {
            JComponent p = (JComponent) parent;
            p.setOpaque(false);
            p.setBorder(POPUP_SHADOW_BORDER);
            p.setSize(p.getPreferredSize());
            if (contents instanceof JToolTip
                    && ((JToolTip) contents).getBorder() instanceof AeroToolTipBorder) {
                // Aero tool tip has round corners, so we add a small "patch"
                // to the bottom-right corner to get rid of the noisy point.
                p.setLayout(null);
                Dimension size = contents.getPreferredSize();
                contents.setBounds(0, 0, size.width, size.height);
                PATCH.setBackground(POPUP_SHADOW_BORDER.getDark().brighter());
                p.add(PATCH);
                PATCH.setBounds(p.getWidth() - 5, p.getHeight() - 5, 1, 1);
            }
        }
        return popup;
    }
}

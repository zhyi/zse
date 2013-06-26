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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import zhyi.zse.swing.AeroEditorBorder.AeroEditorBorderUIResource;

/**
 * The Windows Aero styled border for scroll pane.
 *
 * @author Zhao Yi
 */
public class AeroScrollPaneBorder extends AeroEditorBorderUIResource {
    private Border defaultBorder;

    public AeroScrollPaneBorder(Border defaultBorder) {
        super(2, 2, 2, 2);
        this.defaultBorder = defaultBorder;
    }

    @Override
    public void paintBorder(Component c, Graphics g,
            int x, int y, int width, int height) {
        if (shouldPaintAeroEditorBorder(c)) {
            super.paintBorder(c, g, x, y, width, height);
        } else {
            defaultBorder.paintBorder(c, g, x, y, width, height);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return shouldPaintAeroEditorBorder(c)
                ? super.getBorderInsets(c) : defaultBorder.getBorderInsets(c);
    }

    private boolean shouldPaintAeroEditorBorder(Component c) {
        JScrollPane scrollPane = (JScrollPane) c;
        Component view = scrollPane.getViewport().getView();
        return view instanceof JTextComponent || view instanceof JComponent
                && Boolean.TRUE.equals(((JComponent) view).getClientProperty("editable"));
    }
}

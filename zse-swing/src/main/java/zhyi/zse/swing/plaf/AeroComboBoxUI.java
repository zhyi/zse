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

import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;
import java.awt.Container;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * The combo box UI for Windows Aero look and feel.
 * <p>
 * If the current Windows Aero look and feel is set with {@link
 * SwingUtils#switchLookAndFeel}, tool tips will have this kind of UIs by default.
 *
 * @author Zhao Yi
 */
public class AeroComboBoxUI extends WindowsComboBoxUI {
    /**
     * Creates a UI for the specified component.
     *
     * @param c The component for which the UI is created.
     *
     * @return The created UI.
     */
    public static ComponentUI createUI(JComponent c) {
        return new AeroComboBoxUI();
    }

    @Override
    protected LayoutManager createLayoutManager() {
        final LayoutManager layout = super.createLayoutManager();
        return new ComboBoxLayoutManager() {
            @Override
            public void layoutContainer(Container parent) {
                layout.layoutContainer(parent);
                if (arrowButton != null && comboBox.isEditable()
                        && comboBox.getBorder() instanceof AeroEditorBorder) {
                    // Tune the bounds of the arrow button to fit the border.
                    Insets insets = getInsets();
                    Rectangle bounds = arrowButton.getBounds();
                    arrowButton.setBounds(bounds.x + insets.right, 0, bounds.width,
                            bounds.height + insets.top + insets.bottom);
                }
            }
        };
    }
}

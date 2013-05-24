/*
 * Copyright (C) 2012 Zhao Yi
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import zhyi.zse.lang.ExceptionUtils;

/**
 * Utility methods for Swing.
 *
 * @author Zhao Yi
 */
public final class SwingUtils {
    private SwingUtils() {
    }

    /**
     * Enables or disables the specified components and their children.
     *
     * @param enabled    {@code true} to enable, or {@code false} to disable
     *                   the components.
     * @param components The components to be enabled or disabled.
     */
    public static void enableAll(boolean enabled, Component... components) {
        for (Component c : components) {
            c.setEnabled(enabled);
            Component[] children = null;
            if (c instanceof JMenu) {
                children = ((JMenu) c).getMenuComponents();
            } else if (c instanceof Container) {
                children = ((Container) c).getComponents();
            }
            if (children != null) {
                enableAll(enabled, children);
            }
        }
    }

    /**
     * Displays a window with the position relative to a component.
     *
     * @param window            The window component to display.
     * @param relativeComponent The component relative to which the window is
     *                          positioned; may be {@code null}.
     *
     * @see Window#setLocationRelativeTo(Component)
     */
    public static void showWindow(Window window, Component relativeComponent) {
        window.setLocationRelativeTo(relativeComponent);
        window.setVisible(true);
    }

    /**
     * Displays a window with the position relative to its owner.
     *
     * @param window The window component to display.
     *
     * @see Window#setLocationRelativeTo(Component)
     */
    public static void showWindow(Window window) {
        showWindow(window, window.getOwner());
    }

    /**
     * Displays the stack trace of a throwable in a dialog.
     *
     * @param throwable The throwable to be displayed.
     * @param severe    {@code true} for an error icon, or {@code false} for
     *                  a warning icon.
     * @param parent    The component of which the window ancestor is used as
     *                  the owner of the stack trace dialog.
     */
    public static void showStackTrace(Throwable throwable,
            boolean severe, Component parent) {
        JTextArea stackTraceTextArea = new JTextArea(
                ExceptionUtils.printStackTrace(throwable), 20, 80);
        stackTraceTextArea.setEditable(false);
        JOptionPane.showMessageDialog(parent,
                new JScrollPane(stackTraceTextArea), throwable.getClass().getName(),
                severe ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
}

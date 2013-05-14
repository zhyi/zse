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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import zhyi.zse.lang.ExceptionUtils;

/**
 * This dialog is used to display the stack trace of an exception.
 * <p>
 * The static method {@link #display(Exception, Component) display()}
 * is provided to display such a dialog.
 *
 * @author Zhao Yi
 */
@SuppressWarnings("serial")
public final class ExceptionDialog extends JDialog {
    private JLabel iconLabel;
    private JLabel messageLabel;
    private JTextArea stackTraceTextArea;
    private JButton closeButton;

    @SuppressWarnings("LeakingThisInConstructor")
    public ExceptionDialog(Window owner) {
        GuiParser.parse("ExceptionDialog.xml", this);
    }

    public void show(Exception ex) {
        setTitle(ex.getClass().getName());
        messageLabel.setText(ex.getMessage());
        stackTraceTextArea.setText(ExceptionUtils.printStackTrace(ex));
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    private void closeDialog(ActionEvent e) {
        dispose();
    }

    private void dialogShown(ComponentEvent e) {
        closeButton.requestFocusInWindow();
    }

    /**
     * Displays an exception as an error.
     *
     * @param ex The exception to display.
     * @param c  The component of which the window ancestor is to be used as
     *           the owner of the exception dialog; may be {@code null}.
     */
    public static void showError(Exception ex, Component c) {
        showException(ex, c, true);
    }

    /**
     * Displays an exception as a warning.
     *
     * @param ex The exception to display.
     * @param c  The component of which the window ancestor is to be used as
     *           the owner of the exception dialog; may be {@code null}.
     */
    public static void showWarning(Exception ex, Component c) {
        showException(ex, c, false);
    }

    public static void showException(Exception ex, Component c, boolean isError) {
        ResourceBundle rb = ResourceBundle.getBundle("zhyi.zse.swing.ExceptionDialog");
        Window w = (c == null ? null : SwingUtilities.getWindowAncestor(c));
        ExceptionDialog exceptionDialog = new ExceptionDialog(w);
        exceptionDialog.setTitle(
                isError ? rb.getString("errorTitlePrefix")
                        : rb.getString("warningTitlePrefix")
                + ex.getClass().getName());
        exceptionDialog.iconLabel.setIcon(
                isError ? UIManager.getIcon("OptionPane.errorIcon")
                        : UIManager.getIcon("OptionPane.warningIcon"));
        exceptionDialog.messageLabel.setText(ex.getMessage());
        exceptionDialog.stackTraceTextArea.setText(ExceptionUtils.printStackTrace(ex));
        exceptionDialog.stackTraceTextArea.setCaretPosition(0);
        SwingUtils.showWindow(exceptionDialog, w);
    }
}

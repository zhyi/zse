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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import zhyi.zse.lang.ExceptionUtils;

/**
 * Utility methods for Swing.
 *
 * @author Zhao Yi
 */
public final class SwingUtils {
    private static final Pattern MNEMONIC_PATTERN = Pattern.compile("_._");

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
     * Brings up a dialog displaying the specified message wrapped in a scroll
     * pane.
     *
     * @param parent      The component of which the window ancestor is used as
     *                    the owner of the dialog.
     * @param message     The message to be displayed; may be formatted with HTML.
     * @param wrap        Whether to wrap the lines. Takes no effect if
     *                    the message is an HTML string.
     * @param title       The title for the dialog.
     * @param messageType One of the message type defined in {@link JOptionPane}.
     */
    public static void showLongMessage(Component parent, String message,
             boolean wrap, String title, int messageType) {
        if (BasicHTML.isHTMLString(message)) {
            JEditorPane editorPane = new JEditorPane("text/html", message);
            editorPane.setSize(320, 180);
            editorPane.setEditable(false);
            JOptionPane.showMessageDialog(parent,
                    new JScrollPane(editorPane), title, messageType);
        } else {
            JTextArea textArea = new JTextArea(message);
            textArea.setSize(320, 180);
            textArea.setEditable(false);
            if (wrap) {
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
            }
            JOptionPane.showMessageDialog(parent,
                    new JScrollPane(textArea), title, messageType);
        }
    }

    /**
     * Displays the stack trace of an exception in a dialog.
     *
     * @param parent    The component of which the window ancestor is used as
     *                  the owner of the dialog.
     * @param throwable The exception to be displayed.
     * @param severe    {@code true} for an error icon, or {@code false} for
     *                  a warning icon.
     */
    public static void showStackTrace(Component parent,
            Throwable throwable, boolean severe) {
        showLongMessage(parent, ExceptionUtils.printStackTrace(throwable),
                false, throwable.getClass().getName(),
                severe ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Returns the text, without formatting styles, containing in the specified
     * text component.
     *
     * @param textComponent The text component.
     *
     * @return The raw text.
     */
    public static String getRawText(JTextComponent textComponent) {
        try {
            return textComponent.getDocument().getText(
                    0, textComponent.getDocument().getLength());
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sets the text for a label, with the mnemonic character analyzed.
     * <p>
     * The mnemonic character is defined as the first character that is surrounded
     * with dashes ({@code _}) in the text. If no mnemonic character is found,
     * the text is set to the label directly.
     *
     * @param label The label.
     * @param text  The text with an optional mnemonic character.
     */
    public static void setTextWithMnemonic(JLabel label, String text) {
        Mnemonic mnemonic = analyzeMnemonic(text);
        if (mnemonic != null) {
            label.setText(mnemonic.text);
            label.setDisplayedMnemonic(mnemonic.mnemonicChar);
            label.setDisplayedMnemonicIndex(mnemonic.mnemonicIndex);
        } else {
            label.setText(text);
        }
    }

    /**
     * Sets the text for a button, with the mnemonic character analyzed.
     * <p>
     * The mnemonic character is defined as the first character that is surrounded
     * with dashes ({@code _}) in the text. If no mnemonic character is found,
     * the text is set to the button directly.
     *
     * @param button The button.
     * @param text   The text with an optional mnemonic character.
     */
    public static void setTextWithMnemonic(AbstractButton button, String text) {
        Mnemonic mnemonic = analyzeMnemonic(text);
        if (mnemonic != null) {
            button.setText(mnemonic.text);
            button.setMnemonic(mnemonic.mnemonicChar);
            button.setDisplayedMnemonicIndex(mnemonic.mnemonicIndex);
        } else {
            button.setText(text);
        }
    }

    private static Mnemonic analyzeMnemonic(String text) {
        if (text == null) {
            return null;
        }

        Matcher matcher = MNEMONIC_PATTERN.matcher(text);
        if (matcher.find()) {
            String mnemonicMark = matcher.group();
            char mnemonicChar = mnemonicMark.charAt(1);
            int start = matcher.start();
            int end = matcher.end();
            StringBuilder sb = new StringBuilder()
                    .append(text.substring(0, start));
            if (BasicHTML.isHTMLString(text)) {
                // HTML can be complex so it doesn't always work as expected.
                sb.append("<u>").append(mnemonicChar).append("</u>");
            } else {
                sb.append(mnemonicChar);
            }
            sb.append(text.substring(end));
            return new Mnemonic(sb.toString(), mnemonicChar, start);
        } else {
            return null;
        }
    }

    private static class Mnemonic {
        private String text;
        private char mnemonicChar;
        private int mnemonicIndex;

        private Mnemonic(String text, char mnemonicChar, int mnemonicIndex) {
            this.text = text;
            this.mnemonicChar = mnemonicChar;
            this.mnemonicIndex = mnemonicIndex;
        }
    }
}

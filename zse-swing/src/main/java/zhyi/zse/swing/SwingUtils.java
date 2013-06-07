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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.LookAndFeel;
import javax.swing.Painter;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.nimbus.AbstractRegionPainter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import zhyi.zse.lang.ExceptionUtils;
import zhyi.zse.lang.ReflectionUtils;

/**
 * Utility methods for Swing.
 *
 * @author Zhao Yi
 */
public final class SwingUtils {
    private static final Pattern MNEMONIC_PATTERN = Pattern.compile("_._");
    private static final AWTEventListener CONTAINER_MONITOR = new ContainerMonitor();
    private static final PopupFactory SHADOW_POPUP_FACTORY = new ShadowPopupFactory();
    private static final AWTEventListener TEXT_COMPONENT_POPUP_MONITOR
            = new TextComponentPopupMonitor();

    private static boolean containersMonitored;
    private static boolean lafMonitored;
    private static boolean localeMonitored;
    private static boolean shadowPopupEnabled;
    private static boolean defaultTextPopupEnabled;

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

    /**
     * Changes the look and feel at runtime.
     * <p>
     * This method is a convenient variant of {@link #switchLookAndFeel(LookAndFeel)
     * #switchLookAndFeel(LookAndFeel)}. The {@link LookAndFeel} instance is
     * loaded by the given class name, using the current thread's context class
     * loader.
     *
     * @param lafClassName The FQCN of the new look and feel.
     *
     * @see #switchLookAndFeel(LookAndFeel) switchLookAndFeel(LookAndFeel)
     */
    public static void switchLookAndFeel(String lafClassName) {
        try {
            UIManager.setLookAndFeel(lafClassName);
            applyLookAndFeel();
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Changes the look and feel at runtime.
     * <p>
     * The change takes immediate effect. Additionally, the following fixes and
     * enhancements to the UI are applied:
     * <p>
     * <b>Default Popup Menus for Text Components</b>
     * <p>
     * Text components without component popup menus will have default popup
     * menus with context actions like cut, copy, paste, etc.
     * <p>
     * <b>Fixes for Common Look And Feel Issues</b>
     * <ul>
     * <li>The distance of indent in group layout is too small.
     * <li>Drop shadows are missing for popup components.
     * <li>Read-only text components have wrong cursors.
     * </ul>
     * <b>Fixes for Windows Aero Look And Feel Issues</b>
     * <ul>
     * <li>Combo boxes have extra borders and are insufficiently padded.
     * <li>Menu bar menus are insufficiently padded.
     * <li>Some read-only or disabled text components have wrong default
     * background colors.
     * <li>Display properties are not honored by editor pane.
     * </ul>
     * <b>Fixes for Nimbus Look And Feel Issues</b>
     * <ul>
     * <li>Background color and opacity are not honored by some text components.
     * <li>Some read-only text components have wrong default background colors.
     * </ul>
     *
     * @param lookAndFeel The new look and feel to be applied.
     */
    public static void switchLookAndFeel(LookAndFeel lookAndFeel) {
        try {
            UIManager.setLookAndFeel(lookAndFeel);
            applyLookAndFeel();
        } catch (UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void applyLookAndFeel() {
        fixUiIssues();
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
        }
        lafMonitored = true;
        monitorContainers();
    }

    private static void fixUiIssues() {
        // Add shadow for popup components.
        if (!shadowPopupEnabled) {
            PopupFactory.setSharedInstance(SHADOW_POPUP_FACTORY);
            shadowPopupEnabled = true;
        }

        // Add default popup menus for text components.
        if (!defaultTextPopupEnabled) {
            Toolkit.getDefaultToolkit().addAWTEventListener(
                    TEXT_COMPONENT_POPUP_MONITOR, AWTEvent.MOUSE_EVENT_MASK);
            defaultTextPopupEnabled = true;
        }

        // Double the group layout's indent distance.
        LayoutStyle.setInstance(new GroupLayoutStyle(
                UIManager.getLookAndFeel().getLayoutStyle()));

        // Fix specific L&F issues.
        UIDefaults uid = UIManager.getLookAndFeelDefaults();
        switch (UIManager.getLookAndFeel().getName()) {
            case "Windows":
                if (Double.parseDouble(System.getProperty("os.version")) >= 6.0
                        && Boolean.TRUE.equals(Toolkit.getDefaultToolkit()
                                .getDesktopProperty("win.xpstyle.themeActive"))) {
                    uid.put("ComboBox.border", BorderFactory.createEmptyBorder(1, 2, 1, 1));
                    uid.put("Menu.border", BorderFactory.createEmptyBorder(0, 3, 0, 3));
                    uid.put("TextArea.inactiveBackground",
                            UIManager.get("TextArea.disabledBackground"));
                    uid.put("EditorPane.inactiveBackground",
                            UIManager.get("EditorPane.disabledBackground"));
                    uid.put("TextPane.inactiveBackground",
                            UIManager.get("TextPane.disabledBackground"));
                }
                break;
            case "Nimbus":
                uid.put("TextField[Enabled].backgroundPainter",
                        new NimbusTextBackgroundPainter((AbstractRegionPainter)
                                UIManager.get("TextField[Enabled].backgroundPainter")));
                uid.put("FormattedTextField[Enabled].backgroundPainter",
                        new NimbusTextBackgroundPainter(
                                (AbstractRegionPainter) UIManager.get(
                                "FormattedTextField[Enabled].backgroundPainter")));
                uid.put("PasswordField[Enabled].backgroundPainter",
                        new NimbusTextBackgroundPainter((AbstractRegionPainter)
                                UIManager.get("PasswordField[Enabled].backgroundPainter")));
                uid.put("TextArea[Enabled].backgroundPainter",
                        new NimbusTextBackgroundPainter((AbstractRegionPainter)
                                UIManager.get("TextArea[Enabled].backgroundPainter")));
                uid.put("TextArea[Enabled+NotInScrollPane].backgroundPainter",
                        new NimbusTextBackgroundPainter((AbstractRegionPainter)
                                UIManager.get("TextArea[Enabled+NotInScrollPane].backgroundPainter")));
                uid.put("EditorPane[Enabled].backgroundPainter",
                        new NimbusTextBackgroundPainter((AbstractRegionPainter)
                                UIManager.get("EditorPane[Enabled].backgroundPainter")));
                uid.put("TextPane[Enabled].backgroundPainter",
                        new NimbusTextBackgroundPainter((AbstractRegionPainter)
                                UIManager.get("TextPane[Enabled].backgroundPainter")));
        }
    }

    public static void switchLocale(Locale locale) {
        Locale.setDefault(locale);
        JComponent.setDefaultLocale(locale);
        UIManager.getDefaults().setDefaultLocale(locale);
        UIManager.getLookAndFeelDefaults().setDefaultLocale(locale);
        ComponentOrientation o = ComponentOrientation.getOrientation(locale);
        for (Window w :Window.getWindows()) {
            setComponentTreeLocale(w, locale, o);
        }
        localeMonitored = true;
        monitorContainers();
    }

    private static void setComponentTreeLocale(
            Component c, Locale l, ComponentOrientation o) {
        c.setLocale(l);
        c.setComponentOrientation(o);

        if (c instanceof JComponent) {
            JPopupMenu popupMenu = ((JComponent) c).getComponentPopupMenu();
            if (popupMenu != null) {
                setComponentTreeLocale(popupMenu, l, o);
            }
        }

        Component[] children = null;
        if (c instanceof JMenu) {
            children = ((JMenu) c).getMenuComponents();
        }
        if (c instanceof Container) {
            children = ((Container) c).getComponents();
        }
        if (children != null) {
            for (Component child : children) {
                setComponentTreeLocale(child, l, o);
            }
        }
    }

    private static void monitorContainers() {
        if (!containersMonitored) {
            Toolkit.getDefaultToolkit().addAWTEventListener(
                    CONTAINER_MONITOR, AWTEvent.CONTAINER_EVENT_MASK);
            containersMonitored = true;
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

    private static class NimbusTextBackgroundPainter implements Painter<JTextComponent> {
        private static final Decoder DECODER = new Decoder();
        private static final Color INACTIVE_TEXT_BACKGROUND = DECODER.decodeColor0(
                "nimbusBlueGrey", -0.015872955F, -0.07995863F, 0.15294117F, 0);
        private static final Method GET_PROPERTY_PREFIX
                = ReflectionUtils.getDeclaredMethod(BasicTextUI.class, "getPropertyPrefix");

        private AbstractRegionPainter originalPainter;
        private Rectangle2D.Float rec;

        private NimbusTextBackgroundPainter(AbstractRegionPainter originalPainter) {
            this.originalPainter = originalPainter;
        }

        @Override
        public void paint(Graphics2D g, JTextComponent tc, int width, int height) {
            originalPainter.paint(g, tc, width, height);
            if (!tc.isEditable() && tc.getBackground().equals(UIManager.getColor(
                    ReflectionUtils.invoke(GET_PROPERTY_PREFIX, tc.getUI()) + ".background"))) {
                // If the text component is not editable, and the background is
                // not explicitly set, repaint the background. Note that in Nimbus
                // L&F, isBackgroundSet() always returns true so it's not reliable.
                // The above check is imperfect but should have covered most cases.
                g.setPaint(INACTIVE_TEXT_BACKGROUND);
                if (tc.getParent() instanceof JViewport) {
                    rec.setRect(DECODER.decodeX0(0.0F), DECODER.decodeY0(0.0F),
                            DECODER.decodeX0(3.0F) - DECODER.decodeX0(0.0F),
                            DECODER.decodeY0(3.0F) - DECODER.decodeY0(0.0F));
                } else {
                    rec.setRect(DECODER.decodeX0(0.4F), DECODER.decodeY0(0.4F),
                            DECODER.decodeX0(2.6F) - DECODER.decodeX0(0.4F),
                            DECODER.decodeY0(2.6F) - DECODER.decodeY0(0.4F));
                }
                g.fill(rec);
            }
        }
    }

    private static class Decoder extends AbstractRegionPainter {
        private float decodeX0(float x) {
            return super.decodeX(x);
        }

        private float decodeY0(float y) {
            return super.decodeY(y);
        }

        private Color decodeColor0(String key, float hOffset,
                float sOffset, float bOffset, int aOffset) {
            return super.decodeColor(key, hOffset, sOffset, bOffset, aOffset);
        }

        @Override
        protected PaintContext getPaintContext() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        protected void doPaint(Graphics2D g, JComponent c,
                int width, int height, Object[] extendedCacheKeys) {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class ContainerMonitor implements AWTEventListener {
        private static final Field UI
                = ReflectionUtils.getDeclaredField(JComponent.class, "ui");
        private static final String METAL_THEME_KEY = "metalTheme";

        @Override
        public void eventDispatched(AWTEvent event) {
            ContainerEvent ce = (ContainerEvent) event;
            if (ce.getID() == ContainerEvent.COMPONENT_ADDED) {
                Component c = ce.getChild();

                if (c instanceof JTextComponent) {
                    // Use text cursors for text components (editable or not)
                    // by default.
                    if (!c.isCursorSet()) {
                        c.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                    }

                    // Fix the background color for text areas.
                    // See BasicTextUI.updateBackground(JTextComponent).
                    if (c instanceof JTextArea || c instanceof JEditorPane
                            || c instanceof JTextPane) {
                        c.addPropertyChangeListener(new TextBackgroundMonitor());
                    }

                    // Honor display properties for editor panes by default.
                    if (c instanceof JEditorPane) {
                        JEditorPane ep = (JEditorPane) c;
                        if (ep.getClientProperty(
                                JEditorPane.HONOR_DISPLAY_PROPERTIES) == null) {
                            ep.putClientProperty(
                                JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
                        }
                    }
                }

                // Update L&F as needed.
                if (lafMonitored && c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    Object ui = ReflectionUtils.getValue(UI, jc);
                    if (ui != null) {
                        if (!ui.getClass().equals(
                                UIManager.getDefaults().getUIClass(jc.getUIClassID()))) {
                            jc.updateUI();
                        } else if (UIManager.getLookAndFeel().getName().equals("Metal")) {
                            MetalTheme mt = MetalLookAndFeel.getCurrentTheme();
                            if (!jc.getClientProperty(METAL_THEME_KEY).equals(mt)) {
                                jc.updateUI();
                                jc.putClientProperty(METAL_THEME_KEY, mt);
                            }
                        }
                    }
                }

                // Update locale as needed.
                if (localeMonitored) {
                    Locale l = Locale.getDefault();
                    ComponentOrientation o = ComponentOrientation.getOrientation(l);
                    if (!l.equals(c.getLocale())) {
                        c.setLocale(l);
                    }
                    if (!o.equals(c.getComponentOrientation())) {
                        c.setComponentOrientation(o);
                    }
                }
            }
        }
    }

    private static final class ShadowPopupFactory extends PopupFactory {
        private static final Border POPUP_SHADOW_BORDER = new ShadowBorder(4, 4);

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
                JComponent c = (JComponent) parent;
                c.setOpaque(false);
                c.setBorder(POPUP_SHADOW_BORDER);
                c.setSize(c.getPreferredSize());
            }

            return popup;
        }
    }

    private static final class TextComponentPopupMonitor implements AWTEventListener {
        private static final JPopupMenu TEXT_POPUP_MENU = new JPopupMenu();
        private static final Map<JTextComponent, Action[]>
                TEXT_POPUP_ACTION_MAP = new WeakHashMap<>();

        @Override
        public void eventDispatched(AWTEvent event) {
            MouseEvent me = (MouseEvent) event;
            if (me.isPopupTrigger()) {
                if (me.getComponent() instanceof JTextComponent) {
                    JTextComponent tc = (JTextComponent) me.getComponent();
                    if (tc.isEnabled() && tc.getComponentPopupMenu() == null) {
                        ContextActionHandler cah = (ContextActionHandler)
                                tc.getClientProperty(ContextActionHandler.KEY);
                        if (cah == null) {
                            cah = new TextContextActionHandler(tc);
                            tc.putClientProperty(ContextActionHandler.KEY, cah);
                        }

                        Action[] popupActions = TEXT_POPUP_ACTION_MAP.get(tc);
                        if (popupActions == null) {
                            popupActions = new Action[11];
                            popupActions[0] = ContextActionFactory.createUndoAction(tc);
                            popupActions[1] = ContextActionFactory.createRedoAction(tc);
                            popupActions[2] = ContextActionFactory.createCutAction(tc);
                            popupActions[3] = ContextActionFactory.createCopyAction(tc);
                            popupActions[4] = ContextActionFactory.createPasteAction(tc);
                            popupActions[5] = ContextActionFactory.createDeleteAction(tc);
                            popupActions[6] = ContextActionFactory.createSelectAllAction(tc);
                            popupActions[7] = ContextActionFactory.createCutAllAction(tc);
                            popupActions[8] = ContextActionFactory.createCopyAllAction(tc);
                            popupActions[9] = ContextActionFactory.createReplaceAllAction(tc);
                            popupActions[10] = ContextActionFactory.createDeleteAllAction(tc);
                            TEXT_POPUP_ACTION_MAP.put(tc, popupActions);
                        }

                        boolean editable = tc.isEditable();
                        boolean hasText = !SwingUtils.getRawText(tc).isEmpty();
                        boolean hasSelectedText = tc.getSelectedText() != null;
                        boolean canImport = tc.getTransferHandler().canImport(tc,
                                Toolkit.getDefaultToolkit().getSystemClipboard()
                                        .getAvailableDataFlavors());
                        TEXT_POPUP_MENU.removeAll();
                        if (editable) {
                            UndoManager um = cah.getUndoManager();
                            popupActions[0].putValue(Action.NAME,
                                    um.getUndoPresentationName());
                            popupActions[0].setEnabled(um.canUndo());
                            popupActions[1].putValue(Action.NAME,
                                    um.getRedoPresentationName());
                            popupActions[1].setEnabled(um.canRedo());
                            TEXT_POPUP_MENU.add(popupActions[0]);    // Undo
                            TEXT_POPUP_MENU.add(popupActions[1]);    // Redo
                            TEXT_POPUP_MENU.addSeparator();
                            popupActions[2].setEnabled(hasSelectedText);
                            TEXT_POPUP_MENU.add(popupActions[2]);    // Cut
                        }
                        popupActions[3].setEnabled(hasSelectedText);
                        TEXT_POPUP_MENU.add(popupActions[3]);    // Copy
                        if (editable) {
                            popupActions[4].setEnabled(canImport);
                            TEXT_POPUP_MENU.add(popupActions[4]);    // Paste
                            popupActions[5].setEnabled(hasSelectedText);
                            TEXT_POPUP_MENU.add(popupActions[5]);    // Delete
                        }
                        TEXT_POPUP_MENU.addSeparator();
                        if (editable) {
                            popupActions[6].setEnabled(hasText);
                            TEXT_POPUP_MENU.add(popupActions[6]);    // Select All
                            popupActions[7].setEnabled(hasText);
                            TEXT_POPUP_MENU.add(popupActions[7]);    // Cut All
                        }
                        popupActions[7].setEnabled(hasText);
                        TEXT_POPUP_MENU.add(popupActions[8]);    // Copy All
                        if (editable) {
                            popupActions[9].setEnabled(canImport);
                            TEXT_POPUP_MENU.add(popupActions[9]);    // Replace All
                            popupActions[10].setEnabled(hasText);
                            TEXT_POPUP_MENU.add(popupActions[10]);    // Delete All
                        }
                        TEXT_POPUP_MENU.show(tc, me.getX(), me.getY());
                    }
                }
            }
        }
    }

    private static class GroupLayoutStyle extends LayoutStyle {
        private LayoutStyle delegatedLayoutStyle;

        private GroupLayoutStyle(LayoutStyle delegatedLayoutStyle) {
            this.delegatedLayoutStyle = delegatedLayoutStyle;
        }

        @Override
        public int getPreferredGap(JComponent component1, JComponent component2,
                ComponentPlacement type, int position, Container parent) {
            if (type == ComponentPlacement.INDENT) {
                return 2 * delegatedLayoutStyle.getPreferredGap(
                        component1, component2, ComponentPlacement.UNRELATED,
                        position, parent);
            } else {
                return delegatedLayoutStyle.getPreferredGap(
                        component1, component2, type, position, parent);
            }
        }

        @Override
        public int getContainerGap(JComponent component,
                int position, Container parent) {
            return delegatedLayoutStyle.getContainerGap(
                    component, position, parent);
        }
    }

    private static class TextBackgroundMonitor implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name.equals("enabled") || name.equals("editable")) {
                JTextComponent tc = (JTextComponent) evt.getSource();
                if (!tc.isBackgroundSet()
                        || tc.getBackground() instanceof UIResource) {
                    if (tc.isEnabled()) {
                        tc.setBackground(tc.isEditable()
                                ? UIManager.getColor("TextArea.background")
                                : UIManager.getColor("TextArea.inactiveBackground"));
                    } else if (tc instanceof JTextArea) {
                        tc.setBackground(UIManager.getColor("TextArea.disabledBackground"));
                    }
                }
            }
        }
    }
}

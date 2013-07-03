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
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.LookAndFeel;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.BorderUIResource.EmptyBorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.nimbus.AbstractRegionPainter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import zhyi.zse.lang.ExceptionUtils;
import zhyi.zse.lang.ReflectionUtils;
import zhyi.zse.swing.AeroEditorBorder.AeroEditorBorderUIResource;
import zhyi.zse.swing.AeroToolTipBorder.AeroToolTipBorderUIResource;
import zhyi.zse.swing.cas.ContextActionSupport;
import zhyi.zse.swing.cas.TextContextActionSupport;
import zhyi.zse.swing.plaf.AeroComboBoxUI;
import zhyi.zse.swing.plaf.AeroScrollPaneBorder;
import zhyi.zse.swing.plaf.AeroToolTipUI;
import zhyi.zse.swing.plaf.NimbusTextBackgroundPainter;

/**
 * Utility methods for Swing.
 *
 * @author Zhao Yi
 */
public final class SwingUtils {
    private static final AWTEventListener CONTAINER_HANDLER = new ContainerHandler();
    private static final AWTEventListener AERO_EDITOR_BORDER_HANDLER = new AeroEditorBorderHandler();
    private static final PopupFactory SHADOW_POPUP_FACTORY = new ShadowPopupFactory();
    private static final AWTEventListener CONTEXT_POPUP_HANDLER = new ContextPopupHandler();
    private static final Method GET_PROPERTY_PREFIX
            = ReflectionUtils.getDeclaredMethod(BasicTextUI.class, "getPropertyPrefix");

    private static boolean containerMonitored;
    private static boolean lafMonitored;
    private static boolean localeMonitored;
    private static boolean aeroEditorBorderMonitored;
    private static boolean shadowPopupEnabled;
    private static boolean casPopupEnabled;

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
     * by dashes ({@code _}) in the text. If no mnemonic character is found,
     * the text is set to the label directly.
     *
     * @param label The label.
     * @param text  The text with an optional mnemonic character.
     *
     * @see Mnemonic#analyze
     */
    public static void setTextWithMnemonic(JLabel label, String text) {
        Mnemonic mnemonic = Mnemonic.analyze(text);
        if (mnemonic != null) {
            label.setText(mnemonic.getText());
            label.setDisplayedMnemonic(mnemonic.getMnemonicChar());
            label.setDisplayedMnemonicIndex(mnemonic.getInex());
        } else {
            label.setText(text);
        }
    }

    /**
     * Sets the text for a button, with the mnemonic character analyzed.
     * <p>
     * The mnemonic character is defined as the first character that is surrounded
     * by dashes ({@code _}) in the text. If no mnemonic character is found,
     * the text is set to the button directly.
     *
     * @param button The button.
     * @param text   The text with an optional mnemonic character.
     *
     * @see Mnemonic#analyze
     */
    public static void setTextWithMnemonic(AbstractButton button, String text) {
        Mnemonic mnemonic = Mnemonic.analyze(text);
        if (mnemonic != null) {
            button.setText(mnemonic.getText());
            button.setMnemonic(mnemonic.getMnemonicChar());
            button.setDisplayedMnemonicIndex(mnemonic.getInex());
        } else {
            button.setText(text);
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
     * <b>Default Popup Menus</b>
     * <p>
     * Any component that is installed with a client property with the key as
     * {@link ContextActionSupport#CONTEXT_ACTION_SUPPORT} and the value as
     * an instance of {@link ContextActionSupport} will have a default popup menu.
     * Specially, text components are automatically installed with such client
     * properties.
     * <p>
     * <b>Fixes for Common Look And Feel Issues</b>
     * <ul>
     * <li>The distance of indent in group layout is too small.
     * <li>Drop shadows are missing for popup components.
     * <li>Read-only text components have wrong cursors.
     * <li>Display properties are not honored by editor panes.
     * </ul>
     * <b>Fixes for Windows Aero Look And Feel Issues</b>
     * <ul>
     * <li>Text components, editable combo boxes, spinners and scroll panes have
     * wrong borders.
     * <li>Read-only combo boxes have extra borders and are insufficiently
     * padded.
     * <li>Menu bar menus are insufficiently padded.
     * <li>Some read-only or disabled text components have wrong default
     * background colors.
     * <li>Text components, editable combo boxes and spinners have wrong borders.
     * <li>Tool tips have wrong UIs.
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
        Toolkit tk = Toolkit.getDefaultToolkit();

        // Add shadow for popup components.
        if (!shadowPopupEnabled) {
            PopupFactory.setSharedInstance(SHADOW_POPUP_FACTORY);
            shadowPopupEnabled = true;
        }

        // Add default popup menus for text components.
        if (!casPopupEnabled) {
            tk.addAWTEventListener(CONTEXT_POPUP_HANDLER, AWTEvent.MOUSE_EVENT_MASK);
            casPopupEnabled = true;
        }

        // Enlarge the group layout's indent distance.
        LayoutStyle.setInstance(new GroupLayoutStyle());

        // Fix specific L&F issues.
        UIDefaults uid = UIManager.getLookAndFeelDefaults();
        String laf = UIManager.getLookAndFeel().getName();
        if (laf.equals("Windows")
                && Double.parseDouble(System.getProperty("os.version")) >= 6.0
                && Boolean.TRUE.equals(tk.getDesktopProperty("win.xpstyle.themeActive"))) {
            uid.put("ToolTipUI", AeroToolTipUI.class.getName());
            uid.put("ComboBoxUI", AeroComboBoxUI.class.getName());
            uid.put("TextField.border", new AeroEditorBorderUIResource(3, 5, 3, 5));
            uid.put("PasswordField.border", new AeroEditorBorderUIResource(3, 5, 3, 5));
            uid.put("ComboBox.border", new AeroEditorBorderUIResource(1, 3, 1, 1));
            uid.put("Spinner.border", new AeroEditorBorderUIResource(3, 3, 3, 3));
            uid.put("ScrollPane.border",
                    new AeroScrollPaneBorder(uid.getBorder("ScrollPane.border")));
            uid.put("ToolTip.border", new AeroToolTipBorderUIResource());
            uid.put("Menu.border", new EmptyBorderUIResource(0, 3, 0, 3));
            uid.put("TextArea.inactiveBackground",
                    UIManager.get("TextArea.disabledBackground"));
            uid.put("EditorPane.inactiveBackground",
                    UIManager.get("EditorPane.disabledBackground"));
            uid.put("TextPane.inactiveBackground",
                    UIManager.get("TextPane.disabledBackground"));

            if (!aeroEditorBorderMonitored) {
                tk.addAWTEventListener(AERO_EDITOR_BORDER_HANDLER,
                        AWTEvent.FOCUS_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
                aeroEditorBorderMonitored = true;
            }
        } else {
            if (laf.equals("Nimbus")) {
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

            if (aeroEditorBorderMonitored) {
                tk.removeAWTEventListener(AERO_EDITOR_BORDER_HANDLER);
                aeroEditorBorderMonitored = false;
            }
        }
    }

    /**
     * Changes the locale at runtime.
     * <p>
     * The change takes immediate effect. After invoking this method, the default
     * locales of JVM, {@link JComponent}, UI defaults and all created components
     * are set to the specified one. The orientations of all created components
     * are also changed accordingly. This method can work together with "locale"
     * property change listeners to implement switching the UI language at runtime.
     *
     * @param locale The new locale to be applied.
     */
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
        if (!containerMonitored) {
            Toolkit.getDefaultToolkit().addAWTEventListener(
                    CONTAINER_HANDLER, AWTEvent.CONTAINER_EVENT_MASK);
            containerMonitored = true;
        }
    }

    private static class ContainerHandler implements AWTEventListener {
        private static final Field UI
                = ReflectionUtils.getDeclaredField(JComponent.class, "ui");
        private static final PropertyChangeListener TEXT_BACKGROUND_MONITOR
                = new TextBackgroundHandler();

        @Override
        public void eventDispatched(AWTEvent event) {
            ContainerEvent ce = (ContainerEvent) event;
            if (ce.getID() == ContainerEvent.COMPONENT_ADDED) {
                Component c = ce.getChild();

                if (c instanceof JTextComponent) {
                    JTextComponent tc = (JTextComponent) c;

                    // Use text cursors for text components (editable or not)
                    // by default.
                    if (!tc.isCursorSet()) {
                        tc.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                    }

                    // Fix the background colors of some text components.
                    if (tc instanceof JTextArea || tc instanceof JEditorPane
                            || tc instanceof JTextPane) {
                        Boolean monitored = (Boolean) tc.getClientProperty(
                                PropertyKey.TEXT_BACKGROUND_MONITORED);
                        if (monitored == null) {
                            tc.addPropertyChangeListener(TEXT_BACKGROUND_MONITOR);
                            TEXT_BACKGROUND_MONITOR.propertyChange(
                                    new PropertyChangeEvent(tc, "enabled", null, null));
                            tc.putClientProperty(
                                    PropertyKey.TEXT_BACKGROUND_MONITORED, Boolean.TRUE);
                        }
                    }

                    // Honor display properties for editor panes by default.
                    if (tc instanceof JEditorPane) {
                        JEditorPane ep = (JEditorPane) tc;
                        if (ep.getClientProperty(
                                JEditorPane.HONOR_DISPLAY_PROPERTIES) == null) {
                            ep.putClientProperty(
                                JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
                        }
                    }

                    // Add default context action support.
                    if (tc.getClientProperty(ContextActionSupport.CONTEXT_ACTION_SUPPORT) == null) {
                        tc.putClientProperty(
                                ContextActionSupport.CONTEXT_ACTION_SUPPORT,
                                new TextContextActionSupport(tc));
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
                            if (!jc.getClientProperty(PropertyKey.METAL_THEME).equals(mt)) {
                                jc.updateUI();
                                jc.putClientProperty(PropertyKey.METAL_THEME, mt);
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

    private static class AeroEditorBorderHandler implements AWTEventListener {
        private JComponent activeAeroEditor;

        @Override
        public void eventDispatched(AWTEvent event) {
            if (event instanceof ComponentEvent) {
                ComponentEvent ce = (ComponentEvent) event;
                Component c = ce.getComponent();

                JComponent aeroEditor = null;
                while (c != null) {
                    if (c instanceof JComponent
                            && ((JComponent) c).getBorder() instanceof AeroEditorBorder) {
                        aeroEditor = (JComponent) c;
                        break;
                    }
                    c = c.getParent();
                }

                switch (event.getID()) {
                    case FocusEvent.FOCUS_GAINED:
                    case FocusEvent.FOCUS_LOST:
                        if (aeroEditor != null) {
                            updateAeroBorder(aeroEditor);
                        }
                        return;
                    case MouseEvent.MOUSE_MOVED:
                        if (activeAeroEditor != null) {
                            updateAeroBorder(activeAeroEditor);
                        }
                        if (aeroEditor != null) {
                            activeAeroEditor = aeroEditor;
                            updateAeroBorder(aeroEditor);
                        }
                }
            }
        }

        private void updateAeroBorder(JComponent c) {
            if (AeroEditorBorder.updateState(c)) {
                c.repaint();
            }
        }
    }

    private static class ContextPopupHandler implements AWTEventListener {
        private int x;
        private int y;

        @Override
        public void eventDispatched(AWTEvent event) {
            if (event instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) event;
                if (me.isPopupTrigger()) {
                    Component c = me.getComponent();
                    if (c instanceof JComponent && c.isEnabled()) {
                        final JComponent jc = (JComponent) c;
                        x = me.getX();
                        y = me.getY();
                        if (jc.isFocusOwner()) {
                            mayShowContextPopupMenu(jc);
                        } else if (jc.requestFocusInWindow()) {
                            // Make sure the popup menu is shown only if
                            // the component has gained focus.
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (jc.isFocusOwner()) {
                                        mayShowContextPopupMenu(jc);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }

        private void mayShowContextPopupMenu(JComponent c) {
            if (c.getComponentPopupMenu() == null) {
                ContextActionSupport<?> cas = (ContextActionSupport<?>)
                        c.getClientProperty(ContextActionSupport.CONTEXT_ACTION_SUPPORT);
                if (cas != null) {
                    cas.showContextPopupMenu(x, y);
                }
            }
        }
    }

    private static class TextBackgroundHandler implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name.equals("enabled") || name.equals("editable")) {
                JTextComponent tc = (JTextComponent) evt.getSource();
                if (!tc.isBackgroundSet()
                        || tc.getBackground() instanceof UIResource) {
                    if (tc.isEnabled()) {
                        String prefix = (String) ReflectionUtils.invoke(
                                GET_PROPERTY_PREFIX, tc.getUI());
                        tc.setBackground(tc.isEditable()
                                ? UIManager.getColor(prefix + ".background")
                                : UIManager.getColor(prefix + ".inactiveBackground"));
                    } else if (tc instanceof JTextArea) {
                        tc.setBackground(UIManager.getColor("TextArea.disabledBackground"));
                    }
                }
            }
        }
    }

    private static enum PropertyKey {
        METAL_THEME, TEXT_BACKGROUND_MONITORED;
    }
}

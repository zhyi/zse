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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.LookAndFeel;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.BorderUIResource.EmptyBorderUIResource;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.plaf.basic.BasicTableHeaderUI.MouseInputHandler;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.plaf.nimbus.AbstractRegionPainter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import zhyi.zse.lang.BeanUtils;
import zhyi.zse.lang.ExceptionUtils;
import zhyi.zse.lang.ReflectionUtils;
import zhyi.zse.swing.cas.ContextActionSupport;
import zhyi.zse.swing.cas.TableHeaderContextActionSupport;
import zhyi.zse.swing.cas.TextComponentContextActionSupport;
import zhyi.zse.swing.plaf.AeroComboBoxUI;
import zhyi.zse.swing.plaf.AeroEditorBorder;
import zhyi.zse.swing.plaf.AeroEditorBorder.AeroEditorBorderUIResource;
import zhyi.zse.swing.plaf.AeroScrollPaneBorder;
import zhyi.zse.swing.plaf.AeroToolTipBorder.AeroToolTipBorderUIResource;
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
    private static final AWTEventListener CONTEXT_POPUP_HANDLER = new ContextPopupHandler();
    private static final PropertyChangeListener TEXT_BACKGROUND_HANDLER = new TextBackgroundHandler();
    private static final PopupFactory SHADOW_POPUP_FACTORY = new ShadowPopupFactory();
    private static final Method GET_PROPERTY_PREFIX
            = ReflectionUtils.getDeclaredMethod(BasicTextUI.class, "getPropertyPrefix");
    private static final Method GET_HEADER_RENDERER
            = ReflectionUtils.getDeclaredMethod(
                    BasicTableHeaderUI.class, "getHeaderRenderer", int.class);

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
     * <b>Context Action Support</b>
     * <p>
     * Any component can store a {@link PropertyKeys#CONTEXT_ACTION_SUPPORT}
     * client property to have context action support, and bring up context
     * popup menu by right-clicking if the component popup menu is not set.
     * Specially, text component, table header and link will be installed with
     * this support by default when being added to container and no such client
     * property is present.
     * <p>
     * <b>Enhancements for Table</b>
     * <ul>
     * <li>Table row heights are automatically fit if the table's
     * {@link PropertyKeys#AUTO_FIT_ROW_HEIGHTS} is set to {@link Boolean#TRUE}.
     * This property is turned on by default when it is added to a container
     * and no such client property is present.
     * <li>If a table cell renderer component's {@link PropertyKeys#REAL_CELL_COMPONENT}
     * is set to {@link Boolean#TRUE}, it will be added to the table as a real
     * component instead of a static painter. To simplify the implementation,
     * a table can have at most two real renderer components (one currently active
     * and the other last focused) at the same time. If there are lots of cells
     * needing to repaint itself from time to time, stick to the static renderer
     * and update the corresponding table cell rectangle on demand.
     * <li>Table column can be resized to fit by double clicking table header's
     * separator.
     * </ul>
     * <b>Common Fixes and Enhancements</b>
     * <ul>
     * <li>The distance of indent in group layout is enlarged.
     * <li>Drop shadow is shown for popup components.
     * <li>Text component will always have text cursor.
     * <li>Turn on {@link JEditorPane#HONOR_DISPLAY_PROPERTIES} when editor pane
     * is added to container and no such client property is present.
     * </ul>
     * <b>Fixes and Enhancements for Windows Aero Look And Feel</b>
     * <ul>
     * <li>Aero styled {@link AeroEditorBorder} is set to single-line text
     * component, multi-line text component's enclosing scroll pane, editable
     * combo box, spinner by default. For custom editor, it can have a client
     * property {@link PropertyKeys#AERO_EDITOR} set to {@link Boolean#TRUE}
     * to make its (or its enclosing scroll pane's) {@link AeroEditorBorder}
     * have highlight effect.
     * <li>Removed the extra border for read-only combo box and tuned the padding.
     * <li>Tuned the padding for menu in menu bar.
     * <li>Fixed background color for some read-only or disabled text component.
     * <li>Aero styled {@link AeroToolTipUI} is set to tool tip.
     * </ul>
     * <b>Fixes and Enhancements for Nimbus Look And Feel</b>
     * <ul>
     * <li>Tuned the painter for painting text component's background.
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
        final LookAndFeel laf = UIManager.getLookAndFeel();
        for (Window w : Window.getWindows()) {
            updateComponentTree(w, new ComponentUpdater() {
                @Override
                public void update(Component component) {
                    if (component instanceof JComponent) {
                        JComponent jc = (JComponent) component;
                        jc.updateUI();
                        jc.putClientProperty(Key.LOOK_AND_FEEL, laf);
                    }
                }
            });
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

        // Show default popup menus for components that have context actions.
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
    public static void switchLocale(final Locale locale) {
        Locale.setDefault(locale);
        JComponent.setDefaultLocale(locale);
        UIManager.getDefaults().setDefaultLocale(locale);
        UIManager.getLookAndFeelDefaults().setDefaultLocale(locale);
        final ComponentOrientation o = ComponentOrientation.getOrientation(locale);
        for (Window w : Window.getWindows()) {
            updateComponentTree(w, new ComponentUpdater() {
                @Override
                public void update(Component component) {
                    component.setLocale(locale);
                    component.setComponentOrientation(o);
                }
            });
        }
        localeMonitored = true;
        monitorContainers();
    }

    public static void updateComponentTree(
            Component root, ComponentUpdater updater) {
        updater.update(root);

        if (root instanceof JComponent) {
            JPopupMenu popupMenu = ((JComponent) root).getComponentPopupMenu();
            if (popupMenu != null) {
                updateComponentTree(popupMenu, updater);
            }
        }

        Component[] children = null;
        if (root instanceof JMenu) {
            children = ((JMenu) root).getMenuComponents();
        } else if (root instanceof Container) {
            children = ((Container) root).getComponents();
        }
        if (children != null) {
            for (Component child : children) {
                updateComponentTree(child, updater);
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

    /**
     * Makes a component that own a view to have a fixed size along one axis,
     * and the preferred size along the other. Text components and components
     * that has a {@link BasicHTML#propertyKey} client property are recognized
     * as view owners. If the component does not own a view, no action will be
     * taken.
     *
     * @param component The component to be resized.
     * @param size The size to be fixed along the axis.
     * @param axis The axis along which the size is to be fixed.
     */
    public static void fitView(JComponent component, int size, int axis) {
        View view = (View) component.getClientProperty(BasicHTML.propertyKey);
        if (view == null && component instanceof JTextComponent) {
            view = ((JTextComponent) component).getUI()
                    .getRootView((JTextComponent) component);
        }
        if (view != null) {
            fitView(component, view, size, axis);
        }
    }

    /**
     * Makes a component that own a view to have a fixed size along one axis, and
     * the preferred size along the other.
     *
     * @param component The component to be resized.
     * @param view The view owned by the component.
     * @param size The size to be fixed along the axis.
     * @param axis The axis along which the size is to be fixed.
     */
    public static void fitView(JComponent component, View view, int size, int axis) {
        if (axis != View.X_AXIS && axis != View.Y_AXIS) {
            throw new IllegalArgumentException("Invalid axis " + axis + ".");
        }

        Insets insets = component.getInsets();
        if (axis == View.X_AXIS) {
            int width = size - insets.left - insets.right;
            view.setSize(width, 0);
            int height = (int) Math.ceil(view.getPreferredSpan(View.Y_AXIS)
                    + insets.top + insets.bottom);
            view.setSize(width, height);
            component.setPreferredSize(new Dimension(size, height));
        } else {
            int height = size - insets.top - insets.bottom;
            view.setSize(0, height);
            int width = (int) Math.ceil(view.getPreferredSpan(View.X_AXIS))
                    + insets.left + insets.right;
            view.setSize(width, height);
            component.setPreferredSize(new Dimension(width, size));
        }
    }

    /**
     * Resizes the specified table row to fit contents. {@link JTable#getRowHeight}
     * is used as the minimum row height.
     *
     * @param table The table of which the specified row is to be resized.
     * @param row The row to be resized.
     */
    public static void fitRowHeight(JTable table, int row) {
        int rowHeight = table.getRowHeight();
        int columnCount = table.getColumnCount();
        for (int column = 0; column < columnCount; column++) {
            rowHeight = Math.max(rowHeight,
                    getPreferredCellSize(table, row, column,
                            table.getColumnModel().getColumn(column).getWidth(),
                            View.X_AXIS).height);
        }
        if (rowHeight != table.getRowHeight(row)) {
            table.setRowHeight(row, rowHeight);
        }
    }

    /**
     * Resizes all table rows to fit contents. {@link JTable#getRowHeight}
     * is used as the minimum row height.
     *
     * @param table The table of which all rows are to be resized.
     */
    public static void fitAllRowHeights(JTable table) {
        int rowCount = table.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            fitRowHeight(table, row);
        }
    }

    /**
     * Resizes the specified table column to fit contents.
     * {@link TableColumn#getMinWidth} is used as the minimum column width.
     * If resizing is not allowed, no action will be taken. Note that the result
     * is affected by the table's {@link JTable#getAutoResizeMode auto resize model}.
     *
     * @param table The table of which the specified column is to be resized.
     * @param column The column to be resized.
     */
    public static void fitColumnWidth(JTable table, int column) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        JTableHeader tableHeader = table.getTableHeader();

        if (!table.isEnabled() || !tableColumn.getResizable()
                || tableHeader != null && !tableHeader.isEnabled()
                || tableHeader != null && !tableHeader.getResizingAllowed()) {
            return;
        }

        int columnWidth = tableColumn.getMinWidth();
        int rowCount = table.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            columnWidth = Math.max(columnWidth, getPreferredCellSize(table,
                    row, column, table.getRowHeight(row), View.Y_AXIS).width);
        }

        if (tableHeader != null) {
            TableHeaderUI headerUi = tableHeader.getUI();
            Component headerComponent;
            if (headerUi instanceof BasicTableHeaderUI) {
                headerComponent = (Component) ReflectionUtils.invoke(
                        GET_HEADER_RENDERER, tableHeader.getUI(), column);
            } else {
                TableCellRenderer headerRenderer = tableColumn.getHeaderRenderer();
                if (headerRenderer == null) {
                    headerRenderer = tableHeader.getDefaultRenderer();
                }
                headerComponent = headerRenderer.getTableCellRendererComponent(
                        table, tableColumn.getHeaderValue(), false, false, -1, column);
            }
            columnWidth = Math.max(columnWidth, headerComponent.getPreferredSize().width);
        }

        if (columnWidth != tableColumn.getPreferredWidth()) {
            tableColumn.setPreferredWidth(columnWidth);
        }
    }

    /**
     * Resizes all table columns to fit contents. {@link TableColumn#getMinWidth}
     * is used as the minimum column width. Only resizable column is resized,
     * and the result is affected by the table's {@link JTable#getAutoResizeMode
     * auto resize model}.
     *
     * @param table The table of which all columns are to be resized.
     */
    public static void fitAllColumnWidth(JTable table) {
        int columnCount = table.getColumnCount();
        for (int column = 0; column < columnCount; column++) {
            fitColumnWidth(table, column);
        }
    }

    private static Dimension getPreferredCellSize(
            JTable table, int row, int column, int size, int axis) {
        if (row == table.getEditingRow() && column == table.getEditingColumn()) {
            return table.getEditorComponent().getPreferredSize();
        } else {
            Component renderer = table.prepareRenderer(
                    table.getCellRenderer(row, column), row, column);
            renderer.setPreferredSize(null);
            if (renderer instanceof JComponent) {
                fitView((JComponent) renderer, size, axis);
            }
            return renderer.getPreferredSize();
        }
    }

    private static void removeRealCell(JTable table, Key type) {
        JComponent realCell = (JComponent) table.getClientProperty(type);
        if (realCell != null) {
            if (realCell.getParent() == table) {
                realCell.removeFocusListener((FocusListener)
                        realCell.getClientProperty(Key.TABLE_CELL_FOCUS_LISTENER));
                realCell.putClientProperty(Key.TABLE_CELL_FOCUS_LISTENER, null);
                Rectangle bounds = realCell.getBounds();
                table.remove(realCell);
                table.repaint(bounds);
            }
            table.putClientProperty(type, null);
        }
    }

    private static void removeAllRealCells(JTable table) {
        removeRealCell(table, Key.ACTIVE_TABLE_CELL);
        removeRealCell(table, Key.FOCUSED_TABLE_CELL);
    }

    private static int getResizingColumn(JTableHeader tableHeader, Point location) {
        int column = tableHeader.columnAtPoint(location);
        Rectangle rect = tableHeader.getHeaderRect(column);
        rect.grow(-3, 0);
        if (rect.contains(location)) {
            return -1;
        }

        int middle = rect.x + rect.width / 2;
        if (tableHeader.getComponentOrientation().isLeftToRight()) {
            column = (location.x < middle) ? column - 1 : column;
        } else {
            column = (location.x < middle) ? column : column - 1;
        }
        return column;
    }

    private static class ContainerHandler implements AWTEventListener {
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
                        if (tc.getClientProperty(Key.TEXT_BACKGROUND_MONITORED) == null) {
                            tc.addPropertyChangeListener("enabled", TEXT_BACKGROUND_HANDLER);
                            tc.addPropertyChangeListener("editable", TEXT_BACKGROUND_HANDLER);
                            TEXT_BACKGROUND_HANDLER.propertyChange(
                                    new PropertyChangeEvent(tc, "enabled", null, null));
                            tc.putClientProperty(Key.TEXT_BACKGROUND_MONITORED, Boolean.TRUE);
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
                    if (tc.getClientProperty(
                            PropertyKeys.CONTEXT_ACTION_SUPPORT) == null) {
                        tc.putClientProperty(
                                PropertyKeys.CONTEXT_ACTION_SUPPORT,
                                new TextComponentContextActionSupport(tc));
                    }
                }

                // Fix and enhance tables.
                if (c instanceof JTable) {
                    JTable table = (JTable) c;
                    if (table.getClientProperty(Key.TABLE_MONITORED) == null) {
                        table.putClientProperty(Key.TABLE_MONITORED, Boolean.TRUE);

                        if (table.getClientProperty(
                                PropertyKeys.AUTO_FIT_ROW_HEIGHTS) == null) {
                            table.putClientProperty(
                                    PropertyKeys.AUTO_FIT_ROW_HEIGHTS, Boolean.TRUE);
                        }

                        TableHandler tableHandler = new TableHandler(table);
                        table.addPropertyChangeListener("UI", tableHandler);
                        table.addMouseMotionListener(tableHandler);
                        BeanUtils.keepListener(table, "model",
                                TableModelListener.class, tableHandler);
                        BeanUtils.keepListener(table, "selectionModel",
                                ListSelectionListener.class, tableHandler);
                        BeanUtils.keepListener(table, "columnModel",
                                TableColumnModelListener.class, tableHandler);
                    }
                }

                // Fix and enhance table headers.
                if (c instanceof JTableHeader) {
                    JTableHeader tableHeader = (JTableHeader) c;
                    if (tableHeader.getClientProperty(
                            PropertyKeys.CONTEXT_ACTION_SUPPORT) == null) {
                        tableHeader.putClientProperty(
                                PropertyKeys.CONTEXT_ACTION_SUPPORT,
                                new TableHeaderContextActionSupport(tableHeader));
                    }
                    if (tableHeader.getClientProperty(
                            Key.TABLE_HEADER_MONITORED) == null) {
                        tableHeader.putClientProperty(
                                Key.TABLE_HEADER_MONITORED, Boolean.TRUE);

                        TableHeaderHandler tableHeaderHandler
                                = new TableHeaderHandler(tableHeader);
                        tableHeader.addMouseListener(tableHeaderHandler);
                        tableHeader.addPropertyChangeListener(
                                "UI", tableHeaderHandler);
                    }
                }

                // Update L&F as needed.
                if (lafMonitored && c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    LookAndFeel laf = UIManager.getLookAndFeel();
                    if (jc.getClientProperty(Key.LOOK_AND_FEEL) != laf) {
                        // The UI may be updated twice, but there's no other way
                        // to check whether the UI is created by the current
                        // Look And Feel.
                        jc.updateUI();
                        jc.putClientProperty(Key.LOOK_AND_FEEL, laf);
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

        private void mayShowContextPopupMenu(JComponent c) {
            if (c.getComponentPopupMenu() == null) {
                ContextActionSupport<?> cas = (ContextActionSupport<?>)
                        c.getClientProperty(PropertyKeys.CONTEXT_ACTION_SUPPORT);
                if (cas != null) {
                    cas.showContextPopupMenu(x, y);
                }
            }
        }
    }

    private static class TextBackgroundHandler implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
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

    private static class TableHandler extends MouseAdapter
            implements TableModelListener, ListSelectionListener,
                       TableColumnModelListener, PropertyChangeListener {
        private JTable table;

        private TableHandler(JTable table) {
            this.table = table;
            fitRowHeights();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            JComponent activeCell = (JComponent)
                    table.getClientProperty(Key.ACTIVE_TABLE_CELL);
            JComponent focusedCell = (JComponent)
                    table.getClientProperty(Key.FOCUSED_TABLE_CELL);

            if (activeCell != null && activeCell != focusedCell) {
                removeRealCell(table, Key.ACTIVE_TABLE_CELL);
            }

            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            int column = table.columnAtPoint(p);
            if (row != -1 && column != -1) {
                Component cell = table.prepareRenderer(
                        table.getCellRenderer(row, column), row, column);
                if (cell instanceof JComponent && Boolean.TRUE.equals(
                        ((JComponent) cell).getClientProperty(PropertyKeys.REAL_CELL_COMPONENT))) {
                    FocusListener focusHandler = new TableCellFocusHandler(table);
                    cell.addFocusListener(focusHandler);
                    ((JComponent) cell).putClientProperty(
                            Key.TABLE_CELL_FOCUS_LISTENER, focusHandler);
                    table.add(cell);
                    table.putClientProperty(Key.ACTIVE_TABLE_CELL, cell);
                    cell.setBounds(table.getCellRect(row, column, false));
                }
            }
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            // JTable's implementation doesn't remove the cell editor on table
            // model change, which may result in runtime expections.
            if (table.isEditing()) {
                table.removeEditor();
            }
            removeAllRealCells(table);
            if (isAutoFitRowHeights()) {
                if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                    fitAllRowHeights(table);
                } else {
                    for (int row = e.getFirstRow(); row <= e.getLastRow(); row++) {
                        fitRowHeight(table, row);
                    }
                }
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (isAutoFitRowHeights() && e.getValueIsAdjusting()) {
                fitRowHeight(table, e.getFirstIndex());
                if (e.getFirstIndex() != e.getLastIndex()) {
                    fitRowHeight(table, e.getLastIndex());
                }
            }
        }

        @Override
        public void columnAdded(TableColumnModelEvent e) {
            fitRowHeights();
        }

        @Override
        public void columnRemoved(TableColumnModelEvent e) {
            fitRowHeights();
        }

        @Override
        public void columnMoved(TableColumnModelEvent e) {
            fitRowHeights();
        }

        @Override
        public void columnMarginChanged(ChangeEvent e) {
            fitRowHeights();
        }

        @Override
        public void columnSelectionChanged(ListSelectionEvent e) {
            if (isAutoFitRowHeights() && e.getValueIsAdjusting()) {
                if (table.getColumnSelectionAllowed()
                        && !table.getRowSelectionAllowed()) {
                    fitAllRowHeights(table);
                } else {
                    int focusedRow = table.getSelectionModel().getLeadSelectionIndex();
                    if (focusedRow != -1) {
                        fitRowHeight(table, focusedRow);
                    }
                }
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // UI has changed.
            TableCellEditor editor = table.getCellEditor();
            if (editor != null && !editor.stopCellEditing()) {
                editor.cancelCellEditing();
            }
            removeAllRealCells(table);
        }

        private void fitRowHeights() {
            removeAllRealCells(table);
            if (isAutoFitRowHeights()) {
                fitAllRowHeights(table);
            }
        }

        private boolean isAutoFitRowHeights() {
            return Boolean.TRUE.equals(table.getClientProperty(
                    PropertyKeys.AUTO_FIT_ROW_HEIGHTS));
        }
    }

    private static class TableCellFocusHandler implements FocusListener {
        private JTable table;

        private TableCellFocusHandler(JTable table) {
            this.table = table;
        }

        @Override
        public void focusGained(FocusEvent e) {
            table.putClientProperty(Key.FOCUSED_TABLE_CELL, e.getComponent());
        }

        @Override
        public void focusLost(FocusEvent e) {
            JComponent activeCell = (JComponent)
                    table.getClientProperty(Key.ACTIVE_TABLE_CELL);
            JComponent focusedCell = (JComponent)
                    table.getClientProperty(Key.FOCUSED_TABLE_CELL);
            if (activeCell == focusedCell) {
                table.putClientProperty(Key.ACTIVE_TABLE_CELL, null);
            }
            removeRealCell(table, Key.FOCUSED_TABLE_CELL);
            table.putClientProperty(Key.FOCUSED_TABLE_CELL, null);
        }
    }

    private static class TableHeaderHandler
            implements MouseListener, PropertyChangeListener {
        private static final MouseListener EMPTY_MOUSE_HAMDLER = new MouseAdapter() {};

        private JTableHeader tableHeader;
        private MouseListener defaultMouseHandler;

        private TableHeaderHandler(JTableHeader tableHeader) {
            this.tableHeader = tableHeader;
            updateDefaultMouseHandler();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int resizingColumn = getResizingColumn(tableHeader, e.getPoint());

            // Prevent row sorting if a header column separator is clicked.
            switch (e.getClickCount()) {
                case 1:
                    if (resizingColumn == -1) {
                        defaultMouseHandler.mouseClicked(e);
                    }
                    return;
                case 2:
                    if (resizingColumn != -1) {
                        fitColumnWidth(tableHeader.getTable(), resizingColumn);
                    }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            defaultMouseHandler.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            defaultMouseHandler.mouseReleased(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            defaultMouseHandler.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            defaultMouseHandler.mouseExited(e);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // UI has changed.
            updateDefaultMouseHandler();
        }

        private void updateDefaultMouseHandler() {
            // UI has changed. Only BasicTableHeaderUI can be handled here.
            for (MouseListener l : tableHeader.getMouseListeners()) {
                if (l instanceof MouseInputHandler) {
                    tableHeader.removeMouseListener(l);
                    defaultMouseHandler = l;
                    return;
                }
            }
            // For non BasicTableHeaderUI, just use an empty handler.
            defaultMouseHandler = EMPTY_MOUSE_HAMDLER;
        }
    }

    private static enum Key {
        LOOK_AND_FEEL, TEXT_BACKGROUND_MONITORED, TABLE_MONITORED,
        ACTIVE_TABLE_CELL, FOCUSED_TABLE_CELL, TABLE_CELL_FOCUS_LISTENER,
        TABLE_HEADER_MONITORED;
    }
}

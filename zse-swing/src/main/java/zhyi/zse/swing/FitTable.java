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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.plaf.basic.BasicTableHeaderUI.MouseInputHandler;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.View;
import zhyi.zse.collection.CollectionUtils;
import zhyi.zse.lang.ReflectionUtils;
import zhyi.zse.swing.event.SelectionChangeEvent;
import zhyi.zse.swing.event.SelectionChangeListener;

/**
 * This table component extends {@link JTable} with some advanced features.
 * <ul>
 * <li>Rows are automatically resized to fit cell heights.
 * <li>Column width can be resized to fit cells by double clicking header
 * separators.
 * <li>Columns can be filtered with the header's popup menu.
 * <li>Row sorter and view port height filling are enabled by default.
 * </ul>
 *
 * @author Zhao Yi
 */
@SuppressWarnings("serial")
public class FitTable extends JTable {
    private static final Method GET_HEADER_RENDERER
            = ReflectionUtils.getDeclaredMethod(
                    BasicTableHeaderUI.class, "getHeaderRenderer", int.class);

    private MouseListener tableHeaderMouseListener;
    private MouseListener delegatedRowSortingListener;
    private PropertyChangeListener tableHeaderUiChangeListener;
    private JPopupMenu columnSelectorPopupMenu;
    private MultiValueSelector<JCheckBoxMenuItem, TableColumn> columnSelector;
    private Map<TableColumn, ColumnInfo> hiddenColumnMap;
    private boolean initialized;

    /**
     * Constructs a new instance with an empty data model.
     */
    public FitTable() {
        this(null);
    }

    /**
     * Constructs a new instance with the specified data model.
     *
     * @param tableModel The data model of the table.
     */
    public FitTable(TableModel tableModel) {
        super(tableModel);
        setAutoCreateRowSorter(true);
        setFillsViewportHeight(true);
        hiddenColumnMap = new IdentityHashMap<>();
    }

    @Override
    public void setTableHeader(JTableHeader newHeader) {
        if (tableHeaderMouseListener == null) {
            tableHeaderMouseListener = new TableHeaderMouseListener();
            tableHeaderUiChangeListener = new TableHeaderUiChangeListener();
        }

        if (tableHeader != null) {
            tableHeader.removeMouseListener(tableHeaderMouseListener);
            tableHeader.removeMouseListener(delegatedRowSortingListener);
            tableHeader.removePropertyChangeListener(
                    "UI", tableHeaderUiChangeListener);
        }
        super.setTableHeader(newHeader);
        newHeader.addMouseListener(tableHeaderMouseListener);
        newHeader.addPropertyChangeListener("UI", tableHeaderUiChangeListener);
        tableHeaderUiChangeListener.propertyChange(null);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);

        if (!initialized) {
            return;
        }

        int firstRow = rowAtPoint(new Point(x, y));
        int lastRow = rowAtPoint(new Point(x, y + height));
        if (firstRow == -1 && lastRow == -1) {
            return;
        }

        if (firstRow == -1) {
            firstRow = 0;
        }
        if (lastRow == -1) {
            lastRow = getRowCount() - 1;
        }
        for (int row = firstRow; row <= lastRow; row++) {
            int rh = rowHeight;
            for (int column = 0; column < getColumnCount(); column++) {
                rh = Math.max(rh, getCellSize(row, column).height);
            }
            if (rh != getRowHeight(row)) {
                setRowHeight(row, rh);
            }
        }
    }

    @Override
    public Component prepareRenderer(
            TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        resizeHtmlView(c, column);
        return c;
    }

    @Override
    public Component prepareEditor(
            TableCellEditor editor, int row, int column) {
        Component c = super.prepareEditor(editor, row, column);
        resizeHtmlView(c, column);
        return c;
    }

    @Override
    protected void initializeLocalVars() {
        super.initializeLocalVars();
        initialized = true;
    }

    private void resizeHtmlView(Component c, int column) {
        if (c instanceof JComponent) {
            View view = (View) ((JComponent) c)
                    .getClientProperty(BasicHTML.propertyKey);
            if (view != null) {
                view.setSize(columnModel.getColumn(
                        convertColumnIndexToModel(column)).getWidth(), 0);
            }
        }
    }

    private void resizeColumnWidth(int column) {
        if (column == -1) {
            return;
        }

        TableColumn tableColumn = columnModel.getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }

        int width = tableColumn.getMinWidth();
        for (int row = 0; row < getRowCount(); row++) {
            width = Math.max(width, getCellSize(row, column).width);
        }

        if (tableHeader != null) {
            Component c = (Component) ReflectionUtils.invoke(
                    GET_HEADER_RENDERER, tableHeader.getUI(), column);
            width = Math.max(width, c.getPreferredSize().width);
        }

        if (width != tableColumn.getPreferredWidth()) {
            tableColumn.setPreferredWidth(width);
        }
    }

    private int getResizingColumn(Point p) {
        int column = tableHeader.columnAtPoint(p);
        Rectangle rect = tableHeader.getHeaderRect(column);
        rect.grow(-3, 0);
        if (rect.contains(p)) {
            return -1;
        }

        int middle = rect.x + rect.width / 2;
        if (tableHeader.getComponentOrientation().isLeftToRight()) {
            column = (p.x < middle) ? column - 1 : column;
        } else {
            column = (p.x < middle) ? column : column - 1;
        }
        return column;
    }

    private Dimension getCellSize(int row, int column) {
        return row == editingRow && column == editingColumn
                ? prepareEditor(getCellEditor(row, column),
                        row, column).getPreferredSize()
                : prepareRenderer(getCellRenderer(row, column),
                        row, column).getPreferredSize();
    }

    private void mayShowColumnSelector(Point p) {
        if (getColumnCount() == 0) {
            return;
        }

        if (columnSelectorPopupMenu == null) {
            columnSelectorPopupMenu = new JPopupMenu();
            columnSelector = new MultiValueSelector<>();
            columnSelector.addSelectionChangeListener(
                    new ColumnSelectionChangeListener());
        } else {
            columnSelectorPopupMenu.removeAll();
            columnSelector.removeAll();
            // Remove columns that no longer exist in the column model from
            // the hidden column map.
            Iterator<TableColumn> it = hiddenColumnMap.keySet().iterator();
            while (it.hasNext()) {
                TableColumn hiddenColumn = it.next();
                if (getColumnIndex(hiddenColumn) != -1) {
                    it.remove();
                }
            }
        }

        JCheckBoxMenuItem firstSelectedCheckBox = null;
        for (int column = 0; column < getColumnCount(); column++) {
            TableColumn tableColumn = columnModel.getColumn(column);
            JCheckBoxMenuItem columnCheckBox = new JCheckBoxMenuItem(
                    tableColumn.getHeaderValue().toString(),
                    !hiddenColumnMap.containsKey(tableColumn));
            columnSelectorPopupMenu.add(columnCheckBox);
            columnSelector.add(columnCheckBox, tableColumn);
            if (columnCheckBox.isSelected()) {
                if (firstSelectedCheckBox == null) {
                    columnCheckBox.setEnabled(false);
                    firstSelectedCheckBox = columnCheckBox;
                } else {
                    firstSelectedCheckBox.setEnabled(true);
                }
            }
        }
        columnSelectorPopupMenu.show(tableHeader, p.x, p.y);
    }

    private int getColumnIndex(TableColumn tableColumn) {
        int index = -1;
        int i = 0;
        for (TableColumn tc : CollectionUtils.iterable(columnModel.getColumns())) {
            if (tc == tableColumn) {
                index = i;
                break;
            }
            i++;
        }
        return index;
    }

    /**
     * This listener wraps the default mouse listener installed on the table
     * header to prevent row sorting if a header column separator is clicked.
     */
    private class DelegatedRowSortingListener implements MouseListener {
        private MouseListener headerListener;

        private DelegatedRowSortingListener(MouseListener headerListener) {
            this.headerListener = headerListener;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                if (getResizingColumn(e.getPoint()) == -1) {
                    headerListener.mouseClicked(e);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            headerListener.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            headerListener.mouseReleased(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            headerListener.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            headerListener.mouseExited(e);
        }
    }

    private class TableHeaderMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (!tableHeader.isEnabled()
                    || !tableHeader.getResizingAllowed()
                    || e.getClickCount() != 2) {
                return;
            }
            resizeColumnWidth(getResizingColumn(e.getPoint()));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                mayShowColumnSelector(e.getPoint());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                mayShowColumnSelector(e.getPoint());
            }
        }
    }

    private class TableHeaderUiChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            tableHeader.removeMouseListener(delegatedRowSortingListener);
            MouseListener rowSortingListener = null;
            for (MouseListener l : tableHeader.getMouseListeners()) {
                if (l instanceof MouseInputHandler) {
                    rowSortingListener = l;
                    break;
                }
            }
            tableHeader.removeMouseListener(rowSortingListener);
            delegatedRowSortingListener
                    = new DelegatedRowSortingListener(rowSortingListener);
            tableHeader.addMouseListener(delegatedRowSortingListener);
        }
    }

    private class ColumnSelectionChangeListener implements SelectionChangeListener<JCheckBoxMenuItem, TableColumn> {
        @Override
        public void selectionChanged(SelectionChangeEvent<? extends JCheckBoxMenuItem, ? extends TableColumn> e) {
            for (JCheckBoxMenuItem columnCheckBox : columnSelector.getButtons()) {
                TableColumn tableColumn = columnSelector.getValue(columnCheckBox);

                // Show a hidden column.
                if (columnCheckBox.isSelected() && hiddenColumnMap.containsKey(tableColumn)) {
                    // Size back the hidden column.
                    ColumnInfo columnInfo = hiddenColumnMap.remove(tableColumn);
                    tableColumn.setMinWidth(columnInfo.minWidth);
                    tableColumn.setMaxWidth(columnInfo.maxWidth);
                    tableColumn.setPreferredWidth(columnInfo.prefWidth);

                    // Move it to the "possibly" original position, and keep
                    // all hidden columns in the rightmost side of the table.
                    int originalIndex = columnInfo.originalIndex;
                    int firstHiddenColumnIndex = -1;
                    for (TableColumn tc : CollectionUtils.iterable(
                            columnModel.getColumns())) {
                        firstHiddenColumnIndex++;
                        if (hiddenColumnMap.containsKey(tc)) {
                            break;
                        }
                    }
                    if (originalIndex >= firstHiddenColumnIndex) {
                        originalIndex = firstHiddenColumnIndex - 1;
                    }
                    columnModel.moveColumn(
                            getColumnIndex(tableColumn), originalIndex);
                }

                // Hide a visible column.
                if (columnCheckBox.isSelected()
                        && !hiddenColumnMap.containsKey(tableColumn)) {
                    int originalIndex = getColumnIndex(tableColumn);
                    hiddenColumnMap.put(tableColumn,
                            new ColumnInfo(originalIndex,
                                    tableColumn.getMinWidth(),
                                    tableColumn.getPreferredWidth(),
                                    tableColumn.getMaxWidth()));

                    // Make the column invisible by setting its width to 0.
                    // To make sure it does not affect visible columns, move
                    // it to the rightmost side of the table.
                    tableColumn.setMinWidth(0);
                    tableColumn.setPreferredWidth(0);
                    tableColumn.setMaxWidth(0);
                    columnModel.moveColumn(originalIndex, getColumnCount() - 1);
                }
            }
        }
    }

    private static class ColumnInfo {
        private int originalIndex;
        private int minWidth;
        private int prefWidth;
        private int maxWidth;

        private ColumnInfo(int originalIndex,
                int minWidth, int prefWidth, int maxWidth) {
            this.originalIndex = originalIndex;
            this.minWidth = minWidth;
            this.prefWidth = prefWidth;
            this.maxWidth = maxWidth;
        }
    }
}

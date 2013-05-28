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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * This table can automatically resize its cells to suit the contents.
 *
 * @author Zhao Yi
 */
@SuppressWarnings("serial")
public class AutoResizableTable extends JTable {
    private TableModelListener resizeListener;

    /**
     * Constructs a new instance with an empty data model.
     */
    public AutoResizableTable() {
        this(null);
    }

    /**
     * Constructs a new instance with the specified data model.
     * <p>
     * The auto resize mode is set to {@link #AUTO_RESIZE_OFF} to implement
     * the auto resize strategy, and any other auto resize mode is meaningless
     * to this table. The row sorter and view port height filling are enabled
     * by default for convenient usage.
     *
     * @param tableModel The data model of the table.
     */
    public AutoResizableTable(TableModel tableModel) {
        super(tableModel);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        setAutoCreateRowSorter(true);
        setFillsViewportHeight(true);

        // If this table is to be added to a scroll pane, make sure it fills the
        // view port width.
        addComponentListener(new ComponentAdapter() {
            // When the window is displayed, this table will be resized twice.
            // Reset the width after that.
            private int counter = 2;

            @Override
            public void componentResized(ComponentEvent e) {
                counter--;
                if (counter == 0) {
                    fillViewportWidth();
                    removeComponentListener(this);
                }
            }
        });

        resizeListener = new TableModelListener() {
            @Override
            public void tableChanged(final TableModelEvent e) {
                // Resizing doesn't work without invokeLater...
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                            resize(0, getRowCount() - 1, true);
                        } else if (e.getType() == TableModelEvent.INSERT) {
                            resize(e.getFirstRow(), e.getLastRow(), false);
                        }
                    }
                });
            }
        };
    }

    /**
     * Sets the data model for this table.
     * <p>
     * A table model listener is registered to the new model to track changes
     * to the model for resizing table cells. The listener for the old model
     * is removed.
     *
     * @param newModel The new data model to set.
     */
    @Override
    public void setModel(TableModel newModel) {
        if (dataModel != newModel) {
            dataModel.removeTableModelListener(resizeListener);
            newModel.addTableModelListener(resizeListener);
            super.setModel(newModel);
            resize(0, getRowCount() - 1, true);
        }
    }

    private void resize(int firstRow, int lastRow, boolean accountForHeader) {
        int[] rowHeights = new int[lastRow - firstRow + 1];
        int[] columnWidths = new int[getColumnCount()];

        // Traverse table cells.
        for (int row = 0; row < rowHeights.length; row++) {
            for (int column = 0; column < columnWidths.length; column++) {
                int tableRow = row + firstRow;
                Dimension size = getCellRenderer(tableRow, column).getTableCellRendererComponent(
                        this, getValueAt(tableRow, column), false, false, tableRow, column).getPreferredSize();
                rowHeights[row] = Math.max(rowHeights[row], size.height);
                if (columnWidths[column] == 0) {
                    columnWidths[column] = columnModel.getColumn(column).getPreferredWidth();
                } else {
                    columnWidths[column] = Math.max(columnWidths[column], size.width);
                }
            }
        }

        // Resize table header cells.
        if (accountForHeader && tableHeader != null) {
            TableCellRenderer defaultHeaderCellRenderer = tableHeader.getDefaultRenderer();
            for (int column = 0; column < columnWidths.length; column++) {
                TableColumn tableColumn = columnModel.getColumn(column);
                TableCellRenderer headerCellRenderer = tableColumn.getHeaderRenderer();
                if (headerCellRenderer == null) {
                    headerCellRenderer = defaultHeaderCellRenderer;
                }
                columnWidths[column] = Math.max(columnWidths[column], headerCellRenderer.getTableCellRendererComponent(
                        this, tableColumn.getHeaderValue(), false, false, 0, column).getPreferredSize().width);
            }
        }

        // Apply row heights and column widths.
        for (int i = 0; i < rowHeights.length; i++) {
            int tableRow = i + firstRow;
            if (getRowHeight(tableRow) < rowHeights[i]) {
                setRowHeight(tableRow, rowHeights[i]);
            }
        }
        fillViewportWidth();
        for (int column = 0; column < columnWidths.length; column++) {
            TableColumn tableColumn = columnModel.getColumn(column);
            if (tableColumn.getPreferredWidth() < columnWidths[column]) {
                tableColumn.setPreferredWidth(columnWidths[column]);
            }
        }
    }

    private void fillViewportWidth() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport && getAutoResizeMode() == AUTO_RESIZE_OFF) {
            if (getWidth() < parent.getWidth()) {
                Dimension d = new Dimension(parent.getWidth(), getHeight());
                setPreferredSize(d);
            }
        }
    }
}

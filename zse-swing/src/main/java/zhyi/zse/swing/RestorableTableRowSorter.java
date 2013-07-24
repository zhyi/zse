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
package zhyi.zse.swing;

import java.util.List;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * This table sorter adds the capability to restore unsorted order while toggling
 * between sort orders.
 *
 * @param <M> The table model's type.
 * @author Zhao Yi
 */
public class RestorableTableRowSorter<M extends TableModel> extends TableRowSorter<M> {
    /**
     * Constructs a new instance with an empty model.
     */
    public RestorableTableRowSorter() {
    }

    /**
     * Constructs a new instance with the specified table model.
     *
     * @param model The table model.
     */
    public RestorableTableRowSorter(M model) {
        super(model);
    }

    /**
     * Toggles the sort order from unsorted to ascending, or ascending to descending,
     * or descending to unsorted.
     *
     * @param column The column to be sorted.
     */
    @Override
    public void toggleSortOrder(int column) {
        if (isSortable(column)) {
            List<? extends SortKey> sortKeys = getSortKeys();
            if (!sortKeys.isEmpty()) {
                SortKey primarySortKey = sortKeys.get(0);
                if (primarySortKey.getColumn() == column
                        && primarySortKey.getSortOrder() == SortOrder.DESCENDING) {
                    setSortKeys(null);
                    return;
                }
            }
            super.toggleSortOrder(column);
        }
    }
}

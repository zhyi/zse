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

import javax.swing.table.TableCellRenderer;

/**
 * This interface works together with {@link FitTable} to provide real table
 * cell components instead of static renderers.
 * <p>
 * A {@link FitTable} can have at most two real cell components (one currently
 * active and the other focused) at the same time, so it is recommended that
 * the {@link #getTableCellRendererComponent getTableCellRendererComponent}
 * method returns different components for different cells.
 *
 * @author Zhao Yi
 */
public interface ActiveTableCellRenderer extends TableCellRenderer {
}

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

import java.util.EventListener;

/**
 * The listener interface for receiving selection change events fired by
 * value selectors.
 *
 * @param <T> The selector's value type.
 *
 * @author Zhao Yi
 *
 * @see SingleValueSelector
 * @see MultiValueSelector
 */
public interface SelectionChangeListener<T> extends EventListener {
    /**
     * Invoked when the selection has been changed.
     *
     * @param e The selection change event.
     */
    void selectionChanged(SelectionChangeEvent<? extends T> e);
}

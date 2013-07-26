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
package zhyi.zse.swing.event;

import java.util.EventObject;
import javax.swing.AbstractButton;
import zhyi.zse.swing.MultiValueSelector;
import zhyi.zse.swing.SingleValueSelector;

/**
 * A selection event is emitted when the selected value(s) of a value selector
 * has been changed.
 * <p>
 * The {@link #getSource} method returns the value selector, an instance of either
 * {@link SingleValueSelector} or {@link MultiValueSelector}, on which this event
 * initially occurred.
 *
 * @param <B> The selector's button type.
 * @param <T> The selector's value type.
 *
 * @author Zhao Yi
 * @see SingleValueSelector
 * @see MultiValueSelector
 */
@SuppressWarnings("serial")
public class SelectionChangeEvent<B extends AbstractButton, T> extends EventObject {
    /**
     * Constructs a new instance with a single value selector as the source object.
     *
     * @param source The source single value selector.
     */
    public SelectionChangeEvent(SingleValueSelector<B, T> source) {
        super(source);
    }

    /**
     * Constructs a new instance with a multi-value selector as the source object.
     *
     * @param source The source multi-value selector.
     */
    public SelectionChangeEvent(MultiValueSelector<B, T> source) {
        super(source);
    }
}

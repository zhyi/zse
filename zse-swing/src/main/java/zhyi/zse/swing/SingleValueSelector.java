/*
 * Copyright (C) 2012-2013 Zhao Yi
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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.event.EventListenerList;
import zhyi.zse.swing.event.SelectionEvent;
import zhyi.zse.swing.event.SelectionListener;

/**
 * Groups multiple buttons together as a single-value selector.
 * <p>
 * This class is typically useful to manage radio buttons.
 *
 * @param <T> The value's type.
 *
 * @author Zhao Yi
 */
public class SingleValueSelector<T> {
    private Map<AbstractButton, T> buttonValueMap;
    private ButtonGroup buttonGroup;
    private EventListenerList selectionListeners;
    private ItemListener itemListener;

    /**
     * Constructs a new single-value selector.
     */
    public SingleValueSelector() {
        buttonValueMap = new HashMap<>();
        buttonGroup = new ButtonGroup();
        selectionListeners = new EventListenerList();
        itemListener = new ItemListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void itemStateChanged(ItemEvent e) {
                for (SelectionListener<? super T> l
                        : selectionListeners.getListeners(SelectionListener.class)) {
                    l.selectionChanged(new SelectionEvent<>(SingleValueSelector.this));
                }
            }
        };
    }

    /**
     * Adds a button and its associated value into this selector.
     * <p>
     * The button is also added to an implicit {@link ButtonGroup}. If this selector
     * already contains the button, the value is replaced by the specified one.
     *
     * @param button The button to be added.
     * @param value  The value associated with the button.
     *
     * @return This selector for chained invocations.
     */
    public SingleValueSelector<T> add(AbstractButton button, T value) {
        button.addItemListener(itemListener);
        buttonValueMap.put(button, value);
        buttonGroup.add(button);
        return this;
    }

    /**
     * Removes a button and its associated value from this selector.
     * <p>
     * The button is also removed from the implicit {@link ButtonGroup}.
     *
     * @param button The button to be removed.
     *
     * @return This selector for chained invocations.
     */
    public SingleValueSelector<T> remove(AbstractButton button) {
        button.removeItemListener(itemListener);
        buttonValueMap.remove(button);
        buttonGroup.remove(button);
        return this;
    }

    /**
     * Returns the value that is associated with the selected button,
     * or {@code null} if no button is selected.
     *
     * @return The selected value or {@code null}.
     */
    public T getSelectedValue() {
        for (Entry<AbstractButton, T> e : buttonValueMap.entrySet()) {
            if (e.getKey().isSelected()) {
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * Selects the button with which the specified value is associated.
     * <p>
     * If the specified value is not associated with any button in this selector,
     * no action is taken.
     *
     * @param value The value that the button to be selected is bound to.
     */
    public void setSelectedValue(T value) {
        for (Entry<AbstractButton, T> e : buttonValueMap.entrySet()) {
            if (Objects.equals(e.getValue(), value)) {
                e.getKey().setSelected(true);
                break;
            }
        }
    }

    /**
     * Clears the selection in this selector.
     */
    public void clearSelection() {
        buttonGroup.clearSelection();
    }

    /**
     * Adds a selection change listener to receive selection change events.
     *
     * @param l The selection change listener to be added.
     */
    public void addSelectionChangeListener(SelectionListener<? super T> l) {
        selectionListeners.add(SelectionListener.class, l);
    }

    /**
     * Removes a selection change listener.
     *
     * @param l The selection listener to be removed.
     */
    public void removeSelectionChangeListener(SelectionListener<? super T> l) {
        selectionListeners.remove(SelectionListener.class, l);
    }
}

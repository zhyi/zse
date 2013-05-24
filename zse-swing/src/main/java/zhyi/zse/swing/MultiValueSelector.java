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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.swing.AbstractButton;
import javax.swing.event.EventListenerList;
import zhyi.zse.swing.event.SelectionChangeEvent;
import zhyi.zse.swing.event.SelectionChangeListener;

/**
 * Groups multiple buttons together as a multi-value selector.
 * <p>
 * This class is typically useful to manage check boxes.
 *
 * @param <T> The value's type.
 *
 * @author Zhao Yi
 */
public class MultiValueSelector<T> {
    private Map<AbstractButton, T> buttonValueMap;
    private EventListenerList eventListeners;
    private ItemListener itemListener;

    /**
     * Constructs a new multi-value selector.
     */
    public MultiValueSelector() {
        buttonValueMap = new LinkedHashMap<>();
        eventListeners = new EventListenerList();
        itemListener = new ItemListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void itemStateChanged(ItemEvent e) {
                for (SelectionChangeListener<? super T> l
                        : eventListeners.getListeners(SelectionChangeListener.class)) {
                    l.selectionChanged(new SelectionChangeEvent<>(MultiValueSelector.this));
                }
            }
        };
    }

    /**
     * Adds a button and its associated value into this selector.
     * <p>
     * If the this selector already contains the button, the value is replaced
     * by the new one.
     *
     * @param button The button to be added.
     * @param value  The value associated with the button.
     *
     * @return This selector for chained invocations.
     */
    public MultiValueSelector<T> add(AbstractButton button, T value) {
        button.addItemListener(itemListener);
        buttonValueMap.put(button, value);
        return this;
    }

    /**
     * Removes a button and its associated value from this selector.
     *
     * @param button The button to be removed.
     *
     * @return This selector for chained invocations.
     */
    public MultiValueSelector<T> remove(AbstractButton button) {
        button.removeItemListener(itemListener);
        buttonValueMap.remove(button);
        return this;
    }

    /**
     * Returns a list containing the values associated with the selected buttons,
     * or an empty list if no button is selected.
     *
     * @return The selected values as a list.
     */
    public List<T> getSelectedValues() {
        List<T> selectedValues = new ArrayList<>();
        for (Entry<AbstractButton, T> e : buttonValueMap.entrySet()) {
            if (e.getKey().isSelected()) {
                selectedValues.add(e.getValue());
            }
        }
        return selectedValues;
    }

    /**
     * Selects the buttons that are associated with the specified values.
     * <p>
     * This method is a varargs variant of {@link #setSelectedValues(Collection)
     * setSelectedValues(Collection&lt;V&gt;)}.
     *
     * @param values The values with which the buttons to be selected are associated.
     *
     * @see #setSelectedValues(Collection) setSelectedValues(Collection&lt;V&gt;)
     */
    @SuppressWarnings("unchecked")
    public void setSelectedValues(T... values) {
        setSelectedValues(Arrays.asList(values));
    }

    /**
     * Selects the buttons that are associated with the specified values.
     * <p>
     * If a button is associated with a value that is not one of the specified
     * values, it is deselected. If a value appears {@code n} times, at most
     * the first {@code n} matched buttons are selected in the order they were
     * added to this selector. If a value is not associated with any button
     * in this selector, it is ignored.
     *
     * @param values The values with which the buttons to be selected are associated.
     */
    public void setSelectedValues(Collection<T> values) {
        List<T> valueList = new LinkedList<>(values);
        boolean found;
        for (Entry<AbstractButton, T> e : buttonValueMap.entrySet()) {
            found = false;
            T buttonValue = e.getValue();
            Iterator<T> it = valueList.iterator();
            while (it.hasNext()) {
                T value = it.next();
                if (Objects.equals(buttonValue, value)) {
                    it.remove();
                    found = true;
                    break;
                }
            }
            e.getKey().setSelected(found);
        }
    }

    /**
     * Deselects all buttons in this selector.
     */
    public void clearSelections() {
        for (AbstractButton button : buttonValueMap.keySet()) {
            button.setSelected(false);
        }
    }

    /**
     * Adds a selection change listener to receive selection change events.
     *
     * @param l The selection change listener to be added.
     */
    public void addSelectionChangeListener(SelectionChangeListener<? super T> l) {
        eventListeners.add(SelectionChangeListener.class, l);
    }

    /**
     * Removes a selection change listener.
     *
     * @param l The selection listener to be removed.
     */
    public void removeSelectionChangeListener(SelectionChangeListener<? super T> l) {
        eventListeners.remove(SelectionChangeListener.class, l);
    }
}

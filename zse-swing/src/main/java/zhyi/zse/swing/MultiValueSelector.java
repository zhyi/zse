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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.event.EventListenerList;
import zhyi.zse.swing.event.SelectionChangeEvent;
import zhyi.zse.swing.event.SelectionChangeListener;

/**
 * Groups multiple buttons together as a multi-value selector.
 * <p>
 * This class is typically useful to manage check boxes.
 *
 * @param <B> The button's type.
 * @param <T> The value's type.
 *
 * @author Zhao Yi
 */
public class MultiValueSelector<B extends AbstractButton, T> {
    private Map<B, T> buttonValueMap;
    private EventListenerList listenerList;
    private ItemListener itemListener;

    /**
     * Constructs a new multi-value selector.
     */
    public MultiValueSelector() {
        buttonValueMap = new LinkedHashMap<>();
        listenerList = new EventListenerList();
        itemListener = new ItemListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void itemStateChanged(ItemEvent e) {
                for (SelectionChangeListener<B, T> l
                        : listenerList.getListeners(SelectionChangeListener.class)) {
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
    public MultiValueSelector<B, T> add(B button, T value) {
        button.addItemListener(itemListener);
        buttonValueMap.put(button, value);
        return this;
    }

    /**
     * Returns the value associated with the specified button.
     * <p>
     * A {@code null} return can either mean the button is not contained in this
     * selector, or the button is associated with {@code null}.
     *
     * @param button The button.
     *
     * @return The button's associating value.
     */
    public T getValue(B button) {
        return buttonValueMap.get(button);
    }

    /**
     * Returns all buttons contained in this selector as a read-only set,
     * in the order they were added to this selector.
     *
     * @return A read-only set containing all added buttons.
     */
    public Set<B> getButtons() {
        return Collections.unmodifiableSet(buttonValueMap.keySet());
    }

    /**
     * Removes a button and its associated value from this selector.
     *
     * @param button The button to be removed.
     *
     * @return This selector for chained invocations.
     */
    public MultiValueSelector<B, T> remove(B button) {
        button.removeItemListener(itemListener);
        buttonValueMap.remove(button);
        return this;
    }

    /**
     * Removes all buttons contained and their associated values from this
     * selector.
     */
    public void removeAll() {
        for (AbstractButton button : buttonValueMap.keySet()) {
            button.removeItemListener(itemListener);
        }
        buttonValueMap.clear();
    }

    /**
     * Returns a list containing the values associated with the selected buttons,
     * or an empty list if no button is selected.
     *
     * @return The selected values as a list.
     */
    public List<T> getSelectedValues() {
        return getValues(true);
    }

    /**
     * Returns a list containing the values associated with the unselected buttons,
     * or an empty list if all button are selected.
     *
     * @return The unselected values as a list.
     */
    public List<T> getUnselectedValues() {
        return getValues(false);
    }

    private List<T> getValues(boolean selected) {
        List<T> values = new ArrayList<>();
        for (Entry<B, T> e : buttonValueMap.entrySet()) {
            if (selected && e.getKey().isSelected()) {
                values.add(e.getValue());
            } else if (!selected && !e.getKey().isSelected()) {
                values.add(e.getValue());
            }
        }
        return values;
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
        for (Entry<B, T> e : buttonValueMap.entrySet()) {
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
    public void addSelectionChangeListener(SelectionChangeListener<B, T> l) {
        listenerList.add(SelectionChangeListener.class, l);
    }

    /**
     * Removes a selection change listener.
     *
     * @param l The selection listener to be removed.
     */
    public void removeSelectionChangeListener(SelectionChangeListener<B, T> l) {
        listenerList.remove(SelectionChangeListener.class, l);
    }
}

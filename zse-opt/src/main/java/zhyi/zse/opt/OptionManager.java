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
package zhyi.zse.opt;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import zhyi.zse.conversion.ConverterManager;

/**
 * This interface defines a simple and type-safe way to manage application options.
 *
 * @author Zhao Yi
 */
public abstract class OptionManager {
    protected ConcurrentMap<Option<?>, List<OptionChangeListener<?>>> specificListenerMap;
    protected List<OptionChangeListener<Object>> globalListeners;
    protected ConverterManager converterManager;

    protected OptionManager() {
        specificListenerMap = new ConcurrentHashMap<>();
        globalListeners = new CopyOnWriteArrayList<>();
        converterManager = new ConverterManager();
    }

    /**
     * Adds an option change listener to receive option change events for all
     * managed options.
     *
     * @param listener The listener to be added.
     */
    public void addOptionChangeListener(OptionChangeListener<Object> listener) {
        globalListeners.add(listener);
    }

    /**
     * Adds an option change listener to receive option change events
     * for a specific option.
     *
     * @param <T> The option's value type.
     * @param option The option to be listened on.
     * @param listener The listener to be added.
     */
    public <T> void addOptionChangeListener(
            Option<T> option, OptionChangeListener<? super T> listener) {
        List<OptionChangeListener<?>> specificListeners = specificListenerMap.get(option);
        if (specificListeners == null) {
            List<OptionChangeListener<?>> listeners = new CopyOnWriteArrayList<>();
            specificListeners = specificListenerMap.putIfAbsent(option, listeners);
            if (specificListeners == null) {
                specificListeners = listeners;
            }
        }
        specificListeners.add(listener);
    }

    /**
     * Removes an option change listener for all managed options. If the listener
     * was added more than once, it will be notified one less time after being
     * removed. If the listener is {@code null}, no action is taken.
     *
     * @param listener The listener to be removed.
     */
    public void removeOptionChangeListener(OptionChangeListener<Object> listener) {
        globalListeners.remove(listener);
    }

    /**
     * Removes an option change listener for a specific option. If the listener
     * was added more than once, it will be notified one less time after being
     * removed.
     *
     * @param <T> The option's value type.
     * @param option The option that was listened on.
     * @param listener The listener to be removed.
     */
    public <T> void removeOptionChangeListener(Option<T> option,
            OptionChangeListener<? super T> listener) {
        List<OptionChangeListener<?>> listeners = specificListenerMap.get(option);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Returns the converter manager for string-object conversion so that custom
     * converters can be registered.
     *
     * @return The converter manager used in this option manager.
     */
    public ConverterManager getConverterManager() {
        return converterManager;
    }

    /**
     * Returns the current value of an option, or its default value if the current
     * value is {@code null}. This method is typically useful to avoid null checks.
     * However, it may still return {@code null} when both the current and default
     * value is {@code null}. The best practice is to specify non-null default
     * values.
     *
     * @param <T> The option's value type.
     * @param option The option for which to get the value.
     * @return The option's current or default value.
     */
    public <T> T getNonNullOrDefault(Option<T> option) {
        T value = get(option);
        return value == null ? option.getDefaultValue() : value;
    }

    /**
     * Fires an option change event to all interested listeners.
     *
     * @param <T> The option's value type.
     * @param option The option of which the value has been changed.
     * @param oldValue The option's old value.
     * @param newValue The option's new value.
     */
    @SuppressWarnings("unchecked")
    protected <T> void fireOptionChanged(Option<T> option, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            OptionChangeEvent<T> e = new OptionChangeEvent<>(
                    this, option, oldValue, newValue);

            if (!globalListeners.isEmpty()) {
                for (OptionChangeListener<Object> listener : globalListeners) {
                    listener.optionChanged(e);
                }
            }

            List<OptionChangeListener<?>> specificListeners = specificListenerMap.get(option);
            if (specificListeners != null) {
                for (OptionChangeListener<?> listener : specificListeners) {
                    ((OptionChangeListener<? super T>) listener).optionChanged(e);
                }
            }
        }
    }

    /**
     * Returns the current value of an option. If the option is already associated
     * with a value, that value is returned. Otherwise, if the option was previously
     * stored, the stored value is returned. Failing both, the option's default
     * value is returned.
     *
     * @param <T> The option's value type.
     * @param option The option for which to get the value.
     * @return The option's current value.
     */
    public abstract <T> T get(Option<T> option);

    /**
     * Associates an option with a value. If the value is really changed,
     * an option change event is fired.
     *
     * @param <T> The option's value type.
     * @param option The option with which the value is to be associated.
     * @param value The value to be associated with the option.
     */
    public abstract <T> void set(Option<T> option, T value);

    /**
     * Stores all managed options to some persistent place so that they can be
     * loaded again by another option manager in future.
     *
     * @throws IOException If an I/O error occurs.
     */
    public abstract void store() throws IOException;
}

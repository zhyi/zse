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
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import zhyi.zse.conversion.Converter;
import zhyi.zse.conversion.ConverterManager;

/**
 * This class provides a skeletal implementation of {@link OptionManager}
 * based on the fact that options are usually cached in memory and stored as
 * key-value pairs in string.
 *
 * @author Zhao Yi
 */
public abstract class CachedOptionManager implements OptionManager {
    /**
     * Holder for {@code null} due to {@link ConcurrentHashMap} does not allow
     * {@code null} values.
     */
    private static final Object NULL = new Object();

    private ConcurrentMap<Option<?>, Object> optionMap;
    private ConcurrentMap<Option<?>, List<OptionChangeListener<?>>> specificListenerMap;
    private List<OptionChangeListener<Object>> globalListeners;
    private ConverterManager converterManager;

    /**
     * Constructs a new cached option manager.
     */
    protected CachedOptionManager() {
        optionMap = new ConcurrentHashMap<>();
        specificListenerMap = new ConcurrentHashMap<>();
        globalListeners = new CopyOnWriteArrayList<>();
        converterManager = new ConverterManager();
    }

    @Override
    public <T> T get(final Option<T> option) {
        Object value = optionMap.get(option);
        if (value == null) {
            Object defaultValue = wrapNull(load(option));
            value = optionMap.putIfAbsent(option, defaultValue);
            if (value == null) {
                value = defaultValue;
            }
        }
        return option.getValueClass().cast(unwrapNull(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void set(Option<T> option, T value) {
        Object oldValue = optionMap.put(option, wrapNull(value));
        if (oldValue == null) {
            oldValue = option.getDefaultValue();
        }
        oldValue = unwrapNull(oldValue);

        if (!Objects.equals(oldValue, value)) {
            OptionChangeEvent<T> e = new OptionChangeEvent<>(
                    this, option, option.getValueClass().cast(oldValue), value);

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

    @Override
    public void addOptionChangeListener(OptionChangeListener<Object> listener) {
        globalListeners.add(listener);
    }

    @Override
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

    @Override
    public void removeOptionChangeListener(OptionChangeListener<Object> listener) {
        globalListeners.remove(listener);
    }

    @Override
    public <T> void removeOptionChangeListener(
            Option<T> option, OptionChangeListener<? super T> listener) {
        List<OptionChangeListener<?>> listeners = specificListenerMap.get(option);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void store() throws IOException {
        Map<String, String> stringOptionMap = new HashMap<>();
        for (Entry<Option<?>, Object> e : optionMap.entrySet()) {
            Option<?> option = e.getKey();
            // Recapture the value's type.
            String literalValue = asString(
                    unwrapNull(e.getValue()), option.getValueClass());
            if (literalValue != null) {
                stringOptionMap.put(option.getName(), literalValue);
            }
        }
        store(stringOptionMap);
    }

    /**
     * Loads the previously stored option value as a string.
     *
     * @param name The option's name.
     *
     * @return The previously stored string value, or {@code null} if the option
     *         was not stored.
     */
    protected abstract String load(String name);

    /**
     * Stores all managed options with the provided map.
     *
     * @param stringOptionMap A map containing all managed options with their
     *                        names and values represented by strings.
     *
     * @throws IOException If an I/O error occurs.
     */
    protected abstract void store(Map<String, String> stringOptionMap) throws IOException;

    private <T> T load(Option<T> option) {
        String literalValue = load(option.getName());
        if (literalValue == null) {
            return option.getDefaultValue();
        }

        try {
            Class<T> valueClass = option.getValueClass();
            Converter<T> converter = converterManager.getConverter(valueClass);
            if (converter != null) {
                return converter.asObject(literalValue);
            } else if (Serializable.class.isAssignableFrom(valueClass)) {
                return valueClass.cast(converterManager
                        .getConverter(Serializable.class).asObject(literalValue));
            }
        } catch (Exception ex) {
        }
        return option.getDefaultValue();
    }

    private <T> String asString(Object value, Class<T> valueClass) {
        T typedValue = valueClass.cast(value);
        Converter<T> converter = converterManager.getConverter(valueClass);
        if (converter != null) {
            return converter.asString(typedValue);
        } else if (typedValue instanceof Serializable) {
            return converterManager.getConverter(Serializable.class)
                    .asString((Serializable) typedValue);
        }
        return null;
    }

    private static Object wrapNull(Object o) {
        return o == null ? NULL : o;
    }

    private static Object unwrapNull(Object o) {
        return o == NULL ? null : o;
    }
}

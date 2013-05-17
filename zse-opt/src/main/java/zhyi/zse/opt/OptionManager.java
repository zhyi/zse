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
import zhyi.zse.conversion.ConverterManager;

/**
 * This interface defines a simple and type-safe way to manage application options.
 *
 * @author Zhao Yi
 */
public interface OptionManager {
    /**
     * Gets the value of an option.
     * <p>
     * If the option is already associated with a value, that value is returned.
     * Otherwise, if the option was previously stored, the stored value is
     * returned. Failing both, the option's default value is returned.
     *
     * @param <T> The option's value type.
     *
     * @param option The option for which to get the value.
     *
     * @return The value of the option.
     */
    <T> T get(Option<T> option);

    /**
     * Associates an option with a value.
     * <p>
     * If the value is really changed, an option change event is fired.
     *
     * @param <T> The option's value type.
     *
     * @param option The option with which the value is to be associated.
     * @param value  The value to be associated with the option.
     */
    <T> void set(Option<T> option, T value);

    /**
     * Adds an option change listener to receive option change events for all
     * managed options.
     *
     * @param listener The listener to be added.
     */
    void addOptionChangeListener(OptionChangeListener<Object> listener);

    /**
     * Adds an option change listener to receive option change events
     * for a specific option.
     *
     * @param <T> The option's value type.
     *
     * @param option   The option to be listened on.
     * @param listener The listener to be added.
     */
    <T> void addOptionChangeListener(Option<T> option, OptionChangeListener<? super T> listener);

    /**
     * Removes an option change listener for all managed options.
     * <p>
     * If the listener was added more than once, it will be notified one less
     * time after being removed. If the listener is {@code null}, no action
     * is taken.
     *
     * @param listener The listener to be removed.
     */
    void removeOptionChangeListener(OptionChangeListener<Object> listener);

    /**
     * Removes an option change listener for a specific option.
     * <p>
     * If the listener was added more than once, it will be notified one less
     * time after being removed.
     *
     * @param <T> The option's value type.
     *
     * @param option   The option that was listened on.
     * @param listener The listener to be removed.
     */
    <T> void removeOptionChangeListener(Option<T> option,
            OptionChangeListener<? super T> listener);

    /**
     * Returns the converter manager for string-object conversion so that custom
     * converters can be registered.
     *
     * @return The converter manager used in this option manager.
     */
    ConverterManager getConverterManager();

    /**
     * Stores all managed options to some persistent place so that they can be
     * loaded again by another option manager in future.
     *
     * @throws IOException If an I/O error occurs.
     */
    void store() throws IOException;
}

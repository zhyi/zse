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

import java.util.EventObject;

/**
 * An option change event is emitted when the value of some option has been
 * changed.
 *
 * @param <T> The option's value type.
 *
 * @author Zhao Yi
 */
@SuppressWarnings("serial")
public class OptionChangeEvent<T> extends EventObject {
    private OptionManager optionManager;
    private Option<T> option;
    private T oldValue;
    private T newValue;

    /**
     * Constructs a new option change event.
     *
     * @param optionManager The option manager that fired this event.
     * @param option        The option of which the value was changed.
     * @param oldValue      The option's old value.
     * @param newValue      The option's new value.
     */
    public OptionChangeEvent(OptionManager optionManager,
            Option<T> option, T oldValue, T newValue) {
        super(option);
        this.optionManager = optionManager;
        this.option = option;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Returns the option of which the value was changed.
     *
     * @return The option of which the value was changed.
     */
    @Override
    public Option<T> getSource() {
        return getOption();
    }

    /**
     * Gets the option manager that fired this event.
     *
     * @return The option manager that fired this event.
     */
    public OptionManager getOptionManager() {
        return optionManager;
    }

    /**
     * Returns the option of which the value was changed.
     *
     * @return The option of which the value was changed.
     */
    public Option<T> getOption() {
        return option;
    }

    /**
     * Returns the option's old value.
     *
     * @return The option's old value.
     */
    public T getOldValue() {
        return oldValue;
    }

    /**
     * Returns the option's new value.
     *
     * @return The option's new value.
     */
    public T getNewValue() {
        return newValue;
    }
}

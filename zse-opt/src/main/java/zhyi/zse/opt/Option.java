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

import java.util.Objects;

/**
 * A type-safe class to represent application options.
 *
 * @param <T> The option's value type.
 *
 * @author Zhao Yi
 */
public class Option<T> {
    private String name;
    private Class<T> valueClass;
    private T defaultValue;

    /**
     * Constructs a new option.
     *
     * @param name The option's name.
     * @param valueClass The option's value class.
     * @param defaultValue The option's default value.
     * @throws NullPointerException If the name or the value class is {@code null}.
     */
    public Option(String name, Class<T> valueClass, T defaultValue) {
        this.name = Objects.requireNonNull(name, "Name must not be null.");
        this.valueClass = Objects.requireNonNull(
                valueClass, "Value class must not be null.");
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the option's name.
     *
     * @return The option's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the option's value class.
     *
     * @return The option's value class.
     */
    public Class<T> getValueClass() {
        return valueClass;
    }

    /**
     * Gets the option's default value.
     *
     * @return The option's default value.
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Checks whether this option is equal to some other object. Two options are
     * considered as equal when they have the same name, regardless of the other
     * properties.
     *
     * @param other The other object to check equality with.
     * @return {@code true} if the other object is an option and has the same
     *         name as this one; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return name.equals(((Option<?>) other).name);
    }

    /**
     * Returns the hash code of this option by hashing the option's name.
     *
     * @return The hash code of this option.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

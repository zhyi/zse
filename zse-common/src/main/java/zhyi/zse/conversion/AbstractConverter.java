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
package zhyi.zse.conversion;

/**
 * This abstract converter provides a common way to handle {@code null} values
 * by defining that an empty string is the literal value of {@code null}.
 *
 * @param <T> The object type supported by this converter.
 *
 * @author Zhao Yi
 */
public abstract class AbstractConverter<T> implements Converter<T> {
    @Override
    public T asObject(String literalValue) {
        return literalValue == null || literalValue.isEmpty()
                ? null : asObjectInternal(literalValue);
    }

    @Override
    public String asString(T object) {
        return object == null ? "" : asStringInternal(object);
    }

    /**
     * Converts a literal value to an object.
     * <p>
     * This method is conditionally invoked by {@link #asObject(String)}, when
     * the literal value is not {@code null} or an empty string.
     *
     * @param literalValue The literal representation of an object.
     *
     * @return An object represented by the literal value.
     */
    protected abstract T asObjectInternal(String literalValue);

    /**
     * Converts an object to its literal representation.
     * <p>
     * This method is conditionally invoked by {@link #asString(Object)}, when
     * the object is not {@code null}.
     *
     * @param object The object to be converted.
     *
     * @return The literal representation of the object.
     */
    protected abstract String asStringInternal(T object);
}

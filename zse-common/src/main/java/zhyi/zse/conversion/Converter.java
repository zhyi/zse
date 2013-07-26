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
 * A converter is used for string-to-object and object-to-string conversions.
 *
 * @param <T> The object type supported by this converter.
 *
 * @author Zhao Yi
 */
public interface Converter<T> {
    /**
     * Converts a literal value to an object.
     *
     * @param literalValue The literal representation of an object.
     * @return An object represented by the literal value.
     */
    T asObject(String literalValue);

    /**
     * Converts an object to its literal representation.
     *
     * @param object The object to be converted.
     * @return The literal representation of the object.
     */
    String asString(T object);
}

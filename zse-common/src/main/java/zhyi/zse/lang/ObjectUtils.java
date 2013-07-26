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
package zhyi.zse.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Utility Methods for general Java objects.
 *
 * @author Zhao Yi
 */
public final class ObjectUtils {
    private ObjectUtils() {
    }

    /**
     * Marshals a serializable object to a byte array with
     * {@link ObjectOutputStream}.
     *
     * @param serializable The object to be marshaled.
     * @return A byte array representing the marshaled object.
     * @throws IOException If the object cannot be marshaled, or an I/O error
     *         has happened.
     *
     * @see ObjectOutputStream#writeObject(Object)
     */
    public static byte[] marshal(Serializable serializable) throws IOException {
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        try (ObjectOutputStream oout = new ObjectOutputStream(baout)) {
            oout.writeObject(serializable);
            return baout.toByteArray();
        }
    }

    /**
     * Unmarshals the first object from a byte array with {@link ObjectInputStream}.
     *
     * @param bytes The byte array representing the marshaled object.
     * @return The first object unmarshaled from the byte array.
     * @throws ClassNotFoundException If the class of the serialized object
     * @throws IOException If the byte array cannot be unmarshaled,
     *         or an I/O error has occurred.
     *
     * @see ObjectInputStream#readObject()
     */
    public static Serializable unmarshal(byte[] bytes)
            throws ClassNotFoundException, IOException {
        try (ObjectInputStream oin = new ObjectInputStream(
                new ByteArrayInputStream(bytes))) {
            return (Serializable) oin.readObject();
        }
    }
}

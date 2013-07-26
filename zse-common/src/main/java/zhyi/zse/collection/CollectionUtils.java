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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package zhyi.zse.collection;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Utility methods for the Java Collections Framework.
 *
 * @author Zhao Yi
 */
public class CollectionUtils {
    private CollectionUtils() {
    }

    /**
     * Returns a list containing all elements retrieved from the specified
     * iterable in the order they are returned by the iterable.
     *
     * @param <E> The element's type.
     * @param iterable The iterable that provides elements.
     * @return A list containing all elements retrieved from the iterable.
     */
    public static <E> List<E> list(Iterable<E> iterable) {
        return list(iterable.iterator());
    }

    /**
     * Returns a list containing all elements retrieved from the specified
     * iterator in the order they are returned from the iterator.
     *
     * @param <E> The element's type.
     * @param it The iterator that provides elements.
     * @return A list containing all elements retrieved from the iterator.
     */
    public static <E> List<E> list(Iterator<E> it) {
        List<E> list = new ArrayList<>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    /**
     * Wraps an iterator in an iterable that can be used in a for-each loop.
     *
     * @param <E> The element type.
     * @param it The iterator to be wrapped.
     * @return An iterable object that wraps the passed-in iterator.
     */
    public static <E> Iterable<E> iterable(final Iterator<E> it) {
        return new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return it;
            }
        };
    }

    /**
     * Wraps an enumeration in an iterable object that can be used in a for-each
     * loop. The iterator provided by the returned iterable does not support
     * the {@link Iterator#remove() remove()} operation.
     *
     * @param <E> The element type.
     * @param e The enumeration to be wrapped.
     * @return An iterable object that wraps the passed-in enumeration.
     */
    public static <E> Iterable<E> iterable(final Enumeration<E> e) {
        return new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return new Iterator<E>() {
                    @Override
                    public boolean hasNext() {
                        return e.hasMoreElements();
                    }

                    @Override
                    public E next() {
                        return e.nextElement();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}

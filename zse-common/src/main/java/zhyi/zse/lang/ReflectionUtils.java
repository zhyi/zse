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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for reflection.
 *
 * @author Zhao Yi
 */
public class ReflectionUtils {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAP = new HashMap<>();
    static {
        PRIMITIVE_MAP.put(void.class, Void.class);
        PRIMITIVE_MAP.put(boolean.class, Boolean.class);
        PRIMITIVE_MAP.put(char.class, Character.class);
        PRIMITIVE_MAP.put(byte.class, Byte.class);
        PRIMITIVE_MAP.put(short.class, Short.class);
        PRIMITIVE_MAP.put(int.class, Integer.class);
        PRIMITIVE_MAP.put(long.class, Long.class);
        PRIMITIVE_MAP.put(float.class, Float.class);
        PRIMITIVE_MAP.put(double.class, Double.class);
    }

    private ReflectionUtils() {
    }

    /**
     * Returns the wrapper class of the specified class if it is primitive,
     * otherwise returns the specified class itself.
     *
     * @param <T> The type modeled by the class.
     *
     * @param c The class to be wrapped.
     *
     * @return The wrapper class or the original class.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrap(Class<T> c) {
        return c.isPrimitive() ? (Class<T>) PRIMITIVE_MAP.get(c) : c;
    }

    /**
     * Returns the class represented by the specified FQCN.
     * <p>
     * Unlike {@link Class#forName(String)}, this method returns {@code null}
     * if no matching class is found.
     *
     * @param name The fully qualified name of the desired class.
     *
     * @return The desired class or {@code null}.
     *
     * @see Class#forName(String)
     */
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    /**
     * Returns the class represented by the specified FQCN.
     * <p>
     * Unlike {@link Class#forName(String, boolean, ClassLoader)}, this method
     * returns {@code null} if no matching class is found.
     *
     * @param name       The fully qualified name of the desired class.
     * @param initialize Whether the class must be initialized.
     * @param loader     The class loader from which to load the class.
     *
     * @return The desired class or {@code null}.
     *
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public static Class<?> getClass(String name, boolean initialize, ClassLoader loader) {
        try {
            return Class.forName(name, initialize, loader);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    /**
     * Returns a declared constructor of a class.
     * <p>
     * The returned constructor has been made accessible. Unlike {@link
     * Class#getDeclaredConstructor}, this method returns {@code null} if no
     * matching constructor is found.
     *
     * @param <T> The type modeled by the class.
     *
     * @param c              The class object.
     * @param parameterTypes The method's parameter types.
     *
     * @return The desired constructor or {@code null}.
     *
     * @see Class#getDeclaredConstructor
     */
    public static <T> Constructor<T> getDeclaredConstructor(
            Class<T> c, Class<?>... parameterTypes) {
        try {
            final Constructor<T> con = c.getDeclaredConstructor(parameterTypes);
            if (!con.isAccessible()) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        con.setAccessible(true);
                        return null;
                    }
                });
            }
            return con;
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * Returns a public field of a class.
     * <p>
     * Unlike {@link Class#getField}, this method returns {@code null} if no
     * matching field is found.
     *
     * @param c    The class object.
     * @param name The field name.
     *
     * @return The desired field or {@code null}.
     *
     * @see Class#getField
     */
    public static Field getField(Class<?> c, String name) {
        try {
            return c.getField(name);
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }

    /**
     * Returns a declared method of a class.
     * <p>
     * The returned field has been made accessible. Unlike {@link Class#getMethod},
     * this method returns {@code null} if no matching field is found.
     *
     * @param c    The class object.
     * @param name The method name.
     *
     * @return The desired field or {@code null}.
     *
     * @see Class#getDeclaredField
     */
    public static Field getDeclaredField(Class<?> c, String name) {
        try {
            final Field f = c.getDeclaredField(name);
            makeAccessible(f);
            return f;
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }

    /**
     * Returns the value of a field.
     * <p>
     * The field will be made accessible, if it is inaccessible. Unlike
     * {@link Field#get}, this method throws a {@link RuntimeException}
     * when the reflective operation has failed.
     *
     * @param f The field object.
     * @param o The object from which to retrieve the field, or {@code null} for
     *          a static field.
     *
     * @return The value of the field.
     */
    public static Object getFieldValue(Field f, Object o) {
        try {
            makeAccessible(f);
            return f.get(o);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Changes the value of a field.
     * <p>
     * The field will be made accessible, if it is inaccessible. Unlike
     * {@link Field#set}, this method throws a {@link RuntimeException}
     * when the reflective operation has failed.
     *
     * @param f The field object.
     * @param o The object of which the field should be changed, or {@code null}
     *          for a static field.
     * @param v the new value of the field. 
     */
    public static void setFieldValue(Field f, Object o, Object v) {
        try {
            makeAccessible(f);
            f.set(o, v);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns a public method of a class.
     * <p>
     * Unlike {@link Class#getMethod}, this method returns {@code null} if no
     * matching method is found.
     *
     * @param c              The class object.
     * @param name           The method name.
     * @param parameterTypes The method's parameter types.
     *
     * @return The desired method or {@code null}.
     *
     * @see Class#getMethod
     */
    public static Method getMethod(Class<?> c,
            String name, Class<?>... parameterTypes) {
        try {
            return c.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * Returns a declared method of a class.
     * <p>
     * The returned method has been made accessible. Unlike {@link Class#getMethod},
     * this method returns {@code null} if no matching method is found.
     *
     * @param c              The class object.
     * @param name           The method name.
     * @param parameterTypes The method's parameter types.
     *
     * @return The desired method or {@code null}.
     *
     * @see Class#getDeclaredMethod
     */
    public static Method getDeclaredMethod(Class<?> c,
            String name, Class<?>... parameterTypes) {
        try {
            final Method m = c.getDeclaredMethod(name, parameterTypes);
            makeAccessible(m);
            return m;
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * Invokes a method.
     * <p>
     * The method will be made accessible, if it is inaccessible. Unlike
     * {@link Method#invoke}, this method throws a {@link RuntimeException}
     * when the reflective operation has failed.
     *
     * @param m         The method to be invoked.
     * @param o         The object from which to invoke the method, or {@code null}
     *                  for a static method.
     * @param paramters The parameters for the method call.
     *
     * @return The value returned from the method, or {@code null} for a void
     *         method.
     */
    public static Object invoke(Method m, Object o, Object... paramters) {
        try {
            makeAccessible(m);
            return m.invoke(o, paramters);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a new instance with the default constructor of the specified class.
     * <p>
     * Unlike {@link Class#newInstance}, this method returns {@code null} if
     * the construction fails.
     *
     * @param <T> The type modeled by the class.
     *
     * @param c The class object.
     *
     * @return A new instance or {@code null}.
     *
     * @see Class#newInstance
     */
    public static <T> T newInstance(Class<T> c) {
        try {
            return getDeclaredConstructor(c).newInstance();
        } catch (ReflectiveOperationException ex) {
            return null;
        }
    }

    private static void makeAccessible(final AccessibleObject ao) {
        if (!ao.isAccessible()) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    ao.setAccessible(true);
                    return null;
                }
            });
        }
    }
}

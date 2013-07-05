/*
 * Copyright (C) 2013 Zhao Yi
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility methods for Java Beans.
 *
 * @author Zhao Yi
 */
public class BeanUtils {
    private static final ConcurrentMap<Class<?>, BeanMate>
            BEAN_MATE_MAP = new ConcurrentHashMap<>();

    private BeanUtils() {
    }

    /**
     * Returns the specified bean property's type.
     *
     * @param beanClass The bean class.
     * @param propertyName The property name.
     * @return The property's type, or {@code null} if no such property is defined
     *         in the bean class.
     */
    public static Class<?> getPropertyType(Class<?> beanClass, String propertyName) {
        BeanMate beanMate = getBeanMate(beanClass);
        Method getter = beanMate.getterMap.get(propertyName);
        if (getter != null) {
            return getter.getReturnType();
        }
        Method setter = beanMate.setterMap.get(propertyName);
        if (setter != null) {
            return setter.getParameterTypes()[0];
        }
        return null;
    }

    /**
     * Checks whether the specified bean property is readable.
     *
     * @param beanClass The bean class.
     * @param propertyName The property name.
     * @return {@code true} if the property is defined in the bean class and
     *         is readable, otherwise {@code false}.
     */
    public static boolean isPropertyReadable(Class<?> beanClass, String propertyName) {
        return getBeanMate(beanClass).getterMap.get(propertyName) != null;
    }

    /**
     * Checks whether the specified bean property is writable.
     *
     * @param beanClass The bean class that may define the property.
     * @param propertyName The property name.
     * @return {@code true} if the property is defined in the bean class and
     *         is writable, otherwise {@code false}.
     */
    public static boolean isPropertyWritable(Class<?> beanClass, String propertyName) {
        return getBeanMate(beanClass).setterMap.get(propertyName) != null;
    }

    /**
     * Gets the specified bean property value.
     *
     * @param bean The bean from which the property is got.
     * @param propertyName The property name.
     * @return The property value.
     */
    public static Object getProperty(Object bean, String propertyName) {
        return ReflectionUtils.invoke(
                getBeanMate(bean.getClass()).getterMap.get(propertyName), bean);
    }

    /**
     * Sets a new value to the specified bean property.
     *
     * @param bean The bean to which the new property is set.
     * @param propertyName The property name.
     * @param property The property's new value.
     */
    public static void setProperty(Object bean, String propertyName, Object property) {
        ReflectionUtils.invoke(
                getBeanMate(bean.getClass()).setterMap.get(propertyName), bean, property);
    }

    /**
     * Checks whether the specified listener is supported by the bean class.
     *
     * @param beanClass The bean class.
     * @param listenerClass The listener class to be checked.
     * @return {@code true} if the listener is supported, otherwise {@code false}.
     */
    public static boolean isListenerSupported(
            Class<?> beanClass, Class<? extends EventListener> listenerClass) {
        return getBeanMate(beanClass).addListenerMap.containsKey(listenerClass);
    }

    /**
     * Checks whether {@link PropertyChangeListener} for a specific property
     * is supported by the bean class.
     *
     * @param beanClass The bean class.
     * @return {@code true} if the listener is supported, otherwise {@code false}.
     */
    public static boolean isNamedPropertyChangeListenerSupported(Class<?> beanClass) {
        return getBeanMate(beanClass).addSpecificPropertyChangeListener != null;
    }

    /**
     * Adds a listener to the specified bean.
     *
     * @param <L> The listener's type.
     * @param bean The bean to which the listener is added.
     * @param listenerClass the listener's class.
     * @param listener The listener to be added.
     */
    public static <L extends EventListener> void addListener(
            Object bean, Class<L> listenerClass, L listener) {
        ReflectionUtils.invoke(
                getBeanMate(bean.getClass()).addListenerMap.get(listenerClass),
                bean, listener);
    }

    /**
     * Removes a listener from the specified bean.
     *
     * @param <L> The listener's type.
     * @param bean The bean from which the listener is removed.
     * @param listenerClass the listener's class.
     * @param listener The listener to be removed.
     */
    public static <L extends EventListener> void removeListener(
            Object bean, Class<L> listenerClass, L listener) {
        ReflectionUtils.invoke(
                getBeanMate(bean.getClass()).removeListenerMap.get(listenerClass),
                bean, listener);
    }

    /**
     * Adds a {@link PropertyChangeListener} for a specific property to
     * the specified bean.
     *
     * @param bean The bean to which the listener is added.
     * @param propertyName The property's name.
     * @param propertyChangeListener The listener to be added.
     */
    public static void addSpecificPropertyChangeListener(Object bean,
            String propertyName, PropertyChangeListener propertyChangeListener) {
        ReflectionUtils.invoke(
                getBeanMate(bean.getClass()).addSpecificPropertyChangeListener,
                bean, propertyName, propertyChangeListener);
    }

    /**
     * Removes a {@link PropertyChangeListener} for a specific property from
     * the specified bean.
     *
     * @param bean The bean from which the listener is removed.
     * @param propertyName The property's name.
     * @param propertyChangeListener The listener to be removed.
     */
    public static void removeSpecificPropertyChangeListener(Object bean,
            String propertyName, PropertyChangeListener propertyChangeListener) {
        ReflectionUtils.invoke(
                getBeanMate(bean.getClass()).removeSpecificPropertyChangeListener,
                bean, propertyName, propertyChangeListener);
    }

    /**
     * Adds a listener to the specified bean property, and if the property is
     * changed later, removes it from the old property and adds it to the new
     * one.
     *
     * @param <L> The listener's type
     * @param bean The bean that owns the property.
     * @param propertyName The property name.
     * @param listenerClass The listener's class.
     * @param listener The listener to be added.
     */
    public static <L extends EventListener> void keepListener(Object bean,
            String propertyName, final Class<L> listenerClass, final L listener) {
        Object property = getProperty(bean, propertyName);
        if (property != null) {
            addListener(property, listenerClass, listener);
        }
        addSpecificPropertyChangeListener(bean, propertyName, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Object oldValue = evt.getOldValue();
                if (oldValue != null) {
                    removeListener(oldValue, listenerClass, listener);
                }
                Object newValue = evt.getNewValue();
                if (newValue != null) {
                    addListener(newValue, listenerClass, listener);
                }
            }
        });
    }

    private static BeanMate getBeanMate(Class<?> beanClass) {
        BeanMate beanMate = BEAN_MATE_MAP.get(beanClass);
        return beanMate == null
                ? BEAN_MATE_MAP.putIfAbsent(beanClass, new BeanMate(beanClass))
                : beanMate;
    }

    private static class BeanMate {
        private Map<String, Method> getterMap;
        private Map<String, Method> setterMap;
        private Map<Class<?>, Method> addListenerMap;
        private Map<Class<?>, Method> removeListenerMap;
        private Method addSpecificPropertyChangeListener;
        private Method removeSpecificPropertyChangeListener;

        private BeanMate(Class<?> beanClass) {
            getterMap = new HashMap<>();
            setterMap = new HashMap<>();
            addListenerMap = new HashMap<>();
            removeListenerMap = new HashMap<>();
            for (Method method : beanClass.getMethods()) {
                String name = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                switch (parameterTypes.length) {
                    case 0:
                        if (name.startsWith("get")) {
                            getterMap.put(Character.toLowerCase(name.charAt(3))
                                    + name.substring(4), method);
                        }
                        break;
                    case 1:
                        if (name.startsWith("set")) {
                            setterMap.put(Character.toLowerCase(name.charAt(3))
                                    + name.substring(4), method);
                        } else if (name.endsWith("Listener")
                                && EventListener.class.isAssignableFrom(parameterTypes[0])) {
                            if (name.startsWith("add")) {
                                addListenerMap.put(parameterTypes[0], method);
                            } else if (name.startsWith("remove")) {
                                removeListenerMap.put(parameterTypes[0], method);
                            }
                        }
                        break;
                    case 2:
                        if (parameterTypes[0] == String.class
                                && PropertyChangeListener.class.isAssignableFrom(parameterTypes[1])) {
                            switch (name) {
                                case "addPropertyChangeListener":
                                    addSpecificPropertyChangeListener = method;
                                    break;
                                case "removePropertyChangeListener":
                                    removeSpecificPropertyChangeListener = method;
                            }
                        }
                }
            }
        }
    }
}

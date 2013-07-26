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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * This invocation handler provides default implementations of the {@link
 * Object#equals equals}, {@link Object#hashCode hashCode} and {@link
 * Object#toString toString} methods that are not by default handled by proxy
 * instances.
 *
 * @author Zhao Yi
 */
public abstract class AbstractInvocationHandler implements InvocationHandler {
    private static final Method EQUALS = ReflectionUtils.getMethod(
            Object.class, "equals", Object.class);
    private static final Method HASH_CODE = ReflectionUtils.getMethod(
            Object.class, "hashCode");
    private static final Method TO_STRING = ReflectionUtils.getMethod(
            Object.class, "toString");

    /**
     * {@inheritDoc}
     * <p>
     * If the method is {@link Object#equals equals}, {@link Object#hashCode hashCode}
     * or {@link Object#toString toString}, corresponding method defined in this
     * class is called respectively. Otherwise, {@link #invokeOthers invokeOthers}
     * is called.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.equals(EQUALS)) {
            return equals(proxy, args[0]);
        } else if (method.equals(HASH_CODE)) {
            return hashCode(proxy);
        } else if (method.equals(TO_STRING)) {
            return toString(proxy);
        } else {
            return invokeOthers(proxy, method, args);
        }
    }

    /**
     * Invoked when the proxy's {@link Object#equals equals} method is called.
     * By default this method compares two objects with {@code ==}, and subclasses
     * can override this behavior as needed.
     *
     * @param proxy The proxy instance.
     * @param other The object with which to compare.
     * @return {@code true} if two objects are the same, otherwise {@code false}.
     */
    protected boolean equals(Object proxy, Object other) {
        return proxy == other;
    }

    /**
     * Invoked when the proxy's {@link Object#hashCode hashCode} method is called.
     * By default this method returns the proxy's identity hash code, and subclasses
     * can override this behavior as needed.
     *
     * @param proxy The proxy instance.
     * @return The hash code.
     */
    protected int hashCode(Object proxy) {
        return System.identityHashCode(proxy);
    }

    /**
     * Invoked when the proxy's {@link Object#toString toString} method is called.
     * By default this method returns {@code <class_name>@<hash_code>}, and
     * subclasses can override this behavior as needed.
     *
     * @param proxy The proxy instance.
     * @return The string representation.
     */
    public String toString(Object proxy) {
        return proxy.getClass().getName() + "@" + Integer.toHexString(hashCode(proxy));
    }

    /**
     * Processes a method invocation when the method is not one of {@link
     * Object#equals equals}, {@link Object#hashCode hashCode} and {@link
     * Object#toString toString}.
     *
     * @param proxy The proxy instance.
     * @param method The method being invoked.
     * @param args The method arguments.
     * @return The value to return from the method invocation.
     */
    protected abstract Object invokeOthers(Object proxy, Method method, Object[] args);
}

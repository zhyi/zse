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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility methods for exceptions.
 *
 * @author Zhao Yi
 */
public class ExceptionUtils {
    private ExceptionUtils() {
    }

    /**
     * Prints the stack trace of an exception to a string.
     *
     * @param t The exception from which to get the stack trace.
     *
     * @return The stack trace as a string.
     */
    public static String printStackTrace(Throwable t) {
        StringWriter out = new StringWriter();
        t.printStackTrace(new PrintWriter(out));
        return out.toString();
    }

    /**
     * Returns the root cause of an exception, or {@code null} if it does not
     * have one.
     *
     * @param t The exception to be checked for root cause.
     *
     * @return The root cause or {@code null}.
     *
     * @see Throwable#getCause()
     */
    public static Throwable getRootCause(Throwable t) {
        Throwable cause = null;
        Throwable parent = t.getCause();
        while (parent != null) {
            cause = parent;
            parent = parent.getCause();
        }
        return cause;
    }
}

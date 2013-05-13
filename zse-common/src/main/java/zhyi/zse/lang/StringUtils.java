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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods for strings.
 *
 * @author Zhao Yi
 */
public final class StringUtils {
    private StringUtils() {
    }

    /**
     * Splits a string with the specified delimiter, {@link
     * DelimitationStyle#IGNORE_DELIMITER IGNORE_DELIMITER} delimitation style,
     * and trimming option.
     * <p>
     * This method behaves similar to {@link String#split(String)}, but runs
     * faster because it does not use regular expression.
     * <p>
     * For example, splitting string {@code "/abc//d"} using {@code "/"}
     * as the delimiter yields a result of {@code "", "abc", "", "d"}.
     *
     * @param source    The source string to be split.
     * @param delimiter The delimiter string used to split the source string.
     * @param trim      Whether each segment should be trimmed.
     *
     * @return The resulting segments as a list of strings.
     */
    public static List<String> split(String source, String delimiter, boolean trim) {
        return split(source, delimiter, DelimitationStyle.IGNORE_DELIMITER, trim, -1);
    }

    /**
     * Splits a string with the specified delimiter, delimitation style, and
     * trimming option.
     * <p>
     * For example, splitting string {@code "/abc//d"} using {@code "/"}
     * as the delimiter yields the following results with different styles:
     * <dl>
     * <dt>{@link DelimitationStyle#IGNORE_DELIMITER IGNORE_DELIMITER}
     * <dd>{@code "", "abc", "", "d"}
     * <dt>{@link DelimitationStyle#PREPEND_DELIMITER PREPEND_DELIMITER}
     * <dd>{@code "", "/abc", "/", "/d"}
     * <dt>{@link DelimitationStyle#APPEND_DELIMITER APPEND_DELIMITER}
     * <dd>{@code "/", "abc/", "/", "d"}
     * </dl>
     *
     * @param source    the source string to be split.
     * @param delimiter The delimiter string used to split the source string.
     * @param style     The delimitation style; may be {@code null}, in which
     *                  case {@link DelimitationStyle#IGNORE_DELIMITER IGNORE_DELIMITER}
     *                  is used. Note that {@link DelimitationStyle#INSERT_DELIMITER
     *                  INSERT_DELIMITER} is not supported.
     * @param trim      Whether each segment should be trimmed.
     *
     * @return The resulting segments as a list of strings.
     *
     * @throws IllegalArgumentException If the delimitation style is
     *                                  {@link DelimitationStyle#INSERT_DELIMITER
     *                                  INSERT_DELIMITER}.
     */
    public static List<String> split(String source, String delimiter,
            DelimitationStyle style, boolean trim) {
        return split(source, delimiter, style, trim, -1);
    }

    /**
     * Splits a string with the specified delimiter, {@link
     * DelimitationStyle#IGNORE_DELIMITER IGNORE_DELIMITER} delimitation style,
     * and the maximum splitting times.
     * <p>
     * For example, splitting string {@code "/abc//d"} twice using {@code "/"}
     * as the delimiter yields a result of {@code "", "abc", "/d"}.
     *
     * @param source    the source string to be split.
     * @param delimiter The delimiter string used to split the source string.
     * @param trim      Whether each segment should be trimmed.
     * @param limit     The maximum splitting times. If it is {@code 0},
     *                  the returned list contains only the source string.
     *                  If it is negative, no limitation is applied.
     *
     * @return The resulting segments as a list of strings.
     */
    public static List<String> split(String source, String delimiter, boolean trim, int limit) {
        return split(source, delimiter, DelimitationStyle.IGNORE_DELIMITER, trim, limit);
    }

    /**
     * Splits a string with the specified delimiter, delimitation style,
     * trimming option, and the maximum splitting times.
     * <p>
     * For example, splitting string {@code "/abc//d"} twice using {@code "/"}
     * as the delimiter yields the following results with different styles:
     * <dl>
     * <dt>{@link DelimitationStyle#IGNORE_DELIMITER IGNORE_DELIMITER}
     * <dd>{@code "", "abc", "/d"}
     * <dt>{@link DelimitationStyle#PREPEND_DELIMITER PREPEND_DELIMITER}
     * <dd>{@code "", "/abc", "//d"}
     * <dt>{@link DelimitationStyle#APPEND_DELIMITER APPEND_DELIMITER}
     * <dd>{@code "/", "abc/", "/d"}
     * </dl>
     *
     * @param source    the source string to be split.
     * @param delimiter The delimiter string used to split the source string.
     * @param style     The delimitation style; may be {@code null}, in which
     *                  case {@link DelimitationStyle#IGNORE_DELIMITER IGNORE_DELIMITER}
     *                  is used. Note that {@link DelimitationStyle#INSERT_DELIMITER
     *                  INSERT_DELIMITER} is not supported.
     * @param trim      Whether each segment should be trimmed.
     * @param limit     The maximum splitting times. If it is {@code 0},
     *                  the returned list contains only the source string.
     *                  If it is negative, no limitation is applied.
     *
     * @return The resulting segments as a list of strings.
     *
     * @throws IllegalArgumentException If the delimitation style is
     *                                  {@link DelimitationStyle#INSERT_DELIMITER
     *                                  INSERT_DELIMITER}.
     */
    public static List<String> split(String source, String delimiter,
            DelimitationStyle style, boolean trim, int limit) {
        if (style == DelimitationStyle.INSERT_DELIMITER) {
            throw new IllegalArgumentException(
                    "INSERT_DELIMITER delimitation style is not supported.");
        }

        if (limit == 0 || source.isEmpty()) {
            return Arrays.asList(source);
        }

        boolean limited = limit > 0;
        List<String> segments = new ArrayList<>();

        // Special processing for an empty dilimiter by breaking the source
        // string to single characters.
        if (delimiter.isEmpty()) {
            int i;
            for (i = -1; i <= source.length(); i++) {
                if (limited) {
                    limit--;
                    if (limit < 0) {
                        String lastSegment = source.substring(i);
                        segments.add(trim ? lastSegment.trim() : lastSegment);
                        break;
                    }
                }

                if (i == -1 || i == source.length()) {
                    segments.add("");
                } else {
                    String segment = String.valueOf(source.charAt(i));
                    segments.add(trim ? segment.trim() : segment);
                }
            }
            return segments;
        }

        int searchFrom = 0;
        int delimiterIndex;
        int splitFrom;
        int splitTo;
        boolean over = false;

        while (true) {
            if (limited) {
                limit--;
                if (limit < 0) {
                    over = true;
                }
            }

            if (over) {
                delimiterIndex = source.length();
            } else {
                delimiterIndex = source.indexOf(delimiter, searchFrom);
                if (delimiterIndex == -1) {
                    delimiterIndex = source.length();
                    over = true;
                }
            }

            switch (style) {
                case PREPEND_DELIMITER:
                    splitFrom = searchFrom - delimiter.length();
                    if (splitFrom < 0) {
                        // The source string doesn't start with the dilimiter,
                        // and the first segment will not contain the delimiter.
                        splitFrom = 0;
                    }
                    splitTo = delimiterIndex;
                    break;
                case APPEND_DELIMITER:
                    splitFrom = searchFrom;
                    splitTo = delimiterIndex + delimiter.length();
                    if (splitTo > source.length()) {
                        // The last segment will not contain the delimiter.
                        splitTo = source.length();
                    }
                    break;
                case IGNORE_DELIMITER:
                default:    // ?!
                    splitFrom = searchFrom;
                    splitTo = delimiterIndex;
            }

            String segment = source.substring(splitFrom, splitTo);
            segments.add(trim ? segment.trim() : segment);

            if (over) {
                break;
            } else {
                searchFrom = delimiterIndex + delimiter.length();
            }
        }

        return segments;
    }

    /**
     * Concatenates the string representations of the objects contained in the
     * specified collection.
     * <p>
     * The concatenation order is the same as the iteration order of
     * the specified collection.
     * <p>
     * For example, concatenating list {@code ["a", "b", "c"]} yields a result
     * of {@code "abc"}.
     *
     * @param sources The source objects to be concatenated.
     *
     * @return The concatenation result.
     */
    public static String concat(Collection<?> sources) {
        return concat(sources, "", DelimitationStyle.IGNORE_DELIMITER);
    }

    /**
     * Concatenates the string representations of the objects contained in the
     * specified list with the specified delimiter and delimitation style.
     * <p>
     * The concatenation order is the same as the iteration order of
     * the specified collection.
     * <p>
     * For example, concatenating list {@code ["a", "b", "c"]} using {@code "/"}
     * as the delimiter yields the following results with different delimitation
     * styles:
     * <dl>
     * <dt>{@link DelimitationStyle#IGNORE_DELIMITER IGNORE_DELIMITER}
     * <dd>{@code "abc"}
     * <dt>{@link DelimitationStyle#PREPEND_DELIMITER PREPEND_DELIMITER}
     * <dd>{@code "/a/b/c"}
     * <dt>{@link DelimitationStyle#APPEND_DELIMITER APPEND_DELIMITER}
     * <dd>{@code "a/b/c/"}
     * </dl>
     *
     * @param sources   The source objects to be concatenated.
     * @param delimiter The delimiter to be inserted between source objects.
     * @param style     The delimitation style; may be {@code null}, in which
     *                  case {@link DelimitationStyle#IGNORE_DELIMITER IGNORE_DELIMITER}
     *                  is used.
     *
     * @return The concatenation result.
     */
    public static String concat(Collection<? extends Object> sources,
            String delimiter, DelimitationStyle style) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Object source : sources) {
            switch (style) {
                case PREPEND_DELIMITER:
                    result.append(delimiter).append(source);
                    break;
                case APPEND_DELIMITER:
                    result.append(source).append(delimiter);
                    break;
                case INSERT_DELIMITER:
                    if (first) {
                        result.append(source);
                        first = false;
                    } else {
                        result.append(delimiter).append(source);
                    }
                    break;
                case IGNORE_DELIMITER:
                default:    // ?!
                    result.append(source);
            }
        }
        return result.toString();
    }

    /**
     * Replaces the first occurrence of string {@code target} with string
     * {@code replacement} in a string.
     *
     * @param source      The source string.
     * @param target      The character to be replaced.
     * @param replacement The character to be substituted for the first match.
     *
     * @return The resulting string.
     */
    public static String replaceFirst(String source, String target, String replacement) {
        return replace(source, target, replacement, 1);
    }

    /**
     * Replaces all occurrences of string {@code target} with string
     * {@code replacement}.
     * <p>
     * This method behaves the same as {@link String#replace(CharSequence,
     * CharSequence)}, but runs faster because it does not use regular expression.
     *
     * @param source      The source string.
     * @param target      The character to be replaced.
     * @param replacement The character to be substituted for each match.
     *
     * @return The resulting string.
     */
    public static String replaceAll(String source, String target, String replacement) {
        return replace(source, target, replacement, -1);
    }

    /**
     * Replaces at most the first {@code limit} occurrences of string
     * {@code target} with string {@code replacement} in a string.
     *
     * @param source      The source string.
     * @param target      The string to be replaced.
     * @param replacement The string to be substituted for each match.
     * @param limit       The maximum replacing times. If it is {@code 0},
     *                    the source string is returned. If if is negative,
     *                    no limitation is applied.
     *
     * @return The resulting string.
     */
    public static String replace(String source, String target, String replacement, int limit) {
        if (target.equals(replacement) || limit == 0) {
            return source;
        }

        boolean limited = limit > 0;
        StringBuilder sb = new StringBuilder();

        // Special processing for an empty target string by inserting the replacement
        // string between every two characters in the source string.
        if (target.isEmpty()) {
            if (replacement.isEmpty()) {
                return source;
            }

            for (int i = 0; i <= source.length(); i++) {
                if (limited) {
                    limit--;
                    if (limit < 0) {
                        sb.append(source.substring(i));
                        break;
                    }
                }

                if (i == source.length()) {
                    sb.append(replacement);
                } else {
                    sb.append(replacement).append(String.valueOf(source.charAt(i)));
                }
            }
            return sb.toString();
        }

        boolean over = false;
        int searchFrom = 0;
        int targetIndex;
        while (true) {
            if (limited) {
                limit--;
                if (limit == 0) {
                    over = true;
                }
            }

            targetIndex = source.indexOf(target, searchFrom);
            if (targetIndex == -1) {
                over = true;
            } else {
                sb.append(source.substring(searchFrom, targetIndex)).append(replacement);
                searchFrom = targetIndex + target.length();
            }

            if (over) {
                sb.append(source.substring(searchFrom));
                break;
            }
        }
        return sb.toString();
    }

    /**
     * The delimitation styles that are used by some string operating methods.
     */
    public static enum DelimitationStyle {
        /**
         * Indicates the delimiter should not appear in all segments.
         */
        IGNORE_DELIMITER,
        /**
         * Indicates the delimiter should be prepended on each but the first
         * segment.
         */
        PREPEND_DELIMITER,
        /**
         * Indicates the delimiter should be appended on each but the last
         * segment.
         */
        APPEND_DELIMITER,
        /**
         * Indicates the delimiter should be inserted between each two segments.
         */
        INSERT_DELIMITER;
    }
}

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

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import zhyi.zse.lang.ReflectionUtils;

/**
 * Provides management for converters.
 * <p>
 * By default, converter manager instances support the following types:
 * <ul>
 * <li>{@code boolean} and its wrapper class {@link Boolean}.
 * <li>{@code byte} and its wrapper class {@link Byte}.
 * <li>{@code char} and its wrapper class {@link Character}.
 * <li>{@code short} and its wrapper class {@link Short}.
 * <li>{@code int} and its wrapper class {@link Integer}.
 * <li>{@code long} and its wrapper class {@link Long}.
 * <li>{@code float} and its wrapper class {@link Float}.
 * <li>{@code double} and its wrapper class {@link Double}.
 * <li>{@link String}.
 * <li>{@link Date}.
 * <li>{@link Locale}.
 * <li>Enumeration types. Converters for these types are automatically
 * registered in method {@link #getConverter(Class)}.
 * <li>{@link Serializable}. The literal value of a serializable object is
 * the BASE-64 representation of its serialized bytes.
 * </ul>
 * For most of the above types, the literal value of {@code null} is an empty
 * string, except that for {@link String} type, {@code null} is represented by
 * a special string {@code "**null**"}, and for {@link Serializable} type,
 * {@code null} is still represented by the BASE-64 representation of its
 * serialized bytes.
 *
 * @author Zhao Yi
 */
public class ConverterManager {
    private static final Converter<Boolean> BOOLEAN_CONVERTER = new BooleanConverter();
    private static final Converter<Byte> BYTE_CONVERTER = new ByteConverter();
    private static final Converter<Character> CHARACTER_CONVERTER = new CharacterConverter();
    private static final Converter<Short> SHORT_CONVERTER = new ShortConverter();
    private static final Converter<Integer> INTEGER_CONVERTER = new IntegerConverter();
    private static final Converter<Long> LONG_CONVERTER = new LongConverter();
    private static final Converter<Float> FLOAT_CONVERTER = new FloatConverter();
    private static final Converter<Double> DOUBLE_CONVERTER = new DoubleConverter();
    private static final Converter<String> STRING_CONVERTER = new StringConverter();
    private static final Converter<Date> DATE_CONVERTER = new DateConverter();
    private static final Converter<Locale> LOCALE_CONVERTER = new LocaleConverter();
    private static final Converter<Serializable> SERIALIZABLE_CONVERTER = new SerializableConverter();

    private ConcurrentMap<Class<?>, Converter<?>> converterMap = new ConcurrentHashMap<>();

    /**
     * Constructs a new converter manager.
     */
    public ConverterManager() {
        converterMap.put(Boolean.class, BOOLEAN_CONVERTER);
        converterMap.put(Byte.class, BYTE_CONVERTER);
        converterMap.put(Character.class, CHARACTER_CONVERTER);
        converterMap.put(Short.class, SHORT_CONVERTER);
        converterMap.put(Integer.class, INTEGER_CONVERTER);
        converterMap.put(Long.class, LONG_CONVERTER);
        converterMap.put(Float.class, FLOAT_CONVERTER);
        converterMap.put(Double.class, DOUBLE_CONVERTER);
        converterMap.put(String.class, STRING_CONVERTER);
        converterMap.put(Date.class, DATE_CONVERTER);
        converterMap.put(Locale.class, LOCALE_CONVERTER);
        converterMap.put(Serializable.class, SERIALIZABLE_CONVERTER);
    }

    /**
     * Registers a converter for a class.
     * <p>
     * If another converter for the same class has been registered, it is
     * replaced by the specified converter.
     *
     * @param <T> The type modeled by the target class.
     *
     * @param targetClass The target class of the converter.
     * @param converter   The converter to be registered.
     */
    public <T> void register(Class<T> targetClass, Converter<T> converter) {
        converterMap.put(ReflectionUtils.wrap(targetClass), converter);
    }

    /**
     * Deregisters the converter for the specified class.
     *
     * @param <T> The type modeled by the target class.
     *
     * @param forClass The target class of the converter to be removed.
     */
    public <T> void deregister(Class<T> forClass) {
        converterMap.remove(forClass);
    }

    /**
     * Returns the registered converter for the specified class.
     * <p>
     * If the target class is an {@code enum}, a converter will be automatically
     * registered.
     *
     * @param <T> The type modeled by the target class.
     *
     * @param targetClass The target class of the converter.
     *
     * @return The desired converter, or {@code null} if no such converter
     *         is found.
     */
    @SuppressWarnings("unchecked")
    public <T> Converter<T> getConverter(Class<T> targetClass) {
        Converter<T> converter = (Converter<T>) converterMap.get(
                ReflectionUtils.wrap(targetClass));
        if (converter == null && targetClass.isEnum()) {
            Converter<T> enumConverter = (Converter<T>) new EnumConverter<>(
                    targetClass.asSubclass(Enum.class));
            converter = (Converter<T>) converterMap.putIfAbsent(
                    targetClass, enumConverter);
            if (converter == null) {
                converter = enumConverter;
            }
        }
        return converter;
    }
}

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
package zhyi.zse.i18n;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * This class extends {@link Control} so that the fallback locale can be
 * specified in the constructor.
 *
 * @author Zhao Yi
 */
public class FallbackLocaleControl extends Control {
    /**
     * The instance that uses {@code null} as the fallback locale.
     */
    public static final FallbackLocaleControl NULL_CONTROL
            = new FallbackLocaleControl(null);
    /**
     * The instance that uses {@link Locale#US} as the fallback locale.
     */
    public static final FallbackLocaleControl EN_US_CONTROL
            = new FallbackLocaleControl(Locale.US);
    /**
     * The instance that uses {@link Locale#CHINA} as the fallback locale.
     */
    public static final FallbackLocaleControl ZH_CN_CONTROL
            = new FallbackLocaleControl(Locale.CHINA);

    private Locale fallbackLocale;

    /**
     * Constructs a new instance with the specified fallback locale.
     *
     * @param fallbackLocale The fallback locale.
     */
    public FallbackLocaleControl(Locale fallbackLocale) {
        this.fallbackLocale = fallbackLocale;
    }

    @Override
    public Locale getFallbackLocale(String baseName, Locale locale) {
        return fallbackLocale;
    }
}

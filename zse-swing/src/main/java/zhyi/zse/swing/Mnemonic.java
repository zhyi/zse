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
package zhyi.zse.swing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides the information of a mnemonic.
 *
 * @author Zhao Yi
 */
public class Mnemonic {
    private static final Pattern MNEMONIC_PATTERN = Pattern.compile("_._");

    private String text;
    private char mChar;
    private int inex;

    /**
     * Constructs a new mnemonic.
     *
     * @param text  The display text.
     * @param mChar The mnemonic character.
     * @param index The mnemonic character's index in the display text.
     */
    public Mnemonic(String text, char mChar, int index) {
        this.text = text;
        this.mChar = mChar;
        this.inex = index;
    }

    /**
     * Returns the display text.
     *
     * @return The display text.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the mnemonic character.
     *
     * @return The mnemonic character.
     */
    public char getMnemonicChar() {
        return mChar;
    }

    /**
     * Returns the mnemonic character's index.
     *
     * @return The mnemonic character's index.
     */
    public int getInex() {
        return inex;
    }

    /**
     * Analyzes the specified string and build a mnemonic.
     * <p>
     * The mnemonic character is defined as the first character that is surrounded
     * by dashes ({@code _}) in the string. The two dashes will be removed in
     * the display text of the returned mnemonic.
     *
     * @param s The string to be analyzed.
     *
     * @return The mnemonic, or {@code null} if the string is {@code null} or
     *         does not have a mnemonic character.
     */
    public static Mnemonic analyze(String s) {
        if (s == null) {
            return null;
        }
        Matcher matcher = MNEMONIC_PATTERN.matcher(s);
        if (matcher.find()) {
            String mark = matcher.group();
            char mChar = mark.charAt(1);
            int start = matcher.start();
            int end = matcher.end();
            return new Mnemonic(
                    s.substring(0, start) + mChar + s.substring(end), mChar, start);
        } else {
            return null;
        }
    }
}

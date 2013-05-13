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

/**
 * Converter for {@code char} and its wrapper class {@link Character}.
 *
 * @author Zhao Yi
 */
class CharacterConverter extends AbstractConverter<Character> {
    @Override
    public Character asObjectInternal(String literalValue) {
        return Character.valueOf(literalValue.charAt(0));
    }

    @Override
    public String asStringInternal(Character object) {
        return object.toString();
    }
}

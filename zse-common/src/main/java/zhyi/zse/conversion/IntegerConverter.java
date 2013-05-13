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
 * Converter for {@code int} and its wrapper class {@link Integer}.
 *
 * @author Zhao Yi
 */
class IntegerConverter extends AbstractConverter<Integer> {
    @Override
    public Integer asObjectInternal(String literalValue) {
        return Integer.valueOf(literalValue);
    }

    @Override
    public String asStringInternal(Integer object) {
        return object.toString();
    }
}

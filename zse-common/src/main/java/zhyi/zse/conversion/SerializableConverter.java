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

import java.io.IOException;
import java.io.Serializable;
import javax.xml.bind.DatatypeConverter;
import zhyi.zse.lang.ObjectUtils;

/**
 * @author Zhao Yi
 */
class SerializableConverter implements Converter<Serializable> {
    @Override
    public Serializable asObject(String literalValue) {
        try {
            return ObjectUtils.unmarshal(
                    DatatypeConverter.parseBase64Binary(literalValue));
        } catch (ClassNotFoundException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String asString(Serializable object) {
        try {
            return DatatypeConverter.printBase64Binary(ObjectUtils.marshal(object));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

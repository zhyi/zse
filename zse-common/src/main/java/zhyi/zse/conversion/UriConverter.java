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
package zhyi.zse.conversion;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Zhao Yi
 */
public class UriConverter extends AbstractConverter<URI> {
    @Override
    protected URI asObjectInternal(String literalValue) {
        try {
            return new URI(literalValue);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected String asStringInternal(URI object) {
        return object.toString();
    }
}

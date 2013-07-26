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
package zhyi.zse.swing.parser;

import org.w3c.dom.Element;

/**
 * A bean processor can be registered to a {@link GuiParser gui parser} for
 * custom bean handling.
 *
 * @author Zhao Yi
 */
public interface BeanProcessor {
    /**
     * The callback method to be invoked during parsing the GUI.
     *
     * @param bean The bean produced by the standard parser.
     * @param e The XML element representing the bean.
     */
    void process(Object bean, Element e);
}

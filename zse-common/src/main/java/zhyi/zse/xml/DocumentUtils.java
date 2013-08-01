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
package zhyi.zse.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility methods for XML documents.
 *
 * @author Zhao Yi
 */
public class DocumentUtils {
    private DocumentUtils() {
    }

    /**
     * Returns the specified node's child nodes with the specified type.
     *
     * @param <T> The type of the child nodes.
     * @param node The node from which to get child nodes.
     * @param nodeClass The class of the child nodes.
     * @return A list of the desired child nodes.
     */
    public static <T extends Node> List<T> getChildNodes(Node node, Class<T> nodeClass) {
        List<T> childNodes = new ArrayList<>();
        NodeList childList = node.getChildNodes();
        int length = childList.getLength();
        for (int i = 0; i < length; i++) {
            Node child = childList.item(i);
            if (nodeClass.isInstance(child)) {
                childNodes.add(nodeClass.cast(child));
            }
        }
        return childNodes;
    }

    /**
     * Returns the specified element's child elements by tag name.
     *
     * @param e The element from which to get child elements.
     * @param name The name of the elements to match on. The special value"*"
     *        matches all tags.
     * @return A list of the matching child elements.
     */
    public static List<Element> getChildElements(Element e, String name) {
        List<Element> childElements = new ArrayList<>();
        NodeList childList = e.getElementsByTagName(name);
        int length = childList.getLength();
        for (int i = 0; i < length; i++) {
            childElements.add((Element) childList.item(i));
        }
        return childElements;
    }

    /**
     * Returns the specified element's child elements by namespace URI and local
     * name.
     *
     * @param e The element from which to get child elements.
     * @param ns The namespace URI of the elements to match on. The special value
     *        "*" matches all namespaces.
     * @param name The local name of the elements to match on. The special value
     *        "*" matches all tags.
     * @return A list of the matching child elements.
     */
    public static List<Element> getChildElementsNs(Element e, String ns, String name) {
        List<Element> childElements = new ArrayList<>();
        NodeList childList = e.getElementsByTagNameNS(ns, name);
        int length = childList.getLength();
        for (int i = 0; i < length; i++) {
            childElements.add((Element) childList.item(i));
        }
        return childElements;
    }

    /**
     * Returns the specified element's attributes.
     *
     * @param e The element from which to get attributes.
     * @return A list of attributes.
     */
    public static List<Attr> getAttributes(Element e) {
        List<Attr> attrs = new ArrayList<>();
        NamedNodeMap attrMap = e.getAttributes();
        int length = attrMap.getLength();
        for (int i = 0; i < length; i++) {
            attrs.add((Attr) attrMap.item(i));
        }
        return attrs;
    }

    /**
     * Returns the specified element's attribute value by name.
     *
     * @param e The element from which to get the attribute value.
     * @param name The name of the attribute.
     * @return The attribute value, or {@code null} if the element does not
     *         have the attribute.
     */
    public static String getAttribute(Element e, String name) {
        Attr attr = e.getAttributeNode(name);
        return attr == null ? null : attr.getValue();
    }

    /**
     * Returns the specified element's attribute value by namespace URI and
     * local name.
     *
     * @param e The element from which to get the attribute value.
     * @param ns The namespace URI of the attribute to retrieve.
     * @param name The local name of the attribute.
     * @return The attribute value, or {@code null} if the element does not
     *         have the attribute.
     */
    public static String getAttributeNs(Element e, String ns, String name) {
        Attr attr = e.getAttributeNodeNS(ns, name);
        return attr == null ? null : attr.getValue();
    }
}

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

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.plaf.basic.BasicHTML;

/**
 * This class simulates a hyper link with a button.
 *
 * @author Zhao Yi
 */
@SuppressWarnings("serial")
public class Link extends JButton {
    private static final JPopupMenu POPUP_MENU = new JPopupMenu();
    private static final JMenuItem COPY_LINK_MENU_ITEM = new JMenuItem();
    private static final String BUNDLE = "zhyi.zse.swing.Link";
    static {
        COPY_LINK_MENU_ITEM.setText(
                ResourceBundle.getBundle(BUNDLE).getString("copyLink"));
        COPY_LINK_MENU_ITEM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Link link = (Link) POPUP_MENU.getInvoker();
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                        new StringSelection(link.getUri().toString()), null);
            }
        });
        COPY_LINK_MENU_ITEM.addPropertyChangeListener(
                "locale", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                COPY_LINK_MENU_ITEM.setText(
                        ResourceBundle.getBundle(BUNDLE).getString("copyLink"));
            }
        });
    }

    private boolean visited;
    private String styledText;

    /**
     * Constructs a new link.
     */
    public Link() {
        this(null, null, null);
    }

    /**
     * Constructs a new link with the target URI and the URI's string representation
     * as the text.
     *
     * @param uri 
     */
    public Link(URI uri) {
        this(uri.toString(), null, uri);
    }

    /**
     * Constructs a new link with the text and target URI.
     *
     * @param text The link's text.
     * @param uri  The link's target URI.
     */
    public Link(String text, URI uri) {
        this(text, null, uri);
    }

    /**
     * Constructs a new link with the icon and the target URI.
     *
     * @param icon The link's icon.
     * @param uri  The link's target URI.
     */
    public Link(Icon icon, URI uri) {
        this(null, icon, uri);
    }

    /**
     * Constructs a new link with the text, icon and target URI.
     *
     * @param text The link's text.
     * @param icon The link's icon.
     * @param uri  The link's target URI.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Link(String text, Icon icon, URI uri) {
        super(text, icon);

        setUri(uri);
        setNormalStyle("color: blue");
        setHoveredStyle("text-decoration: underline");
        setVisitedStyle("color: purple");

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false);
        setMargin(new Insets(0, 0, 0, 0));
        setBorderPainted(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mayShowDefaultPopupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mayShowDefaultPopupMenu(e);
            }

            private void mayShowDefaultPopupMenu(MouseEvent e) {
                if (e.isPopupTrigger() && getComponentPopupMenu() == null) {
                    POPUP_MENU.show(Link.this, e.getX(), e.getY());
                }
            }
        });
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                URI uri = getUri();
                if (uri != null) {
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException ex) {
                        SwingUtils.showStackTrace(Link.this, ex, false);
                    }
                }

                if (!visited) {
                    visited = true;
                    revalidate();
                    repaint();
                }
            }
        });
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals(PropertyKey.NORMAL_STYLE.name())
                        || name.equals(PropertyKey.HOVERED_STYLE.name())
                        || name.equals(PropertyKey.VISITED_STYLE.name())) {
                    revalidate();
                    repaint();
                } else if (name.equals("UI")) {
                    styledText = null;
                }
            }
        });
    }

    /**
     * Returns this link's target URI.
     *
     * @return This link's target URI.
     */
    public URI getUri() {
        return (URI) getClientProperty(PropertyKey.URI);
    }

    /**
     * Sets this link's target URI.
     *
     * @param uri The new target URI for this link.
     */
    public void setUri(URI uri) {
        putClientProperty(PropertyKey.URI, uri);
    }

    /**
     * Returns this link's normal style.
     * <p>
     * This default value is "{@code color: blue}".
     *
     * @return This link's normal style.
     */
    public String getNormalStyle() {
        return (String) getClientProperty(PropertyKey.NORMAL_STYLE);
    }

    /**
     * Sets this link's normal style.
     *
     * @param normalStyle The new normal style for this link.
     */
    public void setNormalStyle(String normalStyle) {
        putClientProperty(PropertyKey.NORMAL_STYLE, normalStyle);
    }

    /**
     * Returns this link's style when the mouse is hovered.
     * <p>
     * This default value is "{@code text-decoration: underline}".
     *
     * @return This link's hovered style.
     */
    public String getHoveredStyle() {
        return (String) getClientProperty(PropertyKey.HOVERED_STYLE);
    }

    /**
     * Sets this link's style when the mouse is hovered.
     *
     * @param hoveredStyle The new hovered style for this link.
     */
    public void setHoveredStyle(String hoveredStyle) {
        putClientProperty(PropertyKey.HOVERED_STYLE, hoveredStyle);
    }

    /**
     * Returns this link's style when it has already been visited.
     * <p>
     * This default value is "{@code color: purple}".
     *
     * @return This link's visited style.
     */
    public String getVisitedStyle() {
        return (String) getClientProperty(PropertyKey.VISITED_STYLE);
    }

    /**
     * Sets this link's style when it has already been visited.
     *
     * @param visitedStyle The new visited style for this link.
     */
    public void setVisitedStyle(String visitedStyle) {
        putClientProperty(PropertyKey.VISITED_STYLE, visitedStyle);
    }

    @Override
    protected void paintComponent(Graphics g) {
        String text = getText();
        if (text == null) {
            text = "";
        }

        StringBuilder styleBuilder = new StringBuilder();
        appendStyle(styleBuilder, getNormalStyle());
        appendStyle(styleBuilder, getHoveredStyle());
        appendStyle(styleBuilder, getVisitedStyle());

        String newStyledText = String.format(
                "<html><div style=\"%s\">%s</div>", styleBuilder, text);
        if (!newStyledText.equals(styledText)) {
            styledText = newStyledText;
            BasicHTML.updateRenderer(this, styledText);
        } else {
            super.paintComponent(g);
        }
    }

    private void appendStyle(StringBuilder styleBuilder, String style) {
        if (style != null && !style.isEmpty()) {
            styleBuilder.append(style);
            if (!style.endsWith(";")) {
                styleBuilder.append(";");
            }
        }
    }

    private static enum PropertyKey {
        URI, NORMAL_STYLE, HOVERED_STYLE, VISITED_STYLE;
    }
}

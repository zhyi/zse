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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicHTML;

/**
 * This class simulates a hyper link with a button.
 *
 * @author Zhao Yi
 */
@SuppressWarnings("serial")
public class Link extends JButton {
    public static final String URI_KEY = "uri";
    public static final String NORMAL_STYLE_KEY = "normalStyle";
    public static final String HOVERED_STYLE_KEY = "hoveredStyle";
    public static final String VISITED_STYLE_KEY = "visitedStyle";

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
        setNormalStyle("color: blue;");
        setHoveredStyle("text-decoration: underline;");
        setVisitedStyle("color: purple;");

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false);
        setMargin(new Insets(0, 0, 0, 0));
        setBorderPainted(false);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                URI uri = getUri();
                if (uri != null) {
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException ex) {
                        SwingUtils.showStackTrace(ex, false, Link.this);
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
                switch (evt.getPropertyName()) {
                    case NORMAL_STYLE_KEY:
                    case HOVERED_STYLE_KEY:
                    case VISITED_STYLE_KEY:
                        revalidate();
                        repaint();
                        return;
                    case "UI":
                        styledText = null;
                }
                styledText = null;
            }
        });
        getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                revalidate();
                repaint();
            }
        });
    }

    /**
     * Returns this link's target URI.
     * <p>
     * This property is the client property mapping by {@link #URI_KEY URI_KEY}.
     *
     * @return This link's target URI.
     */
    public URI getUri() {
        return (URI) getClientProperty(URI_KEY);
    }

    /**
     * Sets this link's target URI.
     * <p>
     * This property is the client property mapping by {@link #URI_KEY URI_KEY}.
     *
     * @param uri The new target URI for this link.
     */
    public void setUri(URI uri) {
        putClientProperty(URI_KEY, uri);
    }

    /**
     * Returns this link's normal style.
     * <p>
     * This property is the client property mapping by {@link #NORMAL_STYLE_KEY
     * NORMAL_STYLE_KEY}.
     *
     * @return This link's normal style.
     */
    public String getNormalStyle() {
        return (String) getClientProperty(NORMAL_STYLE_KEY);
    }

    /**
     * Sets this link's normal style.
     * <p>
     * This property is the client property mapping by {@link #NORMAL_STYLE_KEY
     * NORMAL_STYLE_KEY}.
     *
     * @param normalStyle The new normal style for this link.
     */
    public void setNormalStyle(String normalStyle) {
        putClientProperty(NORMAL_STYLE_KEY, normalStyle);
    }

    /**
     * Returns this link's style when the mouse is hovered.
     * <p>
     * This property is the client property mapping by {@link #HOVERED_STYLE_KEY
     * HOVERED_STYLE_KEY}.
     *
     * @return This link's hovered style.
     */
    public String getHoveredStyle() {
        return (String) getClientProperty(HOVERED_STYLE_KEY);
    }

    /**
     * Sets this link's style when the mouse is hovered.
     * <p>
     * This property is the client property mapping by {@link #VISITED_STYLE_KEY
     * VISITED_STYLE_KEY}.
     *
     * @param hoveredStyle The new hovered style for this link.
     */
    public void setHoveredStyle(String hoveredStyle) {
        putClientProperty(HOVERED_STYLE_KEY, hoveredStyle);
    }

    /**
     * Returns this link's style when it has already been visited.
     * <p>
     * This property is the client property mapping by {@link #VISITED_STYLE_KEY
     * VISITED_STYLE_KEY}.
     *
     * @return This link's visited style.
     */
    public String getVisitedStyle() {
        return (String) getClientProperty(VISITED_STYLE_KEY);
    }

    /**
     * Sets this link's style when it has already been visited.
     * <p>
     * This property is the client property mapping by {@link #VISITED_STYLE_KEY
     * VISITED_STYLE_KEY}.
     *
     * @param visitedStyle The new visited style for this link.
     */
    public void setVisitedStyle(String visitedStyle) {
        putClientProperty(VISITED_STYLE_KEY, visitedStyle);
    }

    @Override
    protected void paintComponent(Graphics g) {
        String text = getText();
        if (text == null) {
            text = "";
        }

        String style = getNormalStyle();
        if (model.isRollover()) {
            style += getHoveredStyle();
        }
        if (visited) {
            style += getVisitedStyle();
        }

        String newStyledText = String.format(
                "<html><div style=\"%s\">%s</div>", style, text);
        if (!newStyledText.equals(styledText)) {
            styledText = newStyledText;
            BasicHTML.updateRenderer(this, styledText);
        } else {
            super.paintComponent(g);
        }
    }
}

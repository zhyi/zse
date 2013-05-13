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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class simulates a hyper link with a button.
 * <p>
 * It is recommended against to use HTML to format the link's text. Instead,
 * use the {@code setXxxStyle} methods to control the display styles.
 *
 * @author Zhao Yi
 */
@SuppressWarnings("serial")
public class Link extends JButton {
    private URI uri;
    private Font normalFont;
    private Font hoveredFont;
    private Font visitedFont;
    private boolean visited;

    /**
     * Constructs a new link.
     */
    public Link() {
        this(null, null, null);
    }

    /**
     * Constructs a new link with text and a target URI.
     *
     * @param text The link's text.
     * @param uri  The link's target URI.
     */
    public Link(String text, URI uri) {
        this(text, null, uri);
    }

    /**
     * Constructs a new link with an icon and the target URI.
     *
     * @param icon The link's icon.
     * @param uri  The link's target URI.
     */
    public Link(Icon icon, URI uri) {
        this(null, icon, uri);
    }

    /**
     * Constructs a new link with text, an icon and a target URI.
     *
     * @param text The link's text.
     * @param icon The link's icon.
     * @param uri  The link's target URI.
     */
    public Link(String text, Icon icon, URI uri) {
        super(text, icon);
        this.uri = uri;

        Map<TextAttribute, Object> attrMap = new HashMap<>();
        attrMap.put(TextAttribute.FOREGROUND, Color.BLUE);
        normalFont = getFont().deriveFont(attrMap);
        attrMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        hoveredFont = getFont().deriveFont(attrMap);
        attrMap.clear();
        attrMap.put(TextAttribute.FOREGROUND, Color.MAGENTA);
        visitedFont = getFont().deriveFont(attrMap);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Link.this.uri != null) {
                    try {
                        Desktop.getDesktop().browse(Link.this.uri);
                    } catch (IOException ex) {
                        ExceptionDialog.showError(ex, Link.this);
                    }
                }

                if (!visited) {
                    visited = true;
                    revalidate();
                    repaint();
                }
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
     *
     * @return This link's target URI.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the link's target URI.
     *
     * @param uri This link's new target URI.
     */
    public void setUri(URI uri) {
        if (!Objects.equals(this.uri, uri)) {
            URI oldUri = this.uri;
            this.uri = uri;
            firePropertyChange("uri", oldUri, uri);
        }
    }

    /**
     * Returns the normal font of the link.
     *
     * @return The normal font of the link.
     */
    public Font getNormalFont() {
        return normalFont;
    }

    /**
     * Sets the normal font of the link.
     *
     * @param normalFont The normal font of the link.
     */
    public void setNormalFont(Font normalFont) {
        if (!this.normalFont.equals(normalFont)) {
            this.normalFont = normalFont;
            revalidate();
            repaint();
        }
    }

    /**
     * Returns the font to be used when the link is hovered.
     *
     * @return The font to be used when the link is hovered.
     */
    public Font getHoveredFont() {
        return hoveredFont;
    }

    /**
     * Sets the font to be used when the link is hovered.
     *
     * @param hoveredFont The font to be used when the link is hovered.
     */
    public void setHoveredFont(Font hoveredFont) {
        if (!this.hoveredFont.equals(hoveredFont)) {
            this.hoveredFont = hoveredFont;
            revalidate();
            repaint();
        }
    }

    /**
     * Returns the font to be used when the link has been visited.
     *
     * @return The font to be used when the link has been visited.
     */
    public Font getVisitedFont() {
        return visitedFont;
    }

    /**
     * Sets the font to be used when the link has been visited.
     *
     * @param visitedFont The font to be used when the link has been visited.
     */
    public void setVisitedFont(Font visitedFont) {
        if (!this.visitedFont.equals(visitedFont)) {
            this.visitedFont = visitedFont;
            revalidate();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        boolean rollover = getModel().isRollover();
        if (visited && rollover) {
            setFont(hoveredFont.deriveFont(visitedFont.getAttributes()));
        } else if (visited && !rollover) {
            setFont(visitedFont);
        } else if (!visited && rollover) {
            setFont(hoveredFont);
        } else {
            setFont(normalFont);
        }
        super.printComponent(g);
    }
}

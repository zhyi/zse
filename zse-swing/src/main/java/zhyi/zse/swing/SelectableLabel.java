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
package zhyi.zse.swing;

import javax.swing.JEditorPane;

/**
 * A selectable label is simulated with a read-only editor pane, so that the user
 * can select and copy its text.
 *
 * @author Zhao Yi
 */
@SuppressWarnings("serial")
public class SelectableLabel extends JEditorPane {
    /**
     * Constructs a new selectable label.
     */
    public SelectableLabel() {
        this(null);
    }

    /**
     * Constructs a new selectable label with text.
     *
     * @param text The label's text; may be formatted with HTML tags.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public SelectableLabel(String text) {
        super("text/html", text);
        setBorder(null);
        setOpaque(false);
        setEditable(false);
    }
}

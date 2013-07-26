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

import java.util.Locale;
import javax.swing.undo.UndoManager;
import zhyi.zse.swing.cas.TextComponentContextActionSupport;

/**
 * The collection of property keys.
 *
 * @author Zhao Yi
 */
public class PropertyKeys {
    /**
     * Key to store an {@link UndoManager} client property in a text component,
     * used by {@link TextComponentContextActionSupport}.
     */
    public static final String UNDO_MANAGER = "undoManager";
    /**
     * Key to store a {@link ContextActionSupport} client property in a component,
     * used for context popup menu.
     */
    public static final String CONTEXT_ACTION_SUPPORT = "ContextActionSupport";
    /**
     * Key to store a boolean client property in a table, indicating whether
     * the table's row heights should be automatically resized to fit contents.
     */
    public static final String AUTO_FIT_ROW_HEIGHTS = "autoFitRowHeight";
    /**
     * Key to store a boolean client property in a table cell renderer component,
     * indicating whether the component should be treated as a real component
     * instead of a static painter.
     */
    public static final String REAL_CELL_COMPONENT = "realCellComponent";
    /**
     * Key to store a boolean client property in a component, indicating whether
     * the component is an editor, so that its {@link AeroEditorBorder} can
     * have highlight effect.
     */
    public static final String AERO_EDITOR = "aeroEditor";
    /**
     * Key to store a boolean property in a context action, indicating whether
     * the action is visible in the context popup menu.
     */
    public static final String ACTION_VISIBLE = "visible";
    /**
     * Key to store a {@link Locale} property in a context action, indicating
     * how the action's properties should be localized.
     */
    public static final String ACTION_LOCALE = "locale";

    private PropertyKeys() {
    }
}

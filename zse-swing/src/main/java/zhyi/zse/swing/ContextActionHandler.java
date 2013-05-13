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

import javax.swing.undo.UndoManager;

/**
 * The handler for a component's context actions, such as copy, paste, etc.
 * <p>
 * Once a such handler is set to a client property of a component with key
 * {@link #KEY KEY}, corresponding actions can be created with the methods
 * defined by {@link ContextActionFactory}.
 * <p>
 * All operations are optional, and if any of the operation is not supported,
 * a {@link UnsupportedOperationException} is thrown.
 *
 * @author Zhao Yi
 */
public interface ContextActionHandler {
    /**
     * The client property's key for a handler instance.
     */
    String KEY = "ContextActionHandler";

    /**
     * Returns the undo manager for the undo and redo actions.
     *
     * @return The undo manager for the undo and redo actions.
     */
    UndoManager getUndoManager();

    /**
     * Undos the last edit.
     */
    void undo();

    /**
     * Redos the last undone edit.
     */
    void redo();

    /**
     * Cuts the selected contents from the component to the clipboard.
     */
    void cut();

    /**
     * Copies the selected contents from the component to the clipboard.
     */
    void copy();

    /**
     * Pastes the contents from the clipboard to the component.
     */
    void paste();

    /**
     * Deletes the selected contents in the component.
     */
    void delete();

    /**
     * Selects all contents in the component.
     */
    void selectAll();

    /**
     * Cuts all contents from the component to the clipboard.
     */
    void cutAll();

    /**
     * Copies all contents from the component to the clipboard.
     */
    void copyAll();

    /**
     * Replaces all contents in the component by the contents in the clipboard.
     */
    void replaceAll();

    /**
     * Deletes all contents in the component.
     */
    void deleteAll();
}

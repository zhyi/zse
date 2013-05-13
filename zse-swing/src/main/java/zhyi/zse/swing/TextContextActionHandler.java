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

import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

/**
 * The context action implementation for text components.
 * <p>
 * All operations are supported.
 *
 * @author Zhao Yi
 */
public class TextContextActionHandler implements ContextActionHandler {
    private JTextComponent tc;
    private UndoManager um;

    public TextContextActionHandler(JTextComponent tc) {
        this.tc = tc;
        um = new UndoManager();
        tc.getDocument().addUndoableEditListener(um);
    }

    @Override
    public UndoManager getUndoManager() {
        return um;
    }

    @Override
    public void undo() {
        if (um.canUndo()) {
            um.undo();
        }
    }

    @Override
    public void redo() {
        if (um.canRedo()) {
            um.redo();
        }
    }

    @Override
    public void cut() {
        tc.cut();
    }

    @Override
    public void copy() {
        tc.copy();
    }

    @Override
    public void paste() {
        tc.paste();
    }

    @Override
    public void delete() {
        tc.replaceSelection("");
    }

    @Override
    public void selectAll() {
        tc.selectAll();
    }

    @Override
    public void cutAll() {
        tc.selectAll();
        tc.cut();
    }

    @Override
    public void copyAll() {
        tc.selectAll();
        tc.copy();
    }

    @Override
    public void replaceAll() {
        tc.selectAll();
        tc.paste();
    }

    @Override
    public void deleteAll() {
        tc.selectAll();
        tc.replaceSelection("");
    }
}

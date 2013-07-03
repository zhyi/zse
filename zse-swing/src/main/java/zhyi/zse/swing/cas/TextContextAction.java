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
package zhyi.zse.swing.cas;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import javax.swing.undo.UndoManager;
import zhyi.zse.swing.SwingUtils;

/**
 * Defines commonly used context actions for text component.
 *
 * @author Zhao Yi
 */
@SuppressWarnings(value = "serial")
public class TextContextAction extends TextAction {
    /**
     * The action that undoes the last edit. To support this action, the text
     * component must add an {@link UndoManager} to its document, and associate
     * it to the "{@code undoManager}" property.
     */
    public static final TextContextAction UNDO
            = new TextContextAction(TextContextActionType.undo, "ctrl Z");
    /**
     * The action that redoes the last undone edit. To support this action,
     * the text component must add an {@link UndoManager} to its document,
     * and associate it to the "{@code undoManager}" property.
     */
    public static final TextContextAction REDO
            = new TextContextAction(TextContextActionType.redo, "ctrl Y");
    /**
     * The action that cuts the selected text to the clipboard.
     */
    public static final TextContextAction CUT
            = new TextContextAction(TextContextActionType.cut, "ctrl X");
    /**
     * The action that copies the selected text to the clipboard.
     */
    public static final TextContextAction COPY
            = new TextContextAction(TextContextActionType.copy, "ctrl C");
    /**
     * The action that replaces the selected text by the text in the clipboard.
     */
    public static final TextContextAction PASTE
            = new TextContextAction(TextContextActionType.paste, "ctrl V");
    /**
     * The action that deletes the selected text.
     */
    public static final TextContextAction DELETE
            = new TextContextAction(TextContextActionType.delete, "DELETE");
    /**
     * The action that selects all text.
     */
    public static final TextContextAction SELECT_ALL
            = new TextContextAction(TextContextActionType.selectAll, "ctrl A");
    /**
     * The action that cuts all text to the clipboard.
     */
    public static final TextContextAction CUT_ALL
            = new TextContextAction(TextContextActionType.cutAll, "ctrl shift X");
    /**
     * The action that copies all text to the clipboard.
     */
    public static final TextContextAction COPY_ALL
            = new TextContextAction(TextContextActionType.copyAll, "ctrl shift C");
    /**
     * The action that replaces all text by the text in the clipboard.
     */
    public static final TextContextAction REPLACE_ALL
            = new TextContextAction(TextContextActionType.replaceAll, "ctrl shift V");
    /**
     * The action that deletes all text.
     */
    public static final TextContextAction DELETE_ALL
            = new TextContextAction(TextContextActionType.deleteAll, "ctrl DELETE");

    private TextContextActionType type;

    private TextContextAction(TextContextActionType type, String ks) {
        super(null);
        this.type = type;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(ks));
    }

    @Override
    public Object getValue(String key) {
        JTextComponent tc = getFocusedComponent();
        switch (key) {
            case NAME:
                UndoManager um = (UndoManager) tc.getClientProperty("undoManager");
                switch (type) {
                    case undo:
                        return um == null
                                ? UIManager.getString("AbstractUndoableEdit.undoText")
                                : um.getUndoPresentationName();
                    case redo:
                        return um == null
                                ? UIManager.getString("AbstractUndoableEdit.redoText")
                                : um.getRedoPresentationName();
                    default:
                        return ResourceBundle.getBundle("zhyi.zse.swing.cas.TextContextAction")
                                .getString(type.name());
                }
            case "visible":
                switch (type) {
                    case undo:
                    case redo:
                    case cut:
                    case paste:
                    case delete:
                    case cutAll:
                    case replaceAll:
                    case deleteAll:
                        return tc.isEditable();
                    default:
                        return true;
                }
            default:
                return super.getValue(key);
        }
    }

    @Override
    public boolean isEnabled() {
        JTextComponent tc = getFocusedComponent();
        if (!tc.isEnabled()) {
            return false;
        }

        UndoManager um = (UndoManager) tc.getClientProperty("undoManager");
        if (tc.isEditable()) {
            switch (type) {
                case undo:
                    return um == null ? false : um.canUndo();
                case redo:
                    return um == null ? false : um.canRedo();
                case cut:
                case copy:
                case delete:
                    return tc.getSelectedText() != null;
                case paste:
                case replaceAll:
                    return tc.getTransferHandler().canImport(tc,
                            Toolkit.getDefaultToolkit()
                                    .getSystemClipboard().getAvailableDataFlavors());
                default:    // selectAll, cutAll, copyAll and deleteAll.
                    return !SwingUtils.getRawText(tc).isEmpty();
            }
        } else {
            switch (type) {
                case copy:
                case copyAll:
                    return tc.getSelectedText() != null;
                case selectAll:
                    return !SwingUtils.getRawText(tc).isEmpty();
                default:
                    return false;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent tc = getFocusedComponent();
        UndoManager um = (UndoManager) tc.getClientProperty("undoManager");
        if (isEnabled()) {
            switch (type) {
                case undo:
                    if (um != null) {
                        um.undo();
                    }
                    return;
                case redo:
                    if (um != null) {
                        um.redo();
                    }
                    return;
                case cut:
                    tc.cut();
                    return;
                case copy:
                    tc.copy();
                    return;
                case paste:
                    tc.paste();
                    return;
                case delete:
                    tc.replaceSelection("");
                    return;
                case selectAll:
                    tc.selectAll();
                    return;
                case cutAll:
                    tc.selectAll();
                    tc.cut();
                    return;
                case copyAll:
                    tc.selectAll();
                    tc.copy();
                    return;
                case replaceAll:
                    tc.selectAll();
                    tc.paste();
                    return;
                case deleteAll:
                    // Don't use tc.setText("") in case of HTML document.
                    tc.selectAll();
                    tc.replaceSelection("");
            }
        }
    }

    private UndoManager getUndoManager() {
        return (UndoManager) getFocusedComponent().getClientProperty("undoManager");
    }

    private static enum TextContextActionType {
        undo, redo, cut, copy, paste, delete,
        selectAll, cutAll, copyAll, replaceAll, deleteAll;
    }
}

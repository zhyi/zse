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
import java.lang.reflect.Method;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import zhyi.zse.lang.ReflectionUtils;
import zhyi.zse.swing.PropertyKeys;
import zhyi.zse.swing.SwingUtils;

/**
 * The context action support for text component.
 * <p>
 * The following actions are provided by this class:
 * <dl>
 * <dt><b>Undo</b>
 * <dd>Undoes the last edit.
 * <dt><b>Redo</b>
 * <dd>Redoes the last undone edit.
 * <dt><b>Cut</b>
 * <dd>Cuts the selected text to the clipboard.
 * <dt><b>Copy</b>
 * <dd>Copies the selected text to the clipboard.
 * <dt><b>Paste</b>
 * <dd>Replaces the selected text with the text in the clipboard.
 * <dt><b>Delete</b>
 * <dd>Deletes the selected text.
 * <dt><b>Select All</b>
 * <dd>Selects all text.
 * <dt><b>Cut All</b>
 * <dd>Cuts all text to the clipboard.
 * <dt><b>Copy All</b>
 * <dd>Copies all text to the clipboard.
 * <dt><b>Replace All</b>
 * <dd>Replaces all text with the text in the clipboard.
 * <dt><b>Delete All</b>
 * <dd>Deletes all text.
 * </dl>
 *
 * @author Zhao Yi
 */
public class TextComponentContextActionSupport extends ContextActionSupport<JTextComponent> {
    private static final Method GET_FOCUSED_COMPONENT
            = ReflectionUtils.getDeclaredMethod(JTextComponent.class, "getFocusedComponent");

    /**
     * Constructs a new instance. To make "Undo" and "Redo" actions work,
     * an {@link UndoManager} needs be added to the document and stored as
     * a client property with the key {@link PropertyKeys#UNDO_MANAGER}.
     * If the {@link PropertyKeys#UNDO_MANAGER} client property is {@code null},
     * an {@link UndoManager} is automatically created and set up.
     *
     * @param textComponent The text component to be supported.
     */
    @SuppressWarnings("serial")
    public TextComponentContextActionSupport(JTextComponent textComponent) {
        super(textComponent);

        UndoManager um = (UndoManager) textComponent.getClientProperty(
                PropertyKeys.UNDO_MANAGER);
        if (um == null) {
            um = new UndoManager();
            textComponent.putClientProperty(PropertyKeys.UNDO_MANAGER, um);
            textComponent.getDocument().addUndoableEditListener(um);
        }

        install(TextContextAction.UNDO, 0);
        install(TextContextAction.REDO, 0);
        install(TextContextAction.CUT, 1);
        install(TextContextAction.COPY, 1);
        install(TextContextAction.PASTE, 1);
        install(TextContextAction.DELETE, 1);
        install(TextContextAction.SELECT_ALL, 2);
        install(TextContextAction.CUT_ALL, 2);
        install(TextContextAction.COPY_ALL, 2);
        install(TextContextAction.REPLACE_ALL, 2);
        install(TextContextAction.DELETE_ALL, 2);
    }

    private static JTextComponent getTextComponent() {
        return (JTextComponent) ReflectionUtils
                .invoke(GET_FOCUSED_COMPONENT, null);
    }

    @SuppressWarnings(value = "serial")
    private static class TextContextAction extends AbstractContextAction {
        private static final TextContextAction UNDO
                = new TextContextAction(TextContextActionType.undo, "ctrl Z");
        private static final TextContextAction REDO
                = new TextContextAction(TextContextActionType.redo, "ctrl Y");
        private static final TextContextAction CUT
                = new TextContextAction(TextContextActionType.cut, "ctrl X");
        private static final TextContextAction COPY
                = new TextContextAction(TextContextActionType.copy, "ctrl C");
        private static final TextContextAction PASTE
                = new TextContextAction(TextContextActionType.paste, "ctrl V");
        private static final TextContextAction DELETE
                = new TextContextAction(TextContextActionType.delete, "DELETE");
        private static final TextContextAction SELECT_ALL
                = new TextContextAction(TextContextActionType.selectAll, "ctrl A");
        private static final TextContextAction CUT_ALL
                = new TextContextAction(TextContextActionType.cutAll, "ctrl shift X");
        private static final TextContextAction COPY_ALL
                = new TextContextAction(TextContextActionType.copyAll, "ctrl shift C");
        private static final TextContextAction REPLACE_ALL
                = new TextContextAction(TextContextActionType.replaceAll, "ctrl shift V");
        private static final TextContextAction DELETE_ALL
                = new TextContextAction(TextContextActionType.deleteAll, "ctrl DELETE");

        private TextContextActionType type;

        private TextContextAction(TextContextActionType type, String ks) {
            super("zhyi.zse.swing.cas.TextComponentContextActionSupport", type.name());
            this.type = type;
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(ks));
        }

        @Override
        public Object getValue(String key) {
            JTextComponent tc = getTextComponent();
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
                            return super.getValue(NAME);
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
            JTextComponent tc = getTextComponent();
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
        public void doAction() {
            JTextComponent tc = getTextComponent();
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
                        tc.setText("");
                }
            }
        }

        private static enum TextContextActionType {
            undo, redo, cut, copy, paste, delete,
            selectAll, cutAll, copyAll, replaceAll, deleteAll;
        }
    }
}

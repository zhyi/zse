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

import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import zhyi.zse.swing.PropertyKeys;

/**
 * The context action support for text component.
 *
 * @author Zhao Yi
 */
public class TextContextActionSupport extends ContextActionSupport<JTextComponent> {
    /**
     * Constructs a new instance. To make {@link TextContextAction#UNDO} and
     * {@link TextContextAction#UNDO} actions work, an {@link UndoManager} must
     * be added to the document and stored as a client property with the key
     * {@link PropertyKeys#UNDO_MANAGER}. If the {@link PropertyKeys#UNDO_MANAGER}
     * client property is {@code null}, an {@link UndoManager} is automatically
     * created and set up.
     *
     * @param textComponent The text component to be supported.
     */
    @SuppressWarnings("serial")
    public TextContextActionSupport(JTextComponent textComponent) {
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
}

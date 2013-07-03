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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

/**
 * The context action support for text component.
 *
 * @author Zhao Yi
 */
public class TextContextActionSupport extends ContextActionSupport<JTextComponent> {
    private static final PropertyChangeListener
            DOCUMENT_CHANGE_HANDLER = new DocumentChangeHandler();

    /**
     * Constructs a new instance. For {@link TextContextAction#UNDO} and
     * {@link TextContextAction#UNDO} actions, an {@link UndoManager} is created
     * automatically, if the "{@code undoManager}" client property is {@code null}.
     * Any subsequent changes to the "{@code undoManager}" client property and
     * document are tracked.
     *
     * @param textComponent The text component to be supported.
     */
    @SuppressWarnings("serial")
    public TextContextActionSupport(JTextComponent textComponent) {
        super(textComponent);

        UndoManager um = (UndoManager) textComponent.getClientProperty("undoManager");
        if (um == null) {
            um = new UndoManager();
            textComponent.putClientProperty("undoManager", um);
            textComponent.getDocument().addUndoableEditListener(um);
            textComponent.addPropertyChangeListener("document", DOCUMENT_CHANGE_HANDLER);
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

    private static class DocumentChangeHandler implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case "undoManager":
                    Document doc = ((JTextComponent) evt.getSource()).getDocument();
                    doc.removeUndoableEditListener((UndoManager) evt.getOldValue());
                    doc.addUndoableEditListener((UndoManager) evt.getNewValue());
                    return;
                case "document":
                    UndoManager um = (UndoManager) ((JTextComponent) evt.getSource())
                            .getClientProperty("undoManager");
                    ((Document) evt.getOldValue()).removeUndoableEditListener(um);
                    ((Document) evt.getNewValue()).addUndoableEditListener(um);
            }
        }
    }
}

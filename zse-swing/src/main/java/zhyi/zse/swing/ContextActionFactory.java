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

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import zhyi.zse.lang.ReflectionUtils;

/**
 * Factory methods to create context actions for a component.
 * <p>
 * All created actions are based on the {@link ContextActionHandler} client
 * property of the target component, with the property name as
 * {@link ContextActionHandler#KEY}.
 *
 * @author Zhao Yi
 */
public class ContextActionFactory {
    private static final String BUNDLE = "zhyi.zse.swing.TextContextAction";
    private static final Map<String, Method> METHOD_MAP = new HashMap<>();
    static {
        for (Method m : ContextActionHandler.class.getMethods()) {
            METHOD_MAP.put(m.getName(), m);
        }
    }

    private ContextActionFactory() {
    }

    /**
     * Creates an {@link ContextActionHandler#undo() undo} action, with
     * {@code Ctrl+Z} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createUndoAction(final JComponent c) {
        return createAction(c, ActionKey.undo, "ctrl Z");
    }

    /**
     * Creates an {@link ContextActionHandler#undo() undo} action, with
     * {@code Ctrl+Y} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createRedoAction(final JComponent c) {
        return createAction(c, ActionKey.redo, "ctrl Y");
    }

    /**
     * Creates a {@link ContextActionHandler#redo() redo} action, with
     * {@code Ctrl+X} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createCutAction(final JComponent c) {
        return createAction(c, ActionKey.cut, "ctrl X");
    }

    /**
     * Creates a {@link ContextActionHandler#copy() copy} action, with
     * {@code Ctrl+C} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createCopyAction(final JComponent c) {
        return createAction(c, ActionKey.copy, "ctrl C");
    }

    /**
     * Creates a {@link ContextActionHandler#paste() paste} action, with
     * {@code Ctrl+V} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createPasteAction(final JComponent c) {
        return createAction(c, ActionKey.paste, "ctrl V");
    }

    /**
     * Creates a {@link ContextActionHandler#delete() delete} action, with
     * {@code Delete} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createDeleteAction(final JComponent c) {
        return createAction(c, ActionKey.delete, "DELETE");
    }

    /**
     * Creates a {@link ContextActionHandler#selectAll() select-all} action,
     * with {@code Ctrl+A} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createSelectAllAction(final JComponent c) {
        return createAction(c, ActionKey.selectAll, "ctrl A");
    }

    /**
     * Creates a {@link ContextActionHandler#cutAll() cut-all} action, with
     * {@code Ctrl+Shift+X} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createCutAllAction(final JComponent c) {
        return createAction(c, ActionKey.cutAll, "ctrl shift X");
    }

    /**
     * Creates a {@link ContextActionHandler#copyAll() copy-all} action, with
     * {@code Ctrl+Shift+C} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createCopyAllAction(final JComponent c) {
        return createAction(c, ActionKey.copyAll, "ctrl shift C");
    }

    /**
     * Creates a {@link ContextActionHandler#replaceAll() replace-all} action,
     * with {@code Ctrl+Shift+V} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createReplaceAllAction(final JComponent c) {
        return createAction(c, ActionKey.replaceAll, "ctrl shift V");
    }

    /**
     * Creates an {@link ContextActionHandler#deleteAll() delete-all} action,
     * with {@code Ctrl+Delete} as the accelerator key.
     *
     * @param c The component that originates this action.
     *
     * @return The created action object.
     */
    public static Action createDeleteAllAction(final JComponent c) {
        return createAction(c, ActionKey.deleteAll, "ctrl DELETE");
    }

    @SuppressWarnings("serial")
    private static Action createAction(
            final JComponent c, final ActionKey key, String ks) {
        final String name = key.name();
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object cah = c.getClientProperty(ContextActionHandler.KEY);
                if (cah instanceof ContextActionHandler) {
                    ReflectionUtils.invoke(METHOD_MAP.get(name), cah);
                }
            }
        };
        ResourceBundle rb = ResourceBundle.getBundle(BUNDLE);
        if (rb.containsKey(name)) {
            a.putValue(Action.NAME, rb.getString(name));
        }
        a.putValue(Action.ACTION_COMMAND_KEY, name);
        KeyStroke ak = KeyStroke.getKeyStroke(ks);
        a.putValue(Action.ACCELERATOR_KEY, ak);
        c.getInputMap(JComponent.WHEN_FOCUSED).put(ak, key);
        c.getActionMap().put(key, a);
        return a;
    }

    private static enum ActionKey {
        undo, redo, cut, copy, paste, delete,
        selectAll, cutAll, copyAll, replaceAll, deleteAll;
    }
}

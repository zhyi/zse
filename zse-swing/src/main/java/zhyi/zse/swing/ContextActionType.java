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
 * Enumerates all action types supported by {@link ContextActionHandler}.
 *
 * @author Zhao Yi
 */
public enum ContextActionType {
    /**
     * The action that undoes the last edit in the component.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+Z}.
     */
    UNDO("undo", "ctrl Z"),
    /**
     * The action that redoes the last edit in the component.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+Y}.
     */
    REDO("redo", "ctrl Y"),
    /**
     * The action that cuts the selected contents from the component
     * to the clipboard.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+X}.
     */
    CUT("cut", "ctrl X"),
    /**
     * The action that copies the selected contents from the component
     * to the clipboard.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+C}.
     */
    COPY("copy", "ctrl C"),
    /**
     * The action that pastes the contents from the clipboard to the component.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+V}.
     */
    PASTE("paste", "ctrl V"),
    /**
     * The action that deletes the selected contents in the component.
     * <p>
     * The default accelerator key for this action is {@code Delete}.
     */
    DELETE("delete", "DELETE"),
    /**
     * The action that selects all contents in the component.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+A}.
     */
    SELECT_ALL("selectAll", "ctrl A"),
    /**
     * The action that cuts all contents from the component to the clipboard.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+Shift+X}.
     */
    CUT_ALL("cutAll", "ctrl shift X"),
    /**
     * The action that copies all contents from the component to the clipboard.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+Shift+C}.
     */
    COPY_ALL("copyAll", "ctrl shift C"),
    /**
     * The action that replaces all contents in the component with those
     * in the clipboard.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+Shift+V}.
     */
    REPLACE_ALL("replaceAll", "ctrl shift V"),
    /**
     * The action that deletes all contents in the component.
     * <p>
     * The default accelerator key for this action is {@code Ctrl+Delete}.
     */
    DELETE_ALL("deleteAll", "ctrl DELETE");

    private static final Map<String, Method> METHOD_MAP = new HashMap<>();
    static {
        for (Method m : ContextActionHandler.class.getMethods()) {
            METHOD_MAP.put(m.getName(), m);
        }
    }

    private String key;
    private KeyStroke acceleratorKey;

    private ContextActionType(String key, String ak) {
        this.key = key;
        this.acceleratorKey = KeyStroke.getKeyStroke(ak);
    }

    /**
     * Returns the key of this action type.
     *
     * @return The key of this action type.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the default accelerator key of this action type.
     *
     * @return The default accelerator key of this action type.
     */
    public KeyStroke getAcceleratorKey() {
        return acceleratorKey;
    }

    /**
     * Creates a context action for a component.
     *
     * @param c The component that originates actions of this type.
     *
     * @return The created action object.
     */
    @SuppressWarnings("serial")
    public Action createAction(final JComponent c) {
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object cah = c.getClientProperty(ContextActionHandler.KEY);
                if (cah instanceof ContextActionHandler) {
                    ReflectionUtils.invoke(METHOD_MAP.get(key), cah);
                }
            }
        };
        ResourceBundle rb = ResourceBundle.getBundle(
                "zhyi.zse.swing.ContextAction");
        if (rb.containsKey(key)) {
            // Not an undo or redo action.
            a.putValue(Action.NAME, rb.getString(key));
        }
        a.putValue(Action.ACTION_COMMAND_KEY, key);
        a.putValue(Action.ACCELERATOR_KEY, acceleratorKey);
        c.getInputMap(JComponent.WHEN_FOCUSED).put(acceleratorKey, this);
        c.getActionMap().put(this, a);
        return a;
    }
}

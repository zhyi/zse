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

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import zhyi.zse.swing.SwingUtils;

/**
 * Provides support for context actions and context popup menu.
 *
 * @param <C> The supported component's type.
 *
 * @author Zhao Yi
 */
public abstract class ContextActionSupport<C extends JComponent> {
    /**
     * If a {@link ContextActionSupport} instance is stored to a component with
     * this key, the popup menu can be automatically shown.
     *
     * @see SwingUtils#switchLookAndFeel(LookAndFeel)
     */
    public static final String CONTEXT_ACTION_SUPPORT = "ContextActionSupport";

    protected C component;
    private Map<Integer, List<Action>> actionGroupMap;
    private JPopupMenu contextMenu;
    private PropertyChangeListener acceleratorChangeHandler;

    /**
     * Constructs a new instance.
     *
     * @param component The component to be supported.
     */
    protected ContextActionSupport(C component) {
        this.component = component;
        actionGroupMap = new TreeMap<>();
        contextMenu = new JPopupMenu();
        acceleratorChangeHandler = new AcceleratorChangeHandler();
    }

    /**
     * Installs a context action to the component.
     * <p>
     * If the action has defined an accelerator, the accelerator is bound to
     * the action in the component, and any change to the accelerator is tracked.
     * <p>
     * The action can have a "{@code visible}" property, and if that property's
     * value is {@link Boolean#FALSE}, it is not shown in the context popup menu.
     * <p>
     * The {@code groupId} parameter is used to control how actions are organized
     * in the context popup menu. Actions with the same group ID are displayed
     * in the same section in the order they are installed, and different sections
     * are divided by separators.
     *
     * @param action The action to be installed.
     * @param groupId The action's group ID.
     */
    public void install(Action action, int groupId) {
        action.addPropertyChangeListener(acceleratorChangeHandler);
        bind(action);

        List<Action> actionGroup = actionGroupMap.get(groupId);
        if (actionGroup == null) {
            actionGroup = new ArrayList<>();
            actionGroupMap.put(groupId, actionGroup);
        }
        actionGroup.add(action);
    }

    /**
     * Uninstalls a context action from the component.
     * <p>
     * If the action has defined an accelerator, it will be unbound from
     * the component.
     *
     * @param action The action to be uninstalled.
     */
    public void uninstall(Action action) {
        for (List<Action> actionGroup : actionGroupMap.values()) {
            if (actionGroup.remove(action)) {
                action.removePropertyChangeListener(acceleratorChangeHandler);
                unbind((KeyStroke) action.getValue(Action.ACCELERATOR_KEY));
                return;
            }
        }
    }

    /**
     * Returns all installed actions as an array.
     *
     * @return An array containing all installed actions.
     */
    public Action[] getInstalledActions() {
        List<Action> actions = new ArrayList<>();
        for (List<Action> actionGroup : actionGroupMap.values()) {
            actions.addAll(actionGroup);
        }
        return actions.toArray(new Action[actions.size()]);
    }

    /**
     * Shows a popup menu containing all visible actions.
     *
     * @param x The popup menu's x coordinate in the component's coordinate space.
     * @param y The popup menu's y coordinate in the component's coordinate space.
     */
    public void showContextPopupMenu(int x, int y) {
        if (!component.isEnabled()) {
            return;
        }

        for (Component c : contextMenu.getComponents()) {
            if (c instanceof JMenuItem) {
                ((JMenuItem) c).setAction(null);
            }
        }
        contextMenu.removeAll();

        boolean isFirstGroup = true;
        for (List<Action> actionGroup : actionGroupMap.values()) {
            boolean isSeparatorAdded = false;
            for (Action action : actionGroup) {
                if (!Boolean.FALSE.equals(action.getValue("visible"))) {
                    if (!isSeparatorAdded) {
                        if (isFirstGroup) {
                            isFirstGroup = false;
                        } else {
                            contextMenu.addSeparator();
                        }
                        isSeparatorAdded = true;
                    }
                    contextMenu.add(action);
                }
            }
        }
        if (contextMenu.getComponentCount() > 0) {
            contextMenu.show(component, x, y);
        }
    }

    private void bind(Action action) {
        KeyStroke accelerator = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
        if (accelerator != null) {
            component.getInputMap().put(accelerator, accelerator);
            component.getActionMap().put(accelerator, action);
        }
    }

    private void unbind(KeyStroke accelerator) {
        if (accelerator != null) {
            component.getInputMap().remove(accelerator);
            component.getActionMap().remove(accelerator);
        }
    }

    private class AcceleratorChangeHandler implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            unbind((KeyStroke) evt.getOldValue());
            bind((Action) evt.getSource());
        }
    }
}

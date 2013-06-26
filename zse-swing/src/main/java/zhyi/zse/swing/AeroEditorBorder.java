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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.FocusManager;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

/**
 * This class implements the Windows Aero styled editor border.
 *
 * @author Zhao Yi
 */
public class AeroEditorBorder implements Border {
    // Color0    Color1    Color2    ...    Color2   Color1    Color0
    // Color3    Color4                              Color4    Color3
    // Color3                                                  Color3
    // Color3    Color4                              Color4    Color3
    // Color5    Color6    Color6    ...    Color6   Color6    Color5
    private static final Color[] HILIGHTED_COLORS = {
            new Color(150, 191, 194), new Color(92, 147, 188),
            new Color(61, 123, 173), new Color(198, 222, 238),
            new Color(181, 207, 231), new Color(195, 223, 216),
            new Color(183, 217, 237)};
    private static final Color[] NORMAL_COLORS = {
            new Color(191, 210, 196), new Color(187, 189, 194),
            new Color(171, 173, 179), new Color(226, 227, 234),
            new Color(233, 236, 240), new Color(212, 230, 217),
            new Color(227, 233, 239)};
    private static final Color[] DISABLED_COLORS = {
            new Color(183, 204, 185), new Color(175, 175, 175),
            new Color(175, 175, 175), new Color(175, 175, 175),
            new Color(227, 227, 227), new Color(183, 204, 185),
            new Color(175, 175, 175)};
    private Insets insets;

    /**
     * Constructs a new Aero editor border.
     *
     * @param top    The inset from the top.
     * @param left   The inset from the left.
     * @param bottom The inset from the bottom.
     * @param right  The inset from the right.
     */
    public AeroEditorBorder(int top, int left, int bottom, int right) {
        insets = new Insets(top, left, bottom, right);
    }

    @Override
    public void paintBorder(Component c, Graphics g,
            int x, int y, int width, int height) {
        if (!(c instanceof JComponent)) {
            return;
        }

        JComponent jc = (JComponent) c;
        State state;
        state = (State) jc.getClientProperty(PropertyKey.STATE);
        if (state == null) {
            updateState(jc);
            state = (State) jc.getClientProperty(PropertyKey.STATE);
        }

        int w = width - 1;
        int h = height - 1;
        switch (state) {
            case NORMAL:
                paintBorder(g, NORMAL_COLORS, x, y, w, h);
                return;
            case HIGHLIGHTED:
                paintBorder(g, HILIGHTED_COLORS, x, y, w, h);
                return;
            case DISABLED:
                paintBorder(g, DISABLED_COLORS, x, y, w, h);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    private void paintBorder(Graphics g, Color[] colors,
            int x, int y, int w, int h) {
        g.setColor(colors[0]);
        g.drawLine(x, y, x, y);
        g.drawLine(x + w, y, x + w, y);

        g.setColor(colors[1]);
        g.drawLine(x + 1, y, x + 1, y);
        g.drawLine(x + w - 1, y, x + w - 1, y);

        g.setColor(colors[2]);
        g.drawLine(x + 2, y, x + w - 2, y);

        g.setColor(colors[3]);
        g.drawLine(x, y + 1, x, y + h - 1);
        g.drawLine(x + w, y + 1, x + w, y + h - 1);

        g.setColor(colors[4]);
        g.drawLine(x + 1, y + 1, x + 1, y + 1);
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);
        g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
        g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1);

        g.setColor(colors[5]);
        g.drawLine(x, y + h, x, y + h);
        g.drawLine(x + w, y + h, x + w, y + h);

        g.setColor(colors[6]);
        g.drawLine(x + 1, y + h, x + w - 1, y + h);
    }

    /**
     * Updates the state of the specified component's Aero editor border, if it
     * has one.
     * <p>
     * Aero editor border has four different states:
     * <dl>
     * <dt><b>Normal</b>
     * <dd>The normal state.
     * <dt><b>Highlighted</b>
     * <dd>Applies only to editable components, such as text components.
     * To indicate a custom component is editable, set a client property with
     * the key {@code "editable"} and a value of {@link Boolean#TRUE}.
     * <dt><b>Disabled</b>
     * <dd>The state when the owning component is disabled.
     * <dt><b>None</b>
     * <dd>Indicates that the border should not be painted.
     * </dl>
     * Note that if the component is a scroll pane, its view port view is checked
     * instead of the scroll pane itself.
     * <p>
     * Generally, the component should be repainted if the returned value is
     * {@code true} to update the border's visual effect.
     *
     * @param c The component of which the border is to be checked.
     *
     * @return {@code true} if the component has an Aero border and the border's
     *         state has been changed, otherwise {@code false}.
     */
    public static boolean updateState(JComponent c) {
        if (!(c.getBorder() instanceof AeroEditorBorder)) {
            return false;
        }

        JComponent editor = c;
        if (c instanceof JScrollPane) {
            Component view = ((JScrollPane) c).getViewport().getView();
            if (view instanceof JComponent) {
                editor = (JComponent) view;
            }
        }

        State state = null;
        boolean enabled = editor.isEnabled();
        boolean editable = false;
        if (editor instanceof JTextComponent) {
            editable = ((JTextComponent) editor).isEditable();
        } else if (editor instanceof JComboBox) {
            editable = ((JComboBox<?>) editor).isEditable();
            if (!editable) {
                state = State.NONE;
            }
        } else if (editor instanceof JSpinner) {
            editable = enabled;
        } else if (Boolean.TRUE.equals(editor.getClientProperty("editable"))) {
            editable = true;
        }

        if (state == null) {
            if (enabled) {
                boolean focused = false;
                Component fo = FocusManager.getCurrentManager().getFocusOwner();
                if (fo != null && SwingUtilities.isDescendingFrom(fo, editor)) {
                    focused = true;
                }
                if (editable && (focused || editor.getMousePosition() != null)) {
                    state = State.HIGHLIGHTED;
                } else {
                    state = State.NORMAL;
                }
            } else {
                state = State.DISABLED;
            }
        }

        if (state != c.getClientProperty(PropertyKey.STATE)) {
            c.putClientProperty(PropertyKey.STATE, state);
            return true;
        }
        return false;
    }

    /**
     * A subclass of {@link AeroEditorBorder} that implements {@link UIResource}.
     */
    public static class AeroEditorBorderUIResource
            extends AeroEditorBorder implements UIResource {
        /**
         * Constructs a new Aero editor border.
         *
         * @param top    The inset from the top.
         * @param left   The inset from the left.
         * @param bottom The inset from the bottom.
         * @param right  The inset from the right.
         */
        public AeroEditorBorderUIResource(int top, int left, int bottom, int right) {
            super(top, left, bottom, right);
        }
    }

    private static enum PropertyKey {
        STATE;
    }

    private static enum State {
        NORMAL, HIGHLIGHTED, DISABLED, NONE;
    }
}

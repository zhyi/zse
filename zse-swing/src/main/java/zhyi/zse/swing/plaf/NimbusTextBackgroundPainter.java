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
package zhyi.zse.swing.plaf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import javax.swing.JViewport;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.plaf.nimbus.AbstractRegionPainter;
import javax.swing.text.JTextComponent;
import zhyi.zse.lang.ReflectionUtils;

/**
 * This painter makes the background of read-only text components gray.
 *
 * @author Zhao Yi
 */
public class NimbusTextBackgroundPainter implements Painter<JTextComponent> {
    private static final Method DECODE_X = ReflectionUtils.getDeclaredMethod(
            AbstractRegionPainter.class, "decodeX", float.class);
    private static final Method DECODE_Y = ReflectionUtils.getDeclaredMethod(
            AbstractRegionPainter.class, "decodeY", float.class);
    private static final Method DECODE_COLOR = ReflectionUtils.getDeclaredMethod(
            AbstractRegionPainter.class, "decodeColor",
            String.class, float.class, float.class, float.class, int.class);
    private static final Method GET_PROPERTY_PREFIX
            = ReflectionUtils.getDeclaredMethod(BasicTextUI.class, "getPropertyPrefix");

    private AbstractRegionPainter defaultPainter;
    private Rectangle2D.Float rec;

    /**
     * Constructs a new painter.
     *
     * @param defaultPainter The default painter. Generally it should be one
     *                       installed by Nimbus Look and Feel.
     */
    public NimbusTextBackgroundPainter(AbstractRegionPainter defaultPainter) {
        this.defaultPainter = defaultPainter;
        rec = new Rectangle2D.Float();
    }

    @Override
    public void paint(Graphics2D g, JTextComponent tc, int width, int height) {
        defaultPainter.paint(g, tc, width, height);
        if (!tc.isEditable() && tc.getBackground().equals(
                UIManager.getColor(getPropertyPrefix(tc) + ".background"))) {
            // If the text component is not editable, and the background is
            // not explicitly set, repaint the background. Note that in Nimbus
            // L&F, isBackgroundSet() always returns true so it's unreliable.
            // Since getBackground() always returns the a fake derived color,
            // we can check for that color to determine whether the color has
            // been explicitly set or not. Though this check is imperfect but
            // it should have covered most cases.
            g.setPaint(decodeColor("nimbusBlueGrey",
                    -0.015872955F, -0.07995863F, 0.15294117F, 0));
            if (tc.getParent() instanceof JViewport) {
                rec.setRect(decodeX(0.0F), decodeY(0.0F),
                        decodeX(3.0F) - decodeX(0.0F),
                        decodeY(3.0F) - decodeY(0.0F));
            } else {
                rec.setRect(decodeX(0.4F), decodeY(0.4F),
                        decodeX(2.6F) - decodeX(0.4F),
                        decodeY(2.6F) - decodeY(0.4F));
            }
            g.fill(rec);
        }
    }

    private String getPropertyPrefix(JTextComponent tc) {
        return (String) ReflectionUtils.invoke(GET_PROPERTY_PREFIX, tc.getUI());
    }

    private Color decodeColor(String key, float hOffset,
            float sOffset, float bOffset, int aOffset) {
        return (Color) ReflectionUtils.invoke(DECODE_COLOR, defaultPainter,
                key, hOffset, sOffset, bOffset, aOffset);
    }

    private float decodeX(float x) {
        return (float) ReflectionUtils.invoke(DECODE_X, defaultPainter, x);
    }

    private float decodeY(float y) {
        return (float) ReflectionUtils.invoke(DECODE_Y, defaultPainter, y);
    }
}

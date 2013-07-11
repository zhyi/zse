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
import javax.swing.JComponent;
import javax.swing.JViewport;
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
public class NimbusTextBackgroundPainter extends AbstractRegionPainter {
    private static final Method GET_EXTENDED_CACHE_KEYS = ReflectionUtils.getDeclaredMethod(
            AbstractRegionPainter.class, "getExtendedCacheKeys", JComponent.class);
    private static final Method CONFIGURE_GRAPHICS = ReflectionUtils.getDeclaredMethod(
            AbstractRegionPainter.class, "configureGraphics", Graphics2D.class);
    private static final Method GET_PAINT_CONTEXT = ReflectionUtils.getDeclaredMethod(
            AbstractRegionPainter.class, "getPaintContext");
    private static final Method DO_PAINT = ReflectionUtils.getDeclaredMethod(
            AbstractRegionPainter.class, "doPaint", Graphics2D.class,
            JComponent.class, int.class, int.class, Object[].class);
    private static final Method GET_PROPERTY_PREFIX = ReflectionUtils.getDeclaredMethod(
            BasicTextUI.class, "getPropertyPrefix");

    private AbstractRegionPainter defaultPainter;
    private Color inactiveBackground;
    private Rectangle2D.Float rec;

    /**
     * Constructs a new painter.
     *
     * @param defaultPainter The default painter. Generally it should be one
     *                       installed by Nimbus Look and Feel.
     */
    public NimbusTextBackgroundPainter(AbstractRegionPainter defaultPainter) {
        this.defaultPainter = defaultPainter;
        inactiveBackground = decodeColor("nimbusBlueGrey",
                -0.015872955F, -0.07995863F, 0.15294117F, 0);
        rec = new Rectangle2D.Float();
    }

    @Override
    protected Object[] getExtendedCacheKeys(JComponent c) {
        return (Object[]) ReflectionUtils.invoke(
                GET_EXTENDED_CACHE_KEYS, defaultPainter, c);
    }

    @Override
    protected void configureGraphics(Graphics2D g) {
        ReflectionUtils.invoke(CONFIGURE_GRAPHICS, defaultPainter, g);
    }

    @Override
    protected PaintContext getPaintContext() {
        return (PaintContext) ReflectionUtils.invoke(
                GET_PAINT_CONTEXT, defaultPainter);
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c,
            int width, int height, Object[] extendedCacheKeys) {
        if (!c.isOpaque()) {
            return;
        }

        JTextComponent tc = (JTextComponent) c;
        if (tc.isEnabled() && !tc.isEditable() && tc.getBackground().equals(UIManager.getColor(
                ReflectionUtils.invoke(GET_PROPERTY_PREFIX, tc.getUI()) + ".background"))) {
            // If the text component is not editable, and the background is not
            // explicitly set, paint the background with inactiveBackground.
            // In Nimbus L&F, getBackground() always returns the a fake derived
            // color, we can check that color to determine whether the background
            // has been changed by the developer or not. This check is imperfect
            // but should have covered most cases.
            g.setPaint(inactiveBackground);
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
        } else {
            ReflectionUtils.invoke(DO_PAINT, defaultPainter,
                    g, c, width, height, extendedCacheKeys);
        }
    }
}

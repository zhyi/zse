/*
 * Copyright (C) 2012 Zhao Yi
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.GroupLayout;

/**
 * Utility methods for creating and using group layouts.
 *
 * @author Zhao Yi
 */
public final class GroupLayoutUtils {
    private GroupLayoutUtils() {
    }

    /**
     * Creates a group layout for a container component with container and
     * component gaps automatically created.
     *
     * @param container The container for which the group layout is created.
     *
     * @return The created group layout.
     */
    public static GroupLayout createGroupLayout(Container container) {
        return createGroupLayout(container, true, true);
    }

    /**
     * Creates a group layout for a container component.
     *
     * @param container The container for which the group layout is created.
     * @param autoCreateContainerGaps Whether the gaps between container edge
     *        and components should be created automatically.
     * @param autoCreateGaps Whether the gaps between components should be
     *        created automatically.
     * @return The created group layout.
     */
    public static GroupLayout createGroupLayout(Container container,
            boolean autoCreateContainerGaps, boolean autoCreateGaps) {
        GroupLayout gl = new GroupLayout(container);
        gl.setAutoCreateContainerGaps(autoCreateContainerGaps);
        gl.setAutoCreateGaps(autoCreateGaps);
        container.setLayout(gl);
        return gl;
    }

    /**
     * Forces the companion components to always have the same preferred width
     * as the reference component.
     * <p>
     * This method can be used as an alternative to {@link GroupLayout#linkSize(
     * int, Component[]) GroupLayout.linkSize(int, Component...)} when the common
     * preferred width should be retrieved from a specific component, instead of
     * using the maximum preferred width of the linked components. Additionally,
     * it is advised to apply {@link GroupLayout#PREFERRED_SIZE} to the minimal,
     * preferred and maximum sizes for every companion component when adding it
     * to the horizontal group, so as to prevent the group layout from resizing
     * their widths at runtime.
     *
     * @param reference The component of which the preferred width is to be
     *        used as the common preferred width.
     * @param companions The components to have the same preferred width with
     *        the reference component.
     */
    public static void linkPreferredWidth(
            final Component reference, Component... companions) {
        for (final Component companion : companions) {
            companion.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int refWidth = reference.getPreferredSize().width;
                    Dimension comSize = companion.getPreferredSize();
                    if (comSize.width != refWidth) {
                        comSize.width = refWidth;
                        companion.setPreferredSize(comSize);
                    }
                }
            });
        }
    }

    /**
     * Forces the companion components to always have the same preferred height
     * as the reference component.
     * <p>
     * This method can be used as an alternative to {@link GroupLayout#linkSize(
     * int, Component[]) GroupLayout.linkSize(int, Component...)} when the common
     * preferred height should be retrieved from a specific component, instead of
     * using the maximum preferred height of the linked components. Additionally,
     * it is advised to apply {@link GroupLayout#PREFERRED_SIZE} to the minimal,
     * preferred and maximum sizes for every companion component when adding it
     * to the vertical group, so as to prevent the group layout from resizing
     * their heights at runtime.
     *
     * @param reference The component of which the preferred height is to be
     *        used as the common preferred height.
     * @param companions The components to have the same preferred height with
     *        the reference component.
     */
    public static void linkPreferredHeight(
            final Component reference, Component... companions) {
        for (final Component companion : companions) {
            companion.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int refHeight = reference.getPreferredSize().height;
                    Dimension comSize = companion.getPreferredSize();
                    if (comSize.height != refHeight) {
                        comSize.height = refHeight;
                        companion.setPreferredSize(comSize);
                    }
                }
            });
        }
    }

    /**
     * Forces the companion components to always have the same preferred size
     * as the reference component.
     * <p>
     * This method can be used as an alternative to {@link GroupLayout#linkSize(
     * int, Component[]) GroupLayout.linkSize(int, Component...)} when the common
     * preferred size should be retrieved from a specific component instead of
     * using the maximum preferred size of the linked components. Additionally,
     * it is advised to apply {@link GroupLayout#PREFERRED_SIZE} to the minimal,
     * preferred and maximum sizes for every companion component when adding it
     * to both the horizontal and vertical groups, so as to prevent the group
     * layout from resizing them at runtime.
     *
     * @param reference The component of which the preferred size is to be
     *        used as the common preferred size.
     * @param companions The components to have the same preferred size with
     *        the reference component.
     */
    public static void linkPreferredSize(
            final Component reference, Component... companions) {
        for (final Component companion : companions) {
            companion.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    Dimension refSize = reference.getPreferredSize();
                    if (!companion.getPreferredSize().equals(refSize)) {
                        companion.setPreferredSize(refSize);
                    }
                }
            });
        }
    }
}

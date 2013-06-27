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

import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;

/**
 * This layout style enlarges the distance for {@link ComponentPlacement#INDENT}.
 *
 * @author Zhao Yi
 */
public class GroupLayoutStyle extends LayoutStyle {
    private LayoutStyle defaultLayoutStyle;

    public GroupLayoutStyle() {
        this.defaultLayoutStyle = UIManager.getLookAndFeel().getLayoutStyle();
    }

    @Override
    public int getPreferredGap(JComponent component1, JComponent component2,
            ComponentPlacement type, int position, Container parent) {
        if (type == ComponentPlacement.INDENT) {
            return 2 * defaultLayoutStyle.getPreferredGap(component1, component2,
                    ComponentPlacement.UNRELATED, position, parent);
        } else {
            return defaultLayoutStyle.getPreferredGap(
                    component1, component2, type, position, parent);
        }
    }

    @Override
    public int getContainerGap(JComponent component, int position, Container parent) {
        return defaultLayoutStyle.getContainerGap(component, position, parent);
    }
}

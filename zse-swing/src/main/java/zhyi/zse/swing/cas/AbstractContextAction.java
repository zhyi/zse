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

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import zhyi.zse.swing.PropertyKeys;

/**
 * @author Zhao Yi
 */
abstract class AbstractContextAction extends AbstractAction {
    private String bundle;
    private String i18nKey;

    AbstractContextAction(String bundle, String i18nKey) {
        this.bundle = bundle;
        this.i18nKey = i18nKey;
    }

    @Override
    public Object getValue(String key) {
        switch (key) {
            case NAME:
                return getName();
            case PropertyKeys.ACTION_VISIBLE:
                return isVisible();
            default:
                return super.getValue(key);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isEnabled()) {
            doAction();
        }
    }

    String getName() {
        return ResourceBundle.getBundle(bundle).getString(i18nKey);
    }

    boolean isVisible() {
        return true;
    }

    abstract void doAction();
}

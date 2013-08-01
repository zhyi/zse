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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import zhyi.zse.i18n.FallbackLocaleControl;
import zhyi.zse.swing.PropertyKeys;

/**
 * A convenient class that eases the implementation of context action classes.
 *
 * @author Zhao Yi
 */
public abstract class AbstractContextAction extends AbstractAction {
    private static final LocaleChangeHandler LOCALE_CHANGE_HANDLER = new LocaleChangeHandler();

    private String bundle;
    private String i18nKey;

    /**
     * Constructs a new instance.
     *
     * @param bundle The base name of the resource bundle from which the action's
     *        name is retrieved.
     * @param i18nKey The key to get the action's name from the resource bundle.
     */
    protected AbstractContextAction(String bundle, String i18nKey) {
        this.bundle = bundle;
        this.i18nKey = i18nKey;
        putName(bundle, i18nKey);
        addPropertyChangeListener(LOCALE_CHANGE_HANDLER);
    }

    @Override
    public Object getValue(String key) {
        switch (key) {
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

    /**
     * Returns whether this context action should be visible in the context
     * popup menu. By default this methods returns {@code true}, an subclasses
     * should override it as needed.
     *
     * @return {@code true} or {@code false}.
     */
    protected boolean isVisible() {
        return true;
    }

    /**
     * Performs the context action. Only invoked when it is enabled.
     */
    protected abstract void doAction();

    private void putName(String bundle, String i18nKey) {
        ResourceBundle rb = ResourceBundle.getBundle(bundle,
                Locale.getDefault(), getClass().getClassLoader(),
                FallbackLocaleControl.EN_US_CONTROL);
        if (rb.containsKey(i18nKey)) {
            putValue(NAME, rb.getString(i18nKey));
        }
    }

    private static class LocaleChangeHandler implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PropertyKeys.ACTION_LOCALE)) {
                AbstractContextAction a = (AbstractContextAction) evt.getSource();
                a.putName(a.bundle, a.i18nKey);
            }
        }
    }
}

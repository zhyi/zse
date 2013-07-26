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
package zhyi.zse.opt;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Java Preferences API based implementation of {@link OptionManager}.
 *
 * @author Zhao Yi
 */
public class PreferencesOptionManager extends CachedOptionManager {
    private Preferences preferences;

    /**
     * Constructs a new preferences option manager.
     *
     * @param nodePath The node path for creating preferences node to store
     *        options. The node is always created as a sub node of the user's
     *        root node.
     * @throws IllegalArgumentException If the node path is empty or absolute.
     */
    public PreferencesOptionManager(String nodePath) {
        if (nodePath.isEmpty() || nodePath.startsWith("/")) {
            throw new IllegalArgumentException("Node path must be relative.");
        }
        preferences = Preferences.userRoot().node(nodePath);
    }

    @Override
    protected String load(String name) {
        return preferences.get(name, null);
    }

    @Override
    protected void store(Map<String, String> stringOptionMap) throws IOException {
        try {
            for (Entry<String, String> e : stringOptionMap.entrySet()) {
                preferences.put(e.getKey(), e.getValue());
            }
            preferences.flush();
        } catch (BackingStoreException ex) {
            throw new IOException(ex);
        }
    }
}

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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

/**
 * Properties file based implementation of {@link OptionManager}.
 *
 * @author Zhao Yi
 */
public class PropertiesOptionManager extends CachedOptionManager {
    private Properties properties;
    private Path file;
    private boolean xml;

    /**
     * Constructs a new properties option manager.
     *
     * @param path The path to the properties file.
     * @param xml Whether the properties file is in XML format.
     */
    public PropertiesOptionManager(String path, boolean xml) {
        properties = new Properties();
        file = Paths.get(path);
        this.xml = xml;
        try (InputStream in = Files.newInputStream(file)) {
            if (xml) {
                properties.loadFromXML(in);
            } else {
                properties.load(in);
            }
        } catch (IOException ex) {
            // Ignore.
        }
    }

    @Override
    protected String load(String name) {
        return properties.getProperty(name);
    }

    @Override
    protected void store(Map<String, String> stringOptionMap) throws IOException {
        properties.putAll(stringOptionMap);
        try (OutputStream out = Files.newOutputStream(file)) {
            if (xml) {
                properties.storeToXML(out, null);
            } else {
                properties.store(out, null);
            }
        }
    }
}

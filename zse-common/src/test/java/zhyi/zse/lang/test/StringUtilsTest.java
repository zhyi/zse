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
package zhyi.zse.lang.test;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import zhyi.zse.lang.StringUtils;
import zhyi.zse.lang.StringUtils.DelimitationStyle;

/**
 * @author Zhao Yi
 */
public class StringUtilsTest {
    @Test
    public void testConcat() {
        List<String> sources = Arrays.asList("a", "", " b ", "c");
        String delimiter = "/";
        Assert.assertEquals("a b c", StringUtils.concat(sources));
        Assert.assertEquals("a b c", StringUtils.concat(
                sources, delimiter, DelimitationStyle.IGNORE_DELIMITER));
        Assert.assertEquals("/a// b /c", StringUtils.concat(
                sources, delimiter, DelimitationStyle.PREPEND_DELIMITER));
        Assert.assertEquals("a// b /c/", StringUtils.concat(
                sources, delimiter, DelimitationStyle.APPEND_DELIMITER));
        Assert.assertEquals("a// b /c", StringUtils.concat(
                sources, delimiter, DelimitationStyle.INSERT_DELIMITER));
    }

    @Test
    public void testReplace() {
        String source = "abccdeccc";
        Assert.assertEquals("zabccdeccc", StringUtils.replaceFirst(source, "", "z"));
        Assert.assertEquals("abzcdeccc", StringUtils.replaceFirst(source, "c", "z"));
        Assert.assertEquals("abzdeccc", StringUtils.replaceFirst(source, "cc", "z"));
        Assert.assertEquals("zazbzczczdzezczczcz", StringUtils.replaceAll(source, "", "z"));
        Assert.assertEquals("abzzdezzz", StringUtils.replaceAll(source, "c", "z"));
        Assert.assertEquals("abzdezc", StringUtils.replaceAll(source, "cc", "z"));
        Assert.assertEquals("zazbzccdeccc", StringUtils.replace(source, "", "z", 3));
        Assert.assertEquals("abzzdezcc", StringUtils.replace(source, "c", "z", 3));
        Assert.assertEquals("abzdezc", StringUtils.replace(source, "cc", "z", 3));
    }

    @Test
    public void testSplit() {
        String source = "/a&*// &&**b/xyz / ";
        String empty = "";
        String slash = "/";
        String pf = "&*";
        int limit = 3;

        // Test general splitting.
        Assert.assertEquals(Arrays.asList("", "/", "a", "&", "*", "/", "/", " ",
                "&", "&", "*", "*", "b", "/", "x", "y", "z", " ", "/", " ", ""),
                StringUtils.split(source, empty, false));
        Assert.assertEquals(Arrays.asList("", "a&*", "", " &&**b", "xyz ", " "),
                StringUtils.split(source, slash, false));
        Assert.assertEquals(Arrays.asList("/a", "// &", "*b/xyz / "),
                StringUtils.split(source, pf, false));

        // Test trimming.
        Assert.assertEquals(Arrays.asList("", "/", "a", "&", "*", "/", "/", "",
                "&", "&", "*", "*", "b", "/", "x", "y", "z", "", "/", "", ""),
                StringUtils.split(source, empty, true));
        Assert.assertEquals(Arrays.asList("", "a&*", "", "&&**b", "xyz", ""),
                StringUtils.split(source, slash, true));
        Assert.assertEquals(Arrays.asList("/a", "// &", "*b/xyz /"),
                StringUtils.split(source, pf, true));

        // Test delimitation styles.
        Assert.assertEquals(Arrays.asList("", "/", "a", "&", "*", "/", "/", " ",
                "&", "&", "*", "*", "b", "/", "x", "y", "z", " ", "/", " ", ""),
                StringUtils.split(source, empty, DelimitationStyle.PREPEND_DELIMITER, false));
        Assert.assertEquals(Arrays.asList("", "/", "a", "&", "*", "/", "/", " ",
                "&", "&", "*", "*", "b", "/", "x", "y", "z", " ", "/", " ", ""),
                StringUtils.split(source, empty, DelimitationStyle.APPEND_DELIMITER, false));
        Assert.assertEquals(Arrays.asList("", "/a&*", "/", "/ &&**b", "/xyz ", "/ "),
                StringUtils.split(source, slash, DelimitationStyle.PREPEND_DELIMITER, false));
        Assert.assertEquals(Arrays.asList("/", "a&*/", "/", " &&**b/", "xyz /", " "),
                StringUtils.split(source, slash, DelimitationStyle.APPEND_DELIMITER, false));
        Assert.assertEquals(Arrays.asList("/a", "&*// &", "&**b/xyz / "),
                StringUtils.split(source, pf, DelimitationStyle.PREPEND_DELIMITER, false));
        Assert.assertEquals(Arrays.asList("/a&*", "// &&*", "*b/xyz / "),
                StringUtils.split(source, pf, DelimitationStyle.APPEND_DELIMITER, false));

        // Test splitting times.
        Assert.assertEquals(Arrays.asList("", "/", "a", "&*// &&**b/xyz / "),
                StringUtils.split(source, empty, DelimitationStyle.IGNORE_DELIMITER, false, limit));
        Assert.assertEquals(Arrays.asList("", "a&*", "", " &&**b/xyz / "),
                StringUtils.split(source, slash, DelimitationStyle.IGNORE_DELIMITER, false, limit));
        Assert.assertEquals(Arrays.asList("/a", "// &", "*b/xyz / "),
                StringUtils.split(source, pf, DelimitationStyle.IGNORE_DELIMITER, false, limit));
    }
}

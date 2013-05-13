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
package zhyi.zse.opt.test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import zhyi.zse.opt.CachedOptionManager;
import zhyi.zse.opt.Option;
import zhyi.zse.opt.OptionChangeEvent;
import zhyi.zse.opt.OptionChangeListener;
import zhyi.zse.opt.OptionManager;

/**
 * @author Zhao Yi
 */
public class OptionManagerTest {
    @Test
    public void testOptionGetSetStoreOptions() throws IOException {
        Option<Integer> intOption = new Option<>("int", Integer.class, 9);
        Option<Character> charOption = new Option<>("char", Character.class, 'x');
        Option<String> stringOption = new Option<>("string", String.class, "abc");
        Option<TomAndJerry> enumOption = new Option<>("enum", TomAndJerry.class, TomAndJerry.TOM);
        Option<Date> dateOption = new Option<>("date", Date.class, new Date(1L));

        OptionManager optionManager1 = new MemoryOptionManager();
        optionManager1.set(intOption, 3);
        optionManager1.set(enumOption, TomAndJerry.JERRY);
        optionManager1.set(dateOption, new Date(8L));
        Assert.assertEquals(Integer.valueOf(3), optionManager1.get(intOption));
        Assert.assertEquals(Character.valueOf('x'), optionManager1.get(charOption));
        Assert.assertEquals("abc", optionManager1.get(stringOption));
        Assert.assertEquals(TomAndJerry.JERRY, optionManager1.get(enumOption));
        Assert.assertEquals(new Date(8L), optionManager1.get(dateOption));
        optionManager1.store();

        OptionManager optionManager2 = new MemoryOptionManager();
        Assert.assertEquals(Integer.valueOf(3), optionManager2.get(intOption));
        Assert.assertEquals(Character.valueOf('x'), optionManager2.get(charOption));
        Assert.assertEquals("abc", optionManager2.get(stringOption));
        Assert.assertEquals(TomAndJerry.JERRY, optionManager2.get(enumOption));
        Assert.assertEquals(new Date(8L), optionManager2.get(dateOption));
    }

    @Test
    public void testOptionChangeListener() {
        final Option<String> option1 = new Option<>("option1", String.class, "abc");
        final Option<String> option2 = new Option<>("option2", String.class, "xyz");
        final OptionManager optionManager = new MemoryOptionManager();
        final int[] counters = new int[2];
        OptionChangeListener<Object> globalListener = new OptionChangeListener<Object>() {
            @Override
            public void optionChanged(OptionChangeEvent<? extends Object> e) {
                counters[0]++;
                Assert.assertEquals(optionManager, e.getOptionManager());
                Option<? extends Object> option = e.getOption();
                if (option.equals(option1)) {
                    Assert.assertEquals("abc", e.getOldValue());
                    Assert.assertEquals("ABC", e.getNewValue());
                } else if (option.equals(option2)) {
                    Assert.assertEquals("xyz", e.getOldValue());
                    Assert.assertEquals("XYZ", e.getNewValue());
                } else {
                    Assert.fail();
                }
            }
        };
        OptionChangeListener<String> specificListener = new OptionChangeListener<String>() {
            @Override
            public void optionChanged(OptionChangeEvent<? extends String> e) {
                counters[1]++;
                Assert.assertEquals(optionManager, e.getOptionManager());
                Assert.assertEquals(option2, e.getOption());
                Assert.assertEquals("xyz", e.getOldValue());
                Assert.assertEquals("XYZ", e.getNewValue());
            }
        };

        optionManager.addOptionChangeListener(globalListener);
        optionManager.addOptionChangeListener(option2, specificListener);
        optionManager.addOptionChangeListener(option2, specificListener);
        optionManager.addOptionChangeListener(option2, specificListener);
        optionManager.removeOptionChangeListener(option2, specificListener);

        optionManager.set(option1, "ABC");
        optionManager.set(option1, "ABC");
        optionManager.set(option2, "XYZ");
        Assert.assertEquals(2, counters[0]);
        Assert.assertEquals(2, counters[1]);
    }

    private static class MemoryOptionManager extends CachedOptionManager {
        private static final Map<String, String> OPTION_STORAGE = new HashMap<>();

        @Override
        protected String load(String name) {
            return OPTION_STORAGE.get(name);
        }

        @Override
        protected void store(Map<String, String> stringOptionMap) throws IOException {
            OPTION_STORAGE.putAll(stringOptionMap);
        }
    }

    private static enum TomAndJerry {
        TOM, JERRY;
    }
}

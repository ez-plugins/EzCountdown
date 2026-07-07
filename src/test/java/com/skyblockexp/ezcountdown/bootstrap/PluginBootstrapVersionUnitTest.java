package com.skyblockexp.ezcountdown.bootstrap;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginBootstrapVersionUnitTest {

    @Test
    void isNewerVersionReturnsTrueForHigherPatch() throws Exception {
        assertTrue(invokeIsNewerVersion("2.0.1", "2.0.2"));
    }

    @Test
    void isNewerVersionReturnsTrueForHigherMinor() throws Exception {
        assertTrue(invokeIsNewerVersion("2.0.9", "2.1.0"));
    }

    @Test
    void isNewerVersionReturnsFalseForLowerPatch() throws Exception {
        assertFalse(invokeIsNewerVersion("2.0.2", "2.0.1"));
    }

    @Test
    void isNewerVersionReturnsFalseForEqualVersion() throws Exception {
        assertFalse(invokeIsNewerVersion("2.0.2", "2.0.2"));
    }

    @Test
    void isNewerVersionHandlesSuffixes() throws Exception {
        assertTrue(invokeIsNewerVersion("2.0.2", "2.0.3-RC1"));
        assertFalse(invokeIsNewerVersion("2.0.3-RC1", "2.0.2"));
    }

    @Test
    void parseIntSafeReturnsDigitsOnlyPrefix() throws Exception {
        assertEquals(2501, invokeParseIntSafe("25-R0.1-SNAPSHOT"));
    }

    @Test
    void parseIntSafeReturnsZeroForNonNumeric() throws Exception {
        assertEquals(0, invokeParseIntSafe("alpha"));
        assertEquals(0, invokeParseIntSafe(""));
    }

    private static boolean invokeIsNewerVersion(String current, String latest) throws Exception {
        Method m = PluginBootstrap.class.getDeclaredMethod("isNewerVersion", String.class, String.class);
        m.setAccessible(true);
        return (boolean) m.invoke(null, current, latest);
    }

    private static int invokeParseIntSafe(String value) throws Exception {
        Method m = PluginBootstrap.class.getDeclaredMethod("parseIntSafe", String.class);
        m.setAccessible(true);
        return (int) m.invoke(null, value);
    }
}

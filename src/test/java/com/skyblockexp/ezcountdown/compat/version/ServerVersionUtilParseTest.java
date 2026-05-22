package com.skyblockexp.ezcountdown.compat.version;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pure-Java tests for {@link ServerVersionUtil#parseFrom(String)}.
 * No Bukkit server is required.
 */
class ServerVersionUtilParseTest {

    @Test
    void legacy_1_21_1() {
        String[] r = ServerVersionUtil.parseFrom("1.21.1-R0.1-SNAPSHOT");
        assertEquals("21",   r[0], "effective minor");
        assertEquals("1.21", r[1], "display string");
    }

    @Test
    void legacy_1_18_2() {
        String[] r = ServerVersionUtil.parseFrom("1.18.2-R0.1-SNAPSHOT");
        assertEquals("18",   r[0]);
        assertEquals("1.18", r[1]);
    }

    @Test
    void newFormat_26_1() {
        String[] r = ServerVersionUtil.parseFrom("26.1-R0.1-SNAPSHOT");
        assertEquals("26",   r[0], "effective minor");
        assertEquals("26.1", r[1], "display string");
    }

    @Test
    void newFormat_27_0() {
        String[] r = ServerVersionUtil.parseFrom("27.0-R0.1-SNAPSHOT");
        assertEquals("27",   r[0]);
        assertEquals("27.0", r[1]);
    }

    @Test
    void newFormat_bareMajor_27() {
        String[] r = ServerVersionUtil.parseFrom("27-R0.1-SNAPSHOT");
        assertEquals("27", r[0]);
        assertEquals("27", r[1]);
    }

    @Test
    void garbage_falls_back() {
        String[] r = ServerVersionUtil.parseFrom("garbage");
        assertEquals("21",   r[0], "fallback minor");
        assertEquals("1.21", r[1], "fallback display");
    }

    @Test
    void empty_falls_back() {
        String[] r = ServerVersionUtil.parseFrom("");
        assertEquals("21",   r[0]);
        assertEquals("1.21", r[1]);
    }
}

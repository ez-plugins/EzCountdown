package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GuiInteractionFeatureTest extends MockBukkitTestBase {

    @Test
    public void guiManagerIsAvailable_and_mainGuiCanBeOpened() {
        assertNotNull(plugin, "Plugin should be loaded");
        assertNotNull(registry, "Registry should be available after startup");

        assertNotNull(registry.gui(), "GuiManager should be registered in registry");

        // TODO: Use MockBukkit to open the main GUI for a player and simulate clicks
        // TODO: Assert permission flows and that edits persist to CountdownManager
    }
}

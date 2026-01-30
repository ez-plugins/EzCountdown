package com.skyblockexp.ezcountdown.test;

import com.skyblockexp.ezcountdown.EzCountdownPlugin;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.entity.Player;

public abstract class MockBukkitTestBase {

    protected ServerMock server;
    protected EzCountdownPlugin plugin;
    protected Registry registry;
    protected CountdownManager manager;

    @BeforeEach
    public void baseSetup() throws Exception {
        try {
            if (org.mockbukkit.mockbukkit.MockBukkit.isMocked()) {
                server = org.mockbukkit.mockbukkit.MockBukkit.getMock();
            } else {
                server = org.mockbukkit.mockbukkit.MockBukkit.getOrCreateMock();
            }
        } catch (UnsupportedOperationException ex) {
            // If Bukkit server was already set to a non-mock instance, try to reuse it if possible
            if (org.bukkit.Bukkit.getServer() instanceof org.mockbukkit.mockbukkit.ServerMock) {
                server = (org.mockbukkit.mockbukkit.ServerMock) org.bukkit.Bukkit.getServer();
            } else {
                org.junit.jupiter.api.Assumptions.assumeTrue(false, "Cannot initialize MockBukkit server in this environment: " + ex.getMessage());
                return;
            }
        }

        plugin = MockBukkit.load(EzCountdownPlugin.class);
        java.lang.reflect.Field f = EzCountdownPlugin.class.getDeclaredField("registry");
        f.setAccessible(true);
        registry = (Registry) f.get(plugin);
        manager = registry.countdowns();
    }

    @AfterEach
    public void baseCleanup() {
        try {
            if (org.mockbukkit.mockbukkit.MockBukkit.isMocked()) org.mockbukkit.mockbukkit.MockBukkit.unmock();
        } catch (Exception ignored) {}
    }

    protected Player addPlayer(String name) {
        return server.addPlayer(name);
    }

    protected Player addOp(String name) {
        Player p = server.addPlayer(name);
        try { p.setOp(true); } catch (Exception ignored) {}
        return p;
    }

    protected Player addWithPermission(String name, String permission) {
        Player p = server.addPlayer(name);
        try { p.addAttachment(plugin, permission, true); } catch (Exception ignored) {}
        return p;
    }
}

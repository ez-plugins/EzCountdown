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
        server = MockBukkit.mock();
        plugin = MockBukkit.load(EzCountdownPlugin.class);
        java.lang.reflect.Field f = EzCountdownPlugin.class.getDeclaredField("registry");
        f.setAccessible(true);
        registry = (Registry) f.get(plugin);
        manager = registry.countdowns();
    }

    @AfterEach
    public void baseCleanup() {
        MockBukkit.unmock();
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

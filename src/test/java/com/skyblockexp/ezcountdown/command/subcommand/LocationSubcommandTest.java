package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationSubcommandTest extends MockBukkitTestBase {

    @Test
    public void addAndDeleteLocation() {
        Player p = addOp("locplayer");
        LocationSubcommand sub = new LocationSubcommand(registry);

        sub.execute(p, new String[]{"location", "add", "home"});
        assertTrue(registry.locations().getLocationNames().contains("home"));

        sub.execute(server.getConsoleSender(), new String[]{"location", "delete", "home"});
        assertFalse(registry.locations().getLocationNames().contains("home"));
    }
}

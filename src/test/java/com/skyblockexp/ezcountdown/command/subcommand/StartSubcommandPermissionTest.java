package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class StartSubcommandPermissionTest extends MockBukkitTestBase {

    @Test
    public void playerWithoutPermissionCannotStart() {
        Countdown cd = new Countdown("permtest", CountdownType.MANUAL,
                EnumSet.copyOf(registry.defaults().displayTypes()), registry.defaults().updateIntervalSeconds(),
                registry.defaults().visibilityPermission(), registry.defaults().formatMessage(), registry.defaults().startMessage(), registry.defaults().endMessage(), java.util.List.of(), registry.defaults().zoneId());
        manager.createCountdown(cd);

        Player p = addPlayer("noperm");
        StartSubcommand sub = new StartSubcommand(registry);
        sub.execute(p, new String[]{"start", "permtest"});

        assertFalse(manager.getCountdown("permtest").get().isRunning());
    }
}

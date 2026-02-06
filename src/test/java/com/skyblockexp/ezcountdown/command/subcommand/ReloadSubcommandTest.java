package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReloadSubcommandTest extends MockBukkitTestBase {

    @Test
    public void execute_runsReloadAction() {
        final boolean[] ran = {false};
        ReloadSubcommand sub = new ReloadSubcommand(registry, () -> ran[0] = true);
        sub.execute(server.getConsoleSender(), new String[]{"reload"});
        assertTrue(ran[0], "Reload action should have been invoked");
    }
}

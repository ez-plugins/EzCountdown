package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GuiSubcommandTest extends MockBukkitTestBase {

    @Test
    public void execute_opensGuiForPlayer() {
        Player p = addOp("gplayer");
        GuiSubcommand sub = new GuiSubcommand(registry);
        sub.execute(p, new String[]{"gui"});

        assertNotNull(p.getOpenInventory(), "Player should have an open inventory after GUI command");
    }
}

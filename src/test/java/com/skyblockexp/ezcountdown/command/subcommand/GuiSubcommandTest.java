package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GuiSubcommandTest extends MockBukkitTestBase {

    @Test
    public void execute_opensGuiForPlayer() {
        Player p = addOp("gplayer");
        com.skyblockexp.ezcountdown.manager.MessageManager spy = spyMessages();

        GuiSubcommand sub = new GuiSubcommand(registry);
        sub.execute(p, new String[]{"gui"});

        assertNotNull(p.getOpenInventory(), "Player should have an open inventory after GUI command");
        // Ensure no player-only or no-permission messages were sent
        org.mockito.Mockito.verify(spy, org.mockito.Mockito.never()).message(org.mockito.Mockito.eq("commands.gui.only-players"));
        org.mockito.Mockito.verify(spy, org.mockito.Mockito.never()).message(org.mockito.Mockito.eq("commands.gui.no-permission"));
    }
}

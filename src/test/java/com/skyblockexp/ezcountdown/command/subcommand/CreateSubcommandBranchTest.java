package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateSubcommandBranchTest extends MockBukkitTestBase {

    @Test
    void createWithoutPermissionDoesNotCreate() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        var player = addPlayer("no-perm-create");

        sub.execute(player, new String[]{"create", "not-created", "duration", "60s"});

        assertFalse(manager.getCountdown("not-created").isPresent());
    }

    @Test
    void createWithDisplayOverrideUsesOnlyProvidedDisplayType() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        CommandSender sender = server.getConsoleSender();

        sub.execute(sender, new String[]{"create", "display-override", "duration", "60s", "--display", "chat"});

        var created = manager.getCountdown("display-override");
        assertTrue(created.isPresent());
        assertEquals(EnumSet.of(DisplayType.CHAT), created.orElseThrow().getDisplayTypes());
    }

    @Test
    void createWithInvalidDisplayOverrideDoesNotCreate() {
        CreateSubcommand sub = new CreateSubcommand(registry);

        sub.execute(server.getConsoleSender(), new String[]{"create", "bad-display", "duration", "60s", "--display", "nope"});

        assertFalse(manager.getCountdown("bad-display").isPresent());
    }

    @Test
    void createRecurringWithInvalidTimezoneDoesNotCreate() {
        CreateSubcommand sub = new CreateSubcommand(registry);

        sub.execute(server.getConsoleSender(), new String[]{"create", "bad-tz", "recurring", "12", "31"});

        assertFalse(manager.getCountdown("bad-tz").isPresent());
    }

    @Test
    void createFixedDateFromTokenParsesAndCreatesCountdown() {
        CreateSubcommand sub = new CreateSubcommand(registry);

        sub.execute(server.getConsoleSender(), new String[]{"create", "fixed-token", "2026-12-31", "23:59"});

        var created = manager.getCountdown("fixed-token");
        assertTrue(created.isPresent());
        assertEquals(CountdownType.FIXED_DATE, created.orElseThrow().getType());
        assertNotNull(created.orElseThrow().getTargetInstant());
    }

    @Test
    void createRecurringWithInvalidMissedRunPolicyDoesNotCreate() {
        CreateSubcommand sub = new CreateSubcommand(registry);

        assertThrows(java.time.format.DateTimeParseException.class,
                () -> sub.execute(server.getConsoleSender(), new String[]{"create", "bad-policy", "recurring", "1", "2", "not-a-time"}));

        assertFalse(manager.getCountdown("bad-policy").isPresent());
    }

    @Test
    void tabCompleteReturnsMatchingTypeSuggestions() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        var values = sub.tabComplete(server.getConsoleSender(), new String[]{"create", "name", "d"});
        assertEquals(java.util.List.of("duration"), values);
    }

    @Test
    void createDuplicateNameDoesNotCreateSecondCountdown() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        sub.execute(server.getConsoleSender(), new String[]{"create", "dupe", "duration", "60s"});
        sub.execute(server.getConsoleSender(), new String[]{"create", "dupe", "duration", "120s"});

        var stored = manager.getCountdown("dupe");
        assertTrue(stored.isPresent());
        assertEquals(60L, stored.orElseThrow().getDurationSeconds());
    }
}

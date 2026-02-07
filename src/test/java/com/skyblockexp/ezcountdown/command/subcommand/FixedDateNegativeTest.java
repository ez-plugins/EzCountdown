package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.format.DateTimeParseException;

public class FixedDateNegativeTest extends MockBukkitTestBase {

    @Test
    public void invalidDateDoesNotCreate() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        // Provide an invalid date token for a fixed-date countdown; expect parse error to bubble
        assertThrows(DateTimeParseException.class, () ->
                sub.execute(server.getConsoleSender(), new String[]{"create", "baddate", "not-a-date"})
        );
        assertFalse(manager.getCountdown("baddate").isPresent());
    }
}

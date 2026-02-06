package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FixedDateHandlerEditorInputTest extends MockBukkitTestBase {

    @Test
    public void invalidEditorInputThrows() {
        FixedDateHandler handler = new FixedDateHandler();
        Countdown cd = new Countdown("edt", CountdownType.FIXED_DATE,
                EnumSet.copyOf(registry.defaults().displayTypes()), registry.defaults().updateIntervalSeconds(),
                registry.defaults().visibilityPermission(), registry.defaults().formatMessage(), registry.defaults().startMessage(), registry.defaults().endMessage(), java.util.List.of(), registry.defaults().zoneId());

        assertThrows(IllegalArgumentException.class, () -> handler.tryApplyEditorInput("not a date", cd, java.time.Instant.now()));
    }
}

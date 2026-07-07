package com.skyblockexp.ezcountdown.listener.actions;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionResultTest {

    @Test
    public void constructorsAndFactoriesExposeExpectedState() {
        ActionResult handled = ActionResult.handled();
        assertTrue(handled.isHandled());
        assertFalse(handled.isMutated());
        assertFalse(handled.isCloseInventory());
        assertTrue(handled.getReopenTitle().isEmpty());

        ActionResult mutated = ActionResult.handledAndMutated();
        assertTrue(mutated.isHandled());
        assertTrue(mutated.isMutated());

        ActionResult close = ActionResult.handledAndClose();
        assertTrue(close.isHandled());
        assertTrue(close.isCloseInventory());

        ActionResult none = ActionResult.none();
        assertFalse(none.isHandled());

        ActionResult custom = new ActionResult(true, false, true, Optional.of("title"));
        assertEquals("title", custom.getReopenTitle().orElseThrow());

        ActionResult nullReopen = new ActionResult(true, false, false, null);
        assertTrue(nullReopen.getReopenTitle().isEmpty());
    }
}

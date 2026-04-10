package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import java.util.Collection;
import java.util.Map;

/**
 * Marker interface for display handlers that can render multiple countdowns in a single pass.
 */
public interface StackableDisplay extends DisplayHandler {
    /**
     * Display multiple countdowns at once. Implementations should efficiently render
     * all provided countdowns according to their capabilities.
     * @param countdowns collection of countdowns to render
     * @param messages map of countdown -> formatted message
     * @param remaining map of countdown -> remaining seconds
     */
    void displayMultiple(Collection<Countdown> countdowns, Map<Countdown, String> messages, Map<Countdown, Long> remaining);
}

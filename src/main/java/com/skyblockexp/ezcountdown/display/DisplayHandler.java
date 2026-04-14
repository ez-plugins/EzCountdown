package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;

public interface DisplayHandler {
    void display(Countdown countdown, String message, long remainingSeconds);
    void clear(Countdown countdown);
    void clearAll();

    /**
     * Display the countdown within a batched render pass.
     *
     * <p>Handlers that render via the chat channel (i.e. that call
     * {@code player.sendMessage()} — either directly or as a fallback) should
     * override this method and route those calls through {@code batch.add()}
     * instead. {@link MessageBatch#flush()} is called by {@link
     * com.skyblockexp.ezcountdown.manager.DisplayManager} after all handlers
     * have contributed to the batch, ensuring each player receives at most one
     * chat message per countdown per tick regardless of how many display types
     * are active.
     *
     * <p>The default implementation simply delegates to {@link #display} so that
     * third-party implementations remain source- and binary-compatible without
     * any changes.
     */
    default void displayBatched(Countdown countdown, String message, long remainingSeconds, MessageBatch batch) {
        display(countdown, message, remainingSeconds);
    }
}

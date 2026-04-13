package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Collects outgoing chat-channel messages within a single display tick and
 * deduplicates them before delivery.
 *
 * <p>When a countdown has multiple display types (e.g. CHAT, ACTION_BAR, TITLE),
 * handlers that fall back to {@code player.sendMessage()} on legacy servers would
 * otherwise each deliver the same text. By routing all such messages through this
 * batch, each player receives at most one message per countdown per tick regardless
 * of how many display types are configured.
 *
 * <p>Key: {@code (playerUUID, countdownName)} — one entry per player per countdown.
 * Re-adding the same pair is a no-op (first-write wins, preserving message stability).
 */
public final class MessageBatch {

    /** Composite key: player UUID + countdown name (lower-case). */
    private record Key(UUID playerId, String countdownName) {}

    private final Map<Key, DisplayMessage> entries = new LinkedHashMap<>();

    /**
     * Adds a pending message to the batch.
     * If an entry for the same player + countdown already exists it is ignored.
     */
    public void add(Player player, Countdown countdown, String message) {
        Key key = new Key(player.getUniqueId(), countdown.getName().toLowerCase(java.util.Locale.ROOT));
        entries.putIfAbsent(key, new DisplayMessage(player, countdown, message));
    }

    /**
     * Sends all deduplicated messages and clears the batch.
     */
    public void flush() {
        for (DisplayMessage dm : entries.values()) {
            try {
                dm.player().sendMessage(dm.message());
            } catch (Exception ignored) {}
        }
        entries.clear();
    }

    /** Returns the number of pending (unique) entries in this batch. */
    public int size() {
        return entries.size();
    }
}

package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import org.bukkit.entity.Player;

/**
 * Represents a pending chat message to be sent to a player for a specific countdown.
 * Used by {@link MessageBatch} to deduplicate messages when multiple display types
 * would otherwise each deliver the same text to the same player in the same tick.
 */
public record DisplayMessage(Player player, Countdown countdown, String message) {}

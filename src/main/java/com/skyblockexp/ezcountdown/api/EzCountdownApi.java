package com.skyblockexp.ezcountdown.api;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * Public API for other plugins to control countdowns.
 */
public interface EzCountdownApi {
    boolean startCountdown(String name);
    boolean stopCountdown(String name);
    Optional<Countdown> getCountdown(String name);
    Collection<Countdown> listCountdowns();
    boolean createCountdown(Countdown countdown);
    /**
     * Convenience helper to create a simple countdown using plugin defaults.
     * The method generates a unique name for the countdown, uses the configured
     * defaults for display types and messaging, sets the provided duration
     * (seconds) for duration/manual types and starts the countdown immediately.
     *
     * Note: the created countdown is registered globally; displays will follow
     * the plugin's configured visibility rules. The provided `players` list is
     * notified when the countdown is created but is not used to restrict display
     * visibility.
     *
     * @param type countdown type
     * @param amountSeconds duration or amount in seconds (used for DURATION/MANUAL)
     * @param players players to notify of creation (optional, may be null)
     * @return true if the countdown was created; false if a generated name collision occurred
     */
    boolean createCountdown(CountdownType type, long amountSeconds, Collection<Player> players);
    boolean deleteCountdown(String name);
}

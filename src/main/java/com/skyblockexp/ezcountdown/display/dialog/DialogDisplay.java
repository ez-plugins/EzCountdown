package com.skyblockexp.ezcountdown.display.dialog;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Displays countdown information as a Paper {@link Dialog}.
 *
 * <p><strong>No-flicker strategy:</strong> The handler tracks the last message
 * that was sent to each player for a given countdown. When {@link #display} is
 * called and the formatted message has not changed since the previous call, the
 * existing open dialog is left on screen — no packet is sent and no visual
 * disruption occurs. A dialog is only (re-)sent when the displayed text
 * actually changes.
 *
 * <p>This class is intentionally scoped to Paper 1.21.7+ servers; the plugin's
 * {@link DialogDisplayValidator} ensures it is never instantiated on runtimes
 * that lack the API.
 */
@SuppressWarnings("UnstableApiUsage")
public class DialogDisplay implements DisplayHandler {

    /** Tracks the countdown name + last message text shown to each player. */
    private record LastShown(String countdownName, String message) {}

    /** Last shown state per player UUID. */
    private final Map<UUID, LastShown> lastShown = new HashMap<>();

    /**
     * Reverse index: countdown name → set of player UUIDs who currently have
     * a dialog from this countdown open. Used for efficient cleanup.
     */
    private final Map<String, Set<UUID>> sentByCountdown = new HashMap<>();

    // -------------------------------------------------------------------------
    // DisplayHandler
    // -------------------------------------------------------------------------

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        if (remainingSeconds <= 0L) {
            clear(countdown);
            return;
        }

        String cdName = countdown.getName();
        String perm = countdown.getVisibilityPermission();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (perm != null && !perm.isBlank() && !player.hasPermission(perm)) {
                // Player lost permission — close any open dialog from this countdown
                UUID uid = player.getUniqueId();
                LastShown prev = lastShown.get(uid);
                if (prev != null && prev.countdownName().equals(cdName)) {
                    closeDialogSafely(player);
                    lastShown.remove(uid);
                    Set<UUID> viewers = sentByCountdown.get(cdName);
                    if (viewers != null) viewers.remove(uid);
                }
                continue;
            }

            UUID uid = player.getUniqueId();
            LastShown prev = lastShown.get(uid);

            // Skip if this player is already showing the exact same text
            if (prev != null && prev.countdownName().equals(cdName) && prev.message().equals(message)) {
                continue;
            }

            showDialogToPlayer(player, countdown, message);
            lastShown.put(uid, new LastShown(cdName, message));
            sentByCountdown.computeIfAbsent(cdName, k -> new HashSet<>()).add(uid);
        }
    }

    @Override
    public void clear(Countdown countdown) {
        String cdName = countdown.getName();
        Set<UUID> viewers = sentByCountdown.remove(cdName);
        if (viewers == null) return;

        for (UUID uid : viewers) {
            LastShown prev = lastShown.get(uid);
            if (prev != null && prev.countdownName().equals(cdName)) {
                Player player = Bukkit.getPlayer(uid);
                if (player != null && player.isOnline()) {
                    closeDialogSafely(player);
                }
                lastShown.remove(uid);
            }
        }
    }

    @Override
    public void clearAll() {
        for (Map.Entry<String, Set<UUID>> entry : sentByCountdown.entrySet()) {
            for (UUID uid : entry.getValue()) {
                Player player = Bukkit.getPlayer(uid);
                if (player != null && player.isOnline()) {
                    closeDialogSafely(player);
                }
            }
        }
        sentByCountdown.clear();
        lastShown.clear();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void showDialogToPlayer(Player player, Countdown countdown, String message) {
        try {
            Dialog dialog = Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(
                                    Component.text(countdown.getName(), NamedTextColor.GOLD))
                            .body(List.of(DialogBody.plainMessage(
                                    Component.text(message))))
                            .canCloseWithEscape(true)
                            .build())
                    .type(DialogType.notice(
                            ActionButton.create(
                                    Component.text("Close", NamedTextColor.GRAY),
                                    Component.text("Dismiss this dialog"),
                                    200,
                                    null))));
            player.showDialog(dialog);
        } catch (Exception e) {
            // Defensive: API may behave unexpectedly on edge-case server builds
            Bukkit.getLogger().fine("EzCountdown: failed to show dialog to " + player.getName() + ": " + e.getMessage());
        }
    }

    private void closeDialogSafely(Player player) {
        try {
            player.closeDialog();
        } catch (Exception ignored) {}
    }

    /**
     * Removes stale entries for players who have gone offline, preventing
     * unbounded map growth on long-running servers.
     */
    public void pruneOfflinePlayers() {
        lastShown.entrySet().removeIf(e -> Bukkit.getPlayer(e.getKey()) == null);
        for (Set<UUID> viewers : sentByCountdown.values()) {
            viewers.removeIf(uid -> Bukkit.getPlayer(uid) == null);
        }
    }
}

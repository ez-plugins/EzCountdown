package com.skyblockexp.ezcountdown.display.actionbar;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBarDisplay implements DisplayHandler {

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String perm = countdown.getVisibilityPermission();
            if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                try {
                    player.sendActionBar(message);
                } catch (NoSuchMethodError err) {
                    try {
                        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
                    } catch (NoClassDefFoundError ignored) {
                        player.sendMessage(message);
                    }
                }
            }
        }
    }

    @Override
    public void clear(Countdown countdown) {
        // action bar messages are ephemeral; nothing to clear
    }

    @Override
    public void clearAll() {
        // no global state
    }
}

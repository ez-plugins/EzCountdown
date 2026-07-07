package com.skyblockexp.ezcountdown.listener.actions;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;

final class SoundEditorHelper {
    private SoundEditorHelper() {}

    static void sendAvailableSounds(Player player) {
        String[] names = Arrays.stream(Sound.values())
                .map(SoundEditorHelper::soundName)
                .sorted()
                .toArray(String[]::new);

        player.sendMessage(ChatColor.AQUA + "Available sounds (" + names.length + "):");
        player.sendMessage(ChatColor.DARK_GRAY + "Use one exact enum value; input is case-insensitive.");

        final int perLine = 6;
        for (int i = 0; i < names.length; i += perLine) {
            StringBuilder line = new StringBuilder(ChatColor.YELLOW.toString());
            int end = Math.min(i + perLine, names.length);
            for (int j = i; j < end; j++) {
                if (j > i) {
                    line.append(ChatColor.GRAY).append(", ").append(ChatColor.YELLOW);
                }
                line.append(names[j]);
            }
            player.sendMessage(line.toString());
        }
    }

    private static String soundName(Sound sound) {
        if (sound == null) {
            return "";
        }
        try {
            java.lang.reflect.Method nameMethod = sound.getClass().getMethod("name");
            Object value = nameMethod.invoke(sound);
            if (value instanceof String s) {
                return s;
            }
        } catch (Exception ignored) {
            // Fallback for API variants where Sound is not a Java enum type.
        }
        return String.valueOf(sound);
    }
}

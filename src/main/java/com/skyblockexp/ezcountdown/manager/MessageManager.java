package com.skyblockexp.ezcountdown.manager;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MessageManager {

    private File messagesFile;
    private FileConfiguration configuration;
    private String prefix;
    private boolean miniMessageEnabled;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public MessageManager(File messagesFile) {
        this.messagesFile = Objects.requireNonNull(messagesFile, "messagesFile");
        reload();
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(messagesFile);
        prefix = configuration.getString("prefix", "");
        miniMessageEnabled = configuration.getBoolean("use-minimessage", false);
    }

    public String raw(String key) {
        return configuration.getString(key, "");
    }

    public String message(String key) {
        return formatWithPrefix(raw(key), Map.of());
    }

    public String message(String key, Map<String, String> placeholders) {
        return formatWithPrefix(raw(key), placeholders);
    }

    public String format(String input) {
        return serialize(input);
    }

    public String formatWithPrefix(String input, Map<String, String> placeholders) {
        String message = applyPlaceholders(input, placeholders);
        message = message.replace("{prefix}", prefix == null ? "" : prefix);
        return serialize(message);
    }

    private String applyPlaceholders(String input, Map<String, String> placeholders) {
        String result = input == null ? "" : input;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    private String serialize(String input) {
        if (input == null) {
            return "";
        }
        if (!miniMessageEnabled) {
            return ChatColor.translateAlternateColorCodes('&', input);
        }
        Component component = miniMessage.deserialize(input);
        String legacy = legacySerializer.serialize(component);
        return ChatColor.translateAlternateColorCodes('&', legacy);
    }
}

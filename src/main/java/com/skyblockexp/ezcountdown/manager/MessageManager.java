package com.skyblockexp.ezcountdown.manager;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.skyblockexp.ezcountdown.EzCountdownPlugin;
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
    private static final int TRANSLATE_MAX_DEPTH = 3;
    private static final Pattern TRANSLATE_PATTERN = Pattern.compile("\\{translate:([^}]+)\\}");

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
        // Resolve {translate:...} tokens first so translated text can contain placeholders
        String resolved = resolveTranslations(input, 0);
        String message = applyPlaceholders(resolved, placeholders);
        message = message.replace("{prefix}", prefix == null ? "" : prefix);
        return serialize(message);
    }

    private String resolveTranslations(String input, int depth) {
        if (input == null) return "";
        if (depth > TRANSLATE_MAX_DEPTH) return input;

        Matcher matcher = TRANSLATE_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        boolean found = false;
        while (matcher.find()) {
            found = true;
            String key = matcher.group(1);
            String val = configuration.getString(key, null);
            if (val == null) {
                try {
                    EzCountdownPlugin plugin = JavaPlugin.getPlugin(EzCountdownPlugin.class);
                    if (plugin != null) plugin.getLogger().warning("Missing translation key: " + key);
                } catch (Throwable ignored) {}
                val = "";
            } else {
                // resolve nested translations up to the depth limit
                val = resolveTranslations(val, depth + 1);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(val));
        }
        matcher.appendTail(sb);

        String result = sb.toString();
        // If we found replacements and there may be further translate tokens, handle remaining up to limit
        if (found && depth < TRANSLATE_MAX_DEPTH && TRANSLATE_PATTERN.matcher(result).find()) {
            return resolveTranslations(result, depth + 1);
        }
        return result;
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

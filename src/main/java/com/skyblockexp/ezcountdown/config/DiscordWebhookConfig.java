package com.skyblockexp.ezcountdown.config;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.*;

public class DiscordWebhookConfig {
    public static class Webhook {
        public String name;
        public String url;
        public boolean enabled;
        public Set<String> triggers;
        public Embed embed;
    }
    public static class Embed {
        public String title;
        public String description;
        public String color;
        public Footer footer;
        public String thumbnailUrl;
        public String imageUrl;
        public Author author;
    }
    public static class Footer {
        public String text;
        public String iconUrl;
    }
    public static class Author {
        public String name;
        public String iconUrl;
    }

    private final List<Webhook> webhooks = new ArrayList<>();

    public static DiscordWebhookConfig load(File file) {
        DiscordWebhookConfig config = new DiscordWebhookConfig();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> list = yaml.getMapList("webhooks");
        for (Map<?, ?> entry : list) {
            Webhook webhook = new Webhook();
            webhook.name = Objects.toString(entry.get("name"), "");
            webhook.url = Objects.toString(entry.get("url"), "");
            webhook.enabled = Boolean.parseBoolean(Objects.toString(entry.get("enabled"), "true"));
            webhook.triggers = new HashSet<>();
            List<?> triggers = (List<?>) entry.get("triggers");
            if (triggers != null) {
                for (Object t : triggers) webhook.triggers.add(Objects.toString(t, ""));
            }
            Map<?, ?> embedMap = (Map<?, ?>) entry.get("embed");
            if (embedMap != null) {
                Embed embed = new Embed();
                embed.title = Objects.toString(embedMap.get("title"), "");
                embed.description = Objects.toString(embedMap.get("description"), "");
                embed.color = Objects.toString(embedMap.get("color"), "");
                Map<?, ?> footerMap = (Map<?, ?>) embedMap.get("footer");
                if (footerMap != null) {
                    Footer footer = new Footer();
                    footer.text = Objects.toString(footerMap.get("text"), "");
                    footer.iconUrl = Objects.toString(footerMap.get("icon_url"), "");
                    embed.footer = footer;
                }
                embed.thumbnailUrl = Objects.toString(embedMap.get("thumbnail_url"), "");
                embed.imageUrl = Objects.toString(embedMap.get("image_url"), "");
                Map<?, ?> authorMap = (Map<?, ?>) embedMap.get("author");
                if (authorMap != null) {
                    Author author = new Author();
                    author.name = Objects.toString(authorMap.get("name"), "");
                    author.iconUrl = Objects.toString(authorMap.get("icon_url"), "");
                    embed.author = author;
                }
                webhook.embed = embed;
            }
            config.webhooks.add(webhook);
        }
        return config;
    }

    public List<Webhook> getWebhooks() {
        return webhooks;
    }
}

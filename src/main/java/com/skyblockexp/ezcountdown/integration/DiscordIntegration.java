package com.skyblockexp.ezcountdown.integration;

import com.skyblockexp.ezcountdown.config.DiscordWebhookConfig;

public final class DiscordIntegration {
    private DiscordIntegration() {}

    public static void applyConfig(DiscordWebhookConfig config) {
        // No-op: Discord config is passed directly to CountdownManager during bootstrap.
    }
}

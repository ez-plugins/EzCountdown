package com.skyblockexp.ezcountdown.integration.discord;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DiscordWebhookSender {
    public static void sendWebhook(String webhookUrl, Map<String, Object> embedJson) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String payload = buildPayload(embedJson);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                System.err.println("Discord webhook failed: HTTP " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildPayload(Map<String, Object> embedJson) {
        // Simple JSON builder for { "embeds": [ embedJson ] }
        StringBuilder sb = new StringBuilder();
        sb.append("{\"embeds\":[");
        sb.append(mapToJson(embedJson));
        sb.append("]}");
        return sb.toString();
    }

    private static String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Map) {
                sb.append(mapToJson((Map<String, Object>) value));
            } else if (value instanceof Iterable) {
                sb.append("[");
                boolean firstArr = true;
                for (Object o : (Iterable<?>) value) {
                    if (!firstArr) sb.append(",");
                    firstArr = false;
                    if (o instanceof Map) {
                        sb.append(mapToJson((Map<String, Object>) o));
                    } else if (o instanceof String) {
                        sb.append("\"").append(escapeJson((String) o)).append("\"");
                    } else {
                        sb.append(o);
                    }
                }
                sb.append("]");
            } else {
                sb.append(value);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

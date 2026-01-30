package com.skyblockexp.ezcountdown.integration.discord;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DiscordWebhookSenderTest {

    @Test
    public void buildPayload_escapesAndBuildsJson() throws Exception {
        Map<String, Object> embed = new HashMap<>();
        embed.put("title", "Hello \"World\"");
        Map<String, Object> nested = new HashMap<>();
        nested.put("text", "a\\b");
        embed.put("desc", nested);

        Method m = DiscordWebhookSender.class.getDeclaredMethod("buildPayload", Map.class);
        m.setAccessible(true);
        String payload = (String) m.invoke(null, embed);

        String expected = "{\"embeds\":[{\"title\":\"Hello \\\"World\\\"\",\"desc\":{\"text\":\"a\\\\b\"}}]}";
        assertEquals(expected, payload);
    }
}

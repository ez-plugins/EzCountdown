package com.skyblockexp.ezcountdown.manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageManagerTest {

    @Test
    void translateSingleKey_returnsTranslation(@TempDir Path tempDir) throws IOException {
        Path messages = tempDir.resolve("messages.yml");
        String content = "prefix: ''\nuse-minimessage: false\nexample:\n  hello: 'Hello {name}'\n";
        Files.write(messages, content.getBytes());

        MessageManager mm = new MessageManager(messages.toFile());
        String out = mm.formatWithPrefix("{translate:example.hello}", Map.of("name", "World"));
        assertEquals("Hello World", out);
    }

    @Test
    void nestedTranslate_resolvesPlaceholders(@TempDir Path tempDir) throws IOException {
        Path messages = tempDir.resolve("messages.yml");
        String content = "prefix: ''\nuse-minimessage: false\na: '{translate:b}'\nb: 'Value {name}'\n";
        Files.write(messages, content.getBytes());

        MessageManager mm = new MessageManager(messages.toFile());
        String out = mm.formatWithPrefix("{translate:a}", Map.of("name", "X"));
        assertEquals("Value X", out);
    }

    @Test
    void missingKey_replacedWithEmpty(@TempDir Path tempDir) throws IOException {
        Path messages = tempDir.resolve("messages.yml");
        String content = "prefix: ''\nuse-minimessage: false\n";
        Files.write(messages, content.getBytes());

        MessageManager mm = new MessageManager(messages.toFile());
        String out = mm.formatWithPrefix("Start {translate:does.not.exist} End", Map.of());
        assertEquals("Start  End", out);
    }
}

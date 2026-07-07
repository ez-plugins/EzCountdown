package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuiSoundEditorFeatureTest extends MockBukkitTestBase {

    @SuppressWarnings("unchecked")
        private static Map<UUID, Consumer<String>> pendingMap(ChatInputListener listener) throws Exception {
        Field f = ChatInputListener.class.getDeclaredField("pending");
        f.setAccessible(true);
                return (Map<UUID, Consumer<String>>) f.get(listener);
        }

        private static boolean hasPending(ChatInputListener listener, UUID id) throws Exception {
                return pendingMap(listener).containsKey(id);
        }

        private static Consumer<String> getPending(ChatInputListener listener, UUID id) throws Exception {
                return pendingMap(listener).get(id);
    }

    @Test
    public void editorSlotStartSound_registersChatInputRequest() throws Exception {
        Countdown cd = new Countdown("gui-sound-start", CountdownType.MANUAL,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
        cd.setDurationSeconds(10);
        manager.createCountdown(cd);

        var player = addPlayer("gui-sound-player-start");
        registry.gui().editorMenu().openEditor(player, cd);

        InventoryClickEvent click = new InventoryClickEvent(
                player.getOpenInventory(), InventoryType.SlotType.CONTAINER,
                9, ClickType.LEFT, InventoryAction.PICKUP_ALL);
        plugin.getServer().getPluginManager().callEvent(click);

        assertTrue(hasPending(registry.gui().chatInputListener(), player.getUniqueId()),
                "Expected start sound slot to register chat input callback");
    }

    @Test
    public void editorStartSoundInput_validValue_updatesCountdown() throws Exception {
        Countdown cd = new Countdown("gui-sound-apply-start", CountdownType.MANUAL,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
        cd.setDurationSeconds(10);
        manager.createCountdown(cd);

        var player = addPlayer("gui-sound-player-apply-start");
        registry.gui().editorMenu().openEditor(player, cd);

        InventoryClickEvent click = new InventoryClickEvent(
                player.getOpenInventory(), InventoryType.SlotType.CONTAINER,
                9, ClickType.LEFT, InventoryAction.PICKUP_ALL);
        plugin.getServer().getPluginManager().callEvent(click);

        Consumer<String> cb = getPending(registry.gui().chatInputListener(), player.getUniqueId());
        assertNotNull(cb, "Expected pending callback for start sound input");

        String validSound = "BLOCK.NOTE_BLOCK.PLING";
        cb.accept(validSound);

        assertEquals(validSound, cd.getStartSound());
    }

    @Test
    public void editorStartSoundInput_none_clearsCountdownSound() throws Exception {
        Countdown cd = new Countdown("gui-sound-clear-start", CountdownType.MANUAL,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
        cd.setDurationSeconds(10);
        cd.setStartSound("BLOCK.NOTE_BLOCK.PLING");
        manager.createCountdown(cd);

        var player = addPlayer("gui-sound-player-clear-start");
        registry.gui().editorMenu().openEditor(player, cd);

        InventoryClickEvent click = new InventoryClickEvent(
                player.getOpenInventory(), InventoryType.SlotType.CONTAINER,
                9, ClickType.LEFT, InventoryAction.PICKUP_ALL);
        plugin.getServer().getPluginManager().callEvent(click);

        Consumer<String> cb = getPending(registry.gui().chatInputListener(), player.getUniqueId());
        assertNotNull(cb, "Expected pending callback for start sound input");

        cb.accept("none");

        assertNull(cd.getStartSound());
    }

    @Test
    public void editorSlotEndSound_registersChatInputRequest() throws Exception {
        Countdown cd = new Countdown("gui-sound-end", CountdownType.MANUAL,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
        cd.setDurationSeconds(10);
        manager.createCountdown(cd);

        var player = addPlayer("gui-sound-player-end");
        registry.gui().editorMenu().openEditor(player, cd);

        InventoryClickEvent click = new InventoryClickEvent(
                player.getOpenInventory(), InventoryType.SlotType.CONTAINER,
                10, ClickType.LEFT, InventoryAction.PICKUP_ALL);
        plugin.getServer().getPluginManager().callEvent(click);

        assertTrue(hasPending(registry.gui().chatInputListener(), player.getUniqueId()),
                "Expected end sound slot to register chat input callback");
        assertNotNull(player.getOpenInventory());
    }

        @Test
        public void editorEndSoundInput_validValue_updatesCountdown() throws Exception {
                Countdown cd = new Countdown("gui-sound-apply-end", CountdownType.MANUAL,
                                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                                1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
                cd.setDurationSeconds(10);
                manager.createCountdown(cd);

                var player = addPlayer("gui-sound-player-apply-end");
                registry.gui().editorMenu().openEditor(player, cd);

                InventoryClickEvent click = new InventoryClickEvent(
                                player.getOpenInventory(), InventoryType.SlotType.CONTAINER,
                                10, ClickType.LEFT, InventoryAction.PICKUP_ALL);
                plugin.getServer().getPluginManager().callEvent(click);

                Consumer<String> cb = getPending(registry.gui().chatInputListener(), player.getUniqueId());
                assertNotNull(cb, "Expected pending callback for end sound input");

                String validSound = "BLOCK.NOTE_BLOCK.PLING";
                cb.accept(validSound);

                assertEquals(validSound, cd.getEndSound());
        }
}

package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.type.CountdownTypeHandler;
import com.skyblockexp.ezcountdown.util.DurationParser;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.time.Instant;
import java.util.Optional;

public class EditDurationOrTargetAction implements GuiAction {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final ChatInputListener chatInputListener;
    private final com.skyblockexp.ezcountdown.bootstrap.Registry registry;

    public EditDurationOrTargetAction(CountdownManager manager, MessageManager messageManager, ChatInputListener chatInputListener, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.chatInputListener = chatInputListener;
        this.registry = registry;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdownOpt) {
        if (countdownOpt.isEmpty()) return ActionResult.none();
        Countdown cd = countdownOpt.get();
        chatInputListener.request(player, new EditDurationConsumer(player, cd, cdName, manager, messageManager, registry));
        return ActionResult.handled();
    }

    private static final class EditDurationConsumer implements java.util.function.Consumer<String> {
        private final Player player;
        private final Countdown cd;
        private final String cdName;
        private final CountdownManager manager;
        private final MessageManager messageManager;
        private final com.skyblockexp.ezcountdown.bootstrap.Registry registry;

        EditDurationConsumer(Player player, Countdown cd, String cdName, CountdownManager manager, MessageManager messageManager, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
            this.player = player;
            this.cd = cd;
            this.cdName = cdName;
            this.manager = manager;
            this.messageManager = messageManager;
            this.registry = registry;
        }

        @Override
        public void accept(String input) {
            try {
                CountdownTypeHandler handler = manager.getHandler(cd.getType());
                if (handler != null) {
                    boolean applied = handler.tryApplyEditorInput(input, cd, Instant.now());
                    if (!applied) throw new IllegalArgumentException("Input not applicable");
                } else {
                    try {
                        long seconds = DurationParser.parseToSeconds(input);
                        cd.setDurationSeconds(seconds);
                        if (cd.getType() == com.skyblockexp.ezcountdown.api.model.CountdownType.DURATION || cd.getType() == com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL) {
                            if (cd.isRunning()) cd.setTargetInstant(Instant.now().plusSeconds(seconds));
                        }
                    } catch (IllegalArgumentException ex) {
                        Instant inst = Instant.parse(input);
                        cd.setTargetInstant(inst);
                    }
                }
                manager.save();
                player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName)));
                org.bukkit.Bukkit.getScheduler().runTask(registry.plugin(), () -> registry.gui().editorMenu().openEditor(player, cd));
            } catch (IllegalArgumentException ex) {
                player.sendMessage(messageManager.message("gui.edit.invalid-duration", java.util.Map.of("reason", ex.getMessage())));
            } catch (Exception ex) {
                player.sendMessage(messageManager.message("gui.edit.invalid-duration", java.util.Map.of("reason", ex.getMessage())));
            }
        }
    }
}

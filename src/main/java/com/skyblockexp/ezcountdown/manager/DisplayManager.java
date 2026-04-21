package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.display.Validator;
import com.skyblockexp.ezcountdown.display.actionbar.ActionBarDisplay;
import com.skyblockexp.ezcountdown.display.actionbar.ActionBarValidator;
import com.skyblockexp.ezcountdown.display.bossbar.BossBarDisplay;
import com.skyblockexp.ezcountdown.display.bossbar.BossbarValidator;
import com.skyblockexp.ezcountdown.display.chat.ChatDisplay;
import com.skyblockexp.ezcountdown.display.scoreboard.ScoreboardDisplay;
import com.skyblockexp.ezcountdown.display.scoreboard.ScoreboardValidator;
import com.skyblockexp.ezcountdown.display.chat.ChatValidator;
import com.skyblockexp.ezcountdown.display.title.TitleValidator;
import com.skyblockexp.ezcountdown.display.title.TitleDisplay;
import com.skyblockexp.ezcountdown.display.MessageBatch;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class DisplayManager {
    private final Map<DisplayType, DisplayHandler> handlers = new EnumMap<>(DisplayType.class);
    private Validator.ValidationResult bossbarValidation;
    /** Cached last-known display message per countdown name; used by the fast-refresh path. */
    private final java.util.Map<String, String> lastMessageCache = new java.util.concurrent.ConcurrentHashMap<>();
    private int bossbarRefreshTicks = 1;
    private int scoreboardRefreshTicks = 1;
    private long tickCount = 0;

    public DisplayManager(com.skyblockexp.ezcountdown.config.ConfigService configService) {
        this.bossbarRefreshTicks = configService.loadBossbarRefreshTicks();
        this.scoreboardRefreshTicks = configService.loadScoreboardRefreshTicks();
        configureHandlers(configService);
    }

    private void configureHandlers(com.skyblockexp.ezcountdown.config.ConfigService configService) {
        handlers.clear();
        java.util.Map<com.skyblockexp.ezcountdown.display.DisplayType, Boolean> overrides = configService.loadDisplayOverrides();

        // Action bar
        Validator.ValidationResult actionValidation = new ActionBarValidator().validate();
        boolean actionForce = overrides.getOrDefault(DisplayType.ACTION_BAR, false);
        if (actionValidation.isValid() || actionForce) {
            handlers.put(DisplayType.ACTION_BAR, new ActionBarDisplay());
            if (!actionValidation.isValid()) Bukkit.getLogger().info("EzCountdown: action bar force-enabled via config.");
        } else {
            Bukkit.getLogger().warning("EzCountdown: action bar disabled: " + actionValidation.getMessage());
        }

        // Chat
        Validator.ValidationResult chatValidation = new ChatValidator().validate();
        boolean chatForce = overrides.getOrDefault(DisplayType.CHAT, false);
        if (chatValidation.isValid() || chatForce) {
            handlers.put(DisplayType.CHAT, new ChatDisplay());
            if (!chatValidation.isValid()) Bukkit.getLogger().info("EzCountdown: chat force-enabled via config.");
        } else {
            Bukkit.getLogger().warning("EzCountdown: chat display disabled: " + chatValidation.getMessage());
        }

        // Title
        Validator.ValidationResult titleValidation = new TitleValidator().validate();
        boolean titleForce = overrides.getOrDefault(DisplayType.TITLE, false);
        if (titleValidation.isValid() || titleForce) {
            handlers.put(DisplayType.TITLE, new TitleDisplay());
            if (!titleValidation.isValid()) Bukkit.getLogger().info("EzCountdown: title force-enabled via config.");
        } else {
            Bukkit.getLogger().warning("EzCountdown: title display disabled: " + titleValidation.getMessage());
        }

        // Scoreboard
        Validator.ValidationResult scoreboardValidation = new ScoreboardValidator().validate();
        boolean scoreboardForce = overrides.getOrDefault(DisplayType.SCOREBOARD, false);
        if (scoreboardValidation.isValid() || scoreboardForce) {
            handlers.put(DisplayType.SCOREBOARD, new ScoreboardDisplay());
            if (!scoreboardValidation.isValid()) Bukkit.getLogger().info("EzCountdown: scoreboard force-enabled via config.");
        } else {
            Bukkit.getLogger().warning("EzCountdown: scoreboard disabled: " + scoreboardValidation.getMessage());
        }

        // Boss bar
        bossbarValidation = new BossbarValidator().validate();
        boolean bossForce = overrides.getOrDefault(DisplayType.BOSS_BAR, false);
        if (bossbarValidation.isValid() || bossForce) {
            handlers.put(DisplayType.BOSS_BAR, new BossBarDisplay());
            if (!bossbarValidation.isValid()) Bukkit.getLogger().info("EzCountdown: boss bar force-enabled via config.");
        } else {
            Bukkit.getLogger().warning("EzCountdown: boss bar display disabled: " + bossbarValidation.getMessage());
        }
    }

    public void reload(com.skyblockexp.ezcountdown.config.ConfigService configService) {
        // Clear existing display state to avoid duplicating visuals when handlers are recreated
        for (DisplayHandler h : handlers.values()) {
            try { h.clearAll(); } catch (Exception ignored) {}
        }
        lastMessageCache.clear();
        tickCount = 0;
        bossbarRefreshTicks = configService.loadBossbarRefreshTicks();
        scoreboardRefreshTicks = configService.loadScoreboardRefreshTicks();
        configureHandlers(configService);
    }

    public void display(Countdown countdown, String message, long remainingSeconds) {
        // Do not show displays for countdowns that reached zero
        if (remainingSeconds <= 0L) return;

        MessageBatch batch = new MessageBatch();
        for (DisplayType type : countdown.getDisplayTypes()) {
            DisplayHandler h = handlers.get(type);
            if (h != null) {
                h.displayBatched(countdown, message, remainingSeconds, batch);
            }
        }
        batch.flush();
    }

    /**
     * Batch-display multiple countdowns in a single pass. Handlers that implement
     * {@link com.skyblockexp.ezcountdown.display.StackableDisplay} will receive a
     * single bulk call; other handlers will be invoked per-countdown.
     */
    /**
     * Called every game tick (by default 1-tick scheduler) to fast-refresh bossbar progress
     * and scoreboard visuals using cached messages, without triggering chat/actionbar/title spam.
     */
    public void onTick(java.util.Collection<Countdown> allRunning) {
        tickCount++;
        boolean doBossbar = (tickCount % bossbarRefreshTicks == 0) && handlers.containsKey(DisplayType.BOSS_BAR);
        boolean doScoreboard = (tickCount % scoreboardRefreshTicks == 0) && handlers.containsKey(DisplayType.SCOREBOARD);
        if (!doBossbar && !doScoreboard) return;

        java.time.Instant now = java.time.Instant.now();
        java.util.List<Countdown> bossbarList = new java.util.ArrayList<>();
        java.util.List<Countdown> scoreboardList = new java.util.ArrayList<>();
        java.util.Map<Countdown, String> msgMap = new java.util.HashMap<>();
        java.util.Map<Countdown, Long> remMap = new java.util.HashMap<>();

        for (Countdown c : allRunning) {
            if (!c.isRunning() || c.getTargetInstant() == null) continue;
            boolean hasBossbar = doBossbar && c.getDisplayTypes().contains(DisplayType.BOSS_BAR);
            boolean hasScoreboard = doScoreboard && c.getDisplayTypes().contains(DisplayType.SCOREBOARD);
            if (!hasBossbar && !hasScoreboard) continue;

            long rem = Math.max(0L, c.getTargetInstant().getEpochSecond() - now.getEpochSecond());
            msgMap.put(c, lastMessageCache.getOrDefault(c.getName(), ""));
            remMap.put(c, rem);
            if (hasBossbar) bossbarList.add(c);
            if (hasScoreboard) scoreboardList.add(c);
        }

        if (doBossbar && !bossbarList.isEmpty()) {
            DisplayHandler bh = handlers.get(DisplayType.BOSS_BAR);
            if (bh instanceof com.skyblockexp.ezcountdown.display.StackableDisplay sd) {
                try { sd.displayMultiple(bossbarList, msgMap, remMap); } catch (Exception ignored) {}
            }
        }
        if (doScoreboard && !scoreboardList.isEmpty()) {
            DisplayHandler sh = handlers.get(DisplayType.SCOREBOARD);
            if (sh instanceof com.skyblockexp.ezcountdown.display.StackableDisplay sd) {
                try { sd.displayMultiple(scoreboardList, msgMap, remMap); } catch (Exception ignored) {}
            }
        }
    }

    public void displayAll(java.util.Collection<Countdown> countdowns, java.util.Map<Countdown, String> messages, java.util.Map<Countdown, Long> remaining) {
        // Cache messages so the fast-refresh path (onTick) can use up-to-date text
        for (Countdown c : countdowns) {
            String msg = messages.get(c);
            if (msg != null) lastMessageCache.put(c.getName(), msg);
        }
        MessageBatch batch = new MessageBatch();
        for (DisplayType type : DisplayType.values()) {
            DisplayHandler h = handlers.get(type);
            if (h == null) continue;
            if (h instanceof com.skyblockexp.ezcountdown.display.StackableDisplay sd) {
                java.util.List<Countdown> typed = countdowns.stream()
                        .filter(c -> c.getDisplayTypes().contains(type))
                        .toList();
                try {
                    sd.displayMultiple(typed, messages, remaining);
                } catch (Exception ignored) {}
            } else {
                // Route non-stackable handlers through the batch to deduplicate chat-channel messages
                for (Countdown c : countdowns) {
                    if (c.getDisplayTypes().contains(type)) {
                        long rem = remaining.getOrDefault(c, 0L);
                        if (rem <= 0L) continue; // skip showing zero timers
                        try { h.displayBatched(c, messages.get(c), rem, batch); } catch (Exception ignored) {}
                    }
                }
            }
        }
        batch.flush();
    }

    public void broadcastMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public void clearCountdown(Countdown countdown) {
        lastMessageCache.remove(countdown.getName());
        for (DisplayType type : countdown.getDisplayTypes()) {
            DisplayHandler h = handlers.get(type);
            if (h != null) h.clear(countdown);
        }
    }

    public void clearAll() {
        lastMessageCache.clear();
        for (DisplayHandler h : handlers.values()) h.clearAll();
    }
}

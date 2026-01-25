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
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class DisplayManager {
    private final Map<DisplayType, DisplayHandler> handlers = new EnumMap<>(DisplayType.class);
    private final Validator.ValidationResult bossbarValidation;

    public DisplayManager(com.skyblockexp.ezcountdown.config.ConfigService configService) {
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

    public void display(Countdown countdown, String message, long remainingSeconds) {
        for (DisplayType type : countdown.getDisplayTypes()) {
            DisplayHandler h = handlers.get(type);
            if (h != null) {
                h.display(countdown, message, remainingSeconds);
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public void clearCountdown(Countdown countdown) {
        for (DisplayType type : countdown.getDisplayTypes()) {
            DisplayHandler h = handlers.get(type);
            if (h != null) h.clear(countdown);
        }
    }

    public void clearAll() {
        for (DisplayHandler h : handlers.values()) h.clearAll();
    }
}

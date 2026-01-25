package com.skyblockexp.ezcountdown.api;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.bootstrap.Registry;

import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class EzCountdownApiImpl implements EzCountdownApi {
    private final Registry registry;

    public EzCountdownApiImpl(Registry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public boolean startCountdown(String name) {
        return registry.countdowns().startCountdown(name);
    }

    @Override
    public boolean stopCountdown(String name) {
        return registry.countdowns().stopCountdown(name);
    }

    @Override
    public Optional<Countdown> getCountdown(String name) {
        return registry.countdowns().getCountdown(name);
    }

    @Override
    public Collection<Countdown> listCountdowns() {
        return registry.countdowns().getCountdowns();
    }

    @Override
    public boolean createCountdown(Countdown countdown) {
        return registry.countdowns().createCountdown(countdown);
    }

    @Override
    public boolean createCountdown(CountdownType type, long amountSeconds, Collection<Player> players) {
        String name = "api-" + UUID.randomUUID().toString().substring(0, 8);
        // Use configured defaults for display types and messages
        com.skyblockexp.ezcountdown.manager.CountdownDefaults defs = registry.defaults();
        EnumSet<DisplayType> displayTypes = EnumSet.copyOf(defs.displayTypes());
        Countdown countdown = new Countdown(name,
                type,
                displayTypes,
                defs.updateIntervalSeconds(),
                defs.visibilityPermission(),
                defs.formatMessage(),
                defs.startMessage(),
                defs.endMessage(),
                java.util.List.of(),
                defs.zoneId());

        if (type == CountdownType.DURATION || type == CountdownType.MANUAL) {
            countdown.setDurationSeconds(amountSeconds);
            countdown.setRunning(true);
            countdown.setTargetInstant(Instant.now().plusSeconds(amountSeconds));
        } else {
            // For FIXED_DATE or RECURRING the "amountSeconds" is ignored; create stopped countdown
            countdown.setRunning(false);
        }

        boolean created = registry.countdowns().createCountdown(countdown);
        if (created && players != null) {
            String notify = "Countdown '" + name + "' created.";
            for (Player p : players) {
                try { p.sendMessage(notify); } catch (Exception ignored) {}
            }
        }
        return created;
    }

    @Override
    public boolean deleteCountdown(String name) {
        return registry.countdowns().deleteCountdown(name);
    }
}

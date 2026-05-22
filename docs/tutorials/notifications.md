---
title: Per-Player Notifications (Developer)
parent: Tutorials
nav_order: 3
---

# Tutorial: Per-Player Notifications

This tutorial is aimed at **plugin developers**. You will use the EzCountdown Java API to send a timed notification to a specific subset of online players  -  for example, a VIP countdown shown only to players with a particular rank.

**Prerequisites:** You have added the EzCountdown API as a `provided` dependency. See the [Developer API quick start](../api/) for dependency setup instructions.

---

## What is a notification?

A `Notification` is an ephemeral (temporary) countdown that:
- Runs in memory only  -  not saved to `countdowns.yml`.
- Disappears automatically when its duration expires.
- Can be restricted to specific players.

---

## Step 1  -  Get the API

In your plugin's `onEnable` or wherever you need it:

```java
import com.skyblockexp.ezcountdown.api.EzCountdownApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

RegisteredServiceProvider<EzCountdownApi> rsp =
        Bukkit.getServicesManager().getRegistration(EzCountdownApi.class);
if (rsp == null) {
    getLogger().warning("EzCountdown not found  -  notifications disabled.");
    return;
}
EzCountdownApi ezApi = rsp.getProvider();
```

---

## Step 2  -  Send a global notification

The simplest case  -  show an action bar countdown to every online player for 60 seconds:

```java
import com.skyblockexp.ezcountdown.api.model.Notification;

ezApi.sendNotification(Notification.ofSeconds(60));
```

---

## Step 3  -  Send to specific players

Collect the players you want to target and pass them to `sendNotification`:

```java
import java.util.List;
import org.bukkit.entity.Player;

// Example: notify all players who have the VIP permission
List<Player> vipPlayers = Bukkit.getOnlinePlayers().stream()
        .filter(p -> p.hasPermission("myserver.vip"))
        .toList();

ezApi.sendNotification(Notification.ofSeconds(60), vipPlayers);
```

Only the players in `vipPlayers` will see the action bar notification. All other online players are unaffected.

---

## Step 4  -  Build a richer notification

Use `NotificationBuilder` to customise the display type, messages, and target players in one go:

```java
import com.skyblockexp.ezcountdown.api.model.Notification;
import com.skyblockexp.ezcountdown.display.DisplayType;
import java.time.Duration;
import java.util.EnumSet;

Notification notif = Notification.builder()
    .duration(Duration.ofMinutes(5))
    .displays(EnumSet.of(DisplayType.ACTION_BAR, DisplayType.BOSS_BAR))
    .message("<gold>VIP event</gold> <gray>in</gray> <white>{formatted}</white>")
    .startMessage("<gold>VIP event countdown started!</gold>")
    .endMessage("<gold>VIP event is starting now!</gold>")
    .players(vipPlayers)   // restrict to VIP players
    .build();

Optional<String> handle = ezApi.sendNotification(notif);

// You can stop the notification early using the returned handle:
handle.ifPresent(name -> ezApi.stopCountdown(name));
```

---

## Step 5  -  Handle the exception

`NotificationBuilder.build()` throws `InvalidConfigurationException` (a subclass of `EzCountdownException`) if the duration was not set or is not positive:

```java
import com.skyblockexp.ezcountdown.api.exception.InvalidConfigurationException;

try {
    Notification notif = Notification.builder()
        // forgot .duration(...)
        .message("{formatted}")
        .build();  // throws here
} catch (InvalidConfigurationException e) {
    getLogger().severe("Bad notification config: " + e.getMessage());
}
```

---

## Display type compatibility

| Display type | Min. MC version | Notes |
|---|---|---|
| `ACTION_BAR` | Paper/Spigot 1.18 | Falls back to chat on older builds |
| `BOSS_BAR` | Paper/Spigot 1.18 | |
| `TITLE` | Paper/Spigot 1.18 | Falls back to action bar then chat |
| `CHAT` | All supported versions | Most compatible |
| `SCOREBOARD` | Paper/Spigot 1.18 | |
| `DIALOG` | Paper 1.21.7+ | Skipped on older builds |

---

## Next steps

- Listen to [`CountdownEndEvent`](../api/event/CountdownEndEvent) to run logic when a notification ends.
- See the full [`EzCountdownApi`](../api/EzCountdownApi) reference for all available methods.
- Explore the [`Notification` model](../api/model/Notification) for the complete builder API.

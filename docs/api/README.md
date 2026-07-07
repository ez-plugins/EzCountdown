---
title: Developer API
nav_order: 5
has_children: true
---

# EzCountdown Public API

## Prerequisites

- Java 17 or newer.
- Access to the GitHub Packages repository.

## Quick start

Follow these steps to add and use the EzCountdown API from your plugin.

### Installation

1) Add the GitHub Packages repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>ez-plugins</name>
        <url>https://maven.pkg.github.com/ez-plugins/EzCountdown</url>
    </repository>
</repositories>
```

2) Add the dependency:

```xml
<dependency>
    <groupId>com.github.ez-plugins</groupId>
    <artifactId>ezcountdown</artifactId>
    <version>2.0.0</version>
    <scope>provided</scope>
</dependency>
```

> Use `<scope>provided</scope>`  -  the plugin jar is already on the server; you do not want to shade it into your own jar.

## Service lookup

### Getting the API

Use Bukkit's `ServicesManager` to obtain a reference to the `EzCountdownApi` service:

```java
import com.skyblockexp.ezcountdown.api.EzCountdownApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

RegisteredServiceProvider<EzCountdownApi> rsp =
        Bukkit.getServicesManager().getRegistration(EzCountdownApi.class);
if (rsp == null) {
    // EzCountdown is not installed or not loaded yet
    return;
}
EzCountdownApi api = rsp.getProvider();
```

## Examples

### Start/Stop

```java
api.startCountdown("example-countdown");
api.stopCountdown("example-countdown");
```

### Create

```java
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.api.model.CountdownBuilder;
import com.skyblockexp.ezcountdown.display.DisplayType;
import java.time.ZoneId;
import java.time.Duration;
import java.util.EnumSet;

Countdown countdown = CountdownBuilder.builder("launch")
    .type(CountdownType.DURATION)
    .displayTypes(EnumSet.of(DisplayType.ACTION_BAR, DisplayType.TITLE))
    .updateIntervalSeconds(1)
    .formatMessage("countdown.format")
    .startMessage("Server Launching")
    .endMessage("Server Launched")
    .startSound("ENTITY_PLAYER_LEVELUP")
    .endSound("BLOCK_NOTE_BLOCK_PLING")
    .zoneId(ZoneId.systemDefault())
    .duration(Duration.ofMinutes(10))
    .build();

api.createCountdown(countdown);
api.startCountdown("launch");
```

### Inspect

```java
Optional<Countdown> maybe = api.getCountdown("launch");
Collection<Countdown> all = api.listCountdowns();
```

### Send a notification (global)

`sendNotification` fires a one-shot ephemeral display that runs for the specified duration and then vanishes  -  no YAML entry is created and no `/countdown list` entry appears.

```java
import com.skyblockexp.ezcountdown.api.model.Notification;

// Show an action bar countdown for 30 seconds using plugin defaults.
api.sendNotification(Notification.ofSeconds(30));
```

Full builder example:

```java
import com.skyblockexp.ezcountdown.api.model.Notification;
import com.skyblockexp.ezcountdown.display.DisplayType;
import java.time.Duration;
import java.util.EnumSet;

Notification notif = Notification.builder()
    .duration(Duration.ofMinutes(5))
    .displays(EnumSet.of(DisplayType.ACTION_BAR, DisplayType.BOSS_BAR))
    .message("{formatted}")
    .startMessage("Event starts soon!")
    .endMessage("Event started!")
    .build();

Optional<String> handle = api.sendNotification(notif);

// Stop early if needed:
handle.ifPresent(name -> api.stopCountdown(name));
```

### Send a notification to specific players

Two ways to send a notification to a subset of online players:

**Option A  -  via the builder:**

```java
import com.skyblockexp.ezcountdown.api.model.Notification;
import java.util.List;

List<Player> vipPlayers = /* your player collection */;

Notification notif = Notification.builder()
    .duration(60)
    .message("VIP event in {formatted}")
    .players(vipPlayers)   // restrict to these players
    .build();

api.sendNotification(notif);
```

**Option B  -  inline (without a pre-built Notification):**

```java
api.sendNotification(Notification.ofSeconds(60), vipPlayers);
```

### Exceptions

EzCountdown throws typed exceptions from the `com.skyblockexp.ezcountdown.api.exception` package:

| Exception | When thrown |
|---|---|
| `EzCountdownException` | Base class  -  catch this to handle any EzCountdown error |
| `CountdownNotFoundException` | A countdown name does not exist |
| `DuplicateCountdownException` | Creating a countdown with an already-used name |
| `InvalidConfigurationException` | `NotificationBuilder.build()` called without a valid duration |

```java
import com.skyblockexp.ezcountdown.api.exception.EzCountdownException;
import com.skyblockexp.ezcountdown.api.exception.CountdownNotFoundException;

try {
    Notification notif = Notification.builder().build(); // missing duration!
} catch (InvalidConfigurationException e) {
    getLogger().warning("Bad notification config: " + e.getMessage());
}
```

## Further reading

- [EzCountdownApi interface](EzCountdownApi)  -  full method list
- [Events](event/)  -  `CountdownStartEvent`, `CountdownTickEvent`, `CountdownEndEvent`
- [Models](model/)  -  `Countdown`, `Notification`, `CountdownType`


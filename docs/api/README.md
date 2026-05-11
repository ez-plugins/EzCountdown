---
title: Developer API
nav_order: 4
has_children: true
---

# EzCountdown Public API

## Prerequisites

- Java 21 (matches Paper API classfile version used by the plugin).
- Access to the GitHub Packages repository

## Quick start

Follow these steps to add and use the EzCountdown API from your plugin.

### Installation

1) Add the GitHub Packages repository to your `pom.xml` (replace owner/repo if different):

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>ez-plugins</name>
        <url>https://maven.pkg.github.com/ez-plugins/EzCountdown</url>
    </repository>
</repositories>
```

2) Add the dependency (use the published version tag):

```xml
<dependency>
    <groupId>com.skyblockexp</groupId>
    <artifactId>ezcountdown</artifactId>
    <version>1.3.1</version>
</dependency>
```

## Service lookup

### Getting the API

Use Bukkit's `ServicesManager` to obtain a reference to the `EzCountdownApi` service. The example below shows the common pattern used in other plugins:

```java
import com.skyblockexp.ezcountdown.api.EzCountdownApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

RegisteredServiceProvider<EzCountdownApi> rsp = Bukkit.getServicesManager().getRegistration(EzCountdownApi.class);
if (rsp == null) {
    // EzCountdown not available
    return;
}
EzCountdownApi api = rsp.getProvider();
```

## Examples

### Start/Stop

Start an existing countdown by id:

```java
api.startCountdown("example-countdown");
```

Stop a running countdown:

```java
api.stopCountdown("example-countdown");
```

### Create

Create a countdown using `CountdownBuilder`:

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
    .zoneId(ZoneId.systemDefault())
    .duration(Duration.ofMinutes(10))
    .build();

api.createCountdown(countdown);
api.startCountdown("launch");
```

### Inspect

Inspect or list countdowns:

```java
Optional<Countdown> maybe = api.getCountdown("launch");
Collection<Countdown> all = api.listCountdowns();
```

### Send a notification

`sendNotification` fires a one-shot ephemeral display that runs for the specified duration and then vanishes — no YAML entry is created and no `/countdown list` entry appears.

The simplest usage:

```java
import com.skyblockexp.ezcountdown.api.model.Notification;

// Show an action bar countdown for 30 seconds (plugin defaults for display).
api.sendNotification(Notification.ofSeconds(30));
```

Use the builder for full control:

```java
import com.skyblockexp.ezcountdown.api.model.Notification;
import com.skyblockexp.ezcountdown.display.DisplayType;
import java.time.Duration;
import java.util.EnumSet;

Notification notif = Notification.builder()
    .duration(Duration.ofMinutes(5))
    .displays(EnumSet.of(DisplayType.ACTION_BAR, DisplayType.BOSS_BAR))
    .message("{formatted}")               // optional: format message key
    .startMessage("Event starts soon!")   // optional: broadcast on start
    .endMessage("Event started!")         // optional: broadcast on end
    .build();

Optional<String> handle = api.sendNotification(notif);
```

`sendNotification` returns the generated internal name wrapped in `Optional.of(...)` on success, or `Optional.empty()` on the rare name collision. You can use the name to stop the notification early:

```java
handle.ifPresent(name -> api.stopCountdown(name));
```

## Javadoc & API reference

See the generated API docs for full type and method details: `docs/api/EzCountdownApi.md` and the `model/` and `event/` pages in this folder. If you want, I can add a GitHub Pages workflow to publish hosted Javadoc automatically on release.

## Further reading

- API reference: [docs/api/EzCountdownApi.md](docs/api/EzCountdownApi.md)
- Events: [docs/api/event](docs/api/event)
- Models: [docs/api/model](docs/api/model)
- Notification model: [docs/api/model/Notification.md](docs/api/model/Notification.md)


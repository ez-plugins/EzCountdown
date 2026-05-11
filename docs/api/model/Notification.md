---
title: Notification
parent: Models
grand_parent: Developer API
nav_order: 3
---

# Notification (model)

Lightweight immutable value object that describes a one-shot ephemeral display notification.

- Package: `com.skyblockexp.ezcountdown.api.model`
- Class: `Notification`

Notifications are used exclusively with [`EzCountdownApi.sendNotification`](../EzCountdownApi) and never written to `countdowns.yml`. They run in memory for their configured duration then are deleted automatically.

## Factory methods

- `static Notification ofSeconds(long seconds)` — Create a notification with the given duration (seconds) and all other settings at their defaults.
- `static Notification of(Duration duration)` — Create a notification from a `java.time.Duration` with all other settings at their defaults.
- `static NotificationBuilder builder()` — Start a fluent `NotificationBuilder`.

## Accessors

- `long getDurationSeconds()` — Duration of the notification in seconds (always > 0).
- `EnumSet<DisplayType> getDisplayTypes()` — Which displays are shown. Defaults to `ACTION_BAR`.
- `String getFormatMessage()` — Format message key. Defaults to `{formatted}`.
- `String getStartMessage()` — Optional message broadcast when the notification starts. May be `null`.
- `String getEndMessage()` — Optional message broadcast when the notification ends. May be `null`.

## Default values

| Field | Default |
|---|---|
| `displayTypes` | `EnumSet.of(DisplayType.ACTION_BAR)` |
| `formatMessage` | `{formatted}` |
| `startMessage` | `null` (no broadcast) |
| `endMessage` | `null` (no broadcast) |

## NotificationBuilder

Obtained via `Notification.builder()`. All methods return `this` for chaining.

| Method | Description |
|---|---|
| `duration(long seconds)` | Set duration in seconds (required; must be > 0) |
| `duration(Duration)` | Set duration from a `java.time.Duration` |
| `display(DisplayType...)` | Replace the display set with the given types (varargs) |
| `displays(EnumSet<DisplayType>)` | Replace the display set; pass `null` to reset to default |
| `addDisplay(DisplayType)` | Add a single display type to the current set |
| `message(String)` | Set the format message; `null` or blank resets to `{formatted}` |
| `startMessage(String)` | Set the start broadcast; blank treated as `null` |
| `endMessage(String)` | Set the end broadcast; blank treated as `null` |
| `build()` | Build and return the `Notification`. Throws `IllegalStateException` if duration was never set or is ≤ 0. |

## Examples

One-liner (action bar for 30 s, plugin defaults):

```java
Notification notif = Notification.ofSeconds(30);
api.sendNotification(notif);
```

Full builder:

```java
Notification notif = Notification.builder()
    .duration(Duration.ofMinutes(5))
    .displays(EnumSet.of(DisplayType.ACTION_BAR, DisplayType.BOSS_BAR))
    .message("{formatted}")
    .startMessage("Event starting soon!")
    .endMessage("Event has started!")
    .build();

Optional<String> handle = api.sendNotification(notif);
// Stop early if needed:
handle.ifPresent(name -> api.stopCountdown(name));
```

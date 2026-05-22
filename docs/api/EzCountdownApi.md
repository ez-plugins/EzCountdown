---
title: EzCountdownApi
parent: Developer API
nav_order: 2
---

# EzCountdownApi (interface)

Public service interface exposed by the plugin: `com.skyblockexp.ezcountdown.api.EzCountdownApi`

## Methods

### Countdown lifecycle

- `boolean startCountdown(String name)`
  - Start a configured countdown by name. Returns `true` if started successfully, `false` if the countdown was already running or does not exist.

- `boolean stopCountdown(String name)`
  - Stop a running countdown by name. Returns `true` if stopped, `false` if it was already stopped or does not exist.

- `Optional<Countdown> getCountdown(String name)`
  - Retrieve a countdown configuration and runtime state by name.

- `Collection<Countdown> listCountdowns()`
  - List all countdowns currently known to the plugin.

### Countdown management

- `boolean createCountdown(Countdown countdown)`
  - Create and persist a new countdown configuration. Returns `true` on success, `false` if a countdown with the same name already exists.

- `boolean createCountdown(CountdownType type, long durationSeconds, Collection<Player> targetPlayers)`
  - Convenience overload: create an ephemeral countdown of the given type and duration, visible only to the specified players. Returns `true` on success.

- `boolean deleteCountdown(String name)`
  - Delete a configured countdown by name. Returns `true` when deleted.

### Notifications

- `Optional<String> sendNotification(Notification notification)`
  - Fire a one-shot ephemeral timed display notification. The notification runs for its configured duration then is removed automatically  -  it is never written to `countdowns.yml` and does not appear in `/countdown list`. Returns an `Optional` containing the generated internal name on success, or an empty `Optional` on name collision.

- `Optional<String> sendNotification(Notification notification, Collection<Player> players)`
  - Same as above, but restrict the notification to the specified players. Equivalent to building a `Notification` with `NotificationBuilder.players(players)`.

## Exceptions

Methods return boolean flags or Optional on expected failures (e.g. countdown not found). The API also defines a typed exception hierarchy for use in builders and validation:

| Exception | Package | Description |
|---|---|---|
| `EzCountdownException` | `api.exception` | Base class for all EzCountdown API exceptions |
| `CountdownNotFoundException` | `api.exception` | A countdown name does not exist; carries `getCountdownName()` |
| `DuplicateCountdownException` | `api.exception` | A countdown with this name already exists; carries `getCountdownName()` |
| `InvalidConfigurationException` | `api.exception` | Thrown by `NotificationBuilder.build()` when duration is missing or invalid |

## Usage example

```java
RegisteredServiceProvider<EzCountdownApi> rsp =
        Bukkit.getServicesManager().getRegistration(EzCountdownApi.class);
if (rsp != null) {
    EzCountdownApi api = rsp.getProvider();
    api.createCountdown(myCountdown);
    api.startCountdown("myCountdownName");
}
```

Per-player notification:

```java
List<Player> targets = List.of(player1, player2);
api.sendNotification(Notification.ofSeconds(30), targets);
```

## Notes

- Changes made via the API are persisted using the plugin's `countdowns.yml` storage.
- Ephemeral countdowns created by `sendNotification` are held in memory only; they are deleted automatically when they end and are never stored in `countdowns.yml`.
- `createCountdown(CountdownType, long, Collection<Player>)` creates an ephemeral (in-memory) countdown visible only to the provided players.

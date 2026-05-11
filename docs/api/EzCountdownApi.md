---
title: EzCountdownApi
parent: Developer API
nav_order: 2
---

# EzCountdownApi (interface)

Public service interface exposed by the plugin: `com.skyblockexp.ezcountdown.api.EzCountdownApi`

Methods

- `boolean startCountdown(String name)`
  - Start a configured countdown by name. Returns `true` if started successfully.

- `boolean stopCountdown(String name)`
  - Stop a running countdown by name. Returns `true` if stopped.

- `Optional<Countdown> getCountdown(String name)`
  - Retrieve a countdown configuration/runtime instance by name.

- `Collection<Countdown> listCountdowns()`
  - List all countdowns currently known to the plugin.

- `boolean createCountdown(Countdown countdown)`
  - Create and persist a new countdown configuration. Returns `true` on success.

- `boolean deleteCountdown(String name)`
  - Delete a configured countdown by name. Returns `true` when deleted.

- `Optional<String> sendNotification(Notification notification)`
  - Fire a one-shot, ephemeral timed display notification without creating a persistent countdown. The notification runs for its configured duration then disappears automatically — it is never written to `countdowns.yml`. Returns an `Optional` containing the generated internal name on success, or an empty `Optional` on collision.

Usage example (service lookup):

```java
RegisteredServiceProvider<EzCountdownApi> rsp = Bukkit.getServicesManager().getRegistration(EzCountdownApi.class);
if (rsp != null) {
    EzCountdownApi api = rsp.getProvider();
    api.createCountdown(myCountdown);
    api.startCountdown("myCountdownName");
}
```

Notes

- Changes made via the API are persisted using the plugin's `countdowns.yml` storage.
- Methods return boolean flags to indicate success; check plugin logs or events for failure reasons.
- Countdowns created by `sendNotification` are ephemeral: they run in memory only and are deleted automatically when they end. They are never stored in `countdowns.yml` and do not appear in `/countdown list`.

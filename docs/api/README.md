# EzCountdown Public API

This document describes the public API exposed by the EzCountdown plugin for other Bukkit/Spigot plugins.

Quick overview

- Service: `com.skyblockexp.ezcountdown.api.EzCountdownApi`
- Purpose: allow other plugins to create, start, stop, inspect, and delete countdowns programmatically.
- How to obtain: using Bukkit `ServicesManager` or via the plugin's `Registry` if you have direct access.

Example (service lookup):

```java
import com.skyblockexp.ezcountdown.api.EzCountdownApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

RegisteredServiceProvider<EzCountdownApi> rsp = Bukkit.getServicesManager().getRegistration(EzCountdownApi.class);
if (rsp != null) {
    EzCountdownApi api = rsp.getProvider();
    api.startCountdown("example-countdown");
}
```

Files in this folder

- `event/` - documentation for custom Bukkit events fired by the plugin.
- `model/` - documentation for public model classes used by the API.
- `EzCountdownApi.md` - class reference for the `EzCountdownApi` interface.

Notes

- Events are regular Bukkit events; register listeners as usual.
- `Countdown` model is immutable for configuration fields; runtime fields are updated by the plugin.

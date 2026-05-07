---
title: Events
parent: Developer API
nav_order: 3
has_children: true
---

# Events

EzCountdown fires Bukkit events at key lifecycle points. Register listeners in your plugin to react to countdown activity.

| Event | When it fires |
|---|---|
| [CountdownStartEvent](CountdownStartEvent) | A countdown transitions from stopped to running |
| [CountdownTickEvent](CountdownTickEvent) | Each update tick while a countdown is running |
| [CountdownEndEvent](CountdownEndEvent) | A countdown reaches zero and stops |

## Registering a listener

```java
public class MyListener implements Listener {

    @EventHandler
    public void onCountdownEnd(CountdownEndEvent event) {
        // react to countdown completion
    }
}
```

Register the listener in your plugin's `onEnable`:

```java
Bukkit.getPluginManager().registerEvents(new MyListener(), this);
```

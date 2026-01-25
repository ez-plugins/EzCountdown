# CountdownStartEvent

Fired when a countdown is started.

- Package: `com.skyblockexp.ezcountdown.api.event`
- Class: `CountdownStartEvent`
- Extends: `org.bukkit.event.Event`

Fields / accessors:

- `Countdown getCountdown()` — returns the `Countdown` instance that started.

Usage:

```java
@EventHandler
public void onStart(CountdownStartEvent e) {
    Countdown c = e.getCountdown();
    // inspect or react
}
```

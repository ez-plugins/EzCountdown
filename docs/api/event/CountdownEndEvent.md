# CountdownEndEvent

Fired when a countdown finishes.

- Package: `com.skyblockexp.ezcountdown.api.event`
- Class: `CountdownEndEvent`
- Extends: `org.bukkit.event.Event`

Fields / accessors:

- `Countdown getCountdown()` — the countdown instance that ended.

Usage:

```java
@EventHandler
public void onEnd(CountdownEndEvent e) {
    Countdown c = e.getCountdown();
    // react to completion
}
```

# CountdownTickEvent

Fired periodically while a countdown is running to indicate remaining time.

- Package: `com.skyblockexp.ezcountdown.api.event`
- Class: `CountdownTickEvent`
- Extends: `org.bukkit.event.Event`

Fields / accessors:

- `Countdown getCountdown()` — the countdown instance.
- `long getRemainingSeconds()` — remaining seconds until the countdown ends.

Usage:

```java
@EventHandler
public void onTick(CountdownTickEvent e) {
    Countdown c = e.getCountdown();
    long remaining = e.getRemainingSeconds();
    // update UI or react
}
```

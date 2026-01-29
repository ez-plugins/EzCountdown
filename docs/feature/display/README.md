# Display Types Overview

EzCountdown supports multiple display types for showing countdowns to players. Configure displays per-countdown using the `display.types` list (or set defaults in your plugin config). Each display has its trade-offs - use the one that best fits your event and server runtime.

Supported displays

-- `ACTION_BAR` - short, transient messages shown above the hotbar. Good for unobtrusive, frequent updates. See [action_bar.md](display/action_bar.md).
-- `BOSS_BAR` - persistent progress bar at the top of the screen. High visibility for important timers. See [boss_bar.md](display/boss_bar.md).
-- `TITLE` - centered large title/subtitle overlay; great for dramatic announcements. See [title.md](display/title.md).
-- `CHAT` - most compatible; sends countdown updates as chat messages. Use as a reliable fallback. See [chat.md](display/chat.md).
-- `SCOREBOARD` - sidebar scoreboard display for a framed, persistent view. See [scoreboard.md](display/scoreboard.md).

Best practices

- Use `CHAT` as a fallback for mixed-version networks or older servers.
- Combine displays for better coverage (e.g., `BOSS_BAR` + `CHAT`) so players who can't see one UI still receive the message.
- Test display behaviour on a staging server matching your production runtime (client versions and server fork can affect availability).

How to enable

Add the display types under each countdown's `display.types` section:

```yaml
countdowns:
  launch:
    type: FIXED_DATE
    target: "2026-06-01 12:00"
    display:
      types:
        - BOSS_BAR
        - CHAT
```

If a display API isn't available at runtime the plugin will try fallbacks when possible.

See individual display docs in this folder for compatibility details and configuration hints.

# Action Bar

What it is

Displays a short, transient message above the player's hotbar. Good for frequent, unobtrusive countdown updates.

When to use

- Use for timers where players need periodic awareness without a persistent HUD element.

How to enable

- Add `ACTION_BAR` to `display.types` for a countdown or include it in the default display types in your plugin config:

```yaml
countdowns:
  maintenance:
    type: DURATION
    duration: "2h"
    display:
      types:
        - ACTION_BAR
```

Compatibility & behavior

- Uses the best available server API (modern `Player.sendActionBar` where available). The plugin includes fallbacks but may degrade to chat on very old runtimes.

Config override

 - `display-overrides.force-enable.action_bar` - force registration when auto-detection fails. Use only when you understand your server's runtime.

Troubleshooting

- If players see chat instead of an action bar, their server or client may not support the action bar API; use `CHAT` as a fallback.
- Test on a staging server with the same Bukkit/Paper build as production.

Recommendation

- Prefer `ACTION_BAR` on modern Spigot/Paper servers; keep `CHAT` as a fallback for mixed-version networks.

# Action Bar

Short summary

Displays a short message above the player's hotbar. Ideal for short, frequent updates like a ticking countdown.

When to use

- Use for unobtrusive, frequent updates that should not clutter chat.
- Good for countdowns where players need periodic awareness but not a persistent UI element.

How to enable

Add `ACTION_BAR` to the `displays` list for a countdown or include it in your defaults.

```yaml
countdowns:
  maintenance:
    type: duration
    duration: "2h"
    displays:
      - ACTION_BAR
```

Compatibility & behavior

- Primary API: `Player.sendActionBar(String)` (modern Bukkit/Spigot).
- Fallback: `player.spigot().sendMessage(...)` (Spigot/Bungee action-bar).
- Final fallback: plain chat message — used to guarantee delivery on very old or non-standard servers.

Config override

- `display-overrides.force-enable.action_bar` — force the plugin to register the action bar display even if a preferred API is not detected. Use only if you understand your server's capabilities.

Troubleshooting (server owner tips)

- If players report seeing chat instead of an action bar, their server build likely lacks the action bar API; consider using `chat` or enabling the Spigot-compatible fallback.
- To test: create a short duration countdown and monitor how messages appear across client versions.
- If forcing via config, check server logs for warnings about force-enabled displays.

Recommendation

- Prefer `ACTION_BAR` on modern Spigot/Paper servers; keep `CHAT` as a reliable fallback in `countdowns.yml` when supporting a wide range of server builds.

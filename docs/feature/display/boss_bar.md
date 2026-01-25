
# Boss Bar

Short summary

Shows a persistent boss bar at the top of the screen with a title and progress. Best for long-running or important countdowns where players should always see progress.

When to use

- Use for server-wide events (tournaments, long raids, server countdowns) where a visual progress indicator is important.

How to enable

Add `BOSS_BAR` to the `displays` list for a countdown:

```yaml
countdowns:
  event:
    type: date
    date: "2026-01-01 00:00"
    displays:
      - BOSS_BAR
```

Compatibility & requirements

- Requires Bukkit `org.bukkit.boss.BossBar` API (Minecraft 1.9+).
- The plugin performs a runtime check and disables boss bar support when the API/class is missing.

Config override

- `display-overrides.force-enable.boss_bar` — forces registration of the boss bar display even if the API check failed. The plugin includes runtime guards to avoid crashes, but the display may behave inconsistently. Use only when you understand the server build.

Troubleshooting (server owner tips)

- If boss bars don't appear: ensure your server is 1.9+ and running a compatible Bukkit/Spigot/Paper fork.
- When force-enabled and you see warnings in logs, revert the override and prefer `ACTION_BAR` or `CHAT` on that server.
- Test boss bars on a staging server with the same runtime as production before enabling for players.

Recommendation

- Prefer `BOSS_BAR` for high-visibility events on modern servers; otherwise use `ACTION_BAR` or `CHAT` as safer alternatives.

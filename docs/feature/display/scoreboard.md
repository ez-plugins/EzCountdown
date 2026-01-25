
# Scoreboard

Short summary

Displays a sidebar scoreboard containing the countdown message. Useful for persistent, framed displays that don't interrupt the player.

When to use

- Use for matches, tournaments, or any situation where a persistent sidebar is preferred over transient UI elements.

How to enable

Add `SCOREBOARD` to the `displays` list in a countdown definition.

```yaml
countdowns:
  match_start:
    type: duration
    duration: "5m"
    displays:
      - SCOREBOARD
```

Compatibility & behavior

- Requires Bukkit's `ScoreboardManager` (`Bukkit.getScoreboardManager()`) and the per-player `Scoreboard` APIs.
- The plugin validates availability at startup and disables scoreboard displays if the APIs are missing.

Config override

- `display-overrides.force-enable.scoreboard` — force-enable scoreboard support when the validator fails. The plugin wraps scoreboard operations to avoid crashes, but display behavior may be degraded on unsupported platforms.

Troubleshooting (server owner tips)

- If scoreboards do not appear: confirm `Bukkit.getScoreboardManager()` returns non-null on your server runtime.
- If you see errors or corrupt scoreboards, remove the override and use `CHAT` as a fallback until you can test on a compatible build.
- Scoreboards can be theme-dependent — test formatting in `messages.yml` to ensure text fits the sidebar width.

Recommendation

- Use `SCOREBOARD` on modern Spigot/Paper servers; keep `CHAT` as a safe fallback for mixed or older environments.

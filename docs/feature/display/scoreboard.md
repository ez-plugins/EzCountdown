
# Scoreboard

What it is

Displays a persistent sidebar scoreboard containing the countdown message. Useful when you want a framed, always-visible view of the timer.

When to use

- Use for matches, tournaments, or lobby timers where a persistent HUD element is preferred.

How to enable

- Add `SCOREBOARD` to `display.types` in the countdown definition:

```yaml
countdowns:
  match_start:
    type: DURATION
    duration: "5m"
    display:
      types:
        - SCOREBOARD
```

Compatibility & behavior

- Requires Bukkit's `ScoreboardManager` and per-player `Scoreboard` APIs. The plugin checks availability and will skip scoreboards if unsupported.

Troubleshooting

- If scoreboards do not appear, confirm `Bukkit.getScoreboardManager()` is available and test on a staging server with the same runtime.
- If you encounter corrupt scoreboards, remove any `display-overrides.force-enable.scoreboard` setting and use `CHAT` until you can test safely.

Recommendation

- Use `SCOREBOARD` on modern Spigot/Paper servers; keep `CHAT` as a safe fallback for mixed or older environments.

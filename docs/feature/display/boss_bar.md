
# Boss Bar

What it is

Shows a persistent boss bar at the top of the screen with a title and progress indicator. Good for high-visibility, persistent countdowns.

When to use

- Use for server-wide events (tournaments, major resets) where players should always see progress.

How to enable

- Add `BOSS_BAR` to `display.types` for a countdown:

```yaml
countdowns:
  event:
    type: FIXED_DATE
    target: "2026-01-01 00:00"
    display:
      types:
        - BOSS_BAR
```

Compatibility & requirements

- Requires Bukkit `org.bukkit.boss.BossBar` API (Minecraft 1.9+). The plugin checks availability and will skip boss bar registration if unsupported.

Config override

- `display-overrides.force-enable.boss_bar` - forces boss bar registration despite API checks; only use if you understand the risks.

Troubleshooting

- If boss bars don't appear, confirm your server runtime supports the BossBar API and test on a staging server.

Recommendation

- Use `BOSS_BAR` for important, long-running timers on modern servers; provide `ACTION_BAR` or `CHAT` as fallbacks for mixed environments.

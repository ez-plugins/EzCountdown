
# Title

Short summary

Displays a centered title and optional subtitle overlay. Great for high-impact messages (e.g., final seconds).

When to use

- Use for dramatic announcements, last-second alerts, or important server-wide messages.

How to enable

Add `TITLE` to the `displays` list for a countdown.

```yaml
countdowns:
  launch:
    type: date
    date: "2026-06-01 12:00"
    displays:
      - TITLE
```

Compatibility & behavior

- Primary API: `Player.sendTitle(...)` (modern Bukkit/Spigot/Paper).
- If not available, the plugin will try to fall back to the action bar, then chat, so players still receive the message.

Config override

- `display-overrides.force-enable.title` — forces the title display even if the title API wasn't detected. Use with caution; fallback behavior is applied to reduce crash risk.

Troubleshooting (server owner tips)

- If titles do not appear, verify your server version and client support for titles; Paper/Spigot on 1.8+ often provides similar APIs but behavior can vary.
- If clients see garbled or missing subtitles, check `messages.yml` encoding and test with a small sample countdown.

Recommendation

- Use `TITLE` for high-visibility events on modern servers; include `CHAT` or `ACTION_BAR` as secondary displays for mixed-client environments.

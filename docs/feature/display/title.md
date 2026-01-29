
# Title

What it is

Displays a centered title and optional subtitle overlay on the player's screen. Ideal for dramatic announcements (final seconds, event start).

When to use

- Use for last-second alerts or important events where you want to capture player attention immediately.

How to enable

- Add `TITLE` to `display.types` for a countdown:

```yaml
countdowns:
  launch:
    type: FIXED_DATE
    target: "2026-06-01 12:00"
    display:
      types:
        - TITLE
```

Compatibility & behavior

- Uses `Player.sendTitle(...)` where available. The plugin applies fallbacks (action bar, then chat) when titles are not supported.

Config override

- `display-overrides.force-enable.title` - force title registration despite detection; use with caution.

Troubleshooting

- If titles do not appear, verify server/client support and test on a staging server with the same runtime.
- If subtitles are missing or garbled, check `messages.yml` encoding and formatting.

Recommendation

- Use `TITLE` for high-impact moments; include `CHAT` or `ACTION_BAR` as secondary displays for mixed-client environments.

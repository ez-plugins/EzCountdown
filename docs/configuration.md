
# Configuration (Server Owner Notes)

Configuration files are in the plugin data folder and are reloaded with `/countdown reload` or on server restart.

Key files

 - `config.yml` - Global plugin settings and sensible defaults (update intervals, default displays, compatibility overrides).
 - `messages.yml` - All user-facing text; supports MiniMessage for colors and simple HTML-like formatting.
 - `countdowns.yml` - Per-countdown definitions: type, date/duration, enabled displays, messages, end commands, teleport actions.
 - `locations.yml` - Named teleport locations used by countdowns (world, x, y, z, yaw, pitch).

Important settings to review

 - `update-interval` - How often (seconds) countdowns update; lower = smoother but more CPU.
 - `default-displays` - Controls which display types are enabled by default (actionbar, bossbar, chat, title, scoreboard).
 - `display-overrides.force-enable.<display>` - Force-enable a display type when the server lacks native support (use with caution).
 - `placeholder-support` - If enabled, PlaceholderAPI placeholders are available. Example placeholders: `%ezcountdown_<name>_formatted%`.

Example `countdowns.yml` entry

```yaml
countdowns:
  new_year:
    type: date
    date: "2026-01-01 00:00"
    displays:
      - actionbar
      - bossbar
    message: "New Year in %ezcountdown_new_year_formatted%"
    commands_on_end:
      - "broadcast &6Happy New Year!"
    teleport:
      start: spawn
      end: arena
```

Server-owner tips

- Always back up `countdowns.yml` before large edits.
- Use `messages.yml` to localize or brand the messages; test MiniMessage outputs on a test server.
 - `commands_on_end` run as console - avoid dangerous or untested commands in production.
- Use `display-overrides.force-enable` only when you understand the compatibility risks; the plugin includes runtime fallbacks but some features may not work correctly on older servers.

Need a sample `countdowns.yml` with several pre-made events? I can add one tailored to holidays and routine maintenance.

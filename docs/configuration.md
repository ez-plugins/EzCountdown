
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

Recurring alignment options

- `align_to_clock` — boolean, when `true` enables clock-aligned repeats (default `false`).
- `align_interval` — duration string like `2h`, `1d`, or `30m` describing the alignment interval used when `align_to_clock: true`.
- `timezone` / `zone` — an IANA timezone identifier (e.g. `UTC`, `Europe/London`) used to resolve calendar times; `timezone` is an alias for the existing `zone` key.
- `missed_run_policy` — controls behavior when the server was down for scheduled occurrences. Values: `SKIP` (default), `RUN_SINGLE`, `RUN_ALL`.

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

Clock-aligned recurring example

```yaml
countdowns:
  two_hour_utc:
    type: RECURRING
    align_to_clock: true
    align_interval: "2h"
    timezone: "UTC"
    # (other recurring keys like recurring.month/day/time are optional when using align_interval)
    running: true
```

Server-owner tips

- Always back up `countdowns.yml` before large edits.
- Use `messages.yml` to localize or brand the messages; test MiniMessage outputs on a test server.
 - Use `messages.yml` to localize or brand the messages; test MiniMessage outputs on a test server.
 - Translation variables: you can reference message keys from `messages.yml` directly inside `countdowns.yml` (or other config strings) using the `{translate:key.path}` token. Example:

```yaml
# messages.yml
example:
  format: "<white>Example Event</white> <gray>starts in</gray> <yellow>{formatted}</yellow>"

# countdowns.yml
countdowns:
  my_event:
    messages:
      format: "{translate:example.format}"
```

Behavior notes:

 - Missing keys: if a `{translate:...}` key is not present in `messages.yml`, the plugin will log a warning and replace the token with an empty string in the output.
 - Nesting: translated values may themselves contain `{translate:...}` tokens; nesting is resolved up to 3 levels to avoid infinite loops.
 - Ordering: translation resolution happens before runtime placeholder replacement (for example `{name}`, `{formatted}`) and before MiniMessage serialization, so translated text can include placeholders and MiniMessage markup.
 - `commands_on_end` run as console - avoid dangerous or untested commands in production.
- Use `display-overrides.force-enable` only when you understand the compatibility risks; the plugin includes runtime fallbacks but some features may not work correctly on older servers.

Need a sample `countdowns.yml` with several pre-made events? I can add one tailored to holidays and routine maintenance.

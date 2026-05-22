---
title: Configuration
parent: Server Owners
nav_order: 2
---

# Configuration

Configuration files live in `plugins/EzCountdown/` and are reloaded with `/countdown reload` or on server restart.

## Files overview

| File | Purpose |
|---|---|
| `config.yml` | Global plugin settings: update intervals, default displays, time format, compatibility overrides |
| `messages.yml` | All user-facing text; supports MiniMessage for colors and formatting |
| `countdowns.yml` | Per-countdown definitions: type, date/duration, display types, messages, end commands, teleport |
| `locations.yml` | Named teleport locations (world, x, y, z, yaw, pitch) |

---

## `config.yml`  -  key settings

| Key | Description |
|---|---|
| `update-interval` | How often (in seconds) countdowns tick. Lower = smoother display but more CPU. |
| `default-displays` | Display types enabled by default for new countdowns (`actionbar`, `bossbar`, `chat`, `title`, `scoreboard`, `dialog`). |
| `display-overrides.force-enable.<display>` | Force-enable a display type regardless of runtime support (use with caution). |
| `placeholder-support` | Enable PlaceholderAPI integration. Requires [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) to be installed. |

---

## Time format

The `{formatted}` placeholder (and `%ezcountdown_<name>_formatted%`) is controlled by `display.time-format` in `config.yml`:

| Key | Type | Default | Description |
|---|---|---|---|
| `display.time-format.pattern` | string | `"{days}d {hours}h {minutes}m {seconds}s"` | Token layout. Supported tokens: `{days}`, `{hours}`, `{minutes}`, `{seconds}`. |
| `display.time-format.hide-leading-zeros` | boolean | `true` | When `true`, leading zero-valued segments are omitted. The last segment is always shown. |

Examples with `hide-leading-zeros: true`:

| Remaining | Output |
|---|---|
| 2d 5h 3m 7s | `2d 5h 3m 7s` |
| 0d 2h 5m 3s | `2h 5m 3s` |
| 0d 0h 0m 45s | `45s` |
| 0d 0h 0m 0s | `0s` |

---

## Boss bar customization

Set per-countdown inside `countdowns.yml` under `display.bossbar`:

| Key | Type | Default | Valid values |
|---|---|---|---|
| `color` | string | `BLUE` | `BLUE`, `RED`, `GREEN`, `YELLOW`, `PINK`, `PURPLE`, `WHITE` |
| `style` | string | `SOLID` | `SOLID`, `SEGMENTED_6`, `SEGMENTED_10`, `SEGMENTED_12`, `SEGMENTED_20` |

**Compatibility:** boss bar requires Minecraft 1.9+. On older servers the plugin silently skips the boss bar display.

---

## Clock-aligned recurring options

Set these per-countdown in `countdowns.yml`:

| Key | Type | Default | Description |
|---|---|---|---|
| `align_to_clock` | boolean | `false` | Enable clock-aligned repeats |
| `align_interval` | string |  -  | Alignment interval (e.g. `2h`, `1d`, `30m`) |
| `timezone` | string | server default | IANA timezone identifier (e.g. `UTC`, `Europe/London`) |
| `missed_run_policy` | string | `SKIP` | `SKIP`, `RUN_SINGLE`, or `RUN_ALL` |

---

## Translation tokens

Reference `messages.yml` keys from inside `countdowns.yml` using `{translate:key.path}`:

```yaml
# messages.yml
sales:
  format: "<white>Sale</white> <gray>ends in</gray> <yellow>{formatted}</yellow>"
  start: "<green>Sale started!</green>"
  end: "<red>Sale ended.</red>"

# countdowns.yml
countdowns:
  my_sale:
    messages:
      format: "{translate:sales.format}"
      start: "{translate:sales.start}"
      end: "{translate:sales.end}"
```

Translation tokens are resolved before MiniMessage serialization and before runtime placeholders like `{formatted}`, so translated text may include both. Nested `{translate:...}` tokens are resolved up to 3 levels deep. Missing keys are replaced with an empty string and logged as a warning.

---

## Full `countdowns.yml` example

```yaml
countdowns:
  new_year:
    type: FIXED_DATE
    target: "2026-01-01 00:00"
    timezone: "UTC"
    display:
      types: [ACTION_BAR, BOSS_BAR]
      update-interval: 1
      bossbar:
        color: YELLOW
        style: SOLID
    messages:
      format: "New Year in {formatted}"
      start: "Countdown started!"
      end: "Happy New Year!"
    commands_on_end:
      - "broadcast &6Happy New Year!"
    teleport:
      end: spawn

  two_hour_announce:
    type: RECURRING
    align_to_clock: true
    align_interval: "2h"
    timezone: "UTC"
    missed_run_policy: SKIP
    display:
      types: [CHAT]
    messages:
      format: "Next reset in {formatted}"

  maintenance:
    type: DURATION
    duration: "2h"
    display:
      types: [ACTION_BAR, TITLE]
    messages:
      format: "Maintenance in {formatted}"
      end: "Maintenance complete!"
    commands_on_end:
      - "broadcast &aServer is back online!"

  holiday_sale:
    type: FIXED_DATE
    target: "2026-12-20 12:00"
    display:
      types: [BOSS_BAR, ACTION_BAR]
      bossbar:
        color: RED
        style: SEGMENTED_10
    messages:
      format: "{translate:sales.format}"
      start: "{translate:sales.start}"
      end: "{translate:sales.end}"
```

---

## Server-owner tips

- Back up `countdowns.yml` before large edits  -  a YAML syntax error will prevent the file from loading.
- Use `messages.yml` to centralize all player-visible text; test MiniMessage formatting on a staging server.
- Use `display-overrides.force-enable` only when you understand the compatibility risks. The plugin already includes runtime fallbacks for most display types.
- `commands_on_end` run as console with full operator permissions  -  keep the list minimal and review it regularly.

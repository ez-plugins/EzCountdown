# Countdown Types

EzCountdown supports four primary countdown modes. Each section below explains when to use a mode, the exact YAML keys the plugin reads, an example you can paste into `countdowns.yml`, and short troubleshooting notes.

Important keys used by the plugin
- `type` - one of `FIXED_DATE`, `DURATION`, `MANUAL`, `RECURRING` (case-insensitive).
- `display.types` - list of enabled displays (e.g. `ACTION_BAR`, `BOSS_BAR`).
- `messages.*`, `commands.end`, `zone` - per-countdown messages, end commands, and timezone.

## Fixed Date

Use this when you have a specific calendar date/time to count down to.

- YAML keys: `type: FIXED_DATE` and `target: "YYYY-MM-DD HH:mm"` (resolved using the countdown's `zone`).
- Example

```yaml
countdowns:
  new_year:
    type: FIXED_DATE
    target: "2026-01-01 00:00"
    running: true
```

- Notes
  - Ensure `target` is in the future for the configured `zone` or the countdown will be created stopped.

## Duration

Use this when you want a countdown that runs for a fixed length of time (e.g., maintenance windows).

- YAML keys: `type: DURATION` and `duration: "1h30m"`. The duration parser accepts `s`, `m`, `h`, `d` tokens and combinations like `1d4h30m`.
- Example (24 hours)

```yaml
countdowns:
  maintenance:
    type: DURATION
    duration: "1d"   # can also be written as "86400s"
    running: true
```

- Notes
  - If `running: true` the plugin will compute a `target` at creation (now + duration) and start immediately.

## Manual

Same format as `DURATION` but does not auto-start - useful when you want to prepare a timer and start it later.

- YAML keys: `type: MANUAL` and `duration: "30m"`.
- Control: use `/countdown start <name>` and `/countdown stop <name>` to operate the timer.
- Example

```yaml
countdowns:
  manual_event:
    type: MANUAL
    duration: "30m"
    running: false
```

## Recurring (yearly)

Recurring countdowns are currently yearly: they repeat each year on the configured month/day/time.

- YAML keys: `type: RECURRING`, `recurring.month` (1–12), `recurring.day` (1–31), `recurring.time` (HH:mm).
- Example (New Year's Eve yearly)

```yaml
countdowns:
  festival:
    type: RECURRING
    recurring.month: 12
    recurring.day: 31
    recurring.time: "23:59"
    running: true
```

- Notes
  - `RECURRING` computes the next occurrence and, when it completes, advances the next target by one year.
  - If you need weekly or monthly recurrence, use a `DURATION` with `7d` and an `end` command to restart it, or run an external scheduler.

## Tips & common workflows

- To implement weekly repetition, create a `DURATION` countdown of `7d` and add an `end` command that restarts it (see `commands.end`), or use external tooling to trigger `/countdown start` weekly.
- Use `zone: "Europe/Amsterdam"` (or any IANA zone) to control how fixed and recurring times are resolved.

## Troubleshooting

- If a `FIXED_DATE` countdown is stopped on startup, verify the `target` is in the future for the configured `zone`.
- If `RECURRING` appears to wait a year, that is expected: the plugin treats recurring as annual.

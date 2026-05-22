# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.4.3] - 2026-05-16

### Fixed

- **DURATION countdown resets to full duration on `/countdown reload`** — `resumeRunningCountdowns()` previously called `handler.onStart()`, which always sets `targetInstant` to `now + fullDuration`, discarding the `target_epoch` saved in storage. It now calls `handler.ensureTarget()` instead, which is a no-op when a target is already present. The same guard was added to the legacy fallback path for handler-less countdown types. End-commands that fired once before a reload will no longer fire again unexpectedly due to the countdown silently restarting.



### Added

- **Configurable time format** — new `display.time-format` section in `config.yml`.
  - `pattern` (default `"{days}d {hours}h {minutes}m {seconds}s"`) — customize the token layout used for the `{formatted}` placeholder and `%ezcountdown_<name>_formatted%` PAPI expansion.
  - `hide-leading-zeros` (default `true`) — when enabled, leading space-delimited segments whose unit value is zero are suppressed. For example, `0d 0h 5m 3s` is displayed as `5m 3s`.
  - Applies to countdown display messages, discord webhook `{time_left}`, PlaceholderAPI, and the GUI preview action.
  - Hot-reloads with `/countdown reload`.

## [1.4.1] - 2026-05-16

### Fixed

- **Bossbar color/style ignored** - `display.bossbar.color` and `display.bossbar.style` in `countdowns.yml` had no effect; the boss bar always rendered in the default blue/solid style. All four countdown-type handlers (`DurationHandler`, `FixedDateHandler`, `RecurringHandler`, `ManualHandler`) now read these fields from config and pass them to the `Countdown` constructor.

## [1.4.0] - 2026-05-11

### Added

- **Notification API** — `EzCountdownApi.sendNotification(Notification)` fires a one-shot ephemeral timed display from any plugin without creating a persistent countdown or touching `countdowns.yml`.
- **`Notification` model** — lightweight immutable value object with factory methods `Notification.ofSeconds(long)`, `Notification.of(Duration)`, and a fluent `NotificationBuilder` (display types, format message, start/end messages).
- **`NotificationBuilder`** — fluent builder for `Notification`; all fields have sensible defaults (`ACTION_BAR` display, `{formatted}` message); `build()` validates duration > 0.
- **Ephemeral countdown support** — `Countdown.isEphemeral()` / `CountdownBuilder.ephemeral(boolean)`; ephemeral countdowns are removed from memory automatically when they end and are never written to storage.

## [1.3.2] - 2026-04-27

### Fixed

- Fixed countdown count edge-case (PR #47).

## [1.3.1] - 2026-03-15

### Added

- Initial public release on GitHub Packages.
- Clock-aligned recurring countdowns (`align_to_clock`, `align_interval`, `missed_run_policy`).
- Timezone support for fixed-date and recurring countdowns.
- Discord webhook integration.
- Firework shows on countdown start/end.
- GUI editor (`/countdown gui`).
- PlaceholderAPI integration.
- Teleport players on start/end.
- Dialog display type.

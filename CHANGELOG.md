# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

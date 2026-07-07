# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.0.2] - 2026-07-07

### Added

- **Placeholder formatted output regression tests** - added unit coverage in `EzCountdownPlaceholderExpansionUnitTest` to verify `%ezcountdown_<name>_formatted%` honors `display.time-format.hide-leading-zeros` for both enabled and disabled configurations.
- **Optional countdown start/end sounds** - countdowns can now define `sounds.start` and `sounds.end` with Bukkit `Sound` enum names in `countdowns.yml`.
- **GUI sound editors** - the in-game editor now includes dedicated actions to configure and validate start/end sounds, including `none` to disable either sound.

### Changed

- **Runtime start/end flow** - when a configured start/end sound is valid, EzCountdown now plays it to online players at countdown start/end alongside existing message, teleport, firework, and command actions.

## [2.0.1] - 2026-05-22

### Fixed

- **JitPack build compatibility** - corrected `jitpack.yml` Java selector to `openjdk21` and pinned JitPack install to `./mvnw -B -ntp -DskipTests install` so builds consistently use Java 21 when compiling against `paper-api 1.21.11`.
- **GitHub Actions version drift** - updated workflow actions to current major versions across CI/release publishing (`actions/checkout@v6`, `actions/setup-java@v5`, `actions/cache@v5`, `actions/upload-artifact@v7`).
- **Paper/Folia smoke test resolution for 26.x** - CI smoke jobs now resolve builds from `fill.papermc.io/v3` and correctly include MC `26.1` with Java 25 where required.

### Changed

- **Modrinth release step** - replaced the legacy `Kir-Antipov/mc-publish` action invocation in release workflow with a direct Modrinth v2 API upload path for clearer failure diagnostics and up-to-date version resolution.

## [2.0.0] - 2026-05-30

### Added

- **Folia support** - the plugin now runs on Folia (marked `folia-supported: true` in `plugin.yml`). Folia's `GlobalRegionScheduler` is used automatically when detected at runtime; Paper/Spigot continue to use the Bukkit scheduler.
- **`compat/` package hierarchy** - clean OOP abstraction layer:
  - `compat.platform.PlatformDetector` - detects Folia vs. Paper/Spigot at startup via `RegionizedServer` class presence.
  - `compat.scheduler.SchedulerAdapter` / `TaskHandle` - platform-agnostic scheduler interface (`runTask`, `runTaskTimer`, `runTaskLater`, `runTaskAsync`).
  - `compat.scheduler.BukkitSchedulerAdapter` - `SchedulerAdapter` backed by `BukkitScheduler` (Paper/Spigot).
  - `compat.scheduler.FoliaSchedulerAdapter` - `SchedulerAdapter` backed by `GlobalRegionScheduler` (Folia); only class-loaded when Folia is detected.
  - `compat.scheduler.SchedulerAdapterFactory` - chooses the correct adapter with graceful fallback if Folia classes are missing.
  - `compat.version.ServerVersionUtil` - canonical location for runtime Minecraft version detection (replaces `util.ServerVersionUtil`).
  - `compat.material.MaterialCompat` - canonical location for cross-version material resolution (replaces `util.MaterialCompat`).
- **Startup platform log** - `PluginBootstrap` now logs the active scheduler adapter class on enable (e.g. `Scheduler: BukkitSchedulerAdapter`).

### Changed

- `util.ServerVersionUtil` and `util.MaterialCompat` are now `@Deprecated` delegation stubs that forward to their canonical `compat.*` counterparts; they will be removed in a future release.
- All internal scheduler usages (`CountdownManager`, `FireworkShowManager`, `PatternScheduler`, `ChatInputListener`, all `listener.actions.*` GUI action classes, `SpigotIntegration`, `SpigotUpdateChecker`) now go through `SchedulerAdapter` instead of `Bukkit.getScheduler()` / `BukkitRunnable` directly.
- **Scoreboard API modernised** - `ScoreboardDisplay` now delegates to `compat.scoreboard.ScoreboardCompat`. On Paper 1.20.3+ (`Criteria.DUMMY` available) the modern `registerNewObjective(String, Criteria, Component)` overload is used; older builds fall back to the legacy string overload. The deprecated `Scoreboard.resetScores(String)` calls are replaced by objective unregister/re-register via `ScoreboardCompat.resetObjective`.
- **Title API modernised** - `TitleDisplay` now delegates to `compat.title.TitleCompat`. On Paper 1.18+ the Adventure `Player.showTitle(Title)` / `Player.clearTitle()` APIs are preferred over the deprecated `sendTitle(String...)` / `resetTitle()`. `TitleValidator` now also accepts `showTitle` as a valid title capability. A Spigot/legacy string fallback is retained.

- **Per-player notification targeting**  -  `NotificationBuilder.players(Collection<Player>)` restricts a notification to specific players. `EzCountdownApi.sendNotification(Notification, Collection<Player>)` provides an inline alternative without building a `Notification` first.
- **`Countdown.isVisibleTo(Player)`**  -  centralises per-player visibility logic (permission gate + target-player set) in one method; all display handlers now use it instead of duplicated inline checks.
- **`CountdownBuilder.targetPlayers(Collection<Player>)`**  -  restrict a persistent countdown's display to specific players.
- **Developer-friendly exception hierarchy**  -  new `com.skyblockexp.ezcountdown.api.exception` package:
  - `EzCountdownException`  -  base unchecked exception; catch this for all EzCountdown API errors.
  - `CountdownNotFoundException`  -  thrown when referencing a countdown name that does not exist; carries `getCountdownName()`.
  - `DuplicateCountdownException`  -  thrown when creating a countdown whose name already exists; carries `getCountdownName()`.
  - `InvalidConfigurationException`  -  thrown by `NotificationBuilder.build()` and future builder validators when configuration is invalid; replaces the generic `IllegalStateException`.
- **`api-version` bumped to `1.18`** in `plugin.yml`; minimum supported Minecraft version is Paper/Spigot 1.18. Dialog display continues to require Paper 1.21.7+.
- **Broadened Java compatibility** - release JARs now target Java 17 bytecode (previously Java 21), so the plugin runs on Paper 1.18 - 1.20.4 (Java 17) as well as Paper 1.20.5+ (Java 21). The `jdk21` Maven profile also targets Java 17 bytecode.
- **`ServerVersionUtil`** - new utility class (`com.skyblockexp.ezcountdown.util.ServerVersionUtil`) for runtime Minecraft version detection; enables future conditional feature gating without hard API dependencies.
- **Startup guard** - `onEnable` now logs the detected MC and Java version and disables the plugin with a clear error message if the server is too old (MC < 1.18 or Java < 17).

### Changed

- **All display handlers** (`ActionBarDisplay`, `TitleDisplay`, `ChatDisplay`, `BossBarDisplay`, `ScoreboardDisplay`, `DialogDisplay`) use `countdown.isVisibleTo(player)` instead of duplicated inline permission checks.
- **`NotificationBuilder.build()`** now throws `InvalidConfigurationException` (a subclass of `EzCountdownException`) instead of a plain `IllegalStateException`.
- **`ScoreboardDisplay`** catch blocks now include `NoSuchMethodError` so the scoreboard falls back to chat if the String-criteria `registerNewObjective` overload is ever removed in a future Paper build.
- Project version bumped from `1.4.3` to `2.0.0`.

### Fixed

- **DURATION countdown resets to full duration on `/countdown reload`**  -  `resumeRunningCountdowns()` previously called `handler.onStart()`, which always sets `targetInstant` to `now + fullDuration`, discarding the `target_epoch` saved in storage. It now calls `handler.ensureTarget()` instead, which is a no-op when a target is already present. The same guard was added to the legacy fallback path for handler-less countdown types. End-commands that fired once before a reload will no longer fire again unexpectedly due to the countdown silently restarting.

## [1.4.2] - 2026-05-16

### Added

- **Configurable time format**  -  new `display.time-format` section in `config.yml`.
  - `pattern` (default `"{days}d {hours}h {minutes}m {seconds}s"`)  -  customize the token layout used for the `{formatted}` placeholder and `%ezcountdown_<name>_formatted%` PAPI expansion.
  - `hide-leading-zeros` (default `true`)  -  when enabled, leading space-delimited segments whose unit value is zero are suppressed. For example, `0d 0h 5m 3s` is displayed as `5m 3s`.
  - Applies to countdown display messages, discord webhook `{time_left}`, PlaceholderAPI, and the GUI preview action.
  - Hot-reloads with `/countdown reload`.

## [1.4.1] - 2026-05-16

### Fixed

- **Bossbar color/style ignored** - `display.bossbar.color` and `display.bossbar.style` in `countdowns.yml` had no effect; the boss bar always rendered in the default blue/solid style. All four countdown-type handlers (`DurationHandler`, `FixedDateHandler`, `RecurringHandler`, `ManualHandler`) now read these fields from config and pass them to the `Countdown` constructor.

## [1.4.0] - 2026-05-11

### Added

- **Notification API**  -  `EzCountdownApi.sendNotification(Notification)` fires a one-shot ephemeral timed display from any plugin without creating a persistent countdown or touching `countdowns.yml`.
- **`Notification` model**  -  lightweight immutable value object with factory methods `Notification.ofSeconds(long)`, `Notification.of(Duration)`, and a fluent `NotificationBuilder` (display types, format message, start/end messages).
- **`NotificationBuilder`**  -  fluent builder for `Notification`; all fields have sensible defaults (`ACTION_BAR` display, `{formatted}` message); `build()` validates duration > 0.
- **Ephemeral countdown support**  -  `Countdown.isEphemeral()` / `CountdownBuilder.ephemeral(boolean)`; ephemeral countdowns are removed from memory automatically when they end and are never written to storage.

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

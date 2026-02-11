# EzCountdown
[![CI](https://github.com/ez-plugins/EzCountdown/actions/workflows/ci.yml/badge.svg)](https://github.com/ez-plugins/EzCountdown/actions/workflows/ci.yml) [![Release](https://img.shields.io/github/v/release/ez-plugins/EzCountdown)](https://github.com/ez-plugins/EzCountdown/releases) [![License](https://img.shields.io/github/license/ez-plugins/EzCountdown)](https://github.com/ez-plugins/EzCountdown/blob/main/LICENSE) [![Issues](https://img.shields.io/github/issues/ez-plugins/EzCountdown)](https://github.com/ez-plugins/EzCountdown/issues)

EzCountdown provides configurable countdown timers for events, launches, and maintenance windows across your server.

## Features
- Fixed date, duration, recurring, and manual countdown modes.
- Display options: action bar, boss bar (1.9+), chat, title, and scoreboard.
- PlaceholderAPI support for `%ezcountdown_<name>_days%`, `_hours`, `_minutes`, `_seconds`, and `_formatted`.
- Customizable messages (messages.yml), permissions, and update intervals.
- Run console commands when countdowns finish.

## Commands
See [docs/commands.md](docs/commands.md) for the full command list and usage.

## Countdown Types
- **Fixed date**: Runs toward a specific date/time (e.g. `2026-01-01 00:00`).
- **Duration**: Runs for a set amount of time starting immediately (or on create if enabled).
- **Manual**: Uses a duration but stays stopped until `/countdown start` is run.
- **Recurring**: Repeats on the same month/day/time every year.

## Permissions
See [docs/permissions.md](docs/permissions.md) for permission details.

## Configuration
See [docs/configuration.md](docs/configuration.md) for configuration details and examples.

### Clock-aligned recurring schedules

You can configure recurring countdowns to align to real-world clock boundaries and specify a timezone. Example keys in `countdowns.yml`:

```
# every 2 hours on the UTC clock: 00:00, 02:00, 04:00...
my_recurring_countdown:
	type: RECURRING
	align_to_clock: true
	align_interval: "2h"
	timezone: "UTC"
	missed_run_policy: SKIP
```

Behavior:
- `align_to_clock` (default: `false`) enables clock alignment.
- `align_interval` accepts duration strings like `2h`, `1d`, `30m`.
- `timezone` is an IANA ZoneId (e.g. `Europe/London`). If omitted the plugin default zone is used.
- `missed_run_policy` controls what happens if the server was down for one or more scheduled occurrences (defaults to `SKIP`).

See the full timezone reference for recommended IANA identifiers: [docs/feature/timezones.md](docs/feature/timezones.md)

## Documentation

Comprehensive documentation is available in the `docs/` folder. Quick links:

- **Commands & Configuration**
	- [Commands](docs/commands.md)
	- [Configuration](docs/configuration.md)
	- [Permissions](docs/permissions.md)

- **Features**
	- [Countdown types](docs/feature/countdown_types.md)
	- [Teleport integration](docs/feature/teleport.md)
	- [Placeholder support](docs/feature/placeholders.md)
	- [Discord integration](docs/feature/discord_integration.md)
	- [GUI editor](docs/feature/gui.md)
	- [Firework shows](docs/feature/firework_shows.md)
	- Display-specific docs:
		- [Action bar](docs/feature/display/action_bar.md)
		- [Boss bar](docs/feature/display/boss_bar.md)
		- [Chat](docs/feature/display/chat.md)
		- [Title](docs/feature/display/title.md)
		- [Scoreboard](docs/feature/display/scoreboard.md)

- **API**
	- [API overview](docs/api/README.md)
	- [EzCountdown API](docs/api/EzCountdownApi.md)
	- [Model: Countdown](docs/api/model/Countdown.md)
	- [Model: CountdownType](docs/api/model/CountdownType.md)
	- [Events: CountdownStartEvent](docs/api/event/CountdownStartEvent.md)
	- [Events: CountdownTickEvent](docs/api/event/CountdownTickEvent.md)
	- [Events: CountdownEndEvent](docs/api/event/CountdownEndEvent.md)

## Example
```
/countdown create new_year 2026-01-01 00:00
```

## Teleporting Players on Countdown Start/End

You can configure a countdown to teleport all online players to a named location at the start or end:

```
countdowns:
	my_event:
		...
		teleport:
			start: spawn
			end: arena
```
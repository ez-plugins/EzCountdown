# EzCountdown

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

Locations are managed with `/ezcd location add <name>` (saves your current position) and `/ezcd location delete <name>`.
`/countdown create maintenance duration 2h`
```

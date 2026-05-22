---
title: Commands
parent: Server Owners
nav_order: 1
---

# Commands

All commands use the `/countdown` base (aliases: `/ezcountdown`, `/ezcd`).

## Command reference

### `/countdown create`

Create a new countdown. The command syntax depends on the type:

| Type | Syntax | Example |
|---|---|---|
| Fixed date | `/countdown create <name> <YYYY-MM-DD> <HH:mm>` | `/countdown create new_year 2026-01-01 00:00` |
| Duration | `/countdown create <name> duration <time>` | `/countdown create maintenance duration 2h` |
| Manual | `/countdown create <name> manual <time>` | `/countdown create launch manual 30m` |
| Recurring (yearly) | `/countdown create <name> recurring <month> <day> <HH:mm>` | `/countdown create xmas recurring 12 25 00:00` |

Duration values accept `h` (hours), `m` (minutes), `s` (seconds), and combinations like `1h30m`.

**Clock-aligned recurring flags** (append to `create recurring`):

| Flag | Description | Example value |
|---|---|---|
| `--align-to-clock` | Enable clock-aligned repeats | _(no value)_ |
| `--align-interval <value>` | Alignment interval | `2h`, `1d`, `30m` |
| `--timezone <ZoneId>` | IANA timezone | `UTC`, `Europe/London` |
| `--missed-run-policy <policy>` | Handle missed runs | `SKIP`, `RUN_SINGLE`, `RUN_ALL` |

Example  -  announce every 2 hours, UTC:

```
/countdown create announce recurring 0 0 00:00 --align-to-clock --align-interval 2h --timezone UTC --missed-run-policy SKIP
```

---

### `/countdown start` / `/countdown stop`

```
/countdown start <name>
/countdown stop <name>
```

Start or stop a countdown by name. `duration` countdowns start automatically when created; use `manual` if you want to control the start time.

---

### `/countdown delete`

```
/countdown delete <name>
```

Remove a countdown permanently from `countdowns.yml`. This cannot be undone; use `/countdown stop` if you only want to pause it.

---

### `/countdown list`

```
/countdown list
```

Show all configured countdowns and whether they are currently running.

---

### `/countdown info`

```
/countdown info <name>
```

Show full details for a countdown: type, target/duration, display types, enabled commands, and current state.

---

### `/countdown reload`

```
/countdown reload
```

Reload `config.yml`, `messages.yml`, and `countdowns.yml` without restarting the server. Running countdowns resume from where they left off.

---

### `/countdown gui`

```
/countdown gui
/countdown gui <name>
```

Open the in-game visual editor. With no argument, shows a list of all countdowns. Pass a name to edit that countdown directly. Requires `ezcountdown.admin`.

---

### Location management

```
/ezcd location add <name>
/ezcd location delete <name>
```

Save or remove a named teleport location. Use `location add` while standing at the target location; the plugin records your position, world, and facing direction. Named locations are referenced in `countdowns.yml` under `teleport.start` and `teleport.end`.

Requires `ezcountdown.location.add` / `ezcountdown.location.delete`.

---

## Permissions summary

| Node | Default | What it allows |
|---|---|---|
| `ezcountdown.use` | `true` (all players) | `/countdown list` and `/countdown info` |
| `ezcountdown.admin` | `op` | Create, start, stop, delete, reload, GUI |
| `ezcountdown.location.add` | `op` | Save named teleport locations |
| `ezcountdown.location.delete` | `op` | Remove named teleport locations |

See [Permissions](permissions) for recommended group assignments.

---

## Tips

- After creating a countdown, edit `plugins/EzCountdown/countdowns.yml` to configure display types, custom messages, end commands, and teleport behaviour  -  then run `/countdown reload`.
- Test `commands_on_end` on a staging server first; they run as console with full permissions.
- Use `manual` type for countdown events you want to arm in advance and trigger at the right moment.


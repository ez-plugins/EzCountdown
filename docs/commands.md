

# Commands (Quick Reference for Server Owners)

This page lists the most important commands staff will use to manage countdowns. Keep these in a staff guide or paste them into your control panel.

Core commands

```
/countdown create <name> <date|duration|manual|recurring>
/countdown start <name>
/countdown stop <name>
/countdown delete <name>
/countdown list
/countdown info <name>
/countdown reload
```

 - `create` - Create a countdown. Types:
  - `date` - Fixed target: `YYYY-MM-DD HH:mm` (example: `2026-01-01 00:00`).
  - `duration` - Run for a duration (examples: `2h`, `30m`, `1h30m`). Starts immediately unless defaults say otherwise.
  - `manual` - Duration-based but stays stopped until `start`.
  - `recurring` - Yearly event: provide `month day time` (e.g. `12 31 23:59`).

 - `start` / `stop` - Start or stop a running countdown by name.
 - `delete` - Remove a countdown from `countdowns.yml` (use `reload` to apply).
 - `list` - Show all configured countdowns and whether they are running.
 - `info` - Show details for a specific countdown (type, target, displays, commands).
 - `reload` - Reloads `config.yml`, `messages.yml`, and `countdowns.yml` without restarting the server.

Location management

```
/ezcd location add <name>
/ezcd location delete <name>
```

 - `location add` saves the executor's current position to `locations.yml` under the given name.
 - `location delete` removes a named location.
 - Permissions: `ezcountdown.location.add` and `ezcountdown.location.delete`.

Examples

```
/countdown create new_year 2026-01-01 00:00
/countdown create maintenance duration 2h
/countdown create festival recurring 12 31 23:59
/ezcd location add spawn
```

Practical tips for server owners

- After creating a countdown you will often want to edit `countdowns.yml` to configure displays, custom messages, end commands, and teleport behaviour.
- Use `reload` after editing YAML files to apply changes immediately.
- Keep a short list of commonly used countdown names (for holidays or events) so staff can quickly create them.
 - Test `commands_on_end` in a non-production environment first - commands run as console.

Permissions summary

 - `ezcountdown.admin` - Full management (create, start, stop, delete, reload).
 - `ezcountdown.use` - View/list countdowns and run `info`.
 - `ezcountdown.location.add` / `ezcountdown.location.delete` - Manage named teleport locations.

If you'd like, I can add a one-line `staff-help.txt` snippet you can paste into your control panel or staff Discord.


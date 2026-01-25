# Teleporting Players

You can configure EzCountdown to teleport players to named locations when a countdown starts or ends. Locations are stored in `locations.yml` and can be managed in-game.

Config example (inside a countdown entry in `countdowns.yml`):

```
countdowns:
  my_event:
    type: date
    date: "2026-07-01 18:00"
    teleport:
      start: spawn
      end: arena
```

Managing locations

- Add a location using `/ezcd location add <name>` while standing at the desired coordinates. Requires `ezcountdown.location.add`.
- Remove with `/ezcd location delete <name>`. Requires `ezcountdown.location.delete`.

Permissions

- `ezcountdown.location.add` — allow adding locations.
- `ezcountdown.location.delete` — allow deleting locations.

Notes

- Teleport commands act on all online players (subject to visibility permissions configured per countdown).
- Be careful when teleporting players on countdown end/start — ensure safe locations and test in a controlled environment.

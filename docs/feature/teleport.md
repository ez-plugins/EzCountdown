# Teleporting Players

EzCountdown can teleport players to named locations when a countdown starts or ends. Locations are stored in `locations.yml` and are referenced by name from a countdown configuration.

How it works
- The plugin looks for `teleport.start` and `teleport.end` in each countdown's section. When the event fires, every online player is teleported to the named location (subject to visibility permission checks).

Config example (inside a countdown entry in `countdowns.yml`):

```yaml
countdowns:
  my_event:
    type: FIXED_DATE
    target: "2026-07-01 18:00"
    teleport:
      start: spawn    # teleport players to location "spawn" when the countdown starts
      end: arena      # teleport players to location "arena" when the countdown ends
```

Managing locations
- Add a location in-game while standing where you want to teleport players: `/ezcd location add <name>` (permission `ezcountdown.location.add`).
- Remove a location: `/ezcd location delete <name>` (permission `ezcountdown.location.delete`).
- Locations are persisted to `locations.yml` in the plugin data folder.

Permissions
 - `ezcountdown.location.add` - allow adding locations.
 - `ezcountdown.location.delete` - allow deleting locations.

Safety and troubleshooting
 - Teleport actions affect all online players - test location safety (no lava, void, or blocked spawns) in a staging environment first.
- If teleports don't happen, check that the named location exists in `locations.yml` and that the plugin has permission to move players (no other plugin blocking teleports).
- If only some players are teleported, verify per-countdown `display.visibility`/permissions and that players do not have bypassing protection from other plugins.

Tip
- Use short, descriptive names for locations (e.g., `spawn`, `arena`, `lobby`) so configuration and troubleshooting are easier.

---
title: Permissions
parent: Server Owners
nav_order: 3
---

# Permissions

Use a permissions plugin (LuckPerms recommended) to assign these nodes to groups rather than individuals.

## Permission nodes

| Node | Default | Description |
|---|---|---|
| `ezcountdown.use` | `true` | View countdowns: `/countdown list`, `/countdown info` |
| `ezcountdown.admin` | `op` | Full management: create, start, stop, delete, reload, GUI |
| `ezcountdown.location.add` | `op` | Add named teleport locations with `/ezcd location add` |
| `ezcountdown.location.delete` | `op` | Remove named teleport locations with `/ezcd location delete` |

## Recommended group assignments

| Group | Suggested nodes |
|---|---|
| Admins / Server Managers | `ezcountdown.admin` (includes all operations) |
| Moderators / Trusted Staff | `ezcountdown.use` + `ezcountdown.location.add` if they manage spawn/teleports |
| Regular players | `ezcountdown.use` (granted by default) |

## LuckPerms example

```
# Grant admin to your admin group
lp group admin permission set ezcountdown.admin true

# Grant view-only to moderators
lp group moderator permission set ezcountdown.use true

# Grant location management to a dedicated events group
lp group events permission set ezcountdown.location.add true
lp group events permission set ezcountdown.location.delete true
```

## Security notes

- `ezcountdown.admin` allows editing countdowns and running `commands_on_end` entries as console  -  do not grant this to untrusted groups.
- Keep `commands_on_end` entries in `countdowns.yml` to a minimum and audit them regularly if staff members have write access to plugin configuration files.
- Use groups and inheritance in your permissions plugin rather than assigning nodes directly to players.


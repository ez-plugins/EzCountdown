

# Permissions (Who should have which node)

Use a permissions plugin (LuckPerms recommended) to assign these nodes to groups rather than individuals.

 Core nodes
 
 - `ezcountdown.use` - View countdowns and run informational commands (`/countdown list`, `/countdown info`).
 - `ezcountdown.admin` - Full management: create, start, stop, delete, and `reload`.
 - `ezcountdown.location.add` - Add a named teleport location with `/ezcd location add`.
 - `ezcountdown.location.delete` - Remove a named teleport location with `/ezcd location delete`.

Recommended assignment

- Admins/Managers: `ezcountdown.admin`.
- Moderators/Trusted Staff: `ezcountdown.use` (+ `location.*` if they manage spawn/teleports).
- Regular players: no permissions by default unless you want players to query countdowns.

Security notes for server owners

- `ezcountdown.admin` allows editing and running end-of-countdown console commands - do not grant this to the general staff group.
- Use groups and inheritance in your permissions plugin to avoid mistakes.
- Audit `countdowns.yml` regularly if staff can edit files directly; consider limiting filesystem access to only a few trusted admins.

Want me to output a sample LuckPerms commands snippet to create groups and assign these nodes? I can add that next.


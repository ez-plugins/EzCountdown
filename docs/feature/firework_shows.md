# Firework Shows

EzCountdown can trigger firework displays when a countdown starts or ends. Firework shows are useful for celebrations but should be configured carefully to avoid server lag or world damage.

Configuration

 - The plugin supports configuring firework shows per-countdown. Place the configuration under a `firework:` (or `fireworks:`) section inside a countdown entry in `countdowns.yml` - see shipped examples for the exact structure.
- Alternatively, you can execute console commands via `commands.end` to launch fireworks using other plugins or custom scripts.

Safety & performance

 - Fireworks spawn entities and can generate lag if many are launched at once - prefer moderate counts and spread effects across time where possible.
- Test shows in a separate world to confirm visuals and to avoid accidental damage to important builds.

Troubleshooting

- If no fireworks appear, check server logs for errors and ensure the plugin has permission to spawn entities and that no other plugin cancels the spawn event.
- If fireworks cause lag, reduce the number of rockets or complexity of effects.

Configuration example

Below is a minimal example showing how to configure a firework show for both the `start` and `end` phases of a countdown. The plugin reads these keys from `countdowns.yml` under `countdowns.<name>.firework.<phase>`.

```yaml
countdowns:
	festival:
		type: FIXED_DATE
		target: "2026-12-31 23:50"
		firework:
			start:
				location: spawn         # named location defined in locations.yml
				color: RED              # Color names map to Bukkit Color constants (e.g. WHITE, RED, BLUE)
				power: 1                # firework power (flight)
				count: 8                # fireworks per row
				rows: 2                 # number of concentric rows
				interval: 10            # ticks between rows
			end:
				location: spawn
				color: GOLD
				power: 2
				count: 12
				rows: 3
				interval: 6
```

Notes on fields

 - `location` - a name from `locations.yml` (create with `/ezcd location add <name>`).
 - `color` - matches `org.bukkit.Color` field names (case-insensitive). Unknown names default to white.
 - `power` - firework power (higher = farther flight).
 - `count` - number of fireworks spawned per row.
 - `rows` - concentric rows to spawn (visual density).
 - `interval` - delay in ticks between rows (20 ticks = 1 second).

Best practices

- Keep `count` and `rows` modest on large servers to avoid entity/CPU spikes.
- Test shows in an isolated world and verify `locations.yml` entries exist and point to safe coordinates.


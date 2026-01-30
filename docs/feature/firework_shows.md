# Firework Shows

EzCountdown can trigger firework displays when a countdown `start` or `end` phase runs. Firework shows are configurable per-countdown in `countdowns.yml`. Use caution—spawning many rockets may impact server performance.

Configuration location

- Place firework settings under `countdowns.<name>.firework.<phase>` (the plugin accepts `firework` and `fireworks` keys). Phases commonly used are `start` and `end`.

Legacy (simple) schema

The plugin historically supported a simple scalar schema. This is still supported as a fallback:

```yaml
countdowns:
	festival:
		type: FIXED_DATE
		target: "2026-12-31 23:50"
		firework:
			start:
				location: spawn
				color: RED
				power: 1
				count: 8
				rows: 2
				interval: 10
			end:
				location: spawn
				color: GOLD
				power: 2
				count: 12
				rows: 3
				interval: 6
```

Advanced schema (recommended)

To define richer shows you can use an `effects` list at the phase level. Each effect supports per-effect `type`, `colors` (main), `fade` (fade colors), `flicker`, `trail`, `power`, `count`, `interval`, `pattern`, and `offset`.

Example advanced phase using multiple effects (backward-compatible):

```yaml
countdowns:
	festival:
		type: FIXED_DATE
		target: "2026-12-31 23:50"
		firework:
			start:
				location: spawn
				# phase-level defaults (optional)
				count: 12
				rows: 2
				interval: 10
				effects:
					- type: BALL
						colors: [RED, "#FFD700"]   # main and additional colors (named or hex)
						fade: ["#FFFFFF"]
						flicker: true
						trail: true
						power: 1
						pattern: circle
						count: 8
						interval: 6
						offset:
							x: 0
							y: 1
							z: 0
					- type: BURST
						colors: ["#00AEEF", WHITE]
						fade: [WHITE]
						flicker: false
						trail: true
						power: 2
						pattern: cone
						count: 6
						interval: 12
						offset:
							x: 0
							y: 2
							z: 0
			end:
				location: arena
				color: BLUE    # legacy single-effect fallback still supported
				power: 2
				count: 24
				rows: 3
				interval: 6
```

Field reference

- `location` (required): Name defined in `locations.yml` (create via `/ezcd location add <name>`).
- `effects`: Optional list of effect maps. If present, each item may include:
	- `type`: `BALL`, `BALL_LARGE`, `STAR`, `BURST`, `CREEPER`, etc. (matches `FireworkEffect.Type`).
	- `colors`: array or single value; named `org.bukkit.Color` constants (case-insensitive) or hex `#RRGGBB` strings.
	- `fade`: array or single value for fade colors.
	- `flicker`: boolean.
	- `trail`: boolean.
	- `power`: integer flight power for this effect.
	- `count`: how many rockets to spawn for this effect (per-row/phase defaults may apply).
	- `interval`: ticks between spawns for this effect.
	- `pattern`: `circle`, `cone`, `random` (controls spawn positioning behavior).
	- `offset`: optional `{x,y,z}` applied to spawn location.
- Legacy keys: `color`, `power`, `count`, `rows`, `interval` are still accepted and are translated into a single `effects` entry by the plugin.

Color notes

- Colors may be specified by name (e.g., `RED`, `WHITE`, `GOLD`) or hex `#RRGGBB`. Unknown values fallback to white and will log a warning.

Performance & safety

- Fireworks are entities—excessive `count` / `rows` / large concurrent effects can cause lag. The plugin enforces sane caps; if a configured value exceeds limits it will be clamped and a warning will be logged.
- Prefer spreading effects using `interval` ticks and using modest `count`/`rows` on production servers.
- Validate `locations.yml` entries exist and point to safe coordinates. Test shows in a development world first.

Troubleshooting

- If shows do not appear: ensure `locations.yml` contains the named location and the world is loaded; check server logs for parse errors.
- If colors appear wrong: verify you used a named color (case-insensitive) or a `#RRGGBB` hex string.

Migration tip

- Existing countdowns using the legacy simple keys will continue to work. To migrate to the advanced format, replace the legacy keys with an `effects` list (see example). The plugin will also synthesize an `effects` entry at runtime when only legacy keys are present.


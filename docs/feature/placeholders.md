# Placeholders (PlaceholderAPI)

When PlaceholderAPI is installed, EzCountdown registers placeholders for every configured countdown so you can use countdown values in other plugins, scoreboards, or messages.

Available placeholders (replace `<name>` with your countdown's name):

- `%ezcountdown_<name>_days%`
- `%ezcountdown_<name>_hours%`
- `%ezcountdown_<name>_minutes%`
- `%ezcountdown_<name>_seconds%`
 - `%ezcountdown_<name>_formatted%` - combined, human-friendly format (e.g. "1d 2h 3m 4s").

Usage examples

- In a message or scoreboard: `&6Starts in %ezcountdown_new_year_formatted%`
- In a scoreboard plugin config you can set the title or lines using `%ezcountdown_<name>_formatted%`.

Behaviour

 - Placeholders are registered automatically when PlaceholderAPI is detected - no extra setup required beyond installing PlaceholderAPI.
- Placeholders update according to the plugin's configured update interval; if you need faster updates, adjust `updateIntervalSeconds` in your defaults or per-countdown settings.

Troubleshooting

- If a placeholder returns nothing or stale data, ensure PlaceholderAPI is installed and up-to-date, and that the countdown has a valid `target` or `duration`.
- If formatting seems wrong, verify your `messages.format` and that `use-minimessage` (in `messages.yml`) is set appropriately for MiniMessage output.

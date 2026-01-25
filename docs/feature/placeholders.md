# Placeholders

If PlaceholderAPI is installed on the server, EzCountdown registers placeholders for each countdown name. These allow you to embed countdown values in other plugins, scoreboards, messages, and more.

Available placeholders (replace `<name>` with your countdown's name):

- `%ezcountdown_<name>_days%`
- `%ezcountdown_<name>_hours%`
- `%ezcountdown_<name>_minutes%`
- `%ezcountdown_<name>_seconds%`
- `%ezcountdown_<name>_formatted%` — human-friendly combined format (e.g. "1d 2h 3m 4s").

Usage

- In other plugin messages or scoreboards that support PlaceholderAPI, use the placeholders above. For example: `&6Starts in %ezcountdown_new_year_formatted%`.
- The plugin attempts to register placeholders automatically when PlaceholderAPI is present. No additional setup is required beyond installing PlaceholderAPI.

# GUI

EzCountdown provides an in-game GUI for managing countdowns, displays, and command actions. The GUI is accessible to players with the appropriate permissions.

Opening the GUI

- Use the command `/countdown gui` (requires the `commands.list` permission as configured in `config.yml`).

Features

- `MainGui` — Overview of countdowns and quick actions.
- `DisplayEditor` — Configure which display types are used and customize display settings.
- `CommandsEditor` — Add or edit console commands executed when a countdown ends.
- `EditorMenu` — Navigation and helper utilities for editing countdowns.

Notes

- The GUI hooks into the plugin's event listeners; ensure the player has `ezcountdown.admin` or appropriate permissions to make changes.
- GUI changes are typically saved back to `countdowns.yml` when confirmed.

# GUI

EzCountdown provides an in-game GUI for managing countdowns, displays, and end commands. The GUI is helpful for server admins who prefer a visual editor over editing YAML by hand.

Opening the GUI

 - Use `/countdown gui` (player-only) - the permission to view/use the GUI is controlled via your permissions configuration (typically `ezcountdown.admin` or `commands.list` depending on setup).

 What you can do in the GUI
 
 - `Main` - see a list of configured countdowns, their running state, and quick start/stop/delete actions.
 - `Display Editor` - pick which `display.types` will show for a countdown and edit per-display settings.
 - `Messages` - edit `messages.format`, `messages.start`, and `messages.end` templates with live preview.
 - `Commands Editor` - add, remove, or reorder `commands.end` to run console commands when a countdown completes.

Behaviour & saving

- Changes made in the GUI are applied immediately to the in-memory countdown object and saved back to `countdowns.yml` when you confirm or close the editor (so they persist across restarts).
- If you prefer to edit files directly, the GUI is still useful for validating values and avoiding YAML syntax mistakes.

Troubleshooting

- If the GUI opens but actions fail, check server logs for permission or serialization errors and verify the player has the required admin permission.
- If GUI edits don't persist after a restart, confirm the plugin could write `countdowns.yml` (file permissions) and that no other process overwrote the file.

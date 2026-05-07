---
title: Dialog
parent: Display Types
grand_parent: Features
nav_order: 6
---

# Dialog

What it is

Shows countdown information as a Paper Dialog - a native GUI panel that appears in the centre of the screen. The panel displays the countdown name as a title and the formatted countdown message as body text, with a **Close** button players can use to dismiss it.

When to use

- Use for servers running Paper 1.21.7+ where a clean, on-screen panel is preferred over hotbar or chat messages.
- Good for infrequently-changing countdowns (e.g. a 24-hour event timer) because the dialog stays open until it is dismissed or the countdown ends - it does not flash or re-open on every tick.

How to enable

Add `DIALOG` to `display.types` for a countdown:

```yaml
countdowns:
  event:
    type: FIXED_DATE
    target: "2026-01-01 00:00"
    display:
      types:
        - DIALOG
```

Compatibility & requirements

- Requires **Paper 1.21.7+**. The plugin checks for `io.papermc.paper.dialog.Dialog` and `net.kyori.adventure.dialog.DialogLike` at startup using reflection.
- If either class is missing the display type is skipped and a warning is logged:
  ```
  EzCountdown: dialog display disabled: Dialog display requires Paper 1.21.7+ with the Dialog API (io.papermc.paper.dialog.Dialog not found).
  ```
- Does **not** work on Spigot or CraftBukkit.

No-flicker behaviour

The handler tracks the last message sent to each player per countdown. A new dialog packet is only sent when the displayed text changes - the existing dialog is left on screen between ticks so there is no visual flicker.

When the countdown ends the dialog is automatically closed for all players who had it open.

Config override

Set `force-enable.dialog: true` in `config.yml` to bypass the runtime API check:

```yaml
display-overrides:
  force-enable:
    dialog: true
```

Only use this if you are certain your server has the Dialog API and the check is producing a false negative.

Troubleshooting

- If the dialog does not appear, confirm you are running Paper 1.21.7 or newer and check the startup log for the warning message above.
- If only some players see the dialog, check the countdown's `display.visibility` permission - players who do not hold the permission will not receive the dialog.
- Dialogs opened by this display are closed when the countdown ends or the plugin reloads. If a dialog appears stuck after a reload, the player can dismiss it using the **Close** button or the Escape key.

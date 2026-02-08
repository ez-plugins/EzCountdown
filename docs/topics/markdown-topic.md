# EzCountdown

**EzCountdown** is the ultimate custom countdown timer plugin for Minecraft servers. Whether you’re launching a new map, running a flash sale, or celebrating a special event, EzCountdown lets you create, manage, and display any countdown you want, your way.

**Compatible with Spigot, Paper, and Bukkit 1.7–1.21 · Java 8+ · Action bar, boss bar, title, chat & scoreboard displays · PlaceholderAPI ready**

---

## Why EzCountdown?

- **Create any countdown** – Set up timers for launches, events, sales, or anything you can imagine. Fixed dates, durations, recurring, or manual, your choice.
- **Flexible displays** – Show countdowns in the action bar, boss bar, title, chat, or scoreboard. Make your timers visible everywhere players look.
 - **Easy configuration** – Define countdowns in YAML, customize messages, and reload instantly - no coding required.
 - **Easy configuration** – Define countdowns in YAML, customize messages, and reload instantly - no coding required.
 - **Translation variables** – You can keep message text in `messages.yml` and reference it from `countdowns.yml` with the `{translate:key.path}` token. This lets you centralize translations and makes resetting `countdowns.yml` safe because messages are pulled from `messages.yml` at runtime.
- **Permission control** – Limit who can see or manage each countdown.
- **PlaceholderAPI support** – Use countdown placeholders in any plugin or message.

<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/r12mMkx4F9U" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>

---

## Example: Custom Countdown

Want a countdown for a special event? Just add it to `countdowns.yml`:

````yaml
countdowns:
  event_launch:
    type: FIXED_DATE
    target: "2026-02-01 18:00"
    running: true

    display:
      types:
        - ACTION_BAR
        - TITLE
      update-interval: 1
      visibility: "all"

    messages:
      format: "<gold>Event Launch</gold> <gray>in</gray> <aqua>{formatted}</aqua>"
      start: "<green>The event countdown has started!</green>"
      end: "<gold>🎉 The event has begun! </gold>"

    commands:
      end:
        - "broadcast <gold>The event is live!</gold>"

    zone: "UTC"
````

**Tip:** Use `/countdown create <name> <date|duration|manual|recurring>` to make new countdowns in-game!

![New year countdown](https://i.ibb.co/7tg3Tgz9/image.png)

---

## Quick Start

1. Drop `EzCountdown.jar` into your `plugins/` folder and start the server.
2. Edit `plugins/EzCountdown/countdowns.yml` to define your custom countdowns.
3. Use `/countdown list` to see active timers.
4. Reload changes instantly with `/countdown reload`.

---

## Commands & Permissions

| Command | Description | Permission |
| --- | --- | --- |
| `/countdown create <name> <date\|duration\|manual\|recurring>` | Create a countdown. | `ezcountdown.admin` |
| `/countdown start <name>` | Start a countdown. | `ezcountdown.admin` |
| `/countdown stop <name>` | Stop a countdown. | `ezcountdown.admin` |
| `/countdown delete <name>` | Delete a countdown. | `ezcountdown.admin` |
| `/countdown list` | List countdowns. | `ezcountdown.use` |
| `/countdown info <name>` | Show countdown details. | `ezcountdown.use` |
| `/countdown reload` | Reload configuration files. | `ezcountdown.admin` |

---

## Countdown Types

- **Fixed Date** – Counts down to a specific date/time.  
  Example: `/countdown create launch 2026-02-01 18:00`
- **Duration** – Runs for a set time (e.g., 2h).  
  Example: `/countdown create sale duration 2h`
- **Manual** – Timer starts only when triggered.  
  Example: `/countdown create flashsale manual 15m`
- **Recurring** – Repeats at set intervals (e.g., every week).  
- **Recurring** – Repeats yearly on the same month/day/time.  
  Example: `/countdown create festival recurring 12 31 23:59`

---

## Display Types

- **ACTION_BAR** – Subtle, always-on timer above the hotbar.
- **BOSS_BAR** – Dramatic bar at the top of the screen.
- **TITLE** – Large center-screen titles for big moments.
- **CHAT** – Periodic chat messages.
- **SCOREBOARD** – Sidebar timer for ongoing events.

**Mix and match display types per countdown!**

---

## Advanced Features

- **Teleport players** at countdown start/end using named locations.
- **Firework shows** for celebration moments.
- **Custom messages and commands** for every countdown event.
- **PlaceholderAPI** – Use `%ezcountdown_<name>_formatted%` and more in any plugin.

---

## Locations & Fireworks

- Set teleport locations: `/ezcd locations add <name>`
- Configure fireworks and teleports in `countdowns.yml` under `teleport:` and `firework:` sections.

---

## PlaceholderAPI

Use these placeholders anywhere PlaceholderAPI is supported:

- `%ezcountdown_<name>_formatted%`
- `%ezcountdown_<name>_days%`
- `%ezcountdown_<name>_hours%`
- `%ezcountdown_<name>_minutes%`
- `%ezcountdown_<name>_seconds%`

---

**Make your next event legendary, create custom countdowns for anything, anytime!**

[![Try the other Minecraft plugins in the EzPlugins series](https://i.ibb.co/PzfjNjh0/ezplugins-try-other-plugins.png)](https://modrinth.com/collection/Q98Ov6dA)
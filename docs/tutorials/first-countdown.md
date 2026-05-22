---
title: Your First Countdown
parent: Tutorials
nav_order: 1
---

# Tutorial: Your First Countdown

In this tutorial you will create a fixed-date countdown that counts down to New Year 2027 and displays on the action bar and boss bar.

**Time to complete:** about 5 minutes.

---

## Step 1  -  Install the plugin

1. Download `EzCountdown.jar` from [GitHub Releases](https://github.com/ez-plugins/EzCountdown/releases).
2. Place the jar in your server's `plugins/` folder.
3. Start (or restart) the server. You will see a message in the console confirming the plugin loaded.
4. Check that the data folder was created:

   ```
   plugins/EzCountdown/
   ├── config.yml
   ├── countdowns.yml
   ├── messages.yml
   └── locations.yml
   ```

---

## Step 2  -  Create the countdown

In-game (as an operator), run:

```
/countdown create new_year 2027-01-01 00:00
```

The countdown is created and starts immediately. You should see a brief action bar message.

> **Tip:** names must be unique and contain no spaces. Use underscores (`_`) to separate words.

---

## Step 3  -  Check it works

```
/countdown info new_year
```

The output shows the type (`FIXED_DATE`), the target (`2027-01-01 00:00`), and the running state.

```
/countdown list
```

The countdown should appear as running.

---

## Step 4  -  Customise the display

Open `plugins/EzCountdown/countdowns.yml` in a text editor. You will see an entry similar to:

```yaml
countdowns:
  new_year:
    type: FIXED_DATE
    target: "2027-01-01 00:00"
    ...
```

Replace the entry with the following to enable both the action bar and a coloured boss bar, and add custom messages:

```yaml
countdowns:
  new_year:
    type: FIXED_DATE
    target: "2027-01-01 00:00"
    timezone: "UTC"
    display:
      types:
        - ACTION_BAR
        - BOSS_BAR
      update-interval: 1
      bossbar:
        color: YELLOW
        style: SOLID
    messages:
      format: "<yellow>New Year</yellow> <gray>in</gray> <white>{formatted}</white>"
      start: "<green>New Year countdown started!</green>"
      end: "<gold>Happy New Year!</gold>"
    commands_on_end:
      - "broadcast Happy New Year!"
```

---

## Step 5  -  Reload

Apply the changes without restarting the server:

```
/countdown reload
```

The countdown now shows on the action bar and boss bar with the custom messages.

---

## Step 6  -  Stop and restart

You can pause and resume the countdown at any time:

```
/countdown stop new_year
/countdown start new_year
```

Stopping a `FIXED_DATE` countdown and starting it again will resume the timer from where it left off (the target date does not change).

---

## Next steps

- Add the countdown to a [permission node](../permissions) so only certain players see it.
- Learn how to set up a [recurring announcement](recurring-countdown) that fires every few hours.
- Explore [display types](../feature/display/) for scoreboard, title, and dialog displays.

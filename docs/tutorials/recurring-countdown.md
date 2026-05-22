---
title: Recurring Announcements
parent: Tutorials
nav_order: 2
---

# Tutorial: Recurring Announcements

In this tutorial you will create a countdown that resets automatically every two hours and shows a chat message warning players about an upcoming server event. This uses the **clock-aligned recurring** mode.

**Time to complete:** about 10 minutes.

---

## What clock-aligned recurring means

A clock-aligned recurring countdown fires at fixed intervals anchored to the clock. With `align_interval: 2h` and `timezone: UTC` the countdown fires at 00:00, 02:00, 04:00 … UTC. When the current cycle ends, the next one starts automatically.

This is useful for:
- Regular server events (reset, boss spawn, shop refresh).
- Reminders that repeat on a predictable schedule.
- Server-rule announcements.

---

## Step 1  -  Create the countdown

```
/countdown create server_reset recurring 0 0 00:00 --align-to-clock --align-interval 2h --timezone UTC --missed-run-policy SKIP
```

> The `recurring 0 0 00:00` part (month=0, day=0, time=00:00) is a placeholder required by the parser when `--align-to-clock` is used; the actual firing times come from `--align-interval`.

---

## Step 2  -  Verify it was created

```
/countdown info server_reset
```

The output should show `align_to_clock: true`, `align_interval: 2h`, and `timezone: UTC`.

---

## Step 3  -  Customise in `countdowns.yml`

Open `plugins/EzCountdown/countdowns.yml` and update the entry:

```yaml
countdowns:
  server_reset:
    type: RECURRING
    align_to_clock: true
    align_interval: "2h"
    timezone: "UTC"
    missed_run_policy: SKIP
    display:
      types:
        - CHAT
        - ACTION_BAR
      update-interval: 60           # update every 60 s (low priority display)
    messages:
      format: "<gray>Next server reset in</gray> <white>{formatted}</white>"
      start: "<aqua>Server reset cycle started.</aqua>"
      end: "<red>Server reset in progress! Returning in 30 seconds...</red>"
    commands_on_end:
      - "broadcast Server reset complete!"
```

---

## Step 4  -  Reload

```
/countdown reload
```

The countdown will fire chat reminders at each two-hour mark.

---

## Step 5  -  Test with a shorter interval

While testing you may want to use a shorter interval to verify it works. Change `align_interval` to `5m` in `countdowns.yml`, reload, and watch the chat messages appear. Change it back to `2h` when done.

---

## Missed-run policies

If your server was offline when a cycle was due:

| Policy | Behaviour |
|---|---|
| `SKIP` | Skip the missed cycles and wait for the next scheduled time |
| `RUN_SINGLE` | Run one missed cycle immediately on startup |
| `RUN_ALL` | Run all missed cycles back-to-back on startup |

`SKIP` is safe for most scenarios. Use `RUN_SINGLE` if you want the end commands to always fire at least once after a restart.

---

## Next steps

- Combine with [teleport](../feature/teleport) to move players to an arena when the countdown ends.
- Add a [Discord webhook](../feature/discord_integration) to post the reset notification to a staff channel.
- See the full [Countdown Types](../feature/countdown_types) reference for all recurring options.

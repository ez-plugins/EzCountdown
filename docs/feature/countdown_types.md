# Countdown Types

EzCountdown supports several countdown modes. Choose the mode that best fits your event.

- **Fixed date**: Targets a specific calendar date/time. Use `type: date` and set `date: "YYYY-MM-DD HH:mm"`.
- **Duration**: Runs for a specified length (e.g., `2h`, `30m`). Use `type: duration` and `duration: "2h"`.
- **Manual**: Similar to duration but remains stopped until explicitly started with `/countdown start <name>`.
- **Recurring**: Repeats yearly on the same month/day/time — useful for holidays and annual events.

Examples

```
countdowns:
  new_year:
    type: date
    date: "2026-01-01 00:00"

  maintenance:
    type: duration
    duration: "2h"

  festival:
    type: recurring
    recurring-month: 12
    recurring-day: 31
    recurring-time: "23:59"

  manual_event:
    type: manual
    duration: "30m"
```

Notes

- When using `duration` with `start-on-create: true` in `config.yml`, the countdown may start automatically when created.
- Recurring countdowns compute the next occurrence automatically based on configured zone/time.

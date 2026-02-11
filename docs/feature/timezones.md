# Timezones

This document lists recommended timezone identifiers you can use with the `timezone` setting for clock-aligned recurring countdowns.

Overview
- The `timezone` value must be an IANA timezone identifier supported by Java's `ZoneId` (for example `UTC`, `Europe/London`, `America/New_York`).
- Avoid short ambiguous abbreviations like `PST` or `CST`; prefer full region-based IDs.

Common timezones
- UTC
- Europe/London
- Europe/Paris
- Europe/Berlin
- Europe/Moscow
- America/New_York
- America/Chicago
- America/Denver
- America/Los_Angeles
- America/Sao_Paulo
- America/Argentina/Buenos_Aires
- Asia/Tokyo
- Asia/Shanghai
- Asia/Hong_Kong
- Asia/Kolkata
- Asia/Singapore
- Australia/Sydney
- Pacific/Auckland
- Pacific/Honolulu
- Africa/Johannesburg
- Atlantic/Reykjavik

Full list (programmatic)
If you need the complete list of timezones supported by the JVM you can generate it at runtime:

Java (run in a small program or JShell):

```java
import java.time.ZoneId;
ZoneId.getAvailableZoneIds().stream().sorted().forEach(System.out::println);
```

Linux (system list):

```bash
timedatectl list-timezones
```

Windows PowerShell:

```powershell
[System.TimeZoneInfo]::GetSystemTimeZones()
```

Notes and examples
- Use the exact identifier (case-sensitive in some contexts) — for example `Europe/London` or `America/New_York`.
- Example `countdowns.yml` snippet:

```yaml
example-aligned-countdown:
  type: recurring
  timezone: Europe/London
  align_to_clock: true
  align_interval: 2h
  missed_run_policy: SKIP
```

If you'd like, I can insert the full sorted list of all `ZoneId` identifiers into this file, but it is quite long (hundreds of entries). Alternatively I can add a script that outputs the full list into `docs/feature/timezones-full.txt` if you prefer a machine-generated, exhaustive reference.

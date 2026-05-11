---
title: Models
parent: Developer API
nav_order: 4
has_children: true
---

# Models

Core data classes used throughout the EzCountdown API.

| Class | Description |
|---|---|
| [Countdown](Countdown) | Represents a countdown's configuration and runtime state |
| [CountdownType](CountdownType) | Enum of the four countdown behavior modes |
| [Notification](Notification) | Immutable value object for ephemeral one-shot display notifications |

These classes are returned by `EzCountdownApi` methods and fired inside API events.

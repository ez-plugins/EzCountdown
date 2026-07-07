---
title: Home
layout: home
nav_order: 1
---

# EzCountdown

[![CI](https://github.com/ez-plugins/EzCountdown/actions/workflows/ci.yml/badge.svg)](https://github.com/ez-plugins/EzCountdown/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/ez-plugins/EzCountdown)](https://github.com/ez-plugins/EzCountdown/releases)
[![License](https://img.shields.io/github/license/ez-plugins/EzCountdown)](https://github.com/ez-plugins/EzCountdown/blob/main/LICENSE)

A feature-rich countdown plugin for Paper/Spigot servers. Create countdowns for events, launches, maintenance windows, and more  -  all configurable from YAML with no coding required.

## Features

- **Four countdown modes**  -  fixed date, duration, manual, and recurring (yearly or clock-aligned).
- **Six display types**  -  action bar, boss bar, title, chat, scoreboard, and dialog (Paper 1.21.7+).
- **Per-player visibility**  -  restrict any countdown or notification to specific permission nodes or a target-player set.
- **PlaceholderAPI support**  -  expose countdown values to scoreboards, holograms, and other plugins.
- **Discord webhooks**  -  post start/end notifications to a Discord channel.
- **In-game GUI**  -  manage countdowns visually without editing YAML.
- **Firework shows**  -  trigger configurable firework displays on start or end.
- **Start/end sounds**  -  optionally play configurable Bukkit sounds when countdowns start or finish.
- **Teleport actions**  -  move all online players to a named location when a countdown starts or ends.
- **Console commands on end**  -  run any command when a countdown completes.
- **Developer API**  -  create, start, stop, listen to countdown events, and send per-player notifications from other plugins.

## Quick Start

1. Drop `EzCountdown.jar` into your `plugins/` folder and start the server.
2. Create your first countdown:

   ```
   /countdown create new_year 2026-01-01 00:00
   ```

3. The countdown starts automatically and shows on the action bar using defaults from `config.yml`.
4. Edit `plugins/EzCountdown/countdowns.yml` to configure displays, messages, and end commands.
5. Reload without restarting:

   ```
   /countdown reload
   ```

## Compatibility

| Requirement | Minimum version |
|---|---|
| Minecraft / server software | Paper or Spigot **1.18** (or any fork) |
| Java | **17** |
| Dialog display | Paper **1.21.7** (gracefully skipped on older builds) |
| PlaceholderAPI | Any compatible build (soft dependency) |

> **Server owners**: download the release jar from [GitHub Releases](https://github.com/ez-plugins/EzCountdown/releases). Java 17 and Paper/Spigot 1.18 or newer are all you need.
>
> **Plugin developers**: add EzCountdown as a `provided` dependency and compile your plugin against Java 17 or newer.

## Documentation

| Section | Description |
|---|---|
| [Tutorials](tutorials/) | Step-by-step guides: create your first countdown, set up recurring announcements |
| [Server Owners](server-owners) | Install, commands, permissions, configuration reference |
| [Features](feature/) | Countdown types, displays, Discord, fireworks, teleports, and more |
| [Developer API](api/) | Java API, events, and models for plugin developers |

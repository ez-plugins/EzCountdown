---
title: Home
layout: home
nav_order: 1
---

# EzCountdown

[![CI](https://github.com/ez-plugins/EzCountdown/actions/workflows/ci.yml/badge.svg)](https://github.com/ez-plugins/EzCountdown/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/ez-plugins/EzCountdown)](https://github.com/ez-plugins/EzCountdown/releases)
[![License](https://img.shields.io/github/license/ez-plugins/EzCountdown)](https://github.com/ez-plugins/EzCountdown/blob/main/LICENSE)

A feature-rich countdown plugin for Paper/Spigot servers. Create countdowns for events, launches, maintenance windows, and more — all configurable from YAML with no coding required.

## Features

- **Four countdown modes** — fixed date, duration, manual, and recurring (yearly or clock-aligned).
- **Five display types** — action bar, boss bar, title, chat, and scoreboard.
- **PlaceholderAPI support** — expose countdown values to scoreboards, holograms, and other plugins.
- **Discord webhooks** — post start/end notifications to a Discord channel.
- **In-game GUI** — manage countdowns visually without editing YAML.
- **Firework shows** — trigger configurable firework displays on start or end.
- **Teleport actions** — move all online players to a named location when a countdown starts or ends.
- **Console commands on end** — run any command when a countdown completes.
- **Developer API** — create, start, stop, and listen to countdown events from other plugins.

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

## Documentation

| Section | Description |
|---|---|
| [Server Owners](server-owners) | Getting started: install, commands, permissions, configuration |
| [Features](feature/) | Countdown types, displays, Discord, fireworks, teleports, and more |
| [Developer API](api/) | Java API, events, and models for plugin developers |

## Installation

Download the latest release from [GitHub Releases](https://github.com/ez-plugins/EzCountdown/releases) and place the jar in your server's `plugins/` folder. Requires **Paper 1.20+** and **Java 21+**.

For dependency access in your own plugin, see the [Developer API](api/) page.

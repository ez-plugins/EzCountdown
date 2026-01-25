# Discord Integration

EzCountdown can send messages to Discord via incoming webhooks. Configure the webhook in `discord.yml` in the plugin data folder.

Setup

- Create an incoming webhook in your Discord server and copy the URL.
- Place the URL into `discord.yml` under the appropriate key (see `discord.yml` shipped with the plugin for structure).

Behavior

- CountdownManager will post configured messages or notifications to the webhook when countdowns end or as configured in `countdowns.yml`.

Notes

- The plugin reads `discord.yml` from the plugin folder. Use `/countdown reload` after changing the file to apply updates.
- Ensure the webhook has permissions to post in the target channel.

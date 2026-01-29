# Discord Integration

EzCountdown can post notifications to Discord using incoming webhooks. The integration is configured via `discord.yml` in the plugin data folder.

Quick setup

1. Create an incoming webhook in the target Discord channel and copy the webhook URL.
2. Open `plugins/EzCountdown/discord.yml` and add the webhook under the `webhooks:` section (the shipped example file shows structure and options).
3. Reload the plugin with `/countdown reload` to pick up changes.

What the plugin can send

- The plugin sends embed-style notifications for triggers such as `countdown_start` and `countdown_end` depending on your webhook configuration.
- You can control which triggers a webhook listens to and customize the embed title, description, footer, and images in `discord.yml`.

Permissions & safety

 - Ensure the webhook URL is kept secret - treat it like a credential.
- The webhook must have permission to post in the selected channel; otherwise messages will fail silently or be rejected by Discord.

Troubleshooting

- If webhooks don't send, check the server logs for errors and verify the URL is valid. Use `/countdown reload` after edits.
- Rate limits: Discord enforces rate limits on webhooks. Keep webhook usage reasonable (avoid posting thousands of messages per minute).

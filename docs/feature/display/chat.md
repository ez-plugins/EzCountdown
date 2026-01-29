
# Chat

What it is

Sends countdown updates as regular chat messages. This display is the most compatible and reliable across server versions.

When to use

- Use when supporting mixed or older server runtimes, or when you want persistent, logged messages for countdown updates.

How to enable

- Add `CHAT` to `display.types` for a countdown:

```yaml
countdowns:
  reminder:
    type: DURATION
    duration: "30m"
    display:
      types:
        - CHAT
```

Compatibility & notes

- Works on all standard Bukkit/Spigot/Paper servers and is a reliable fallback when other display APIs are unavailable.

Troubleshooting

- If other displays are unexpectedly degraded to chat, check server logs for compatibility warnings and consider enabling a supported display on modern servers.
- Use `messages.yml` to fine-tune formatting; verify MiniMessage usage or color codes render as intended.

Recommendation

- Keep `CHAT` enabled as a fallback for mixed-version networks or when you require messages to be recorded in logs.

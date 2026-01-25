
# Chat

Short summary

Sends countdown updates as normal chat messages. This is the most compatible and reliable display option.

When to use

- Use when supporting a broad set of server runtimes or when you prefer visible, logged messages over transient UI elements.

How to enable

Add `CHAT` to the `displays` list for a countdown:

```yaml
countdowns:
  reminder:
    type: duration
    duration: "30m"
    displays:
      - CHAT
```

Compatibility & notes

- Chat works on all standard Bukkit/Spigot/Paper servers and is a safe fallback whenever other display APIs are missing.
- Chat messages are persistent in logs and visible to offline monitoring tools that capture console output.

Troubleshooting (server owner tips)

- If other displays fall back to chat unexpectedly, check server logs for compatibility warnings and consider enabling a compatible display type on modern servers.
- Use `messages.yml` to customize chat formatting; test on a staging server to ensure MiniMessage or color codes render as expected.

Recommendation

- Keep `CHAT` available as a fallback in `default-displays` if you operate mixed-version networks or older runtimes.

# Countdown (model)

Represents a countdown configuration and runtime state.

- Package: `com.skyblockexp.ezcountdown.api.model`
- Class: `Countdown`

Constructor (primary):

- `Countdown(String name, CountdownType type, EnumSet<DisplayType> displayTypes, int updateIntervalSeconds, String visibilityPermission, String formatMessage, String startMessage, String endMessage, List<String> endCommands, ZoneId zoneId)`

Important accessors and behavior:

- `String getName()` — unique countdown name.
- `CountdownType getType()` — type of countdown (see `CountdownType`).
- `EnumSet<DisplayType> getDisplayTypes()` — which displays are used.
- `int getUpdateIntervalSeconds()` — tick/update interval used by the plugin.
- `String getVisibilityPermission()` — permission node controlling visibility.
- `String getFormatMessage()` — format string used for display messages.
- `String getStartMessage()` — message broadcasted on start (if configured).
- `String getEndMessage()` — message broadcasted on end (if configured).
- `List<String> getEndCommands()` — commands executed when countdown ends.
- `ZoneId getZoneId()` — timezone used when resolving fixed/recurring dates.

Runtime fields (mutable):

- `boolean isRunning()` / `void setRunning(boolean)` — whether it's currently running.
- `long getDurationSeconds()` / `void setDurationSeconds(long)` — duration for DURATION type.
- `Instant getTargetInstant()` / `void setTargetInstant(Instant)` — resolved target for FIXED_DATE or recurring.
- Recurring fields: `getRecurringMonth()`, `getRecurringDay()`, `getRecurringTime()`, and resolver `resolveNextRecurringTarget(Instant now)`.

Notes

- The constructor establishes the immutable configuration; runtime fields are updated by the plugin when starting/stopping the countdown.

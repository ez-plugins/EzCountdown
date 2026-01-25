package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.display.DisplayType;
import java.time.ZoneId;
import java.util.EnumSet;

public record CountdownDefaults(EnumSet<DisplayType> displayTypes,
                                int updateIntervalSeconds,
                                String visibilityPermission,
                                String formatMessage,
                                String startMessage,
                                String endMessage,
                                boolean startOnCreate,
                                ZoneId zoneId) {
}

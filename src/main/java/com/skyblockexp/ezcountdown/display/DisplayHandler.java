package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;

public interface DisplayHandler {
    void display(Countdown countdown, String message, long remainingSeconds);
    void clear(Countdown countdown);
    void clearAll();
}

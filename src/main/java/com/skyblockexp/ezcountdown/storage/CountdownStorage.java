package com.skyblockexp.ezcountdown.storage;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import java.util.Collection;

public interface CountdownStorage {

    Collection<Countdown> loadCountdowns();

    void saveCountdowns(Collection<Countdown> countdowns);
}

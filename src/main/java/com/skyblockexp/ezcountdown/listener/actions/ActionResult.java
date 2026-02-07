package com.skyblockexp.ezcountdown.listener.actions;

import java.util.Optional;

public final class ActionResult {
    private final boolean handled;
    private final boolean mutated;
    private final boolean closeInventory;
    private final Optional<String> reopenTitle;

    public ActionResult(boolean handled, boolean mutated, boolean closeInventory, Optional<String> reopenTitle) {
        this.handled = handled;
        this.mutated = mutated;
        this.closeInventory = closeInventory;
        this.reopenTitle = reopenTitle == null ? Optional.empty() : reopenTitle;
    }

    public boolean isHandled() { return handled; }
    public boolean isMutated() { return mutated; }
    public boolean isCloseInventory() { return closeInventory; }
    public Optional<String> getReopenTitle() { return reopenTitle; }

    public static ActionResult handled() { return new ActionResult(true, false, false, Optional.empty()); }
    public static ActionResult handledAndMutated() { return new ActionResult(true, true, false, Optional.empty()); }
    public static ActionResult handledAndClose() { return new ActionResult(true, false, true, Optional.empty()); }
    public static ActionResult none() { return new ActionResult(false, false, false, Optional.empty()); }
}

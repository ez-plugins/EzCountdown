package com.skyblockexp.ezcountdown.api.model;

/**
 * Policy for handling missed recurring runs when the server/plugin restarts.
 */
public enum MissedRunPolicy {
    /** Do not run missed occurrences; schedule the next future occurrence. */
    SKIP,
    /** Run a single missed occurrence immediately on startup, then continue. */
    RUN_SINGLE,
    /** Attempt to run all missed occurrences (use with caution). */
    RUN_ALL
}

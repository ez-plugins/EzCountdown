package io.papermc.paper.plugin.lifecycle.event.registrar;

/**
 * Test-only stub for older Paper API versions that don't include the
 * {@code io.papermc.paper.plugin.lifecycle.event.registrar.Registrar} type.
 *
 * This class is only used during tests to avoid NoClassDefFoundError when
 * running against older Paper API artifacts in CI. It should not be
 * referenced by production code.
 */
public final class Registrar {
    // intentionally empty
}

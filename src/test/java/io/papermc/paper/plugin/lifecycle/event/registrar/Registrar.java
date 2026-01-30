package io.papermc.paper.plugin.lifecycle.event.registrar;

/**
 * Test-only stub for older Paper API versions that don't include the
 * {@code io.papermc.paper.plugin.lifecycle.event.registrar.Registrar} type.
 *
 * MockBukkit provides a `PaperRegistrarMock` that expects `Registrar` to be
 * an interface. Provide a minimal interface here so the mock can implement
 * it and tests do not fail with IncompatibleClassChangeError.
 */
public interface Registrar {
    // marker interface for test compatibility
}

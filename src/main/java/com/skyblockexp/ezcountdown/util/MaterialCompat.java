package com.skyblockexp.ezcountdown.util;

import org.bukkit.Material;

/**
 * @deprecated Use
 *     {@link com.skyblockexp.ezcountdown.compat.material.MaterialCompat}
 *     instead.
 */
@Deprecated
public final class MaterialCompat {

    private MaterialCompat() {}

    /** @deprecated Use the {@code compat.material} equivalent. */
    @Deprecated
    public static Material resolve(String... names) {
        return com.skyblockexp.ezcountdown.compat.material.MaterialCompat.resolve(names);
    }
}

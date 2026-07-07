package com.skyblockexp.ezcountdown.compat.material;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaterialCompatTest {

    @Test
    void resolveReturnsFirstValidMaterial() {
        Material resolved = MaterialCompat.resolve("NOT_A_REAL_MATERIAL", "PAPER", "STONE");
        assertEquals(Material.PAPER, resolved);
    }

    @Test
    void resolveSkipsNullNamesAndFindsValid() {
        Material resolved = MaterialCompat.resolve(null, "STONE");
        assertEquals(Material.STONE, resolved);
    }

    @Test
    void resolveFallsBackToPaperWhenNamesInvalid() {
        Material resolved = MaterialCompat.resolve("INVALID_ONE", "INVALID_TWO");
        assertEquals(Material.PAPER, resolved);
    }

    @Test
    void resolveFallsBackToPaperWhenNamesArrayIsNull() {
        Material resolved = MaterialCompat.resolve((String[]) null);
        assertEquals(Material.PAPER, resolved);
    }
}

package com.skyblockexp.ezcountdown.firework;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class FireworkEffectBuilderTest {

    @Test
    public void buildEffectWithColorsAndFade() {
        EffectDescriptor d = new EffectDescriptor();
        d.type = "STAR";
        d.colors = Collections.singletonList(Color.RED);
        d.fades = Collections.singletonList(Color.WHITE);
        d.flicker = true;
        d.trail = false;

        FireworkEffect e = FireworkEffectBuilder.buildEffect(d);
        assertNotNull(e);
        assertEquals(FireworkEffect.Type.STAR, e.getType());
        assertTrue(e.getColors().contains(Color.RED));
        assertTrue(e.getFadeColors().contains(Color.WHITE));
        assertTrue(e.hasFlicker());
        assertFalse(e.hasTrail());
    }
}

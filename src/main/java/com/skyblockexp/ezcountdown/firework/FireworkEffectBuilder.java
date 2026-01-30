package com.skyblockexp.ezcountdown.firework;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;

import java.util.List;

public final class FireworkEffectBuilder {
    private FireworkEffectBuilder() {}

    public static FireworkEffect buildEffect(EffectDescriptor desc) {
        FireworkEffect.Type type;
        try {
            type = FireworkEffect.Type.valueOf(desc.type.toUpperCase());
        } catch (Exception e) {
            type = FireworkEffect.Type.BALL;
        }

        FireworkEffect.Builder b = FireworkEffect.builder().with(type).flicker(desc.flicker).trail(desc.trail);

        List<Color> cols = desc.colors;
        if (cols != null && !cols.isEmpty()) {
            Color[] arr = cols.toArray(new Color[0]);
            b.withColor(arr);
        }

        List<Color> fades = desc.fades;
        if (fades != null && !fades.isEmpty()) {
            Color[] arr = fades.toArray(new Color[0]);
            b.withFade(arr);
        }

        return b.build();
    }
}

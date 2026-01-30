package com.skyblockexp.ezcountdown.firework;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class EffectDescriptor {
    public String type = "BALL";
    public List<Color> colors = new ArrayList<>();
    public List<Color> fades = new ArrayList<>();
    public boolean flicker = true;
    public boolean trail = true;
    public int power = 1;
    public int count = 8;
    public int interval = 6;
    public String pattern = "circle"; // circle, cone, random
    public double offsetX = 0;
    public double offsetY = 0;
    public double offsetZ = 0;
}

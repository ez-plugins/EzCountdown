package com.skyblockexp.ezcountdown.firework;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Locale;

public class FireworkShowManager {
    /**
     * Launches a multi-row firework show in concentric circles.
     * @param world World to launch in
     * @param center Center location
     * @param colorName Color name
     * @param power Firework power
     * @param count Number of fireworks per row
     * @param rows Number of concentric rows
     * @param intervalTicks Delay between rows (in ticks)
     */
    public void launchCircleFireworkShow(Plugin plugin, World world, Location center, String colorName, int power, int count, int rows, int intervalTicks) {
        if (plugin == null || world == null || center == null || colorName == null || count <= 0 || rows <= 0) return;
        final Color color = parseColor(colorName);
        final double baseRadius = 6.0;
        for (int row = 0; row < rows; row++) {
            final int currentRow = row;
            new BukkitRunnable() {
                @Override
                public void run() {
                    double radius = baseRadius + currentRow * 2.5;
                    for (int i = 0; i < count; i++) {
                        double angle = 2 * Math.PI * i / count;
                        double x = center.getX() + radius * Math.cos(angle);
                        double z = center.getZ() + radius * Math.sin(angle);
                        Location loc = new Location(world, x, center.getY() + 1, z);
                        spawnFirework(loc, color != null ? color : Color.WHITE, power);
                    }
                }
            }.runTaskLater(plugin, row * intervalTicks);
        }
    }

    private void spawnFirework(Location loc, Color color, int power) {
        try {
            Firework firework = loc.getWorld().spawn(loc, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.addEffect(FireworkEffect.builder()
                    .with(FireworkEffect.Type.BALL)
                    .withColor(color)
                    .withFade(color)
                    .flicker(true)
                    .trail(true)
                    .build());
            meta.setPower(power);
            firework.setFireworkMeta(meta);
        } catch (Exception ignored) {}
    }

    private Color parseColor(String name) {
        return com.skyblockexp.ezcountdown.firework.FireworkUtils.parseColor(name);
    }

    public static final class FireworkConfig {
        public String location;
        public String color = "WHITE";
        public int power = 1;
        public int count = 8;
        public int rows = 1;
        public int interval = 10;
    }

    public void launchConfiguredShow(Plugin plugin, Countdown countdown, String phase) {
        if (plugin == null || countdown == null || phase == null) return;
        // attempt to load advanced config first
        AdvancedFireworkConfig adv = loadAdvancedConfig(plugin, countdown.getName(), phase);
        if (adv != null && adv.location != null) {
            Location loc = loadLocation(plugin, adv.location);
            if (loc == null || loc.getWorld() == null) return;
            // If there are effects defined, schedule them
            if (adv.effects != null && !adv.effects.isEmpty()) {
                for (EffectDescriptor d : adv.effects) {
                    PatternScheduler.scheduleEffect(plugin, loc.getWorld(), loc, d);
                }
                return;
            }
            // otherwise, fallback to phase-level circle show
            launchCircleFireworkShow(plugin, loc.getWorld(), loc, "WHITE", 1, adv.count, adv.rows, adv.interval);
            return;
        }

        // legacy fallback
        FireworkConfig cfg = loadConfig(plugin, countdown.getName(), phase);
        if (cfg == null || cfg.location == null) return;
        Location loc = loadLocation(plugin, cfg.location);
        if (loc == null || loc.getWorld() == null) return;
        launchCircleFireworkShow(plugin, loc.getWorld(), loc, cfg.color, cfg.power, cfg.count, cfg.rows, cfg.interval);
    }

    private AdvancedFireworkConfig loadAdvancedConfig(Plugin plugin, String countdownName, String phase) {
        try {
            File file = new File(plugin.getDataFolder(), "countdowns.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String base = "countdowns." + countdownName + ".firework." + phase;
            return parseAdvancedConfig(config, base);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to load advanced firework config: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Parse advanced firework config from a loaded FileConfiguration using the provided base path.
     * Public for testing.
     */
    public static AdvancedFireworkConfig parseAdvancedConfig(FileConfiguration config, String base) {
        if (!config.contains(base + ".location")) return null;
        AdvancedFireworkConfig cfg = new AdvancedFireworkConfig();
        cfg.location = config.getString(base + ".location");
        cfg.count = config.getInt(base + ".count", cfg.count);
        cfg.rows = config.getInt(base + ".rows", cfg.rows);
        cfg.interval = config.getInt(base + ".interval", cfg.interval);

        if (config.contains(base + ".effects")) {
            for (Object o : config.getMapList(base + ".effects")) {
                if (!(o instanceof java.util.Map)) continue;
                java.util.Map m = (java.util.Map) o;
                EffectDescriptor d = new EffectDescriptor();
                Object t = m.get("type");
                if (t != null) d.type = String.valueOf(t);

                Object colors = m.get("colors");
                if (colors instanceof java.util.List) {
                    java.util.List<String> cl = new java.util.ArrayList<>();
                    for (Object cs : (java.util.List) colors) cl.add(String.valueOf(cs));
                    d.colors = com.skyblockexp.ezcountdown.firework.FireworkUtils.parseColorList(cl);
                } else if (colors != null) {
                    java.util.List<String> cl = new java.util.ArrayList<>();
                    cl.add(String.valueOf(colors));
                    d.colors = com.skyblockexp.ezcountdown.firework.FireworkUtils.parseColorList(cl);
                }

                Object fades = m.get("fade");
                if (fades instanceof java.util.List) {
                    java.util.List<String> cl = new java.util.ArrayList<>();
                    for (Object cs : (java.util.List) fades) cl.add(String.valueOf(cs));
                    d.fades = com.skyblockexp.ezcountdown.firework.FireworkUtils.parseColorList(cl);
                } else if (fades != null) {
                    java.util.List<String> cl = new java.util.ArrayList<>();
                    cl.add(String.valueOf(fades));
                    d.fades = com.skyblockexp.ezcountdown.firework.FireworkUtils.parseColorList(cl);
                }

                Object flick = m.get("flicker");
                if (flick != null) d.flicker = Boolean.parseBoolean(String.valueOf(flick));
                Object trail = m.get("trail");
                if (trail != null) d.trail = Boolean.parseBoolean(String.valueOf(trail));
                Object power = m.get("power");
                if (power != null) d.power = Integer.parseInt(String.valueOf(power));
                Object cnt = m.get("count");
                if (cnt != null) d.count = Integer.parseInt(String.valueOf(cnt));
                Object interval = m.get("interval");
                if (interval != null) d.interval = Integer.parseInt(String.valueOf(interval));
                Object pattern = m.get("pattern");
                if (pattern != null) d.pattern = String.valueOf(pattern);
                Object offset = m.get("offset");
                if (offset instanceof java.util.Map) {
                    java.util.Map off = (java.util.Map) offset;
                    Object ox = off.get("x"); if (ox != null) d.offsetX = Double.parseDouble(String.valueOf(ox));
                    Object oy = off.get("y"); if (oy != null) d.offsetY = Double.parseDouble(String.valueOf(oy));
                    Object oz = off.get("z"); if (oz != null) d.offsetZ = Double.parseDouble(String.valueOf(oz));
                }

                if (d.colors == null || d.colors.isEmpty()) {
                    String legacy = config.getString(base + ".color", null);
                    if (legacy != null) {
                        java.util.List<String> cl = new java.util.ArrayList<>();
                        cl.add(legacy);
                        d.colors = com.skyblockexp.ezcountdown.firework.FireworkUtils.parseColorList(cl);
                    }
                }

                cfg.effects.add(d);
            }
            return cfg;
        }

        EffectDescriptor single = new EffectDescriptor();
        single.count = config.getInt(base + ".count", single.count);
        single.interval = config.getInt(base + ".interval", single.interval);
        single.power = config.getInt(base + ".power", single.power);
        String legacyColor = config.getString(base + ".color", null);
        if (legacyColor != null) {
            java.util.List<String> cl = new java.util.ArrayList<>();
            cl.add(legacyColor);
            single.colors = com.skyblockexp.ezcountdown.firework.FireworkUtils.parseColorList(cl);
        }
        cfg.effects.add(single);
        return cfg;
    }

    private FireworkConfig loadConfig(Plugin plugin, String countdownName, String phase) {
        try {
            File file = new File(plugin.getDataFolder(), "countdowns.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String base = "countdowns." + countdownName + ".firework." + phase;
            if (!config.contains(base + ".location")) return null;
            FireworkConfig cfg = new FireworkConfig();
            cfg.location = config.getString(base + ".location");
            cfg.color = config.getString(base + ".color", cfg.color);
            cfg.power = config.getInt(base + ".power", cfg.power);
            cfg.count = config.getInt(base + ".count", cfg.count);
            cfg.rows = config.getInt(base + ".rows", cfg.rows);
            cfg.interval = config.getInt(base + ".interval", cfg.interval);
            return cfg;
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to load firework config: " + ex.getMessage());
            return null;
        }
    }

    private Location loadLocation(Plugin plugin, String name) {
        if (name == null) return null;
        try {
            File file = new File(plugin.getDataFolder(), "locations.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (!config.contains(name)) return null;
            String worldName = config.getString(name + ".world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) return null;
            double x = config.getDouble(name + ".x");
            double y = config.getDouble(name + ".y");
            double z = config.getDouble(name + ".z");
            float yaw = (float) config.getDouble(name + ".yaw");
            float pitch = (float) config.getDouble(name + ".pitch");
            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to load location: " + ex.getMessage());
            return null;
        }
    }
}

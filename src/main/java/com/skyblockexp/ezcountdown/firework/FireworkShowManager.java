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
    public void launchCircleFireworkShow(World world, Location center, String colorName, int power, int count, int rows, int intervalTicks) {
        if (world == null || center == null || colorName == null || count <= 0 || rows <= 0) return;
        final Color color = parseColor(colorName);
        final double baseRadius = 6.0;
        for (int row = 0; row < rows; row++) {
            final int currentRow = row;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
                double radius = baseRadius + currentRow * 2.5;
                for (int i = 0; i < count; i++) {
                    double angle = 2 * Math.PI * i / count;
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);
                    Location loc = new Location(world, x, center.getY() + 1, z);
                    spawnFirework(loc, color != null ? color : Color.WHITE, power);
                }
            }, row * intervalTicks);
        }
    }

    private void spawnFirework(Location loc, Color color, int power) {
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
    }

    private Color parseColor(String name) {
        try {
            return (Color) Color.class.getField(name.toUpperCase(Locale.ROOT)).get(null);
        } catch (Exception e) {
            return null;
        }
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
        FireworkConfig cfg = loadConfig(plugin, countdown.getName(), phase);
        if (cfg == null || cfg.location == null) return;
        Location loc = loadLocation(plugin, cfg.location);
        if (loc == null || loc.getWorld() == null) return;
        launchCircleFireworkShow(loc.getWorld(), loc, cfg.color, cfg.power, cfg.count, cfg.rows, cfg.interval);
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

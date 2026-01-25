package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.config.LocationsConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LocationManager {
    private final LocationsConfig config;
    private final Map<String, Location> locations = new HashMap<>();

    public LocationManager(LocationsConfig config) {
        this.config = config;
        loadLocations();
    }

    public void loadLocations() {
        locations.clear();
        Map<String, Map<String, Object>> data = config.loadRaw();
        for (String name : data.keySet()) {
            Map<String, Object> locData = data.get(name);
            World world = Bukkit.getWorld((String) locData.get("world"));
            if (world == null) continue;
            double x = Double.parseDouble(locData.get("x").toString());
            double y = Double.parseDouble(locData.get("y").toString());
            double z = Double.parseDouble(locData.get("z").toString());
            float yaw = Float.parseFloat(locData.get("yaw").toString());
            float pitch = Float.parseFloat(locData.get("pitch").toString());
            locations.put(name, new Location(world, x, y, z, yaw, pitch));
        }
    }

    public void saveLocations() {
        Map<String, Map<String, Object>> data = new HashMap<>();
        for (Map.Entry<String, Location> entry : locations.entrySet()) {
            Location loc = entry.getValue();
            Map<String, Object> locData = new HashMap<>();
            locData.put("world", loc.getWorld().getName());
            locData.put("x", loc.getX());
            locData.put("y", loc.getY());
            locData.put("z", loc.getZ());
            locData.put("yaw", loc.getYaw());
            locData.put("pitch", loc.getPitch());
            data.put(entry.getKey(), locData);
        }
        config.saveRaw(data);
    }

    public boolean addLocation(String name, Location location) {
        if (locations.containsKey(name)) return false;
        locations.put(name, location);
        saveLocations();
        return true;
    }

    public boolean deleteLocation(String name) {
        if (!locations.containsKey(name)) return false;
        locations.remove(name);
        saveLocations();
        return true;
    }

    public Location getLocation(String name) {
        return locations.get(name);
    }

    public void teleportPlayer(Player player, String locationName) {
        Location loc = getLocation(locationName);
        if (loc != null) {
            player.teleport(loc);
        }
    }

    public java.util.List<String> getLocationNames() {
        return new java.util.ArrayList<>(locations.keySet());
    }
}

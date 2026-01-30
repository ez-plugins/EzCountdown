package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import com.skyblockexp.ezcountdown.config.LocationsConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LocationManagerFeatureTest extends MockBukkitTestBase {

    @Test
    public void loadAndSaveLocations_roundtrip() throws Exception {
        // create a temp folder for LocationsConfig
        File tmp = Files.createTempDirectory("locconfig").toFile();
        tmp.deleteOnExit();
        LocationsConfig cfg = new LocationsConfig(tmp);

        // prepare raw data referencing the mock server's default world name
        String worldName = "world";
        server.addSimpleWorld(worldName);
        World w = server.getWorld(worldName);
        assertNotNull(w, "MockBukkit should provide a mock world named 'world'");

        Map<String, Object> locData = new HashMap<>();
        locData.put("world", worldName);
        locData.put("x", 1.0);
        locData.put("y", 2.0);
        locData.put("z", 3.0);
        locData.put("yaw", 10.0f);
        locData.put("pitch", 5.0f);

        Map<String, Map<String, Object>> raw = new HashMap<>();
        raw.put("home", locData);
        cfg.saveRaw(raw);

        LocationManager lm = new LocationManager(cfg);
        Location loaded = lm.getLocation("home");
        assertNotNull(loaded);
        assertEquals(1.0, loaded.getX(), 1e-6);
        assertEquals(2.0, loaded.getY(), 1e-6);
        assertEquals(3.0, loaded.getZ(), 1e-6);

        // add a new location and ensure it's saved
        Location newLoc = new Location(w, 4, 5, 6, 0f, 0f);
        assertTrue(lm.addLocation("spawn", newLoc));

        // reload from config file to verify save
        LocationManager lm2 = new LocationManager(cfg);
        Location l2 = lm2.getLocation("spawn");
        assertNotNull(l2);
        assertEquals(4.0, l2.getX(), 1e-6);

        // delete location
        assertTrue(lm2.deleteLocation("spawn"));
        LocationManager lm3 = new LocationManager(cfg);
        assertNull(lm3.getLocation("spawn"));
    }
}

package com.skyblockexp.ezcountdown.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class LocationsConfig {
    private final File file;

    public LocationsConfig(File dataFolder) {
        this.file = new File(dataFolder, "locations.yml");
    }

    public boolean exists() {
        return file.exists();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> loadRaw() {
        if (!file.exists()) return new HashMap<>();
        Yaml yaml = new Yaml();
        try (FileReader reader = new FileReader(file)) {
            Map<String, Object> data = yaml.load(reader);
            if (data == null) return new HashMap<>();
            Map<String, Map<String, Object>> result = new HashMap<>();
            for (String key : data.keySet()) {
                Object val = data.get(key);
                if (val instanceof Map) {
                    result.put(key, (Map<String, Object>) val);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read locations.yml", e);
        }
    }

    public void saveRaw(Map<String, Map<String, Object>> data) {
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write locations.yml", e);
        }
    }
}

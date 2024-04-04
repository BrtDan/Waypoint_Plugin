import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WaypointManager {

    private final Plugin plugin;
    private final Map<String, Location> waypoints = new HashMap<>();
    private final File waypointFile;
    private final FileConfiguration waypointConfig;

    public WaypointManager(Plugin plugin) {
        this.plugin = plugin;
        this.waypointFile = new File(plugin.getDataFolder(), "org/test/tppoint/waypoints.yml");
        this.waypointConfig = YamlConfiguration.loadConfiguration(waypointFile);
        loadWaypoints();
    }

    public void loadWaypoints() {
        if (waypointFile.exists()) {
            Set<String> keys = waypointConfig.getConfigurationSection("waypoints").getKeys(false);
            for (String key : keys) {
                String path = "waypoints." + key;
                String worldName = waypointConfig.getString(path + ".world");
                double x = waypointConfig.getDouble(path + ".x");
                double y = waypointConfig.getDouble(path + ".y");
                double z = waypointConfig.getDouble(path + ".z");
                float yaw = (float) waypointConfig.getDouble(path + ".yaw");
                float pitch = (float) waypointConfig.getDouble(path + ".pitch");
                Location location = new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
                waypoints.put(key, location);
            }
        }
    }

    public void saveWaypoints() {
        waypointConfig.set("waypoints", null);
        for (String key : waypoints.keySet()) {
            Location location = waypoints.get(key);
            String path = "waypoints." + key;
            waypointConfig.set(path + ".world", location.getWorld().getName());
            waypointConfig.set(path + ".x", location.getX());
            waypointConfig.set(path + ".y", location.getY());
            waypointConfig.set(path + ".z", location.getZ());
            waypointConfig.set(path + ".yaw", location.getYaw());
            waypointConfig.set(path + ".pitch", location.getPitch());
        }

        try {
            waypointConfig.save(waypointFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Error saving waypoints.yml file: " + e.getMessage());
        }
    }

    public Map<String, Location> getWaypoints() {
        return waypoints;
    }

    public void setWaypoint(String name, Location location) {
        waypoints.put(name, location);
        saveWaypoints();
    }

    public void removeWaypoint(String name) {
        waypoints.remove(name);
        saveWaypoints();
    }
}

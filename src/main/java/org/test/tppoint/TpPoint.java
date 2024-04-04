package org.test.tppoint;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TpPoint extends JavaPlugin {

    private Map<String, Location> waypoints = new HashMap<>();
    private FileConfiguration waypointConfig;
    private File waypointFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("TpPoint plugin enabled!");

        // Load waypoints from file
        loadWaypoints();

        // Register waypoint saving task
        Bukkit.getScheduler().runTaskTimer(this, this::saveWaypoints, 6000, 6000); // Save waypoints every 5 minutes
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("TpPoint plugin disabled!");

        // Save waypoints before shutdown
        saveWaypoints();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("setwaypoint")) {
            if (args.length != 1) {
                player.sendMessage("Usage: /setwaypoint <waypoint_name>");
                return true;
            }

            String waypointName = args[0];
            Location playerLocation = player.getLocation();

            // Aggiungere il waypoint alla mappa e al file
            waypoints.put(waypointName, playerLocation);
            saveWaypointToFile(waypointName, playerLocation);
            player.sendMessage("Waypoint '" + waypointName + "' set.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("teleport")) {
            if (args.length != 1) {
                player.sendMessage("Usage: /teleport <waypoint_name>");
                return true;
            }

            String waypointName = args[0];
            Location waypointLocation = waypoints.get(waypointName);

            if (waypointLocation == null) {
                player.sendMessage("Waypoint '" + waypointName + "' does not exist.");
                return true;
            }

            // Delay teleport by 3 seconds with countdown
            new BukkitRunnable() {
                int countdown = 3;

                @Override
                public void run() {
                    if (countdown > 0) {
                        player.sendMessage("Teleporting in " + countdown + " seconds...");
                        countdown--;
                    } else {
                        this.cancel();
                        player.teleport(waypointLocation);
                        player.sendMessage("Teleported to waypoint '" + waypointName + "'.");
                    }
                }
            }.runTaskTimer(this, 0, 20); // 20 ticks = 1 second

            return true;
        }

        if (command.getName().equalsIgnoreCase("deletewaypoint")) {
            if (args.length != 1) {
                player.sendMessage("Usage: /deletewaypoint <waypoint_name>");
                return true;
            }

            String waypointName = args[0];

            // Rimuovere il waypoint dalla mappa e dal file
            if (removeWaypoint(waypointName)) {
                player.sendMessage("Waypoint '" + waypointName + "' deleted.");
            } else {
                player.sendMessage("Waypoint '" + waypointName + "' does not exist.");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("waypoints")) {
            if (waypoints.isEmpty()) {
                player.sendMessage("No waypoints set.");
            } else {
                player.sendMessage("List of waypoints:");

                for (String waypoint : waypoints.keySet()) {
                    player.sendMessage("- " + waypoint);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("teleport") || command.getName().equalsIgnoreCase("deletewaypoint")) {
            if (args.length == 1) {
                String partialName = args[0].toLowerCase();
                for (String waypoint : waypoints.keySet()) {
                    if (waypoint.toLowerCase().startsWith(partialName)) {
                        completions.add(waypoint);
                    }
                }
            }
        }

        completions.sort(String::compareToIgnoreCase);
        return completions;
    }

    // Caricare i waypoints dal file
    private void loadWaypoints() {
        waypointFile = new File(getDataFolder(), "waypoints.yml");
        if (!waypointFile.exists()) {
            saveResource("waypoints.yml", false);
        }

        waypointConfig = YamlConfiguration.loadConfiguration(waypointFile);
        if (waypointConfig.contains("waypoints")) {
            Set<String> keys = waypointConfig.getConfigurationSection("waypoints").getKeys(false);
            for (String key : keys) {
                String path = "waypoints." + key;
                double x = waypointConfig.getDouble(path + ".x");
                double y = waypointConfig.getDouble(path + ".y");
                double z = waypointConfig.getDouble(path + ".z");
                float yaw = (float) waypointConfig.getDouble(path + ".yaw");
                float pitch = (float) waypointConfig.getDouble(path + ".pitch");
                Location location = new Location(Bukkit.getWorlds().get(0), x, y, z, yaw, pitch);
                waypoints.put(key, location);
            }
        }
    }

    // Salvare i waypoints nel file
    private void saveWaypoints() {
        if (waypointFile == null || waypointConfig == null) {
            return;
        }

        waypointConfig.set("waypoints", null);
        for (String key : waypoints.keySet()) {
            Location location = waypoints.get(key);
            String path = "waypoints." + key;
            waypointConfig.set(path + ".x", location.getX());
            waypointConfig.set(path + ".y", location.getY());
            waypointConfig.set(path + ".z", location.getZ());
            waypointConfig.set(path + ".yaw", location.getYaw());
            waypointConfig.set(path + ".pitch", location.getPitch());
        }

        try {
            waypointConfig.save(waypointFile);
        } catch (IOException e) {
            getLogger().warning("Error saving waypoints.yml file: " + e.getMessage());
        }
    }

    // Aggiungere un singolo waypoint al file
    private void saveWaypointToFile(String waypointName, Location location) {
        if (waypointFile == null || waypointConfig == null) {
            return;
        }

        String path = "waypoints." + waypointName;
        waypointConfig.set(path + ".x", location.getX());
        waypointConfig.set(path + ".y", location.getY());
        waypointConfig.set(path + ".z", location.getZ());
        waypointConfig.set(path + ".yaw", location.getYaw());
        waypointConfig.set(path + ".pitch", location.getPitch());

        try {
            waypointConfig.save(waypointFile);
        } catch (IOException e) {
            getLogger().warning("Error saving waypoints.yml file: " + e.getMessage());
        }
    }

    // Rimuovere un singolo waypoint dal file
    private boolean removeWaypoint(String waypointName) {
        if (waypoints.containsKey(waypointName)) {
            waypoints.remove(waypointName);

            if (waypointConfig.contains("waypoints." + waypointName)) {
                waypointConfig.set("waypoints." + waypointName, null);
                try {
                    waypointConfig.save(waypointFile);
                } catch (IOException e) {
                    getLogger().warning("Error saving waypoints.yml file: " + e.getMessage());
                }
            }

            return true;
        }
        return false;
    }
}

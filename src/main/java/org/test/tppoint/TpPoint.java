package org.test.tppoint;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TpPoint extends JavaPlugin {

    private Map<String, Location> waypoints = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("TpPoint plugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("TpPoint plugin disabled!");
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

            if (waypoints.containsKey(waypointName)) {
                player.sendMessage("A waypoint with this name already exists.");
                return true;
            }

            waypoints.put(waypointName, playerLocation);
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
            if (waypoints.remove(waypointName) != null) {
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

        if (command.getName().equalsIgnoreCase("teleport")) {
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
}

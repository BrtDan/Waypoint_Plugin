import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
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

        if (command.getName().equalsIgnoreCase("teleport")) {
            if (args.length != 1) {
                player.sendMessage("Usage: /teleport <waypoint_name>");
                return true;
            }

            String waypointName = args[0];
            Location waypointLocation = waypoints.get(waypointName);

            if (waypointLocation == null) {
                player.sendMessage("Waypoint '" + waypointName + "' non esiste.");
                return true;
            }

            // Start countdown and teleport after 3 seconds
            new BukkitRunnable() {
                int countdown = 3;

                @Override
                public void run() {
                    if (countdown > 0) {
                        player.sendMessage("Teletrasporto in " + countdown + "...");
                        countdown--;
                    } else {
                        if (player.isOnline() && !player.isDead() && !player.isInsideVehicle() && player.getLocation().distanceSquared(waypointLocation) < 1) {
                            player.teleport(waypointLocation);
                            player.sendMessage("Teletrasportato al waypoint '" + waypointName + "'.");
                        } else {
                            player.sendMessage("Teletrasporto annullato. Assicurati di essere fermo.");
                        }
                        cancel();
                    }
                }
            }.runTaskTimer(this, 0, 20); // 20 ticks = 1 second

            return true;
        }
        return false;
    }
}

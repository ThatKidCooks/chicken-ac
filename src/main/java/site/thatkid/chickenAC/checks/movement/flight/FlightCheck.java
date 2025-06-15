package site.thatkid.chickenAC.checks.movement.flight;

import org.bukkit.BanList;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import site.thatkid.chickenAC.checks.Category;
import site.thatkid.chickenAC.checks.flag.OverFlag;
import site.thatkid.chickenAC.checks.SettingsChecks;
import site.thatkid.chickenAC.checks.movement.MovementChecks;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SettingsChecks(name = "Flight", description = "Flags people for flying", category = Category.MOVEMENT)
public class FlightCheck {
    private MovementChecks check = MovementChecks.FLIGHT;
    // Map to track players' previous Y positions
    private Map<String, Double> previousYPositions = new HashMap<>();

    public FlightCheck() {
        getLogger().info("[ChickenAC] Flight check initialized");
    }

    public void performCheck(Player player) {
        if (player == null) {
            return;
        }

        // If the player is allowed to fly, skip further checks
        if (player.hasPermission("chicken-ac.allowflight")) {
            return;
        }

        Location currentLocation = player.getLocation();
        double currentY = currentLocation.getY();
        String playerKey = player.getUniqueId().toString();
        Double previousY = previousYPositions.get(playerKey);

        // Update the player's last known Y position
        previousYPositions.put(playerKey, currentY);

        // If the player is not on ground and hasn't changed altitude as expected, flag them
        if (!player.isOnGround()) {
            if (previousY != null && Math.abs(previousY - currentY) < 0.1) {
                flagPlayerForFlight(player);
            }
        }
    }

    private void flagPlayerForFlight(Player player) {
        getLogger().warning("[ChickenAC] Flagging " + player.getName() + " for possible flight hacking.");
        // If the player's flag count is greater than 10, ban them.
        if (OverFlag.get(player.getUniqueId().toString()) > 4) {
            // Calculate the ban expiration date. For example, 30 days from now.
            Date expiration = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30 days ban
            // Ban the player by name with a reason and the expiration date.
            getServer().getBanList(BanList.Type.NAME)
                    .addBan(player.getName(), "Hacking", expiration, "ChickenAC");
            // Immediately kick the player.
            player.kickPlayer("You have been banned for hacks.");
        } else {
            OverFlag.add(player.getUniqueId().toString());
        }
    }

}

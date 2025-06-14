package site.thatkid.chickenAC.checks.movement.flight;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import site.thatkid.chickenAC.checks.Category;
import site.thatkid.chickenAC.checks.SettingsChecks;
import site.thatkid.chickenAC.checks.movement.MovementChecks;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;
import java.util.HashMap;
import java.util.Map;

@SettingsChecks(name = "Flight", description = "Flags people for flying", category = Category.MOVEMENT)
public class FlightCheck {
    private MovementChecks check = MovementChecks.FLIGHT;
    // Map to track players' previous Y positions
    private Map<String, Double> previousYPositions = new HashMap<>();

    public FlightCheck() {
        getLogger().info("Flight check initialized");
    }

    public void performCheck(Player player) {
        if (player == null) {
            getLogger().warning("Player is null");
            return;
        }

        // If the player is allowed to fly, skip further checks
        if (player.hasPermission("chicken-ac.allowflight")) {
            getLogger().info("Player " + player.getName() + " has flight permission.");
            return;
        }

        Location currentLocation = player.getLocation();
        double currentY = currentLocation.getY();
        Double previousY = previousYPositions.get(player.getUniqueId().toString());

        // Update the player's last known Y position
        previousYPositions.put(player.getUniqueId().toString(), currentY);

        // If the player is not on ground and hasn't changed altitude as expected, flag them
        if (!player.isOnGround()) {
            if (previousY != null && Math.abs(previousY - currentY) < 0.1) {
                flagPlayerForFlight(player);
                return;
            }
        }
        getLogger().info("Player " + player.getName() + " is on ground or moving normally.");
    }

    private void flagPlayerForFlight(Player player) {
        getLogger().warning("Flagging " + player.getName() + " for possible flight hacking.");
    }
}

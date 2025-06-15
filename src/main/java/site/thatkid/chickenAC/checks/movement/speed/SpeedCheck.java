package site.thatkid.chickenAC.checks.movement.speed;

import org.bukkit.BanList;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import site.thatkid.chickenAC.checks.Category;
import site.thatkid.chickenAC.checks.flag.OverFlag;
import site.thatkid.chickenAC.checks.SettingsChecks;
import site.thatkid.chickenAC.checks.movement.MovementChecks;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SettingsChecks(name = "Speed", description = "Flags people for going faster than normal", category = Category.MOVEMENT)
public class SpeedCheck {
    // We still reference the movement check enum for enabled state.
    private MovementChecks check = MovementChecks.FLIGHT; // Consider using a separate enum if needed.

    // Map to track players' previous locations
    private Map<String, Location> previousLocations = new HashMap<>();

    // Define a base threshold for horizontal speed (blocks per tick)
    // This value might need tuning based on server mechanics (like sprinting, potion effects, etc.)
    private final double BASE_SPEED_THRESHOLD = 0.7;

    public SpeedCheck() {
        getLogger().info("[ChickenAC] Speed check initialized");
    }

    public void performCheck(Player player) {
        if (player == null) {
            return;
        }

        // If the player is allowed to have high speed via permission, skip further checks.
        if (player.hasPermission("chicken-ac.allowspeed")) {
            return;
        }

        // Adjust the speed threshold if the player has a Speed potion effect.
        double effectiveThreshold = BASE_SPEED_THRESHOLD;
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            PotionEffect speedEffect = player.getPotionEffect(PotionEffectType.SPEED);
            if (speedEffect != null) {
                // Each level (amplifier + 1) increases speed by 20% by default.
                effectiveThreshold *= (1.0 + 0.2 * (speedEffect.getAmplifier() + 1));
            }
        }

        // Get current location
        Location currentLocation = player.getLocation();
        String playerId = player.getUniqueId().toString();

        // Retrieve the previous location
        Location previousLocation = previousLocations.get(playerId);

        // Update the map with the current location for the next tick
        previousLocations.put(playerId, currentLocation);

        // If we have a previous location, calculate horizontal distance traveled.
        if (previousLocation != null) {
            double deltaX = currentLocation.getX() - previousLocation.getX();
            double deltaZ = currentLocation.getZ() - previousLocation.getZ();
            double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            // If the distance exceeds our threshold, flag the player.
            if (horizontalDistance > effectiveThreshold) {
                flagPlayerForSpeed(player, horizontalDistance, effectiveThreshold);
                return;
            }
        }
    }

    private void flagPlayerForSpeed(Player player, double speed, double threshold) {
        getLogger().warning("[ChickenAC] Flagging " + player.getName() + " for possible speed hacking. Speed: "
                + speed + " (Threshold: " + threshold + ")");
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

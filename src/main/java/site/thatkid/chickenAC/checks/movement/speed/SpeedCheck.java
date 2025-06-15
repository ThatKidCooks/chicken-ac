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

import java.util.Deque;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@SettingsChecks(name = "Speed", description = "Flags people for going faster than normal", category = Category.MOVEMENT)
public class SpeedCheck {
    private int notOnGroundWait = 0;
    private int ofGroundLength = 0;
    private MovementChecks check = MovementChecks.FLIGHT;

    // Track player's recent horizontal distances in a small rolling window (last 5 ticks)
    private Map<String, Deque<Double>> recentDistances = new HashMap<>();
    // Track the last location per player
    private Map<String, Location> previousLocations = new HashMap<>();

    // Base threshold: typical max horizontal speed for a sprinting player (blocks/tick).
    // Adjust this value based on your server's normal gameplay dynamics.
    private final double BASE_SPEED_THRESHOLD = 6.7;

    // Number of ticks over which we'll average movement.
    private final int WINDOW_SIZE = 5;

    public SpeedCheck() {
        getLogger().info("[ChickenAC] Speed check initialized");
    }

    public void performCheck(Player player) {
        if (player == null) return;
        if (player.hasPermission("chicken-ac.allowspeed")) return;

        // Adjust threshold based on potion effects: base speed increases by ~20% per amplifier level.
        double effectiveThreshold = BASE_SPEED_THRESHOLD;
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.SPEED);
            if (effect != null) {
                effectiveThreshold *= (1.0 + 0.2 * (effect.getAmplifier() + 1));
            }
        }

        // Get current location and player ID
        Location currentLocation = player.getLocation();
        String playerId = player.getUniqueId().toString();

        // Get the previous location – if null, initialize and exit for now
        Location previousLocation = previousLocations.get(playerId);
        previousLocations.put(playerId, currentLocation);
        if (previousLocation == null) return;

        // Calculate horizontal distance moved this tick.
        double deltaX = currentLocation.getX() - previousLocation.getX();
        double deltaZ = currentLocation.getZ() - previousLocation.getZ();
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        // Update the rolling window of distances.
        Deque<Double> distances = recentDistances.getOrDefault(playerId, new LinkedList<>());
        if (distances.size() >= WINDOW_SIZE) {
            distances.pollFirst();
        }
        distances.addLast(horizontalDistance);
        recentDistances.put(playerId, distances);

        // Compute the average distance over the window.
        double total = 0;
        for (Double dist : distances) {
            total += dist;
        }
        double avgDistance = total / distances.size();

        // If average horizontal distance exceeds threshold, flag the player.
        if (avgDistance > effectiveThreshold) {
            flagPlayerForSpeed(player, avgDistance, effectiveThreshold);
        }
    }

    private void flagPlayerForSpeed(Player player, double avgSpeed, double threshold) {
        if (player.isOnGround()) {
            if (notOnGroundWait > 0) {
                notOnGroundWait--;
                return;
            }
            getLogger().warning("[ChickenAC] Flagging " + player.getName() + " for possible speed hacking. " +
                    "Avg Speed: " + avgSpeed + " (Threshold: " + threshold + ")");
            String playerId = player.getUniqueId().toString();
            int flags = OverFlag.get(playerId);

            OverFlag.add(playerId);

            if (flags + 1 > 4) {
                Date expiration = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
                getServer().getBanList(BanList.Type.NAME)
                        .addBan(player.getName(), "Hacking", expiration, "ChickenAC");
                player.kickPlayer("You have been banned for hacks.");
            }
        } else {
            if (ofGroundLength > 2) {
                notOnGroundWait = 6;
                ofGroundLength = 0;
            } else {
                ofGroundLength++;
                notOnGroundWait = 0;
            }
        }
    }
}

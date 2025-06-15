package site.thatkid.chickenAC.checks.combat.killaura;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import site.thatkid.chickenAC.checks.Category;
import site.thatkid.chickenAC.checks.SettingsChecks;
import site.thatkid.chickenAC.checks.combat.CombatChecks;
import site.thatkid.chickenAC.checks.flag.OverFlag;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SettingsChecks(name = "Kill Aura", description = "Stops people from using Kill Aura", category = Category.COMBAT)
public class KillAuraCheck implements Listener {

    private CombatChecks killAuraCheckEnum = CombatChecks.KILLAURA;

    private final Plugin plugin;
    // Use the local HeadRotationData defined below instead of one in ScaffoldCheck
    private final Map<UUID, HeadRotationData> playerHeadRotation = new ConcurrentHashMap<>();
    // Constant for maximum allowed head rotation speed (degrees per millisecond).
    private static final double MAX_HEAD_ROTATION_SPEED = 0.5;  // adjust as needed

    // Flag and a message builder for reporting reasons
    private boolean flag = false;
    private StringBuilder message = new StringBuilder();

    // Local inner class to store head rotation data
    private static class HeadRotationData {
        long timestamp;
        double yaw;

        public HeadRotationData(long timestamp, double yaw) {
            this.timestamp = timestamp;
            this.yaw = yaw;
        }
    }

    public KillAuraCheck(Plugin plugin) {
        this.plugin = plugin;
        // Register our event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (killAuraCheckEnum.isEnabled()) {
            // Only process if the damager is a player (this means a player attacked an entity)
            if (!(event.getDamager() instanceof Player))
                return;

            Player player = (Player) event.getDamager();
            Entity target = event.getEntity();
            UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();
            Location eyeLocation = player.getEyeLocation();
            Vector lookDirection = eyeLocation.getDirection().normalize();

            // Check if the player has line-of-sight to the target.
            // If not, assume the hit was through a block.
            if (!player.hasLineOfSight(target)) {
                Bukkit.getLogger().warning("[ChickenAC] " + player.getName() +
                        " hit " + target.getName() + " through a block.");
                if (OverFlag.get(player.getUniqueId().toString()) > 4) {
                    Date expiration = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30 days ban
                    Bukkit.getServer().getBanList(BanList.Type.NAME)
                            .addBan(player.getName(), "Hacking", expiration, "ChickenAC");
                    Bukkit.getServer().getBanList(BanList.Type.IP)
                            .addBan(player.getAddress().getAddress().getHostAddress(), "Hacking", expiration, "ChickenAC");
                    player.kickPlayer("You have been banned for hacks.");
                } else {
                    OverFlag.add(player.getUniqueId().toString());
                }
                return; // exit so that the rest of the check is not processed.
            }

            // Perform head rotation check.
            HeadRotationData previousData = playerHeadRotation.get(uuid);
            double headRotationSpeed = 0.0;
            if (previousData != null) {
                long dt = now - previousData.timestamp;
                // Normalize yaw difference accounting for wrap-around (-180 to 180)
                double yawDiff = Math.abs(normalizeAngle(eyeLocation.getYaw() - previousData.yaw));
                if (dt > 0) {
                    headRotationSpeed = yawDiff / dt;
                    if (headRotationSpeed > MAX_HEAD_ROTATION_SPEED) {
                        flag = true;
                        message.append("Head rotation too fast (")
                                .append(String.format("%.2f", headRotationSpeed))
                                .append("°/ms > ")
                                .append(MAX_HEAD_ROTATION_SPEED)
                                .append("°/ms). ");
                    }
                }
            }
            // Update stored rotation data
            playerHeadRotation.put(uuid, new HeadRotationData(now, eyeLocation.getYaw()));

            // If any suspicious behavior was detected, log it and enforce escalation.
            if (flag) {
                Bukkit.getLogger().warning("[ChickenAC] " + player.getName() +
                        " hit " + target.getName() + " suspiciously: " + message.toString());
                if (OverFlag.get(player.getUniqueId().toString()) > 4) {
                    Date expiration = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30 days ban
                    Bukkit.getServer().getBanList(BanList.Type.NAME)
                            .addBan(player.getName(), "Hacking", expiration, "ChickenAC");
                    Bukkit.getServer().getBanList(BanList.Type.IP)
                            .addBan(player.getAddress().getAddress().getHostAddress(), "Hacking", expiration, "ChickenAC");
                    player.kickPlayer("You have been banned for hacks.");
                } else {
                    OverFlag.add(player.getUniqueId().toString());
                }
                // Reset the flag and message for next check.
                flag = false;
                message = new StringBuilder();
            }
        }
    }

    /**
     * Normalize an angle in degrees to the range [-180, 180].
     */
    private double normalizeAngle(double angle) {
        angle %= 360.0;
        if (angle > 180.0) {
            angle -= 360.0;
        } else if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }
}


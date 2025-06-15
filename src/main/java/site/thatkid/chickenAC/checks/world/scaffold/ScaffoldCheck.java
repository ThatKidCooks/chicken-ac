package site.thatkid.chickenAC.checks.world.scaffold;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import site.thatkid.chickenAC.checks.Category;
import site.thatkid.chickenAC.checks.flag.OverFlag;
import site.thatkid.chickenAC.checks.SettingsChecks;
import site.thatkid.chickenAC.checks.world.WorldChecks;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.bukkit.Bukkit.getServer;

@SettingsChecks(name = "Scaffold Check", description = "Flags every suspicious block placement immediately, including abnormal head rotations", category = Category.WORLD)
public class ScaffoldCheck implements Listener {
    private WorldChecks scaffoldCheckEnum =  WorldChecks.SCAFFOLD;

    // Thresholds for block placement validation.
    private static final double MAX_REACH_DISTANCE = 5.0;         // Maximum allowed distance (in blocks)
    private static final double MAX_ALLOWED_ANGLE_DEGREES = 60.0;   // Maximum allowed deviation angle (in degrees)

    // Threshold for head rotation speed.
    // For example, 0.6 means a player rotating faster than 0.6 degree per millisecond is flagged.
    private static final double MAX_HEAD_ROTATION_SPEED = 0.6;      // degrees per millisecond

    private final Plugin plugin;

    // Store each player's last head rotation data.
    private final Map<UUID, HeadRotationData> playerHeadRotation = new ConcurrentHashMap<>();

    // Data holder for tracking player's last head yaw and when it was recorded.
    private static class HeadRotationData {
        long timestamp;
        double yaw;

        public HeadRotationData(long timestamp, double yaw) {
            this.timestamp = timestamp;
            this.yaw = yaw;
        }
    }

    /**
     * Constructor registers this listener.
     */
    public ScaffoldCheck(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * On every block placement, we verify three things:
     * 1. The block's placement is within acceptable distance.
     * 2. The block is placed in the general direction of the player's view.
     * 3. The player's head does not rotate abnormally fast.
     *
     * Any violation is immediately logged.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (scaffoldCheckEnum.isEnabled()) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();
            Location eyeLocation = player.getEyeLocation();
            Vector lookDirection = eyeLocation.getDirection().normalize();

            // Calculate block center position.
            Location blockCenter = event.getBlockPlaced().getLocation().add(0.5, 0.5, 0.5);
            Vector vectorToBlock = blockCenter.toVector().subtract(eyeLocation.toVector());
            double distance = vectorToBlock.length();

            // Compute angle between player's current view and vector toward the block.
            Vector directionToBlock = vectorToBlock.clone().normalize();
            double dot = lookDirection.dot(directionToBlock);
            // Clamp in case of floating point issues.
            dot = Math.max(-1.0, Math.min(1.0, dot));
            double angle = Math.toDegrees(Math.acos(dot));

            boolean flag = false;
            StringBuilder message = new StringBuilder();

            // Check distance and angle thresholds.
            if (distance > MAX_REACH_DISTANCE) {
                flag = true;
                message.append("Distance abnormal (").append(String.format("%.2f", distance)).append(" > ").append(MAX_REACH_DISTANCE).append("). ");
                // If the player's flag count is greater than 10, ban them.
                if (OverFlag.get(player.getUniqueId().toString()) > 10) {
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
            if (angle > MAX_ALLOWED_ANGLE_DEGREES) {
                flag = true;
                message.append("Angle abnormal (").append(String.format("%.2f", angle)).append("° > ").append(MAX_ALLOWED_ANGLE_DEGREES).append("°). ");
            }

            // Check head rotation speed.
            HeadRotationData previousData = playerHeadRotation.get(uuid);
            double headRotationSpeed = 0;
            if (previousData != null) {
                long dt = now - previousData.timestamp;
                // Normalize yaw difference accounting for wrap-around (-180 to 180)
                double yawDiff = Math.abs(normalizeAngle(eyeLocation.getYaw() - previousData.yaw));
                if (dt > 0) {
                    headRotationSpeed = yawDiff / dt;
                    if (headRotationSpeed > MAX_HEAD_ROTATION_SPEED) {
                        flag = true;
                        message.append("Head rotation too fast (").append(String.format("%.2f", headRotationSpeed))
                                .append("°/ms > ").append(MAX_HEAD_ROTATION_SPEED).append("°/ms). ");
                    }
                }
            }
            // Update the last head rotation data for this player.
            playerHeadRotation.put(uuid, new HeadRotationData(now, eyeLocation.getYaw()));

            // If any validation was violated, flag the placement.
            if (flag) {
                Bukkit.getLogger().warning("[ChickenAC] " + player.getName() + " placed a block suspiciously: " + message);
                // If the player's flag count is greater than 10, ban them.
                if (OverFlag.get(player.getUniqueId().toString()) > 4) {
                    // Calculate the ban expiration date. For example, 30 days from now.
                    Date expiration = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30 days ban
                    // Ban the player by name with a reason and the expiration date.
                    getServer().getBanList(BanList.Type.NAME)
                            .addBan(player.getName(), "Hacking", expiration, "ChickenAC");
                    // Ban the players ip
                    getServer().getBanList(BanList.Type.IP)
                            .addBan(player.getAddress().getAddress().getHostAddress(), "Hacking", expiration, "ChickenAC");
                    // Immediately kick the player.
                    player.kickPlayer("You have been banned for hacks.");
                } else {
                    OverFlag.add(player.getUniqueId().toString());
                }
            }
        }
        }

        /**
         * Normalizes an angle to the range [-180, 180].
         */
        private double normalizeAngle ( double angle){
            angle %= 360.0;
            if (angle > 180.0) {
                angle -= 360.0;
            } else if (angle < -180.0) {
                angle += 360.0;
            }
            return angle;
        }
}

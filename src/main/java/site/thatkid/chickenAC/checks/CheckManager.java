package site.thatkid.chickenAC.checks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import site.thatkid.chickenAC.checks.flag.RemoveFlags;
import site.thatkid.chickenAC.checks.movement.MovementChecks;
import site.thatkid.chickenAC.checks.movement.flight.FlightCheck;
import site.thatkid.chickenAC.checks.movement.speed.SpeedCheck;
import site.thatkid.chickenAC.checks.player.PlayerChecks;
import site.thatkid.chickenAC.checks.world.WorldChecks;
import site.thatkid.chickenAC.checks.world.scaffold.ScaffoldCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class CheckManager {

    private Map<Category, List<Class<?>>> checksByCategory = new HashMap<>();
    private final Plugin plugin; // Store the plugin instance
    // Reference to the movement check enum (shows enabled status)
    private MovementChecks flightCheckEnum = MovementChecks.FLIGHT;
    private MovementChecks speedCheckEnum = MovementChecks.SPEED;
    private PlayerChecks timerCheckEnum = PlayerChecks.TIMER;
    // Store a single instance of FlightCheck
    private FlightCheck flightCheckInstance;
    private SpeedCheck speedCheckInstance;
    private ScaffoldCheck scaffoldCheckInstance;

    public CheckManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerChecks() {
        // Register the FlightCheck for configuration or logging
        registerCheck(FlightCheck.class);
        registerCheck(SpeedCheck.class);
        registerCheck(ScaffoldCheck.class);

        // Create check instances with the plugin instance
        flightCheckInstance = new FlightCheck();
        speedCheckInstance = new SpeedCheck();
        scaffoldCheckInstance = new ScaffoldCheck(plugin);

        // Schedule a repeating task that runs the check logic every 80 ticks
        getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                // Iterate over every online player and perform the checks
                for (Player player : getServer().getOnlinePlayers()) {
                    if (flightCheckEnum.isEnabled()) {
                        flightCheckInstance.performCheck(player);
                    }
                    if (speedCheckEnum.isEnabled()) {
                        speedCheckInstance.performCheck(player);
                    }
                }
            }
        }, 0L, 20L);
    }

    public void registerCheck(Class<?> clazz) {
        if (clazz.isAnnotationPresent(SettingsChecks.class)) {
            SettingsChecks annotation = clazz.getAnnotation(SettingsChecks.class);
            Category cat = annotation.category();
            checksByCategory.computeIfAbsent(cat, k -> new ArrayList<>()).add(clazz);
            System.out.println("Registered check: " + annotation.name() + " (" + cat + ")");
        }
    }

    public Map<Category, List<Class<?>>> getChecks() {
        return checksByCategory;
    }
}

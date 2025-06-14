package site.thatkid.chickenAC.checks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import site.thatkid.chickenAC.checks.movement.MovementChecks;
import site.thatkid.chickenAC.checks.movement.flight.FlightCheck;
import site.thatkid.chickenAC.checks.movement.speed.SpeedCheck;

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
    // Store a single instance of FlightCheck
    private FlightCheck flightCheckInstance;
    private SpeedCheck speedCheckInstance;

    public CheckManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerChecks() {
        // Register the FlightCheck for configuration or logging
        registerCheck(FlightCheck.class);
        registerCheck(SpeedCheck.class);

        // Create the FlightCheck instance once
        flightCheckInstance = new FlightCheck();
        speedCheckInstance = new SpeedCheck();

        // Schedule a repeating task that runs the flight check logic every 40 ticks
        getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                    // Iterate over every online player and perform the flight check
                    for (Player player : getServer().getOnlinePlayers()) {
                        if (flightCheckEnum.isEnabled()) {
                            flightCheckInstance.performCheck(player);
                        } if (speedCheckEnum.isEnabled()) {
                            speedCheckInstance.performCheck(player);
                        }
                    }
            }
        }, 0L, 80L);
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

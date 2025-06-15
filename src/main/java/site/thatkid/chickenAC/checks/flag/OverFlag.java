package site.thatkid.chickenAC.checks.flag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.bukkit.Bukkit.getLogger;

public class OverFlag {

    // Static map so it can be accessed from static methods.
    private static final Map<String, Integer> flagMap = new ConcurrentHashMap<>();

    /**
     * Adds a key with a default value of 0 if it does not already exist.
     *
     * @param key the key to add
     */
    public static void add(String key) {
        key = key.toLowerCase();
        flagMap.putIfAbsent(key, 0);
        flagMap.put(key, flagMap.get(key) + 1);
        getLogger().info("Added a flag for " + key);
    }

    /**
     * Retrieves the current flag value for a given key.
     *
     * @param key the key to retrieve
     * @return the flag count, or 0 if the key does not exist
     */
    public static int get(String key) {
        key = key.toLowerCase();
        return flagMap.getOrDefault(key, 0);
    }

    public static void remove() {
        flagMap.clear();
    }

    public static Map<String, Integer> getFlags() {
        return flagMap;
    }
}

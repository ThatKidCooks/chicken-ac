package site.thatkid.chickenAC.checks.flag;

import org.bukkit.plugin.Plugin;
import static org.bukkit.Bukkit.getServer;

public class RemoveFlags {
    private Plugin plugin;

    public RemoveFlags(Plugin plugin) {
        this.plugin = plugin;
        getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                OverFlag.remove();
            }
        }, 0L, 1200);
    }
}

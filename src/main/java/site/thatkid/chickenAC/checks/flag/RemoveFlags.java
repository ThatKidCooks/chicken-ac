package site.thatkid.chickenAC.checks.flag;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

public class RemoveFlags {
    private Plugin plugin;

    public RemoveFlags() {
        getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                OverFlag overFlag = new OverFlag();
                OverFlag.remove();
            }
        }, 0L, 12000L);
    }
}

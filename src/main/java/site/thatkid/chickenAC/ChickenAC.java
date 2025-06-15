package site.thatkid.chickenAC;

import org.bukkit.plugin.java.JavaPlugin;
import site.thatkid.chickenAC.checks.CheckManager;
import site.thatkid.chickenAC.checks.flag.RemoveFlags;
import site.thatkid.chickenAC.listeners.CheckGUIClickListener;
import site.thatkid.chickenAC.listeners.MovementGUIClickListener;
import site.thatkid.chickenAC.listeners.WorldGUIClickListener;

public final class ChickenAC extends JavaPlugin {

    CheckManager checkManager;
    String MOD_ID = "chicken-ac";

    @Override
    public void onEnable() {
        getLogger().info(MOD_ID + "initialized!");
        checkManager = new CheckManager(this);
        checkManager.registerChecks();
        this.getCommand("anticheatgui").setExecutor(new AnticheatGUICommand(checkManager));
        getServer().getPluginManager().registerEvents(new CheckGUIClickListener(), this);
        getServer().getPluginManager().registerEvents(new MovementGUIClickListener(), this);
        getServer().getPluginManager().registerEvents(new WorldGUIClickListener(), this);
        new RemoveFlags(this);
    }

    @Override
    public void onDisable() {
        getLogger().info(MOD_ID + "shutting down!");    }
}

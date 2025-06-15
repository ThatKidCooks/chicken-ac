package site.thatkid.chickenAC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.thatkid.chickenAC.checks.CheckManager;

public class AnticheatGUICommand implements CommandExecutor {

    private final CheckManager checkManager;

    public AnticheatGUICommand(CheckManager checkManager) {
        this.checkManager = checkManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!(sender.hasPermission("chicken-ac.openacpanel"))) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }
        
        Player player = (Player) sender;
        CheckGUI checkGUI = new CheckGUI(checkManager);
        checkGUI.openGUI(player);
        
        return true;
    }
}

package site.thatkid.chickenAC.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import site.thatkid.chickenAC.checks.Category;
import site.thatkid.chickenAC.checks.movement.MovementScreen;
import site.thatkid.chickenAC.checks.player.PlayerScreen;
import site.thatkid.chickenAC.checks.world.WorldScreen;

public class CheckGUIClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // If no inventory was clicked, exit
        if (event.getClickedInventory() == null) {
            return;
        }

        // Only work with our custom GUI inventory
        if (!event.getView().getTitle().equals("Anti-Cheat Checks")) {
            return;
        }

        // Prevent default item movement
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();
        String strippedName = ChatColor.stripColor(itemName).trim();

        System.out.println("DEBUG: Clicked item name (stripped): " + strippedName);

        Player player = (Player) event.getWhoClicked();
        // Check the category and open the appropriate screen
        if (strippedName.equalsIgnoreCase(Category.MOVEMENT.name())) {
            MovementScreen movementScreen = new MovementScreen();
            player.openInventory(movementScreen.getInventory());
        } else if (strippedName.equalsIgnoreCase(Category.WORLD.name())) {
            WorldScreen worldScreen = new WorldScreen();
            player.openInventory(worldScreen.getInventory());
        } else if (strippedName.equalsIgnoreCase(Category.PLAYER.name())) {
            PlayerScreen playerScreen = new PlayerScreen();
            player.openInventory(playerScreen.getInventory());
        }
    }
}

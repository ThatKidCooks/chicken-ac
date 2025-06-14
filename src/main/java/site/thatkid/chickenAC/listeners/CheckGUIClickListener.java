package site.thatkid.chickenAC.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import site.thatkid.chickenAC.checks.Category;
import site.thatkid.chickenAC.checks.movement.MovementScreen;

public class CheckGUIClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // If no inventory was clicked, exit
        if (event.getClickedInventory() == null) {
            return;
        }

        Inventory clickedInventory = event.getView().getTopInventory();
        // Check that the inventory has the title "Anti-Cheat Checks" (adjust if necessary).
        if (!event.getView().getTitle().equals("Anti-Cheat Checks")) {
            return;
        }

        // Prevent taking items out of our custom GUI
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();

        // Check if the clicked item is the "Movement" category.
        if (itemName.equalsIgnoreCase(Category.MOVEMENT.name())) {
            // Open the MovementScreen GUI. Make sure you pass necessary parameters if needed.
            MovementScreen movementScreen = new MovementScreen();
            Player player = (Player) event.getWhoClicked();
            player.openInventory(movementScreen.getInventory());
        }
        
        // Add else if for other categories if needed.
    }
}

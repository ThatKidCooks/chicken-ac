package site.thatkid.chickenAC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import site.thatkid.chickenAC.checks.world.WorldChecks;

import java.util.Arrays;
import java.util.List;

public class WorldGUIClickListener implements Listener {

    @EventHandler
    public void onWorldGUIClick(InventoryClickEvent event) {
        // Check that the inventory is our World Checks GUI by verifying its title.
        if (!event.getView().getTitle().equals("World Checks")) {
            return;
        }

        // Cancel the event to prevent players from removing items.
        event.setCancelled(true);

        int clickedSlot = event.getSlot();
        WorldChecks[] checks = WorldChecks.values();

        // The slot index should correspond to the enum order.
        if (clickedSlot < 0 || clickedSlot >= checks.length) {
            return;
        }

        // Toggle the selected WorldCheck.
        WorldChecks check = checks[clickedSlot];
        check.toggle();

        // Update the GUI to reflect the new state.
        Inventory gui = event.getInventory();
        ItemStack item = gui.getItem(clickedSlot);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = Arrays.asList(
                    "Enabled: " + check.isEnabled(),
                    "Click to toggle check."
            );
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(clickedSlot, item);
        }

        // Notify the player about the change.
        Player player = (Player) event.getWhoClicked();
        player.sendMessage(check.getDisplayName() + " toggled to " + check.isEnabled());
    }
}

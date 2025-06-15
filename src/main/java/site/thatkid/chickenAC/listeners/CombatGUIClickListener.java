package site.thatkid.chickenAC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import site.thatkid.chickenAC.checks.combat.CombatChecks;

import java.util.Arrays;
import java.util.List;

public class CombatGUIClickListener implements Listener {

    @EventHandler
    public void onCombatGUIClick(InventoryClickEvent event) {
        // Check if the inventory is our Combat Checks GUI by verifying its title.
        if (!event.getView().getTitle().equals("Combat Checks")) {
            return;
        }
        
        // Cancel the event to prevent taking items from the GUI.
        event.setCancelled(true);
        
        int clickedSlot = event.getSlot();
        CombatChecks[] checks = CombatChecks.values();
        
        // Because we populated the GUI in order, the slot index should correspond to the enum order.
        if (clickedSlot < 0 || clickedSlot >= checks.length) {
            return;
        }
        
        // Toggle the check on click.
        CombatChecks check = checks[clickedSlot];
        check.toggle();
        
        // Update the GUI item to display the new state.
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
            // Optionally, you can update the slot with the new item.
            gui.setItem(clickedSlot, item);
        }
        
        // Optionally, notify the player.
        Player player = (Player) event.getWhoClicked();
        player.sendMessage(check.getDisplayName() + " toggled to " + check.isEnabled());
    }
}

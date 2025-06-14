package site.thatkid.chickenAC.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class MovementScreen {

    private final Inventory gui;

    public MovementScreen() {
        gui = Bukkit.createInventory(null, 9, "Movement Checks");
        populateGUI();
    }

    private void populateGUI() {
        int slot = 0;
        for (MovementChecks check : MovementChecks.values()) {
            if (slot >= gui.getSize()) {
                break;
            }

            // Create an item representing the check.
            ItemStack checkItem = new ItemStack(Material.PAPER);
            ItemMeta meta = checkItem.getItemMeta();
            meta.setDisplayName(check.getDisplayName());
            List<String> lore = Arrays.asList(
                    "Enabled: " + check.isEnabled(),
                    "Click to toggle check."
            );
            meta.setLore(lore);
            checkItem.setItemMeta(meta);
            gui.setItem(slot, checkItem);
            slot++;
        }
    }

    public Inventory getInventory() {
        return gui;
    }
}

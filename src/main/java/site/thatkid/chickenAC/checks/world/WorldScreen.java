package site.thatkid.chickenAC.checks.world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import site.thatkid.chickenAC.checks.world.WorldChecks;

import java.util.Arrays;
import java.util.List;

public class WorldScreen {

    private final Inventory gui;

    public WorldScreen() {
        gui = Bukkit.createInventory(null, 9, "World Checks");
        populateGUI();
    }

    private void populateGUI() {
        int slot = 0;
        for (WorldChecks check : WorldChecks.values()) {
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

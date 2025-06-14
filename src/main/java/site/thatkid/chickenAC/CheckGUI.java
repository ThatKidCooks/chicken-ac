package site.thatkid.chickenAC;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import site.thatkid.chickenAC.checks.Category;
import site.thatkid.chickenAC.checks.CheckManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CheckGUI {

    private Inventory gui;

    public CheckGUI(CheckManager checkManager) {
        // Create a GUI inventory. Size might depend on how many categories you have.
        gui = Bukkit.createInventory(null, 9, "Anti-Cheat Checks");

        populateGUI(checkManager.getChecks());
    }

    private void populateGUI(Map<Category, List<Class<?>>> checksByCategory) {
        int slot = 0;
        for (Category cat : checksByCategory.keySet()) {
            // Create an item representing the category
            ItemStack categoryItem = new ItemStack(Material.PAPER);
            ItemMeta meta = categoryItem.getItemMeta();
            meta.setDisplayName(cat.name());
            List<String> lore = Arrays.asList(
                "Checks: " + checksByCategory.get(cat).size(),
                "Click to view details."
            );
            meta.setLore(lore);
            categoryItem.setItemMeta(meta);

            gui.setItem(slot, categoryItem);
            slot++;
        }
    }

    public void openGUI(Player player) {
        player.openInventory(gui);
    }
}

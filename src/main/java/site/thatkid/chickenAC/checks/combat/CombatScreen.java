package site.thatkid.chickenAC.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class CombatScreen {

    private final Inventory gui;

    public CombatScreen() {
        gui = Bukkit.createInventory(null, 9, "Combat Checks");
        populateGUI();
    }

    private void populateGUI() {
        int slot = 0;
        for (CombatChecks check : CombatChecks.values()) {
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

package io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link Listener} prevents any {@link SlimefunItem} from being used in a
 * crafting table.
 *
 * @author TheBusyBiscuit
 *
 */
public class CraftingTableListener implements SlimefunCraftingListener {

    public CraftingTableListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        var result = e.getInventory().getResult();
        if (SlimefunItem.getByItem(result) != null) return;

        for (ItemStack item : e.getInventory().getContents()) {
            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            if (sfItem != null && !sfItem.isUseableInWorkbench()) {
                e.setResult(Result.DENY);
                Slimefun.getLocalization().sendMessage(e.getWhoClicked(), "workbench.not-enhanced", true);
                break;
            }
        }
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
        var result = e.getInventory().getResult();

        if (result != null) {
            if (SlimefunItem.getByItem(result) != null) return;

            for (ItemStack item : e.getInventory().getContents()) {
                SlimefunItem sfItem = SlimefunItem.getByItem(item);

                if (sfItem != null && !sfItem.isUseableInWorkbench()) {
                    e.getInventory().setResult(null);
                    break;
                }
            }
        }
    }
}

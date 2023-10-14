package io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link Listener} prevents any {@link SlimefunItem} from being used in a
 * smithing table.
 *
 * @author Sefiraat
 */
public class SmithingTableListener implements SlimefunCraftingListener {

    public SmithingTableListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmith(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.SMITHING && e.getWhoClicked() instanceof Player p) {
            ItemStack materialItem;

            if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_20)) {
                materialItem = e.getInventory().getContents()[2];
            } else {
                materialItem = e.getInventory().getContents()[1];
            }

            if (isUnallowed(materialItem)) {
                e.setResult(Result.DENY);
                Slimefun.getLocalization().sendMessage(p, "smithing_table.not-working", true);
            }
        }
    }
}

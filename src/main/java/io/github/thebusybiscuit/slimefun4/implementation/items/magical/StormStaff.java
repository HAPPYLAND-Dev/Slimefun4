package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import io.github.thebusybiscuit.cscorelib2.chat.ChatColors;
import io.github.thebusybiscuit.cscorelib2.protection.ProtectableAction;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemUseHandler;
import me.mrCookieSlime.Slimefun.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.*;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * This {@link SlimefunItem} casts a {@link LightningStrike} where you are pointing.
 * Unlike the other Staves, it has a limited amount of uses.
 *
 * @author Linox
 * @author Walshy
 * @author TheBusyBiscuit
 */
public class StormStaff extends SimpleSlimefunItem<ItemUseHandler> {

    private static final NamespacedKey usageKey = new NamespacedKey(SlimefunPlugin.instance, "stormstaff_usage");
    public static final int MAX_USES = 8;

    public StormStaff(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe, getCraftedOutput());
    }

    private static ItemStack getCraftedOutput() {
        ItemStack item = SlimefunItems.STAFF_STORM.clone();
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();

        lore.set(4, ChatColors.color("&7还可以使用 &e" + MAX_USES + " 次"));

        im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();
            ItemStack item = e.getItem();

            if (p.getFoodLevel() >= 4 || p.getGameMode() == GameMode.CREATIVE) {
                // Get a target block with max. 30 blocks of distance
                Location loc = p.getTargetBlock(null, 30).getLocation();

                if (loc.getWorld() != null && loc.getChunk().isLoaded()) {
                    if (loc.getWorld().getPVP() && SlimefunPlugin.getProtectionManager().hasPermission(p, loc, ProtectableAction.PVP)) {
                        e.cancel();
                        useItem(p, item, loc);
                    } else {
                        SlimefunPlugin.getLocal().sendMessage(p, "messages.no-pvp", true);
                    }
                }
            } else {
                SlimefunPlugin.getLocal().sendMessage(p, "messages.hungry", true);
            }
        };
    }

    private void useItem(Player p, ItemStack item, Location loc) {
        loc.getWorld().strikeLightning(loc);

        if (p.getInventory().getItemInMainHand().getType() == Material.SHEARS) {
            return;
        }

        if (p.getGameMode() != GameMode.CREATIVE) {
            FoodLevelChangeEvent event = new FoodLevelChangeEvent(p, p.getFoodLevel() - 4);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                p.setFoodLevel(event.getFoodLevel());
            }
        }

        ItemMeta meta = item.getItemMeta();
        int usesLeft = meta.getPersistentDataContainer().getOrDefault(usageKey, PersistentDataType.INTEGER, MAX_USES);

        if (usesLeft == 1) {
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            item.setAmount(0);
        } else {
            usesLeft--;
            meta.getPersistentDataContainer().set(usageKey, PersistentDataType.INTEGER, usesLeft);

            List<String> lore = meta.getLore();
            lore.set(4, ChatColors.color("&7还可以使用 &e" + usesLeft + " 次"));
            meta.setLore(lore);

            item.setItemMeta(meta);
        }
    }

}
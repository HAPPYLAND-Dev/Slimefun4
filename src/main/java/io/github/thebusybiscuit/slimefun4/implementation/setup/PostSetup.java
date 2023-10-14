package io.github.thebusybiscuit.slimefun4.implementation.setup;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.GrindStone;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.MakeshiftSmeltery;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.OreCrusher;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.Smeltery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import net.guizhanss.slimefun4.utils.WikiUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public final class PostSetup {

    private PostSetup() {}

    public static void setupWiki() {
        Slimefun.logger().log(Level.INFO, "加载 Wiki 页面...");

        WikiUtils.setupJson(Slimefun.instance(), (page) -> page.replace("#", "?id="));
    }

    public static void loadItems() {
        Iterator<SlimefunItem> iterator = Slimefun.getRegistry().getEnabledSlimefunItems().iterator();

        while (iterator.hasNext()) {
            SlimefunItem item = iterator.next();

            if (item == null) {
                Slimefun.logger().log(Level.WARNING, "Removed bugged Item ('NULL?')");
                iterator.remove();
            } else {
                try {
                    item.load();
                } catch (Exception | LinkageError x) {
                    item.error("Failed to properly load this Item", x);
                }
            }
        }

        CommandSender sender = Bukkit.getConsoleSender();

        int total = Slimefun.getRegistry().getEnabledSlimefunItems().size();
        int slimefunOnly = countNonAddonItems();

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GREEN + "######################### - Slimefun v" + Slimefun.getVersion() + " - #########################");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GREEN + "成功加载了 " + total + " 个物品和 " + Slimefun.getRegistry().getResearches().size() + " 个研究");
        sender.sendMessage(ChatColor.GREEN + "( " + slimefunOnly + " 物品来自本体, " + (total - slimefunOnly) + " 个物品来自 " + Slimefun.getInstalledAddons().size() + " 扩展 )");
        sender.sendMessage("");

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GREEN + " - 源码:      https://github.com/HAPPYLAND-Dev/Slimefun4");
        sender.sendMessage(ChatColor.GREEN + " - Bug 反馈:  https://github.com/HAPPYLAND-Dev/Slimefun4/issues");

        sender.sendMessage("");

        Slimefun.getItemCfg().save();
        Slimefun.getResearchCfg().save();
        Slimefun.getConfigManager().setAutoLoadingMode(true);
    }

    /**
     * This method counts the amount of {@link SlimefunItem SlimefunItems} registered
     * by Slimefun itself and not by any addons.
     *
     * @return The amount of {@link SlimefunItem SlimefunItems} added by Slimefun itself
     */
    private static int countNonAddonItems() {
        // @formatter:off
        return (int) Slimefun.getRegistry().getEnabledSlimefunItems().stream()
                        .filter(item -> item.getAddon() instanceof Slimefun)
                        .count();
        // @formatter:on
    }

    private static void registerMachineRecipe(String machine, int seconds, ItemStack[] input, ItemStack[] output) {
        for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (item instanceof AContainer container && container.getMachineIdentifier().equals(machine)) {
                container.registerRecipe(seconds, input, output);
            }
        }
    }
}

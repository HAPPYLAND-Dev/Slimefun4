package io.github.thebusybiscuit.slimefun4.implementation.items.teleporter;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.gps.GPSNetwork;
import io.github.thebusybiscuit.slimefun4.api.gps.TeleportationManager;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The Teleporter is a {@link SlimefunItem} that can be placed down and allows a {@link Player}
 * to display to any of his waypoints he set via his {@link GPSNetwork}.
 *
 * @author TheBusyBiscuit
 *
 * @see GPSNetwork
 * @see TeleportationManager
 *
 */
public class Teleporter extends SimpleSlimefunItem<BlockPlaceHandler> {

    @ParametersAreNonnullByDefault
    public Teleporter(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public BlockPlaceHandler getItemHandler() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                StorageCacheUtils.setData(
                        e.getBlock().getLocation(),
                        "owner",
                        e.getPlayer().getUniqueId().toString());
            }
        };
    }
}

package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.KnowledgeFlask;
import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link ExpCollector} is a machine which picks up any nearby {@link ExperienceOrb}
 * and produces a {@link KnowledgeFlask}.
 *
 * @author TheBusyBiscuit
 *
 */
public class ExpCollector extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    private final int[] border = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};

    private static final int ENERGY_CONSUMPTION = 10;
    private static final String DATA_KEY = "stored-exp";

    @ParametersAreNonnullByDefault
    public ExpCollector(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        createPreset(this, ChestMenuUtils.getBlankTexture(), this::constructMenu);

        addItemHandler(onPlace(), onBreak());
    }

    @Nonnull
    private BlockPlaceHandler onPlace() {
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

    @Nonnull
    private ItemHandler onBreak() {
        return new SimpleBlockBreakHandler() {

            @Override
            public void onBlockBreak(Block b) {
                BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());

                if (inv != null) {
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }
            }
        };
    }

    @Override
    public int[] getInputSlots() {
        return new int[0];
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] {12, 13, 14};
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return 1024;
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int slot : border) {
            preset.addItem(
                    slot, new CustomItemStack(Material.PURPLE_STAINED_GLASS_PANE, " "), (p, s, item, action) -> false);
        }
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, SlimefunBlockData data) {
                ExpCollector.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        });
    }

    protected void tick(Block block) {
        Location location = block.getLocation();
        Iterator<Entity> iterator = block.getWorld()
                .getNearbyEntities(location, 4.0, 4.0, 4.0, n -> n instanceof ExperienceOrb && n.isValid())
                .iterator();
        int experiencePoints = 0;

        while (iterator.hasNext() && experiencePoints == 0) {
            ExperienceOrb orb = (ExperienceOrb) iterator.next();

            if (getCharge(location) < ENERGY_CONSUMPTION) {
                return;
            }

            experiencePoints = getStoredExperience(location) + orb.getExperience();

            removeCharge(location, ENERGY_CONSUMPTION);
            orb.remove();
            produceFlasks(location, experiencePoints);
        }
    }

    /**
     * Produces Flasks of Knowledge for the given block until it either uses all stored
     * experience or runs out of room.
     *
     * @param location
     *                  The {@link Location} of the {@link ExpCollector} to produce flasks in.
     * @param experiencePoints
     *                  The number of experience points to use during production.
     */
    private void produceFlasks(@Nonnull Location location, int experiencePoints) {
        int withdrawn = 0;
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        for (int level = 0; level < getStoredExperience(location); level = level + 10) {
            if (menu.fits(SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, getOutputSlots())) {
                withdrawn = withdrawn + 10;
                menu.pushItem(SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE.clone(), getOutputSlots());
            } else {
                // There is no room for more bottles, so lets stop checking if more will fit.
                break;
            }
        }
        StorageCacheUtils.setData(location, DATA_KEY, String.valueOf(experiencePoints - withdrawn));
    }

    private int getStoredExperience(Location location) {
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
        String value = blockData.getData(DATA_KEY);

        if (value != null) {
            return Integer.parseInt(value);
        } else {
            blockData.setData(DATA_KEY, "0");
            return 0;
        }
    }
}

package io.github.thebusybiscuit.slimefun4.implementation.items.cargo;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link TrashCan} is a simple container which simply voids all
 * items that enter it.
 *
 * @author TheBusyBiscuit
 *
 */
public class TrashCan extends SlimefunItem implements InventoryBlock {

    private final int[] border = {0, 1, 2, 3, 5, 4, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    private final ItemStack background = new CustomItemStack(Material.RED_STAINED_GLASS_PANE, " ");

    @ParametersAreNonnullByDefault
    public TrashCan(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        createPreset(this, ChestMenuUtils.getBlankTexture(), this::constructMenu);
    }

    private void constructMenu(BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, background, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    public int[] getInputSlots() {
        return new int[] {10, 11, 12, 13, 14, 15, 16};
    }

    @Override
    public int[] getOutputSlots() {
        return new int[0];
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
                BlockMenu menu = data.getBlockMenu();

                for (int slot : getInputSlots()) {
                    menu.replaceExistingItem(slot, null);
                }
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }
}

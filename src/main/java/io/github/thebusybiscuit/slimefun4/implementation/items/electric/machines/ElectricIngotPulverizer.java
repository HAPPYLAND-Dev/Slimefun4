package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.MakeshiftSmeltery;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.Smeltery;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotHopperable;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;

import static io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils.isDust;

/**
 * The {@link ElectricIngotPulverizer} is an implementation of {@link AContainer} that allows
 * you to turn various Slimefun Ingots back into their dusts.
 * 
 * @author John000708
 * 
 * @see ElectricIngotFactory
 *
 */
public class ElectricIngotPulverizer extends AContainer implements RecipeDisplayItem, NotHopperable {

    public ElectricIngotPulverizer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.IRON_PICKAXE);
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>(recipes.size() * 2);

        for (MachineRecipe recipe : recipes) {
            displayRecipes.add(recipe.getInput()[0]);
            displayRecipes.add(recipe.getOutput()[0]);
        }

        return displayRecipes;
    }

    @Override
    public String getMachineIdentifier() {
        return "ELECTRIC_INGOT_PULVERIZER";
    }

    @Override
    protected void registerDefaultRecipes() {
        // this is an extra recipe on top of PostSetup.loadSmelteryRecipes() for converting
        // Vanilla Gold Ingot to Slimefun gold dust and Vanilla Copper Ingot into Slimefun copper dust
        registerRecipe(3, new ItemStack(Material.GOLD_INGOT), SlimefunItems.GOLD_DUST);

        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_17)) {
            registerRecipe(3, new ItemStack(Material.COPPER_INGOT), SlimefunItems.COPPER_DUST);
        }

        Smeltery smeltery = (Smeltery) SlimefunItems.SMELTERY.getItem();

        if (smeltery != null && !smeltery.isDisabled()) {
            MakeshiftSmeltery makeshiftSmeltery = ((MakeshiftSmeltery) SlimefunItems.MAKESHIFT_SMELTERY.getItem());
            ItemStack[] input = null;

            for (ItemStack[] output : smeltery.getRecipes()) {
                if (input == null) {
                    input = output;
                } else {
                    if (input[0] != null && output[0] != null) {
                        addSmelteryRecipe(input, output, makeshiftSmeltery);
                    }

                    input = null;
                }
            }

            List<MachineRecipe> recipes = this.getMachineRecipes();
            Collections.sort(recipes, Comparator.comparingInt(recipe -> recipe == null ? 0 : -recipe.getInput().length));
        }
    }

    private void addSmelteryRecipe(ItemStack[] input, ItemStack[] output, MakeshiftSmeltery makeshiftSmeltery) {
        List<ItemStack> ingredients = new ArrayList<>();

        // Filter out 'null' items
        for (ItemStack item : input) {
            if (item != null) {
                ingredients.add(item);
            }
        }

        // We want to redirect Dust to Ingot Recipes
        if (ingredients.size() == 1 && isDust(ingredients.get(0))) {
            makeshiftSmeltery.addRecipe(new ItemStack[]{ingredients.get(0)}, output[0]);
            super.registerRecipe(3, new ItemStack[]{output[0]}, new ItemStack[]{ingredients.get(0)});
        }
    }
}

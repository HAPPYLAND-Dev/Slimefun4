package io.github.thebusybiscuit.slimefun4.implementation.setup;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.time.Month;

/**
 * This class holds a reference to every {@link ItemGroup}
 * found in Slimefun itself.
 * <p>
 * Addons should use their own {@link ItemGroup} hence why the visible of this class was now
 * changed to package-private. Only {@link SlimefunItemSetup} has access to this class.
 *
 * @author TheBusyBiscuit
 * @see ItemGroup
 * @see LockedItemGroup
 * @see SeasonalItemGroup
 */
@SuppressWarnings("DataFlowIssue")
public class DefaultItemGroups {

    // Standard Item Groups
    public final ItemGroup weapons = new ItemGroup(new NamespacedKey(Slimefun.instance(), "weapons"), new CustomItemStack(Material.STRUCTURE_VOID, "&7Weapons"), 1);
    public final ItemGroup tools = new ItemGroup(new NamespacedKey(Slimefun.instance(), "tools"), new CustomItemStack(Material.STRUCTURE_VOID, "&7Tools"), 1);
    public final ItemGroup usefulItems = new ItemGroup(new NamespacedKey(Slimefun.instance(), "items"), new CustomItemStack(Material.STRUCTURE_VOID, "&7Useful Items"), 1);
    public final ItemGroup basicMachines = new ItemGroup(new NamespacedKey(Slimefun.instance(), "basic_machines"), new CustomItemStack(SlimefunItems.ENHANCED_CRAFTING_TABLE, "&7Basic Machines"), 1);
    public final ItemGroup food = new ItemGroup(new NamespacedKey(Slimefun.instance(), "food"), new CustomItemStack(Material.STRUCTURE_VOID, "&7Food"), 2);
    public final ItemGroup armor = new ItemGroup(new NamespacedKey(Slimefun.instance(), "armor"), new CustomItemStack(Material.STRUCTURE_VOID, "&7Armor"), 2);

    // Magical
    public final ItemGroup magicalResources = new ItemGroup(new NamespacedKey(Slimefun.instance(), "magical_items"), new CustomItemStack(Material.STRUCTURE_VOID, "&7Magical Items"), 2);
    public final ItemGroup magicalGadgets = new ItemGroup(new NamespacedKey(Slimefun.instance(), "magical_gadgets"), new CustomItemStack(Material.STRUCTURE_VOID, "&7Magical Gadgets"), 3);
    public final ItemGroup magicalArmor = new ItemGroup(new NamespacedKey(Slimefun.instance(), "magical_armor"), new CustomItemStack(Material.STRUCTURE_VOID, "&7Magical Armor"), 2);

    // Resources and tech stuff
    public final ItemGroup misc = new ItemGroup(new NamespacedKey(Slimefun.instance(), "misc"), new CustomItemStack(SlimefunItems.TIN_CAN, "&7Miscellaneous"), 2);
    public final ItemGroup technicalComponents = new ItemGroup(new NamespacedKey(Slimefun.instance(), "tech_misc"), new CustomItemStack(SlimefunItems.HEATING_COIL, "&7Technical Components"), 2);
    public final ItemGroup technicalGadgets = new ItemGroup(new NamespacedKey(Slimefun.instance(), "technical_gadgets"), new CustomItemStack(SlimefunItems.STEEL_JETPACK, "&7Technical Gadgets"), 3);
    public final ItemGroup resources = new ItemGroup(new NamespacedKey(Slimefun.instance(), "resources"), new CustomItemStack(SlimefunItems.SYNTHETIC_SAPPHIRE, "&7Resources"), 1);

    // Locked Item Groups
    public final LockedItemGroup electricity = new LockedItemGroup(new NamespacedKey(Slimefun.instance(), "electricity"), new CustomItemStack(Material.STRUCTURE_VOID, "&bEnergy and Electricity"), 4, basicMachines.getKey());
    public final LockedItemGroup androids = new LockedItemGroup(new NamespacedKey(Slimefun.instance(), "androids"), new CustomItemStack(Material.STRUCTURE_VOID, "&cProgrammable Androids"), 4, basicMachines.getKey());
    public final ItemGroup cargo = new LockedItemGroup(new NamespacedKey(Slimefun.instance(), "cargo"), new CustomItemStack(SlimefunItems.CARGO_MANAGER, "&cCargo Management"), 4, basicMachines.getKey());
    public final LockedItemGroup gps = new LockedItemGroup(new NamespacedKey(Slimefun.instance(), "gps"), new CustomItemStack(Material.STRUCTURE_VOID, "&bGPS-based Machines"), 4, basicMachines.getKey());

    // Seasonal Item Groups
    public final SeasonalItemGroup christmas = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "christmas"), Month.DECEMBER, 1, new CustomItemStack(Material.STRUCTURE_VOID, ChatUtils.christmas("Christmas") + " &7(December only)"));
    public final SeasonalItemGroup valentinesDay = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "valentines_day"), Month.FEBRUARY, 2, new CustomItemStack(Material.STRUCTURE_VOID, "&dValentine's Day" + " &7(14th February)"));
    public final SeasonalItemGroup easter = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "easter"), Month.APRIL, 2, new CustomItemStack(Material.STRUCTURE_VOID, "&6Easter" + " &7(April)"));
    public final SeasonalItemGroup birthday = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "birthday"), Month.OCTOBER, 1, new CustomItemStack(Material.STRUCTURE_VOID, "&a&lTheBusyBiscuit's Birthday &7(26th October)"));
    public final SeasonalItemGroup halloween = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "halloween"), Month.OCTOBER, 1, new CustomItemStack(Material.STRUCTURE_VOID, "&6&lHalloween &7(31st October)"));

    // Flex Item Groups
    public final FlexItemGroup rickFlexGroup = new RickFlexGroup(new NamespacedKey(Slimefun.instance(), "rick"));
}

package io.github.thebusybiscuit.slimefun4.core.guide.options;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.services.LocalizationService;
import io.github.thebusybiscuit.slimefun4.core.services.github.GitHubService;
import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This static utility class offers various methods that provide access to the
 * Settings menu of our {@link SlimefunGuide}.
 *
 * This menu is used to allow a {@link Player} to change things such as the {@link Language}.
 *
 * @author TheBusyBiscuit
 *
 * @see SlimefunGuide
 *
 */
public final class SlimefunGuideSettings {

    private static final int[] BACKGROUND_SLOTS = {
        1, 3, 5, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 48,
        50, 52, 53
    };
    private static final List<SlimefunGuideOption<?>> options = new ArrayList<>();

    static {
        options.add(new GuideModeOption());
        options.add(new FireworksOption());
        options.add(new LearningAnimationOption());
        options.add(new PlayerLanguageOption());
    }

    private SlimefunGuideSettings() {}

    public static <T> void addOption(@Nonnull SlimefunGuideOption<T> option) {
        options.add(option);
    }

    @ParametersAreNonnullByDefault
    public static void openSettings(Player p, ItemStack guide) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.settings"), ChestMenuUtils.getBlankTexture());

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(SoundEffect.GUIDE_OPEN_SETTING_SOUND::playFor);

        ChestMenuUtils.drawBackground(menu, BACKGROUND_SLOTS);

        addHeader(p, menu, guide);
        addConfigurableOptions(p, menu, guide);

        menu.open(p);
    }

    @ParametersAreNonnullByDefault
    private static void addHeader(Player p, ChestMenu menu, ItemStack guide) {
        LocalizationService locale = Slimefun.getLocalization();

        // @formatter:off
        menu.addItem(
                0,
                new CustomItemStack(
                        SlimefunGuide.getItem(SlimefunGuideMode.SURVIVAL_MODE),
                        "&e\u21E6 " + locale.getMessage(p, "guide.back.title"),
                        "",
                        "&7" + locale.getMessage(p, "guide.back.guide")));
        // @formatter:on

        menu.addMenuClickHandler(0, (pl, slot, item, action) -> {
            SlimefunGuide.openGuide(pl, guide);
            return false;
        });

        GitHubService github = Slimefun.getGitHubService();

        List<String> contributorsLore = new ArrayList<>();
        contributorsLore.add("");
        contributorsLore.addAll(locale.getMessages(
                p,
                "guide.credits.description",
                msg -> msg.replace(
                        "%contributors%",
                        String.valueOf(github.getContributors().size()))));
        contributorsLore.add("");
        contributorsLore.add("&7\u21E8 &e" + locale.getMessage(p, "guide.credits.open"));

        // @formatter:off
        menu.addItem(
                2,
                new CustomItemStack(
                        SlimefunUtils.getCustomHead("e952d2b3f351a6b0487cc59db31bf5f2641133e5ba0006b18576e996a0293e52"),
                        "&c" + locale.getMessage(p, "guide.title.credits"),
                        contributorsLore.toArray(new String[0])));
        // @formatter:on

        menu.addMenuClickHandler(2, (pl, slot, action, item) -> {
            ContributorsMenu.open(pl, 0);
            return false;
        });

        // @formatter:off
        menu.addItem(
                4,
                new CustomItemStack(
                        Material.WRITABLE_BOOK,
                        ChatColor.GREEN + locale.getMessage(p, "guide.title.versions"),
                        "&7&o" + locale.getMessage(p, "guide.tooltips.versions-notice"),
                        "",
                        "&f汉化 By StarWishsama",
                        "&c请不要将此版本信息截图到 Discord/Github 反馈 Bug",
                        "&c而是优先到汉化页面反馈",
                        "",
                        "&cTHIS BUILD IS UNOFFICIAL BUILD, DO NOT REPORT TO SLIMEFUN DEV",
                        "",
                        "&fMinecraft: &a" + Bukkit.getBukkitVersion(),
                        "&fSlimefun: &a" + Slimefun.getVersion()),
                ChestMenuUtils.getEmptyClickHandler());
        // @formatter:on

        // @formatter:off
        menu.addItem(
                6,
                new CustomItemStack(
                        Material.COMPARATOR,
                        "&e" + locale.getMessage(p, "guide.title.source"),
                        "",
                        "&7最近活动于: &a" + NumberUtils.getElapsedTime(github.getLastUpdate()) + " 前",
                        "&7Forks: &e" + github.getForks(),
                        "&7Stars: &e" + github.getStars(),
                        "",
                        "&7&oSlimefun 4 是一个由社区参与的项目,",
                        "&7&o源代码可以在 GitHub 上找到",
                        "&7&o如果你想让这个项目持续下去",
                        "&7&o你可以考虑对项目做出贡献",
                        "",
                        "&7\u21E8 &e点击前往汉化版 GitHub 仓库"));
        // @formatter:on

        menu.addMenuClickHandler(6, (pl, slot, item, action) -> {
            pl.closeInventory();
            ChatUtils.sendURL(pl, "https://github.com/StarwishSama/Slimefun4");
            return false;
        });

        // @formatter:off
        menu.addItem(
                8,
                new CustomItemStack(
                        Material.KNOWLEDGE_BOOK,
                        "&3" + locale.getMessage(p, "guide.title.wiki"),
                        "",
                        "&7你需要对物品或机器方面的帮助吗?",
                        "&7你不知道要干什么?",
                        "&7查看我们的由社区维护的维基",
                        "&7并考虑成为一名编辑者!",
                        "",
                        "&7\u21E8 &e点击前往非官方中文 Wiki"));
        // @formatter:on

        menu.addMenuClickHandler(8, (pl, slot, item, action) -> {
            pl.closeInventory();
            ChatUtils.sendURL(pl, "https://slimefun-wiki.guizhanss.cn/");
            return false;
        });

        // @formatter:off
        menu.addItem(
                47,
                new CustomItemStack(
                        Material.BOOKSHELF,
                        "&3" + locale.getMessage(p, "guide.title.addons"),
                        "",
                        "&7Slimefun 是一个大型项目，但附属插件的存在",
                        "&7能让 Slimefun 真正的发光发亮",
                        "&7看一看它们，也许你要寻找的附属插件就在那里!",
                        "",
                        "&7该服务器已安装附属插件: &b" + Slimefun.getInstalledAddons().size(),
                        "",
                        "&7\u21E8 &e点击查看 Slimefun4 可用的附属插件"));
        // @formatter:on

        menu.addMenuClickHandler(47, (pl, slot, item, action) -> {
            pl.closeInventory();
            ChatUtils.sendURL(pl, "https://slimefun-wiki.guizhanss.cn/Addons");
            return false;
        });

        if (Slimefun.getUpdater().getBranch().isOfficial()) {
            // @formatter:off
            menu.addItem(
                    49,
                    new CustomItemStack(
                            Material.REDSTONE_TORCH,
                            "&4" + locale.getMessage(p, "guide.title.bugs"),
                            "",
                            "&7&oBug reports have to be made in English!",
                            "",
                            "&7Open Issues: &a" + github.getOpenIssues(),
                            "&7Pending Pull Requests: &a" + github.getPendingPullRequests(),
                            "",
                            "&7\u21E8 &eClick to go to the Slimefun4 Bug Tracker"));
            // @formatter:on

            menu.addMenuClickHandler(49, (pl, slot, item, action) -> {
                pl.closeInventory();
                ChatUtils.sendURL(pl, "https://github.com/StarWishsama/Slimefun4/issues");
                return false;
            });
        } else {
            menu.addItem(49, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        menu.addItem(
                51,
                new CustomItemStack(
                        Material.TOTEM_OF_UNDYING, ChatColor.RED + locale.getMessage(p, "guide.work-in-progress")),
                (pl, slot, item, action) -> {
                    // Add something here
                    return false;
                });
    }

    @ParametersAreNonnullByDefault
    private static void addConfigurableOptions(Player p, ChestMenu menu, ItemStack guide) {
        int i = 19;

        for (SlimefunGuideOption<?> option : options) {
            Optional<ItemStack> item = option.getDisplayItem(p, guide);

            if (item.isPresent()) {
                menu.addItem(i, item.get());
                menu.addMenuClickHandler(i, (pl, slot, stack, action) -> {
                    option.onClick(p, guide);
                    return false;
                });

                i++;
            }
        }
    }

    /**
     * This method checks if the given {@link Player} has enabled the {@link FireworksOption}
     * in their {@link SlimefunGuide}.
     * If they enabled this setting, they will see fireworks when they unlock a {@link Research}.
     *
     * @param p
     *            The {@link Player}
     *
     * @return Whether this {@link Player} wants to see fireworks when unlocking a {@link Research}
     */
    public static boolean hasFireworksEnabled(@Nonnull Player p) {
        return getOptionValue(p, FireworksOption.class, true);
    }

    /**
     * This method checks if the given {@link Player} has enabled the {@link LearningAnimationOption}
     * in their {@link SlimefunGuide}.
     * If they enabled this setting, they will see messages in chat about the progress of their {@link Research}.
     *
     * @param p
     *            The {@link Player}
     *
     * @return Whether this {@link Player} wants to info messages in chat when unlocking a {@link Research}
     */
    public static boolean hasLearningAnimationEnabled(@Nonnull Player p) {
        return getOptionValue(p, LearningAnimationOption.class, true);
    }

    /**
     * Helper method to get the value of a {@link SlimefunGuideOption} that the {@link Player}
     * has set in their {@link SlimefunGuide}
     *
     * @param p
     *            The {@link Player}
     * @param optionsClass
     *            Class of the {@link SlimefunGuideOption} to get the value of
     * @param defaultValue
     *            Default value to return in case the option is not found at all or has no value set
     * @param <T>
     *            Type of the {@link SlimefunGuideOption}
     * @param <V>
     *            Type of the {@link SlimefunGuideOption} value
     *
     * @return The value of given {@link SlimefunGuideOption}
     */
    @Nonnull
    private static <T extends SlimefunGuideOption<V>, V> V getOptionValue(
            @Nonnull Player p, @Nonnull Class<T> optionsClass, @Nonnull V defaultValue) {
        for (SlimefunGuideOption<?> option : options) {
            if (optionsClass.isInstance(option)) {
                T o = optionsClass.cast(option);
                ItemStack guide = SlimefunGuide.getItem(SlimefunGuideMode.SURVIVAL_MODE);
                return o.getSelectedOption(p, guide).orElse(defaultValue);
            }
        }

        return defaultValue;
    }
}

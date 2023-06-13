package city.norain.slimefun4;

import city.norain.slimefun4.listener.SlimefunMigrateListener;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SlimefunExtended {
    private static Logger logger = null;

    private static SlimefunMigrateListener migrateListener = new SlimefunMigrateListener();

    public static void register(@Nonnull Slimefun sf) {
        logger = sf.getLogger();
        if (checkHybridServer(sf)) {
            return;
        }

        scheduleSlimeGlueCheck(sf);
        VaultIntegration.register(sf);

        migrateListener.register(sf);
    }

    public static void shutdown() {
        logger = null;
        migrateListener = null;

        VaultIntegration.cleanup();
    }

    public static Logger getLogger() {
        return logger;
    }

    private static boolean checkHybridServer(@Nonnull Slimefun sf) {
        try {
            Class.forName("net/minecraftforge/common/MinecraftForge");
            logger.log(Level.WARNING, "检测到正在使用混合端, Slimefun 将会被禁用!");
            Bukkit.getPluginManager().disablePlugin(sf);

            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static void scheduleSlimeGlueCheck(Slimefun sf) {
        Bukkit.getScheduler().runTaskLater(sf, () -> {
            if (Bukkit.getPluginManager().getPlugin("SlimeGlue") == null) {
                logger.log(Level.WARNING, "检测到没有安装 SlimeGlue (粘液胶), 你将缺失对一些插件的额外保护检查!");
                logger.log(Level.WARNING, "下载: https://github.com/Xzavier0722/SlimeGlue");
            }
        }, 300); // 15s
    }
}

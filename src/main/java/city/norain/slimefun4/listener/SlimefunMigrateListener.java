package city.norain.slimefun4.listener;

import com.xzavier0722.mc.plugin.slimefun4.storage.migrator.BlockStorageMigrator;
import com.xzavier0722.mc.plugin.slimefun4.storage.migrator.PlayerProfileMigrator;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;

public class SlimefunMigrateListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        var p = e.getPlayer();

        if ((PlayerProfileMigrator.getInstance().isOldDataExists()
                || BlockStorageMigrator.getInstance().isOldDataExists())
                && p.hasPermission("slimefun.command.migrate")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c检测到使用文件储存的旧数据, 请使用 /sf migrate 迁移旧数据至数据库!"));
        }
    }

    public void register(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}

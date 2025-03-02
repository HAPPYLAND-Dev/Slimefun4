package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import com.xzavier0722.mc.plugin.slimefun4.storage.migrator.BlockStorageMigrator;
import com.xzavier0722.mc.plugin.slimefun4.storage.migrator.MigrateStatus;
import com.xzavier0722.mc.plugin.slimefun4.storage.migrator.PlayerProfileMigrator;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class MigrateCommand extends SubCommand {
    MigrateCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "migrate", true);
    }

    @Nonnull
    @Override
    protected String getDescription() {
        return "commands.migrate.description";
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender.hasPermission("slimefun.command.migrate") || sender instanceof ConsoleCommandSender) {
            if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
                Slimefun.getLocalization().sendMessage(sender, "commands.migrate.started", true);

                Bukkit.getScheduler().runTaskAsynchronously(Slimefun.instance(), () -> {
//                    try {
//                        var status = PlayerProfileMigrator.getInstance().migrateData();
//                        sendMigrateStatus("玩家数据", sender, status);
//                    } catch (Exception e) {
//                        Slimefun.getLocalization().sendMessage(sender, "commands.migrate.failed", true);
//                        plugin.getLogger().log(Level.WARNING, "迁移数据时出现意外", e);
//                    }
                    plugin.getLogger().log(Level.WARNING, "迁移数据时出现意外");
                });

                Bukkit.getScheduler().runTaskAsynchronously(Slimefun.instance(), () -> {
//                    try {
//                        var status = BlockStorageMigrator.getInstance().migrateData();
//                        sendMigrateStatus("方块数据", sender, status);
//                    } catch (Exception e) {
//                        Slimefun.getLocalization().sendMessage(sender, "commands.migrate.failed", true);
//                        plugin.getLogger().log(Level.WARNING, "迁移数据时出现意外", e);
//                    }
                    plugin.getLogger().log(Level.WARNING, "迁移数据时出现意外");
                });
            } else {
                Slimefun.getLocalization().sendMessage(sender, "commands.migrate.confirm", true);
            }
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
        }
    }

    private void sendMigrateStatus(@Nonnull String migrateType, @Nonnull CommandSender sender, MigrateStatus status) {
        switch (status) {
            case SUCCESS -> Slimefun.getLocalization()
                    .sendMessage(
                            sender,
                            "commands.migrate.success",
                            true,
                            msg -> msg.replace("%migrate_type%", migrateType));
            case FAILED -> Slimefun.getLocalization().sendMessage(sender, "commands.migrate.failed", true);
            case MIGRATING -> Slimefun.getLocalization().sendMessage(sender, "commands.migrate.in-progress", true);
            case MIGRATED -> Slimefun.getLocalization().sendMessage(sender, "commands.migrate.already-migrated", true);
        }
    }
}

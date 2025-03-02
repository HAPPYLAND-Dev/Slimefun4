package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * This {@link Listener} removes a {@link PlayerProfile} from memory if the corresponding {@link Player}
 * has left the {@link Server} or was kicked.
 *
 * @author TheBusyBiscuit
 * @author SoSeDiK
 *
 */
public class PlayerProfileListener implements Listener {

    public PlayerProfileListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        Optional<PlayerProfile> profile = PlayerProfile.find(e.getPlayer());

        // if we still have a profile of this Player in memory, delete it
        profile.ifPresent(PlayerProfile::markForDeletion);
        Slimefun.getDatabaseManager()
                .getProfileDataController()
                .invalidateCache(e.getPlayer().getUniqueId().toString());
    }

    @EventHandler(ignoreCancelled = true)
    public void onKick(PlayerKickEvent e) {
        Optional<PlayerProfile> profile = PlayerProfile.find(e.getPlayer());

        // if we still have a profile of this Player in memory, delete it
        profile.ifPresent(PlayerProfile::markForDeletion);
        Slimefun.getDatabaseManager()
                .getProfileDataController()
                .invalidateCache(e.getPlayer().getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Slimefun.getDatabaseManager()
                .getProfileDataController()
                .invalidateCache(e.getPlayer().getUniqueId().toString());
    }
}

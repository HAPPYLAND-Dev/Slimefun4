package com.xzavier0722.mc.plugin.slimefun4.chunk.listener;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunkListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        Slimefun.getDatabaseManager().getBlockDataController(e.getWorld()).loadChunk(e.getChunk(), e.isNewChunk());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Slimefun.getDatabaseManager().loadWorld(e.getWorld());
            }
        }.runTaskAsynchronously(Slimefun.instance());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getServer().isStopping()) return;
                Slimefun.getDatabaseManager().unloadWorld(e.getWorld(), true);
            }
        }.runTaskAsynchronously(Slimefun.instance());
    }
}

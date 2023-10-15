package com.xzavier0722.mc.plugin.slimefun4.storage.listener;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataController;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkListener implements Listener {

    private static final ConcurrentHashMap<World, HashMap<Chunk, Boolean>> delayLoad = new ConcurrentHashMap<>();

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        BlockDataController controller = Slimefun.getDatabaseManager().getBlockDataController(e.getWorld());
        World world = e.getWorld();

        if (controller == null) {
            delayLoad.computeIfAbsent(world, k -> new HashMap<>()).put(e.getChunk(), e.isNewChunk());
        } else {
            controller.loadChunk(e.getChunk(), e.isNewChunk());
            Map<Chunk, Boolean> list = delayLoad.remove(world);

            if (list != null) {
                list.forEach(controller::loadChunk);
            }
        }
    }


    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        Slimefun.getDatabaseManager().loadWorld(e.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getServer().isStopping()) return;
                Slimefun.getDatabaseManager().unloadWorld(e.getWorld());
                delayLoad.remove(e.getWorld());
            }
        }.runTaskAsynchronously(Slimefun.instance());
    }
}

package com.xzavier0722.mc.plugin.slimefun4.storage.util;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;

public class ExecutesUtils {
    public static void execute(Runnable runnable) {
        if (Bukkit.isStopping()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(
                Slimefun.instance(),
                runnable
            );
        }
    }
}

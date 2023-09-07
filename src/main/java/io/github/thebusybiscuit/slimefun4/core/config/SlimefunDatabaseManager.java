package io.github.thebusybiscuit.slimefun4.core.config;

import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.IDataSourceAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.mysql.MysqlAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.mysql.MysqlConfig;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlite.SqliteAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlite.SqliteConfig;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.*;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SlimefunDatabaseManager {
    private static final String PROFILE_CONFIG_FILE_NAME = "profile-storage.yml";
    private static final String BLOCK_STORAGE_FILE_NAME = "block-storage.yml";
    private final Slimefun plugin;
    private final Config profileConfig;
    private final Config blockStorageConfig;
    private StorageType profileStorageType;
    private IDataSourceAdapter<?> profileAdapter;
    private final ConcurrentHashMap<World, SqliteAdapter> worldAdapter;
    private final ConcurrentHashMap<World, BlockDataController> worldController;


    public SlimefunDatabaseManager(Slimefun plugin) {
        this.plugin = plugin;

        if (!new File(plugin.getDataFolder(), PROFILE_CONFIG_FILE_NAME).exists()) {
            plugin.saveResource(PROFILE_CONFIG_FILE_NAME, false);
        }

        if (!new File(plugin.getDataFolder(), BLOCK_STORAGE_FILE_NAME).exists()) {
            plugin.saveResource(BLOCK_STORAGE_FILE_NAME, false);
        }

        worldAdapter = new ConcurrentHashMap<>();
        worldController = new ConcurrentHashMap<>();

        profileConfig = new Config(plugin, PROFILE_CONFIG_FILE_NAME);
        blockStorageConfig = new Config(plugin, BLOCK_STORAGE_FILE_NAME);
    }

    public void init() {
        try {
            profileStorageType = StorageType.valueOf(profileConfig.getString("storageType"));
            var readExecutorThread = profileConfig.getInt("readExecutorThread");
            var writeExecutorThread = profileStorageType == StorageType.SQLITE ? 1 : profileConfig.getInt("writeExecutorThread");

            initAdapter(profileStorageType, profileConfig);
            var profileController = ControllerHolder.createController(ProfileDataController.class, profileStorageType);
            profileController.init(profileAdapter, readExecutorThread, writeExecutorThread);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "加载玩家档案适配器失败", e);
        }
    }

    private void initAdapter(StorageType storageType, Config databaseConfig) throws IOException {
        switch (storageType) {
            case MYSQL -> {
                var adapter = new MysqlAdapter();

                adapter.prepare(
                        new MysqlConfig(
                                databaseConfig.getString("mysql.host"),
                                databaseConfig.getInt("mysql.port"),
                                databaseConfig.getString("mysql.database"),
                                databaseConfig.getString("mysql.tablePrefix"),
                                databaseConfig.getString("mysql.user"),
                                databaseConfig.getString("mysql.password"),
                                databaseConfig.getBoolean("mysql.useSSL"),
                                databaseConfig.getInt("mysql.maxConnection")
                        ));

                profileAdapter = adapter;
            }
            case SQLITE -> {
                var adapter = new SqliteAdapter();

                File databasePath = new File("data-storage/Slimefun", "profile.db");
                profileAdapter = adapter;

                adapter.prepare(new SqliteConfig(databasePath.getAbsolutePath()));
            }
        }
    }

    @Nullable
    public ProfileDataController getProfileDataController() {
        return ControllerHolder.getController(ProfileDataController.class, profileStorageType);
    }

    public BlockDataController getBlockDataController(World world) {
        return worldController.get(world);
    }

    public void shutdown() {
        getProfileDataController().shutdown();
        Bukkit.getWorlds().forEach(world -> unloadWorld(world, true));
        profileAdapter.shutdown();
        ControllerHolder.clearControllers();
    }

    public boolean isBlockDataBase64Enabled() {
        return blockStorageConfig.getBoolean("base64EncodeVal");
    }

    public boolean isProfileDataBase64Enabled() {
        return profileConfig.getBoolean("base64EncodeVal");
    }

    public StorageType getProfileStorageType() {
        return profileStorageType;
    }

    public void loadWorld(World world) {
        plugin.getLogger().info("为世界 " + world.getName() + " 加载数据中...");
        var folder = world.getWorldFolder();
        var readExecutorThread = blockStorageConfig.getInt("readExecutorThread");
        var writeExecutorThread = 1;

        new BukkitRunnable() {
            @Override
            public void run() {
                var adapter = new SqliteAdapter();

                File databasePath = new File(folder, "block-storage.db");
                adapter.prepare(new SqliteConfig(databasePath.getAbsolutePath()));
                worldAdapter.put(world, adapter);

                var blockDataController = new BlockDataController(world);
                worldController.put(world, blockDataController);
                blockDataController.init(worldAdapter.get(world), readExecutorThread, writeExecutorThread);
                if (blockStorageConfig.getBoolean("delayedWriting.enable")) {
                    blockDataController.initDelayedSaving(
                            plugin,
                            blockStorageConfig.getInt("delayedWriting.delayedSecond"),
                            blockStorageConfig.getInt("delayedWriting.forceSavePeriod")
                    );
                }
                plugin.getLogger().info("为世界 " + world.getName() + " 加载数据完成!");
            }
        }.runTaskAsynchronously(Slimefun.instance());
    }

    public void unloadWorld(World world, boolean save) {
        var adapter = worldAdapter.get(world);
        if (adapter != null) {
            if (save) {
                plugin.getLogger().info("为世界 " + world.getName() + " 保存数据中...");
                worldController.get(world).shutdown();
            }
            adapter.shutdown();
            worldController.remove(world);
            worldAdapter.remove(world);
            plugin.getLogger().info("世界 " + world.getName() + " 保存操作完成!");
        }
    }
}

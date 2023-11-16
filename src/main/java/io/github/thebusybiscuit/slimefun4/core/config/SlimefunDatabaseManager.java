package io.github.thebusybiscuit.slimefun4.core.config;

import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.IDataSourceAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.mysql.MysqlAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.mysql.MysqlConfig;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.postgresql.PostgreSqlAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.postgresql.PostgreSqlConfig;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlite.SqliteAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlite.SqliteConfig;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataController;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ChunkDataLoadMode;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ControllerHolder;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ProfileDataController;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.StorageType;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.annotation.Nullable;

public class SlimefunDatabaseManager {
    private static final String PROFILE_CONFIG_FILE_NAME = "profile-storage.yml";
    private static final String BLOCK_STORAGE_FILE_NAME = "block-storage.yml";
    private final Slimefun plugin;
    private final Config profileConfig;
    private final Config blockStorageConfig;
    private StorageType profileStorageType;
    private StorageType blockDataStorageType;
    private IDataSourceAdapter<?> profileAdapter;
    private final ConcurrentHashMap<World, IDataSourceAdapter<?>> blockStorageAdapters;
    private final ConcurrentHashMap<World, BlockDataController> blockDataControllers;

    public SlimefunDatabaseManager(Slimefun plugin) {
        this.plugin = plugin;

        if (!new File(plugin.getDataFolder(), PROFILE_CONFIG_FILE_NAME).exists()) {
            plugin.saveResource(PROFILE_CONFIG_FILE_NAME, false);
        }

        if (!new File(plugin.getDataFolder(), BLOCK_STORAGE_FILE_NAME).exists()) {
            plugin.saveResource(BLOCK_STORAGE_FILE_NAME, false);
        }

        profileConfig = new Config(plugin, PROFILE_CONFIG_FILE_NAME);
        blockStorageConfig = new Config(plugin, BLOCK_STORAGE_FILE_NAME);

        blockStorageAdapters = new ConcurrentHashMap<>();
        blockDataControllers = new ConcurrentHashMap<>();
    }

    public void init() {
        initDefaultVal();

        try {
            profileStorageType = StorageType.valueOf(profileConfig.getString("storageType"));
            var readExecutorThread = profileConfig.getInt("readExecutorThread");
            var writeExecutorThread =
                    profileStorageType == StorageType.SQLITE ? 1 : profileConfig.getInt("writeExecutorThread");

            initAdapter(profileStorageType, DataType.PLAYER_PROFILE, profileConfig);
            var profileController = ControllerHolder.createController(ProfileDataController.class, profileStorageType);
            profileController.init(profileAdapter, readExecutorThread, writeExecutorThread);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "加载玩家档案适配器失败", e);
        }
    }

    private void initAdapter(StorageType storageType, DataType dataType, Config databaseConfig) throws IOException {
        switch (storageType) {
            case MYSQL -> {
                var adapter = new MysqlAdapter();

                adapter.prepare(new MysqlConfig(
                        databaseConfig.getString("mysql.host"),
                        databaseConfig.getInt("mysql.port"),
                        databaseConfig.getString("mysql.database"),
                        databaseConfig.getString("mysql.tablePrefix"),
                        databaseConfig.getString("mysql.user"),
                        databaseConfig.getString("mysql.password"),
                        databaseConfig.getBoolean("mysql.useSSL"),
                        databaseConfig.getInt("mysql.maxConnection")));

                if (Objects.requireNonNull(dataType) == DataType.PLAYER_PROFILE) {
                    profileAdapter = adapter;
                }
            }
            case SQLITE -> {
                var adapter = new SqliteAdapter();

                File databasePath = null;

                if (Objects.requireNonNull(dataType) == DataType.PLAYER_PROFILE) {
                    databasePath = new File("data-storage/Slimefun", "profile.db");
                    profileAdapter = adapter;
                }
                if (databasePath != null) {
                    adapter.prepare(new SqliteConfig(
                        databasePath.getAbsolutePath(), databaseConfig.getInt("sqlite.maxConnection")));
                }
            }
            case POSTGRESQL -> {
                var adapter = new PostgreSqlAdapter();

                adapter.prepare(new PostgreSqlConfig(
                        databaseConfig.getString("postgresql.host"),
                        databaseConfig.getInt("postgresql.port"),
                        databaseConfig.getString("postgresql.database"),
                        databaseConfig.getString("postgresql.tablePrefix"),
                        databaseConfig.getString("postgresql.user"),
                        databaseConfig.getString("postgresql.password"),
                        databaseConfig.getBoolean("postgresql.useSSL"),
                        databaseConfig.getInt("postgresql.maxConnection")));

                if (Objects.requireNonNull(dataType) == DataType.PLAYER_PROFILE) {
                    profileAdapter = adapter;
                }
            }
        }
    }

    public void initDataAdapter(World world, Config databaseConfig) {
        var adapter = new SqliteAdapter();
        var folder = world.getWorldFolder();
        File databasePath = new File(folder, "block-storage.db");

        blockStorageAdapters.put(world, adapter);
        adapter.prepare(new SqliteConfig(databasePath.getAbsolutePath(), databaseConfig.getInt("sqlite.maxConnection")));
    }

    @Nullable public ProfileDataController getProfileDataController() {
        return ControllerHolder.getController(ProfileDataController.class, profileStorageType);
    }

    public BlockDataController getBlockDataController(World world) {
        return blockDataControllers.get(world);
    }

    public void shutdown() {
        if (getProfileDataController() != null) {
            getProfileDataController().shutdown();
        }
        Bukkit.getWorlds().forEach(this::unloadWorld);

        profileAdapter.shutdown();
        ControllerHolder.clearControllers();
    }

    public boolean isBlockDataBase64Enabled() {
        return blockStorageConfig.getBoolean("base64EncodeVal");
    }

    public boolean isProfileDataBase64Enabled() {
        return profileConfig.getBoolean("base64EncodeVal");
    }

    public ChunkDataLoadMode getChunkDataLoadMode() {
        return ChunkDataLoadMode.valueOf(blockStorageConfig.getString("dataLoadMode"));
    }

    public StorageType getProfileStorageType() {
        return profileStorageType;
    }

    public void loadWorld(World world) {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getLogger().info("为世界 " + world.getName() + " 加载数据中...");

                var readExecutorThread = blockStorageConfig.getInt("readExecutorThread");
                var writeExecutorThread = blockStorageConfig.getInt("writeExecutorThread");

                initDataAdapter(world, blockStorageConfig);

                var blockDataController = new BlockDataController(world);
                blockDataController.init(blockStorageAdapters.get(world), readExecutorThread, writeExecutorThread);

                if (blockStorageConfig.getBoolean("delayedWriting.enable")) {
                    blockDataController.initDelayedSaving(
                        plugin,
                        blockStorageConfig.getInt("delayedWriting.delayedSecond"),
                        blockStorageConfig.getInt("delayedWriting.forceSavePeriod")
                    );
                }

                blockDataControllers.put(world, blockDataController);
                plugin.getLogger().info("为世界 " + world.getName() + " 加载数据完成!");
            }
        }.runTaskLaterAsynchronously(Slimefun.instance(), 20L);
    }

    public void unloadWorld(World world) {
        plugin.getLogger().info("为世界 " + world.getName() + " 保存数据中...");
        shutdownWorld(world); // 无论什么情况, 都应该主线程完成任务
        plugin.getLogger().info("世界 " + world.getName() + " 保存操作完成!");
    }

    private void shutdownWorld(World world) {
        blockDataControllers.get(world).shutdown();
        blockStorageAdapters.get(world).shutdown();

        blockDataControllers.remove(world);
        blockStorageAdapters.remove(world);
    }

    private void initDefaultVal() {
        profileConfig.setDefaultValue("sqlite.maxConnection", 5);
        profileConfig.save();
        blockStorageConfig.setDefaultValue("sqlite.maxConnection", 5);
        blockStorageConfig.setDefaultValue("dataLoadMode", "LOAD_WITH_CHUNK");
        blockStorageConfig.save();
    }
}

package com.gyzer.sevendaygift;

import com.gyzer.sevendaygift.DataBase.DataProvider;
import com.gyzer.sevendaygift.DataBase.MysqlStore;
import com.gyzer.sevendaygift.DataBase.SqliteStore;
import com.gyzer.sevendaygift.Listener.EventsListener;
import com.gyzer.sevendaygift.Utils.DataManager;
import com.gyzer.sevendaygift.Utils.LangUtils;
import com.gyzer.sevendaygift.Utils.MenuRead;
import com.gyzer.sevendaygift.Utils.RewardManager;
import com.gyzer.sevendaygift.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class LegendarySevenDayGift extends JavaPlugin {

    // 单例实例
    private static LegendarySevenDayGift instance;
    private static LangUtils langUtils;

    // 核心组件
    private DataProvider dataProvider;
    private DataManager dataManager;
    private RewardManager rewardManager;
    private MenuRead menuRead;

    // 配置
    private boolean usingMysql;
    public static boolean version_high;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        instance = this;
        version_high = isHighVersion();

        // 初始化插件
        if (!initialize()) {
            getLogger().severe("插件初始化失败，禁用插件");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 注册事件监听器
        registerListeners();

        // 注册命令
        registerCommands();

        // 启动自动保存任务
        startAutoSaveTask();

        long loadTime = System.currentTimeMillis() - startTime;
        getLogger().info("插件启动完成，耗时 " + loadTime + "ms");
    }

    @Override
    public void onDisable() {
        // 保存所有玩家数据
        if (dataManager != null) {
            int saved = dataManager.saveAll();
            getLogger().info("已保存 " + saved + " 个玩家数据");
        }

        // 关闭数据库连接
        if (usingMysql && dataProvider != null) {
            dataProvider.disable();
            getLogger().info("MySQL 连接已关闭");
        }

        getLogger().info("插件已卸载");
    }

    /**
     * 初始化插件
     */
    private boolean initialize() {
        try {
            reload();
            return true;
        } catch (Exception e) {
            getLogger().severe("初始化失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 重载插件配置
     */
    public void reload() {
        // 保存默认配置
        saveDefaultConfig();
        reloadConfig();

        // 初始化数据存储
        initializeDataProvider();

        // 加载配置
        menuRead = new MenuRead();
        dataManager = new DataManager();
        langUtils = new LangUtils();
        rewardManager = new RewardManager();

        getLogger().info("配置文件已重载");
    }

    /**
     * 初始化数据提供者
     */
    private void initializeDataProvider() {
        String method = getConfig().getString("store.method", "sqlite");

        if ("mysql".equalsIgnoreCase(method)) {
            dataProvider = new MysqlStore();
            usingMysql = true;
            getLogger().info("使用 MySQL 数据库");
        } else {
            dataProvider = new SqliteStore();
            usingMysql = false;
            getLogger().info("使用 SQLite 数据库");
        }
    }

    /**
     * 注册事件监听器
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new EventsListener(), this);
    }

    /**
     * 注册命令
     */
    private void registerCommands() {
        PluginCommand.registerCommands();

        org.bukkit.command.PluginCommand command = getCommand("LegendarySevenDayGift");
        if (command != null) {
            com.gyzer.sevendaygift.command.PluginCommand executor =
                    new com.gyzer.sevendaygift.command.PluginCommand();
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }

    /**
     * 启动自动保存任务
     */
    private void startAutoSaveTask() {
        int interval = getConfig().getInt("auto-ansysave", 1000);

        if (interval <= 0) {
            getLogger().info("自动保存已禁用");
            return;
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            int saved = dataManager.saveAll();
            if (saved > 0) {
                getLogger().info("自动保存完成，保存了 " + saved + " 个玩家数据");
            }
        }, 20L, 20L * interval);

        getLogger().info("自动保存已启动，间隔: " + interval + " 秒");
    }

    /**
     * 检查是否为高版本 Bukkit (1.13+)
     */
    private boolean isHighVersion() {
        try {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            String versionString = packageName.substring(packageName.lastIndexOf(".") + 1);

            // 提取版本号 (例如: v1_16_R3 -> 16)
            String[] parts = versionString.split("_");
            if (parts.length >= 2) {
                int minorVersion = Integer.parseInt(parts[1]);
                return minorVersion >= 13;
            }
        } catch (Exception e) {
            getLogger().warning("无法检测服务器版本，默认使用高版本模式");
        }

        return true; // 默认为高版本
    }

    // ========== Getter 方法 ==========

    public static LegendarySevenDayGift getInstance() {
        return instance;
    }

    public static LangUtils getLangUtils() {
        return langUtils;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public MenuRead getRead() {
        return menuRead;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public boolean isUsingMysql() {
        return usingMysql;
    }
}
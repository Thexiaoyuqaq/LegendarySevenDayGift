package com.gyzer.sevendaygift.Utils;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {

    // 使用线程安全的 ConcurrentHashMap
    private final Map<String, PlayerData> cache;

    public DataManager() {
        cache = new ConcurrentHashMap<>();
    }

    /**
     * 从缓存获取玩家数据，如果没有则从数据库读取并缓存
     *
     * @param player 玩家名称
     * @return 玩家数据对象
     */
    public PlayerData getPlayerData(String player) {
        return cache.computeIfAbsent(player, this::loadPlayerData);
    }

    /**
     * 从数据库加载玩家数据
     */
    private PlayerData loadPlayerData(String player) {
        try {
            return LegendarySevenDayGift.getInstance().getDataProvider().getData(player);
        } catch (Exception e) {
            Bukkit.getLogger().severe("加载玩家数据失败: " + player);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存单个玩家的数据
     *
     * @param player 玩家名称
     * @return 是否保存成功
     */
    public boolean saveData(String player) {
        if (!cache.containsKey(player)) {
            return false;
        }

        try {
            PlayerData data = cache.remove(player);
            if (data != null) {
                LegendarySevenDayGift.getInstance().getDataProvider().saveData(data);
                return true;
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("保存玩家数据失败: " + player);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 保存所有缓存中的玩家数据
     *
     * @return 成功保存的数据数量
     */
    public int saveAll() {
        int successCount = 0;
        int totalCount = cache.size();

        Bukkit.getLogger().info("开始保存 " + totalCount + " 个玩家数据...");

        // 使用迭代器安全地移除元素
        for (Map.Entry<String, PlayerData> entry : new HashMap<>(cache).entrySet()) {
            String name = entry.getKey();

            try {
                PlayerData data = cache.remove(name);
                if (data != null) {
                    LegendarySevenDayGift.getInstance().getDataProvider().saveData(data);
                    successCount++;
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("保存玩家 " + name + " 的数据时出错");
                e.printStackTrace();
            }
        }

        Bukkit.getLogger().info("成功保存 " + successCount + "/" + totalCount + " 个玩家数据");
        return successCount;
    }

    /**
     * 异步保存所有数据
     */
    public void saveAllAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(
                LegendarySevenDayGift.getInstance(),
                this::saveAll
        );
    }

    /**
     * 检查玩家数据是否在缓存中
     */
    public boolean isCached(String player) {
        return cache.containsKey(player);
    }

    /**
     * 从缓存中移除玩家数据（不保存）
     */
    public void removeFromCache(String player) {
        cache.remove(player);
    }

    /**
     * 获取缓存中的玩家数量
     */
    public int getCacheSize() {
        return cache.size();
    }

    /**
     * 清空缓存（不保存数据）
     * 警告：此操作会导致数据丢失！
     */
    public void clearCache() {
        cache.clear();
    }
}
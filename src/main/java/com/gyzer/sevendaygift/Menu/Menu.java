package com.gyzer.sevendaygift.Menu;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Utils.MenuRead;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Menu implements InventoryHolder {

    private final Inventory inv;
    private final Player player;
    private final MenuRead read;

    public Menu(Player player) {
        this.player = player;
        this.read = LegendarySevenDayGift.getInstance().getRead();
        this.inv = Bukkit.createInventory(this, read.size, read.title);
    }

    /**
     * 加载菜单内容
     */
    public void load() {
        PlayerData data = LegendarySevenDayGift.getInstance()
                .getDataManager()
                .getPlayerData(player.getName());

        for (Map.Entry<Integer, ItemStack> entry : read.items.entrySet()) {
            int slot = entry.getKey();
            String function = read.function.get(slot);
            String value = read.value.get(slot);

            if ("reward".equals(function)) {
                ItemStack rewardItem = createRewardItem(entry.getValue(), value, data);
                inv.setItem(slot, rewardItem);
            } else {
                inv.setItem(slot, entry.getValue().clone());
            }
        }
    }

    /**
     * 创建奖励物品（根据玩家状态更新 lore）
     */
    private ItemStack createRewardItem(ItemStack original, String dayStr, PlayerData data) {
        int day = Integer.parseInt(dayStr);
        ItemStack item = original.clone();
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        String placeholder = getPlaceholder(day, data);
        List<String> updatedLore = new ArrayList<>();

        for (String line : lore) {
            updatedLore.add(line.replace("%placeholder%", placeholder));
        }

        meta.setLore(updatedLore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 获取状态占位符
     */
    private String getPlaceholder(int day, PlayerData data) {
        int claimDay = data.getClaimDay();
        int today = getToday(data);

        if (day == today) {
            return data.isClaim() ? read.claim_already : read.claim_wait;
        } else if (day > claimDay) {
            return read.claim_cant;
        } else {
            return read.claim_already;
        }
    }

    /**
     * 打开菜单
     */
    public void open() {
        playSound();
        player.openInventory(inv);
    }

    /**
     * 播放音效
     */
    private void playSound() {
        if (read.sound == null || read.sound.isEmpty()) {
            return;
        }

        try {
            Sound sound = Sound.valueOf(read.sound.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            LegendarySevenDayGift.getInstance().getLogger()
                    .warning("无效的音效ID: " + read.sound);
        }
    }

    /**
     * 获取今天可领取的天数
     *
     * @return 今天的天数，如果没有返回 -1
     */
    public int getToday(PlayerData data) {
        LinkedList<Integer> days = read.days;
        int claimDay = data.getClaimDay();

        int index = 0;
        for (int day : days) {
            if (claimDay == day) {
                break;
            }
            index++;
        }

        return index < days.size() ? days.get(index) : -1;
    }

    /**
     * 检查指定天数是否可以领取
     */
    public boolean canClaim(int targetDay, PlayerData data) {
        int claimDay = data.getClaimDay();
        int today = getToday(data);

        // 已经领取过
        if (claimDay > targetDay) {
            return false;
        }

        // 不是今天的奖励
        if (today != targetDay) {
            return false;
        }

        // 今天已经领取过
        if (data.isClaim()) {
            return false;
        }

        return true;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public Player getPlayer() {
        return player;
    }
}
package com.gyzer.sevendaygift.Listener;

import com.gyzer.sevendaygift.API.Events.ClaimGiftEvent;
import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Menu.Menu;
import com.gyzer.sevendaygift.Utils.LangUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class EventsListener implements Listener {

    private static final LangUtils lang = LegendarySevenDayGift.getLangUtils();
    private static final LegendarySevenDayGift plugin = LegendarySevenDayGift.getInstance();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getDataManager().saveData(event.getPlayer().getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getDataManager().getPlayerData(player.getName());

        // 检查并更新每日状态
        updateDailyStatus(data);

        // 检查是否有待领取的奖励
        int currentDay = data.getClaimDay();
        if (plugin.getRewardManager().checkTodayHasReward(currentDay) && !data.isClaim()) {
            // 延迟5秒后提醒玩家
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                sendClaimReminder(player, currentDay);
            }, 100L);
        }
    }

    /**
     * 更新玩家的每日状态
     */
    private void updateDailyStatus(PlayerData data) {
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DATE);
        int lastLogin = data.getLastLogin();

        // 如果不是同一天登录
        if (lastLogin != currentDate) {
            LinkedList<Integer> daysList = plugin.getRead().days;
            int maxDay = daysList.getLast();
            int claimDay = data.getClaimDay();

            // 如果还没有达到最大天数
            if (claimDay < maxDay) {
                data.setLastLogin(currentDate);

                // 如果昨天已经领取，则进入下一天
                if (data.isClaim()) {
                    data.setClaim(false);
                    data.setClaimDay(claimDay + 1);
                }
            }
        }
    }

    /**
     * 发送领取提醒
     */
    private void sendClaimReminder(Player player, int day) {
        String message = lang.claimWait.replace("%day%", String.valueOf(day));
        player.sendMessage(lang.plugin + message);

        String[] title = lang.getTitle(day);
        player.sendTitle(title[0], title[1]);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu)) {
            return;
        }

        Menu menu = (Menu) event.getInventory().getHolder();
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        int slot = event.getRawSlot();
        String function = plugin.getRead().function.get(slot);
        String value = plugin.getRead().value.get(slot);

        if (function == null) {
            return;
        }

        handleFunction(player, menu, function, value);
    }

    /**
     * 处理菜单功能
     */
    private void handleFunction(Player player, Menu menu, String function, String value) {
        switch (function) {
            case "close":
                player.closeInventory();
                break;

            case "cmd":
                executeCommand(player, value);
                break;

            case "reward":
                handleRewardClaim(player, menu, value);
                break;

            default:
                break;
        }
    }

    /**
     * 执行命令
     */
    private void executeCommand(Player player, String command) {
        String finalCommand = command.replace("%player%", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
    }

    /**
     * 处理奖励领取
     */
    private void handleRewardClaim(Player player, Menu menu, String dayStr) {
        try {
            int targetDay = Integer.parseInt(dayStr);
            PlayerData data = plugin.getDataManager().getPlayerData(player.getName());

            int claimDay = data.getClaimDay();
            int today = menu.getToday(data);

            // 检查今天是否有可领取的奖励
            if (today == -1) {
                player.sendMessage(lang.plugin + lang.claimCant);
                return;
            }

            // 已经领取过
            if (claimDay > targetDay) {
                player.sendMessage(lang.plugin + lang.claimAlready);
                return;
            }

            // 不是今天的奖励
            if (claimDay < targetDay) {
                player.sendMessage(lang.plugin + lang.claimCant);
                return;
            }

            // 今天已经领取过
            if (data.isClaim()) {
                player.sendMessage(lang.plugin + lang.claimAlready);
                return;
            }

            // 执行奖励发放
            giveReward(player, data, targetDay, today);

            // 刷新菜单
            refreshMenu(player);

        } catch (NumberFormatException e) {
            plugin.getLogger().warning("无效的天数格式: " + dayStr);
        }
    }

    /**
     * 发放奖励
     */
    private void giveReward(Player player, PlayerData data, int targetDay, int today) {
        List<String> rewards = plugin.getRewardManager().getReward(targetDay);
        plugin.getRewardManager().run(player, rewards);

        String successMsg = lang.claimSuccess.replace("%day%", String.valueOf(today));
        player.sendMessage(lang.plugin + successMsg);

        data.setClaim(true);

        // 触发自定义事件
        ClaimGiftEvent event = new ClaimGiftEvent(targetDay, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * 刷新菜单
     */
    private void refreshMenu(Player player) {
        Menu newMenu = new Menu(player);
        newMenu.load();
        newMenu.open();
    }
}
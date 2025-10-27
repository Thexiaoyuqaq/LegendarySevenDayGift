package com.gyzer.sevendaygift.Utils;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RewardManager {

    private HashMap<Integer, List<String>> rewards;

    public RewardManager(){
        rewards = new HashMap<>();
        FileConfiguration yml = LegendarySevenDayGift.getInstance().getConfig();
        ConfigurationSection section = yml.getConfigurationSection("reward");
        if (section != null){
            for (String day : section.getKeys(false)){
                rewards.put(Integer.parseInt(day), yml.getStringList("reward." + day));
            }
        }
    }

    public List<String> getReward(int day){
        return rewards.containsKey(day) ? rewards.get(day) : new ArrayList<>();
    }

    public void run(Player p, List<String> reward){
        for (String cmd : reward){
            // 提取标识标签
            String tag = extractTag(cmd);

            // 处理命令内容
            String deal = cmd.substring(cmd.indexOf(']') + 1)
                    .replace("%player%", p.getName());

            // 执行对应的命令类型
            executeCommand(p, tag, deal);
        }
    }

    /**
     * 从命令字符串中提取标签
     * 例如: "[message]Hello" -> "message"
     */
    private String extractTag(String cmd){
        if (cmd.startsWith("[") && cmd.contains("]")){
            return cmd.substring(1, cmd.indexOf(']'));
        }
        return "";
    }

    /**
     * 根据标签类型执行对应的命令
     */
    private void executeCommand(Player p, String tag, String deal){
        switch (tag){
            case "message":
                p.sendMessage(MsgUtils.color(deal));
                break;

            case "title":
                String[] title = new String[2];
                if (deal.contains(";")){
                    String[] parts = deal.split(";", 2);
                    title[0] = MsgUtils.color(parts[0]);
                    title[1] = MsgUtils.color(parts[1]);
                } else {
                    title[0] = MsgUtils.color(deal);
                    title[1] = "";
                }
                p.sendTitle(title[0], title[1]);
                break;

            case "sound":
                Sound sound = getSound(deal);
                if (sound != null) {
                    p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
                }
                break;

            case "console":
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), deal);
                break;

            case "player":
                p.performCommand(deal);
                break;

            case "op":
                boolean wasOp = p.isOp();
                try {
                    if (!wasOp) {
                        p.setOp(true);
                    }
                    p.performCommand(deal);
                } finally {
                    if (!wasOp) {
                        p.setOp(false);
                    }
                }
                break;

            case "broad":
                Bukkit.broadcastMessage(MsgUtils.color(deal));
                break;
        }
    }

    /**
     * 安全地获取Sound枚举
     */
    private Sound getSound(String soundName){
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e){
            Bukkit.getLogger().warning("无效的音效ID: " + soundName);
            return null;
        }
    }

    /**
     * 检查当天是否有奖励
     */
    public boolean checkTodayHasReward(int day){
        for (int tar : LegendarySevenDayGift.getInstance().getRead().days){
            if (tar == day){
                return true;
            }
        }
        return false;
    }
}
package com.gyzer.sevendaygift.Utils;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import org.apache.commons.lang.text.StrBuilder;
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
        FileConfiguration yml= LegendarySevenDayGift.getInstance().getConfig();
        ConfigurationSection section=yml.getConfigurationSection("reward");
        if (section != null){
            for (String day:section.getKeys(false)){
                rewards.put(Integer.parseInt(day) , yml.getStringList("reward."+day));
            }
        }
    }

    public List<String> getReward(int day){
        return  rewards.containsKey(day) ? rewards.get(day) : new ArrayList<>();
    }

    public void run(Player p,List<String> reward){
        for (String cmd:reward){

            //获取标识
            StrBuilder tag=new StrBuilder();
            char[] chars=cmd.toCharArray();
            boolean begin=false;
            for (int i=0;i < chars.length; i++){
                if (chars[i] == '[' && i ==0){
                    begin=true;
                    continue;
                }
                else if (chars[i] == ']'){
                    break;
                }
                else {
                    if (begin){
                        tag.append(chars[i]);
                    }
                }
            }

            String deal=cmd.replace("["+tag+"]","").replace("%player%",p.getName());
            switch (tag.toString()){
                case "message":
                    p.sendMessage(MsgUtils.color(deal));
                    break;
                case "title":
                    String[] title=new String[2];
                    if (deal.contains(";")){
                        title[0]=MsgUtils.color(deal.split(";")[0]);
                        title[1]=MsgUtils.color(deal.split(";")[1]);
                    }else {
                        title[0]=MsgUtils.color(deal);
                        title[1]="";
                    }
                    p.sendTitle(title[0],title[1]);
                    break;
                case "sound":
                    if (getSound(deal)!=null) {
                        p.playSound(p.getLocation(), Sound.valueOf(deal.toUpperCase()), 1, 1);
                        break;
                    }
                    break;
                case "console":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),deal);
                    break;
                case "player":
                    p.performCommand(deal);
                    break;
                case "op":
                    if (p.isOp()){
                        p.performCommand(deal);
                    }
                    else {
                        p.setOp(true);
                        p.performCommand(deal);
                        p.setOp(false);
                    }
                    break;
                case "broad":
                    Bukkit.broadcastMessage(deal);
                    break;
            }
        }
    }

    private Sound getSound(String sound){
        Sound s=null;
        try {
            s=Sound.valueOf(sound);
        }catch (Exception e){
            System.out.println("音效ID出错！");
        }
        return s;
    }
    public boolean checkTodayHasReward(int day){
        for (int tar:LegendarySevenDayGift.getInstance().getRead().days){
            if (tar == day){
                return true;
            }
        }
        return false;
    }
}

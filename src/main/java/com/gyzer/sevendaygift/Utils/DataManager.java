package com.gyzer.sevendaygift.Utils;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;

import java.util.HashMap;

public class DataManager {

    private HashMap<String, PlayerData> cache;
    public DataManager(){
        cache=new HashMap<>();
    }

    // 从缓存获取玩家数据，如果没有则读取并存入map中
    public PlayerData getPlayerData(String player){
        if (cache.containsKey( player)){
            return cache.get(player);
        }
        PlayerData data= LegendarySevenDayGift.getInstance().getDataProvider().getData(player);
        cache.put(player,data);
        return data;
    }

    //保存数据
    public void saveData(String player){
        if (cache.containsKey(player)){
            PlayerData data=cache.remove(player);
            LegendarySevenDayGift.getInstance().getDataProvider().saveData(data);
        }
    }

    //保存未保存的玩家数据
    public void saveAll(){
        int a=0;
        for (String name:cache.keySet()){
            PlayerData data=cache.remove(name);
            LegendarySevenDayGift.getInstance().getDataProvider().saveData(data);
            a++;
        }
        System.out.println("保存 "+a+" 个玩家数据.");
    }
}

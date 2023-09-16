package com.gyzer.sevendaygift.API;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;

public class LegendarySevenDayGiftAPI {

    public static void resetPlayer(String playerName){
        PlayerData data=LegendarySevenDayGift.getInstance().getDataManager().getPlayerData(playerName);
        data.setClaimDay(1);
        data.setClaim(false);
    }

    public static void setPlayerClaimDay(String playerName,int day){
        LegendarySevenDayGift.getInstance().getDataManager().getPlayerData(playerName).setClaimDay(day);
    }

    public static void setPlayerCanClaim(String playerName,boolean b){
        LegendarySevenDayGift.getInstance().getDataManager().getPlayerData(playerName).setClaim(b);
    }


}

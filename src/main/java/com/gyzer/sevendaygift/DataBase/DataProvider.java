package com.gyzer.sevendaygift.DataBase;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;

import java.util.List;

public abstract class DataProvider {

    public abstract void saveData(PlayerData data);
    public abstract PlayerData getData(String data);
    public abstract List<String> getPlayers();
    public abstract void disable();

}

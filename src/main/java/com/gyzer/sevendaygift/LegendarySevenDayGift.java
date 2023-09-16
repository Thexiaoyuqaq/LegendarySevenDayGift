package com.gyzer.sevendaygift;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.DataBase.DataProvider;
import com.gyzer.sevendaygift.DataBase.MysqlStore;
import com.gyzer.sevendaygift.DataBase.SqliteStore;
import com.gyzer.sevendaygift.Listener.EventsListener;
import com.gyzer.sevendaygift.Menu.Menu;
import com.gyzer.sevendaygift.Utils.DataManager;
import com.gyzer.sevendaygift.Utils.LangUtils;
import com.gyzer.sevendaygift.Utils.MenuRead;
import com.gyzer.sevendaygift.Utils.RewardManager;
import com.gyzer.sevendaygift.command.PluginCommand;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class LegendarySevenDayGift extends JavaPlugin implements Listener {

    private DataProvider dataProvider;
    private static LegendarySevenDayGift legendarySevenDayGift;
    private static LangUtils langUtils;
    private DataManager dataManager;
    private RewardManager rewardManager;
    private boolean mysql;
    public static boolean version_high;
    private MenuRead read;
    @Override
    public void onEnable() {

        long time=System.currentTimeMillis();
        legendarySevenDayGift=this;
        version_high = BukkitVersionHigh();
        reload();
        Bukkit.getPluginManager().registerEvents(new EventsListener(),this);

        //自动异步保存玩家数据
        int autosave=getConfig().getInt("auto-ansysave",1000);
        if (autosave > 0) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    dataManager.saveAll();
                }
            }, 20, 20 * autosave);
        }

        Bukkit.getPluginCommand("LegendarySevenDayGift").setExecutor(new PluginCommand());
        PluginCommand.registerCommands();
        Bukkit.getPluginCommand("LegendarySevenDayGift").setTabCompleter(new PluginCommand());

        Metrics metrics = new Metrics(this, 19814);

        System.out.println("插件启动耗时 "+(System.currentTimeMillis()-time)+"ms");

    }

    public void reload(){

        saveResource("config.yml",false);
        reloadConfig();
        if (getConfig().getString("store.method","sqlite").equals("mysql")){
            dataProvider=new MysqlStore();
            mysql=true;
        }
        else {
            dataProvider=new SqliteStore();
        }
        read=new MenuRead();
        dataManager=new DataManager();
        langUtils=new LangUtils();
        rewardManager=new RewardManager();


    }

    @Override
    public void onDisable() {
        if (mysql){
            dataProvider.disable();
        }
        dataManager.saveAll();
    }

    public boolean BukkitVersionHigh() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        int version = Integer.parseInt(name.substring(name.indexOf("_") + 1, name.indexOf("R") - 1));
        return (version >= 13);
    }

    public static LegendarySevenDayGift getInstance(){return legendarySevenDayGift;}
    public DataProvider getDataProvider(){return dataProvider;}
    public MenuRead getRead(){return read;}
    public DataManager getDataManager(){return dataManager;}
    public static LangUtils getLangUtils(){return langUtils;}
    public RewardManager getRewardManager(){return rewardManager;}

}

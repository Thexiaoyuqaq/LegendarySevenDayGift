package com.gyzer.sevendaygift.command;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LsgCommand {
    private String sub;
    private String permission;
    private int length=-1;

    public LsgCommand(String sub, String permission) {
        this.sub = sub;
        this.permission = permission;
    }

    public LsgCommand(String sub, String permission, int length) {
        this.sub = sub;
        this.permission = permission;
        this.length= length;
    }
    public void runCommand(CommandSender sender, String[] args)
    {
        if (length > -1 && length != args.length)
        {
            return;
        }
        if (sender.hasPermission(permission))
        {
            reslove(sender,args);
            return;
        }
    }
    public boolean checkIsNumber(String arg)
    {
        Pattern pattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
        Matcher isNum = pattern.matcher(arg);
        return isNum.matches();
    }

    public abstract void reslove(CommandSender sender,String[] args);

    public abstract List<String> complete(CommandSender sender, String[] args);

    public String getSub() {
        return sub;
    }

    public String getPermission() {
        return permission;
    }
    public boolean hasPlayerData(String name) {
        return LegendarySevenDayGift.getInstance().getDataProvider().getPlayers().contains(name);
    }
}

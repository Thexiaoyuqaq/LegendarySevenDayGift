package com.gyzer.sevendaygift.command;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Utils.MsgUtils;
import com.gyzer.sevendaygift.command.SubCommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PluginCommand implements CommandExecutor, TabExecutor {

    public static HashMap<String,LsgCommand> commands=new HashMap<>();
    public static void registerCommands()
    {
        commands.put("open",new OpenCommand());
        commands.put("reset",new ResetCommand());
        commands.put("setday",new SetDayCommand());
        commands.put("setclaim",new SetClaimCommand());
        commands.put("reload",new ReloadCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length==0)
        {
            sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+ MsgUtils.color("&3&l指令帮助"));
            sender.sendMessage(MsgUtils.color("&f/lsg open &8&o打开礼包领取界面"));
            if (sender.isOp()){
                sender.sendMessage(MsgUtils.color("&f/lsg reset 玩家 &7&o重置玩家数据"));
                sender.sendMessage(MsgUtils.color("&f/lsg setday 玩家 天数 &7&o设置玩家累计领取天数"));
                sender.sendMessage(MsgUtils.color("&f/lsg setclaim 玩家 true/false &7&o设置玩家今日领取礼包状态"));
            }
            return true;
        }
        else {
            String sub=args[0];
            LsgCommand chanllengeCommand=commands.get(sub);
            if (chanllengeCommand!=null)
            {
                chanllengeCommand.runCommand(sender,args);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> list=new ArrayList<>();
        if (args.length==1)
        {
            for (String name:commands.keySet())
            {
                list.add(name);
            }
            return list;
        }
        else if (args.length > 1){
            String sub=args[0];
            LsgCommand chanllengeCommand=commands.get(sub);
            return chanllengeCommand!=null ? chanllengeCommand.complete(commandSender,args) : new ArrayList<>();
        }
        return new ArrayList<>();
    }
}

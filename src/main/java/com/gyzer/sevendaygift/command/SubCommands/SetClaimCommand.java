package com.gyzer.sevendaygift.command.SubCommands;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Utils.MsgUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetClaimCommand extends com.gyzer.sevendaygift.command.LsgCommand {
    public SetClaimCommand() {
        super("setclaim", "LegendarySevenDayGift.admin", 3);
    }

    @Override
    public void reslove(CommandSender sender, String[] args) {
        String player=args[1];
        if (!hasPlayerData(player)){
            sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+ MsgUtils.color("&c该玩家不存在！"));
            return;
        }
        PlayerData data= LegendarySevenDayGift.getInstance().getDataManager().getPlayerData(player);
        try {
            data.setClaim(Boolean.parseBoolean(args[2]));
            sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+MsgUtils.color("&e设置玩家 &b"+player+" &e领取礼包状态为 &f"+args[2]));
        }catch (ClassCastException ex){
            sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+MsgUtils.color("&c请输入 &btrue或者false"));
        }

    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length==2){
            return Arrays.asList("玩家");
        }
        if (args.length==3){
            return Arrays.asList("true","false");
        }
        return new ArrayList<>();
    }
}

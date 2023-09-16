package com.gyzer.sevendaygift.command.SubCommands;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Utils.LangUtils;
import com.gyzer.sevendaygift.Utils.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResetCommand extends com.gyzer.sevendaygift.command.LsgCommand {
    public ResetCommand() {
        super("reset", "LegendarySevenDayGift.admin", 2);
    }

    @Override
    public void reslove(CommandSender sender, String[] args) {
        String player=args[1];
        if (!hasPlayerData(player)){
            sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+MsgUtils.color("&c该玩家不存在！"));
            return;
        }
        PlayerData data= LegendarySevenDayGift.getInstance().getDataManager().getPlayerData(player);
        data.setClaim(false);
        data.setClaimDay(1);
        sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+ MsgUtils.color("&a成功重置 &b"+player+" 礼包领取数据."));
        if (Bukkit.getPlayer(player) != null){
            Bukkit.getPlayer(player).sendMessage(LegendarySevenDayGift.getLangUtils().plugin+ LegendarySevenDayGift.getLangUtils().reset);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length== 2){
            return Arrays.asList("玩家");
        }
        return new ArrayList<>();
    }
}

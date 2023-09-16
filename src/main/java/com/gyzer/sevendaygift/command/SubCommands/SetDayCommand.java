package com.gyzer.sevendaygift.command.SubCommands;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Utils.MsgUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetDayCommand extends com.gyzer.sevendaygift.command.LsgCommand {
    public SetDayCommand() {
        super("setday", "LegendarySevenDayGift.admin", 3);
    }

    @Override
    public void reslove(CommandSender sender, String[] args) {
        String player=args[1];
        if (!hasPlayerData(player)){
            sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+ MsgUtils.color("&c该玩家不存在！"));
            return;
        }
        PlayerData data= LegendarySevenDayGift.getInstance().getDataManager().getPlayerData(player);
        if (!checkIsNumber(args[2])){
            sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+MsgUtils.color("&c请输入正确的数字"));
            return;
        }
        int day=Integer.parseInt(args[2]);
        int maxDay=LegendarySevenDayGift.getInstance().getRead().days.get(LegendarySevenDayGift.getInstance().getRead().days.size()-1);
        int set=day > maxDay ? maxDay : day;
        data.setClaimDay(set);
        sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+MsgUtils.color("&a设置玩家 &b"+player+" &a的领取天数为 &e"+set));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length== 2){
            return Arrays.asList("玩家");
        }
        if (args.length==3){
            return Arrays.asList("天数");
        }
        return new ArrayList<>();
    }
}

package com.gyzer.sevendaygift.command.SubCommands;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Utils.MsgUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends com.gyzer.sevendaygift.command.LsgCommand {
    public ReloadCommand( ) {
        super("reload", "LegendarySevenDayGift.admin", 1);
    }

    @Override
    public void reslove(CommandSender sender, String[] args) {
        long time=System.currentTimeMillis();
        LegendarySevenDayGift.getInstance().reload();
        sender.sendMessage(LegendarySevenDayGift.getLangUtils().plugin+ MsgUtils.color("&f成功重载插件配置文件,耗时 &b"+(System.currentTimeMillis()-time)+"ms"));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}

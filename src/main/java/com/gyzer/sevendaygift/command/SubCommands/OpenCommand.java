package com.gyzer.sevendaygift.command.SubCommands;

import com.gyzer.sevendaygift.Menu.Menu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OpenCommand extends com.gyzer.sevendaygift.command.LsgCommand {
    public OpenCommand() {
        super("open", "LegendarySevenDayGift.open", 1);
    }

    @Override
    public void reslove(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            Menu menu=new Menu((Player) sender);
            menu.load();
            menu.open();
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}

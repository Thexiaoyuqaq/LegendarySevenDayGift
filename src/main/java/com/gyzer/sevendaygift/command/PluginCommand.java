package com.gyzer.sevendaygift.command;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Utils.MsgUtils;
import com.gyzer.sevendaygift.command.SubCommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

public class PluginCommand implements CommandExecutor, TabCompleter {

    private static final Map<String, LsgCommand> COMMANDS = new HashMap<>();
    private static final String PREFIX = LegendarySevenDayGift.getLangUtils().plugin;

    /**
     * 注册所有子命令
     */
    public static void registerCommands() {
        COMMANDS.put("open", new OpenCommand());
        COMMANDS.put("reset", new ResetCommand());
        COMMANDS.put("setday", new SetDayCommand());
        COMMANDS.put("setclaim", new SetClaimCommand());
        COMMANDS.put("reload", new ReloadCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        LsgCommand lsgCommand = COMMANDS.get(subCommand);

        if (lsgCommand != null) {
            lsgCommand.runCommand(sender, args);
        } else {
            sender.sendMessage(PREFIX + MsgUtils.color("&c未知命令: &f" + subCommand));
            sender.sendMessage(MsgUtils.color("&7使用 &f/lsg &7查看可用命令"));
        }

        return true;
    }

    /**
     * 发送帮助消息
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(PREFIX + MsgUtils.color("&3&l指令帮助"));
        sender.sendMessage(MsgUtils.color("&f/lsg open &8&o打开礼包领取界面"));

        if (sender.hasPermission("lsg.admin") || sender.isOp()) {
            sender.sendMessage("");
            sender.sendMessage(MsgUtils.color("&e&l管理员命令:"));
            sender.sendMessage(MsgUtils.color("&f/lsg reset <玩家> &7&o重置玩家数据"));
            sender.sendMessage(MsgUtils.color("&f/lsg setday <玩家> <天数> &7&o设置玩家累计领取天数"));
            sender.sendMessage(MsgUtils.color("&f/lsg setclaim <玩家> <true/false> &7&o设置玩家今日领取状态"));
            sender.sendMessage(MsgUtils.color("&f/lsg reload &7&o重载配置文件"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return filterCompletions(args[0], COMMANDS.keySet());
        } else if (args.length > 1) {
            String subCommand = args[0].toLowerCase();
            LsgCommand lsgCommand = COMMANDS.get(subCommand);

            if (lsgCommand != null) {
                List<String> completions = lsgCommand.complete(sender, args);
                return completions != null ? completions : new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }

    /**
     * 过滤补全列表（匹配开头）
     */
    private List<String> filterCompletions(String input, Collection<String> options) {
        String lowerInput = input.toLowerCase();
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(lowerInput))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取所有已注册的命令
     */
    public static Map<String, LsgCommand> getCommands() {
        return Collections.unmodifiableMap(COMMANDS);
    }
}
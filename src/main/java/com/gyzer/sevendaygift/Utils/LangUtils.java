package com.gyzer.sevendaygift.Utils;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import org.bukkit.configuration.file.FileConfiguration;

public class LangUtils {

    public final String plugin;
    public final String claimAlready;
    public final String claimSuccess;
    public final String claimCant;
    public final String claimWait;
    public final String claimWaitTitle;
    public final String reset;

    public LangUtils() {
        FileConfiguration yml = LegendarySevenDayGift.getInstance().getConfig();

        plugin = MsgUtils.color(yml.getString("lang.plugin",
                "&7[&e&lLegendarySevenDayGift&7] &f"));
        claimAlready = MsgUtils.color(yml.getString("lang.claim_already",
                "&3你已经领取过该礼包了.."));
        claimSuccess = MsgUtils.color(yml.getString("lang.claim_success",
                "&a你成功领取了新手五日礼包的第 &b%day% &a日礼包."));
        claimCant = MsgUtils.color(yml.getString("lang.claim_cant",
                "&c当前无法领取该礼包"));
        claimWait = MsgUtils.color(yml.getString("lang.claim_wait",
                "&a今天有个礼包待领取.."));
        claimWaitTitle = MsgUtils.color(yml.getString("lang.claim_wait_title",
                "&3&l新手五日礼包;&e今日有个礼包待领取."));
        reset = MsgUtils.color(yml.getString("lang.reset",
                "&a你的五日礼包领取数据已被重置."));
    }

    /**
     * 替换消息中的占位符
     */
    public String replace(String message, String placeholder, String value) {
        return message.replace(placeholder, value);
    }

    /**
     * 获取分割后的标题消息
     * @return [0]=主标题, [1]=副标题
     */
    public String[] getTitle() {
        String[] parts = claimWaitTitle.split(";");
        if (parts.length == 2) {
            return parts;
        }
        return new String[]{parts[0], ""};
    }

    /**
     * 获取带变量的标题消息
     */
    public String[] getTitle(int day) {
        String[] title = getTitle();
        title[0] = title[0].replace("%day%", String.valueOf(day));
        title[1] = title[1].replace("%day%", String.valueOf(day));
        return title;
    }
}
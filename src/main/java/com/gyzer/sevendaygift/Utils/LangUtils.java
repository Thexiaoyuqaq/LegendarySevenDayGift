package com.gyzer.sevendaygift.Utils;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import org.bukkit.configuration.file.FileConfiguration;

public class LangUtils {

    public String plugin;
    public String claim_already;
    public String claim_success;
    public String claim_cant;
    public String claim_wait;
    public String claim_wait_title;
    public String reset;
    public LangUtils(){
        FileConfiguration yml= LegendarySevenDayGift.getInstance().getConfig();

        plugin = MsgUtils.color(yml.getString("lang.plugin","&7[&e&lLegendarySevenDayGift&7] &f"));
        claim_already = MsgUtils.color(yml.getString("lang.claim_already","&3你已经领取过该礼包了.."));
        claim_success = MsgUtils.color(yml.getString("lang.claim_success","&a你成功领取了新手五日礼包的第 &b%day% &a日礼包."));
        claim_cant = MsgUtils.color(yml.getString("lang.claim_cant","&c当前无法领取该礼包"));
        claim_wait = MsgUtils.color(yml.getString("lang.claim_wait","&a今天有个礼包待领取.."));
        claim_wait_title = MsgUtils.color(yml.getString("lang.claim_wait_title","&3&l新手五日礼包;&e今日有个礼包待领取."));
        reset = MsgUtils.color(yml.getString("lang.reset","&a你的五日礼包领取数据已被重置."));

    }
}

package com.gyzer.sevendaygift.Utils;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class MenuRead {

    public String title;
    public String sound;
    public int size;
    public String claim_already;
    public String claim_wait;
    public String claim_cant;
    public HashMap<Integer, ItemStack> items;
    public HashMap<Integer, String> fuction;
    public HashMap<Integer, String> value;
    public LinkedList<Integer> days;

    // 读取菜单配置
    public MenuRead(){
        items=new HashMap<>();
        fuction=new HashMap<>();
        value=new HashMap<>();
        days=new LinkedList<>();

        File file=new File("./plugins/LegendarySevenDayGift","menu.yml");
        if (!file.exists()){
            LegendarySevenDayGift.getInstance().saveResource("menu.yml",false);
        }
        YamlConfiguration yml=YamlConfiguration.loadConfiguration(file);

        title = MsgUtils.color(yml.getString("title",""));
        sound = yml.getString("sound","");
        size = yml.getInt("size",27);

        claim_already = MsgUtils.color(yml.getString("placeholder.claim_already","&f[ &a你已经领取过该礼包 &f]"));
        claim_wait = MsgUtils.color(yml.getString("placeholder.claim_wait","&f[ &e点击领取礼包 &f]"));
        claim_cant = MsgUtils.color(yml.getString("placeholder.claim_cant","&f[ &c目前该礼包无法领取 &f]"));

        ConfigurationSection section=yml.getConfigurationSection("customItems");
        if (section != null){
            for (String key: section.getKeys(false)){
                String path="customItems."+key;
                ItemStack i=new ItemStack(getMaterial(yml.getString(path+".material","STONE")), yml.getInt(path+".amount",1),(short)yml.getInt(path+".data",0));
                ItemMeta id=i.getItemMeta();
                id.setDisplayName(MsgUtils.color(yml.getString(path+".display","")));
                if (LegendarySevenDayGift.version_high){
                    id.setCustomModelData(yml.getInt(path+".model",0));
                }
                List<String> lore=yml.getStringList(path+".lore") != null ? yml.getStringList(path+".lore") : new ArrayList<>();
                id.setLore(MsgUtils.color(lore));
                i.setItemMeta(id);
                for (int slot:deserializeSlot(yml.getString(path+".slot"))){
                    items.put(slot,i);
                    String fuctionNmae=yml.getString(path+".fuction.type","null");
                    fuction.put(slot,fuctionNmae);
                    String v=yml.getString(path+".fuction.value","");
                    value.put(slot,v);
                    if (fuctionNmae.equals("reward")){
                        int day=Integer.parseInt(v);
                        if (!days.contains(day)){
                            days.add(day);
                        }
                    }
                }
            }
        }
        Collections.sort(days);

    }


    //获取物品ID 默认返回石头
    private Material getMaterial(String str){
        return Material.getMaterial(str) != null ? Material.getMaterial(str) : Material.STONE;
    }

    //解析slot [x-x] 、 [x]
    public List<Integer> deserializeSlot(String str){
        if (str == null){
            return new ArrayList<>();
        }
        String deal=str.replace("[","").replace("]","");
        if (deal.isEmpty()){
            return new ArrayList<>();
        }
        String[] args=deal.split(",");
        List<Integer> slot=new ArrayList<>();
        for (String slots:args){
            if (slots.contains("-")){
                int before=Integer.parseInt(slots.split("-")[0].replace(" ",""));
                int after=Integer.parseInt(slots.split("-")[1].replace(" ",""));
                for (int start=before;start <=after;start++){
                    slot.add(start);
                }
            }
            else{
                slot.add(Integer.parseInt(slots.replace(" ","")));
            }
        }
        return slot;
    }
}

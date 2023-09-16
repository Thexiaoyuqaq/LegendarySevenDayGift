package com.gyzer.sevendaygift.Menu;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Utils.MenuRead;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Menu implements InventoryHolder {
    private Inventory inv;
    private Player p;
    private final MenuRead read= LegendarySevenDayGift.getInstance().getRead();
    public Menu(Player p) {
        this.p = p;
        this.inv = Bukkit.createInventory(this,read.size,read.title);
    }

    public void load(){
        PlayerData data=LegendarySevenDayGift.getInstance().getDataManager().getPlayerData(p.getName());
        for (Map.Entry<Integer, ItemStack> entry:read.items.entrySet()){
            int slot = entry.getKey();
            String fuction = read.fuction.get(slot);
            String value = read.value.get(slot);
            if (fuction.equals("reward")){
                int day=Integer.parseInt(value);
                ItemStack i=entry.getValue().clone();
                ItemMeta id=i.getItemMeta();
                List<String> lore=id.getLore() != null ? id.getLore() : new ArrayList<>();
                if (day == getToday(data)){
                    if (data.isClaim()) {
                        lore.replaceAll(l -> l.replace("%placeholder%",read.claim_already));
                    } else {
                        lore.replaceAll(l -> l.replace("%placeholder%", read.claim_wait));
                    }
                }
                else if (day > data.getClaimDay()){
                    lore.replaceAll(l -> l.replace("%placeholder%", read.claim_cant));
                }
                else if (day < data.getClaimDay()){
                    lore.replaceAll(l -> l.replace("%placeholder%",read.claim_already));
                }
                id.setLore(lore);
                i.setItemMeta(id);
                inv.setItem(slot,i);
                continue;
            }
            inv.setItem(slot,entry.getValue());
        }
    }

    public void open(){
        if (getSound(read.sound)!=null){
            p.playSound(p.getLocation(),getSound(read.sound),1,1);
        }
        p.openInventory(inv);
    }

    private Sound getSound(String sound){
        Sound s=null;
        try {
            s=Sound.valueOf(sound);
        }catch (Exception e){
            System.out.println("音效ID出错！");
        }
        return s;
    }

    public int getToday(PlayerData data){
        LinkedList<Integer> list=read.days;
        int in=0;
        for (int day:list){
            if (data.getClaimDay() == day){
                break;
            }
            in++;
        }
        if (list.size() > in){
            return list.get(in);
        }
        return -1;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}

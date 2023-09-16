package com.gyzer.sevendaygift.Listener;

import com.gyzer.sevendaygift.API.Events.ClaimGiftEvent;
import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.gyzer.sevendaygift.Menu.Menu;
import com.gyzer.sevendaygift.Utils.LangUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class EventsListener implements Listener {

    private static final LangUtils lang=LegendarySevenDayGift.getLangUtils();

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        LegendarySevenDayGift.getInstance().getDataManager().saveData(e.getPlayer().getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        PlayerData data=LegendarySevenDayGift.getInstance().getDataManager().getPlayerData(e.getPlayer().getName());

        int claim=data.getClaimDay();
        Calendar calendar=Calendar.getInstance();
        int date=calendar.get(Calendar.DATE);
        if (data.getLastLogin() != date){
            LinkedList<Integer> list=LegendarySevenDayGift.getInstance().getRead().days;
            int maxDay=list.get(list.size()-1);
            if (claim < maxDay) {
                data.setLastLogin(date);
                if (data.isClaim()) {
                    data.setClaim(false);
                    data.setClaimDay(claim + 1);
                    claim++;
                }
            }
        }

        if (LegendarySevenDayGift.getInstance().getRewardManager().checkTodayHasReward(claim)) {
            int finalClaim = claim;
            new BukkitRunnable() {
                @Override
                public void run() {
                    e.getPlayer().sendMessage(lang.plugin + lang.claim_wait.replace("%day%",  finalClaim + ""));
                    e.getPlayer().sendTitle(lang.claim_wait_title.split(";")[0].replace("%day%", finalClaim + ""), lang.claim_wait_title.split(";")[1].replace("%day%", finalClaim + ""));
                }
            }.runTaskLaterAsynchronously(LegendarySevenDayGift.getInstance(), 100);
        }
    }

    @EventHandler
    public void onInv(InventoryClickEvent e){
        if (e.getInventory().getHolder() instanceof Menu){
            Menu menu=(Menu) e.getInventory().getHolder();
            Player p= (Player) e.getWhoClicked();
            PlayerData data=LegendarySevenDayGift.getInstance().getDataManager().getPlayerData(p.getName());
            e.setCancelled(true);
            if (e.getCurrentItem() != null){
                String fuction = LegendarySevenDayGift.getInstance().getRead().fuction.get(e.getRawSlot());
                String value = LegendarySevenDayGift.getInstance().getRead().value.get(e.getRawSlot());
                switch (fuction){
                    case "close":
                        p.closeInventory();
                        break;
                    case "cmd":
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),value.replace("%player%",p.getName()));
                        break;
                    case "reward":
                        int claim = data.getClaimDay();
                        int today = menu.getToday(data);
                        int target = Integer.parseInt(value);
                        if (today == -1){
                            p.sendMessage(lang.plugin+lang.claim_cant);
                            break;
                        }
                        if (claim > target){
                            p.sendMessage(lang.plugin+lang.claim_already);
                            break;
                        }
                        if (claim == target){
                            if (data.isClaim()){
                                p.sendMessage(lang.plugin+lang.claim_already);
                                break;
                            }

                            List<String> rewards=LegendarySevenDayGift.getInstance().getRewardManager().getReward(target);
                            LegendarySevenDayGift.getInstance().getRewardManager().run(p,rewards);
                            p.sendMessage(lang.plugin+lang.claim_success.replace("%day%",""+today));
                            data.setClaim(true);
                            Bukkit.getPluginManager().callEvent(new ClaimGiftEvent(target,p));

                            menu=new Menu(p);
                            menu.load();
                            menu.open();

                            break;
                        }
                        if (claim < target){
                            p.sendMessage(lang.plugin+lang.claim_cant);
                            break;
                        }
                }
            }
        }
    }
}

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
    public HashMap<Integer, String> function;
    public HashMap<Integer, String> value;
    public LinkedList<Integer> days;

    public MenuRead() {
        items = new HashMap<>();
        function = new HashMap<>();
        value = new HashMap<>();
        days = new LinkedList<>();

        File file = new File(LegendarySevenDayGift.getInstance().getDataFolder(), "menu.yml");
        if (!file.exists()) {
            LegendarySevenDayGift.getInstance().saveResource("menu.yml", false);
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        loadBasicSettings(yml);
        loadPlaceholders(yml);
        loadCustomItems(yml);

        Collections.sort(days);
    }

    /**
     * 加载基础配置
     */
    private void loadBasicSettings(YamlConfiguration yml) {
        title = MsgUtils.color(yml.getString("title", ""));
        sound = yml.getString("sound", "");
        size = yml.getInt("size", 27);
    }

    /**
     * 加载占位符配置
     */
    private void loadPlaceholders(YamlConfiguration yml) {
        claim_already = MsgUtils.color(yml.getString("placeholder.claim_already",
                "&f[ &a你已经领取过该礼包 &f]"));
        claim_wait = MsgUtils.color(yml.getString("placeholder.claim_wait",
                "&f[ &e点击领取礼包 &f]"));
        claim_cant = MsgUtils.color(yml.getString("placeholder.claim_cant",
                "&f[ &c目前该礼包无法领取 &f]"));
    }

    /**
     * 加载自定义物品配置
     */
    private void loadCustomItems(YamlConfiguration yml) {
        ConfigurationSection section = yml.getConfigurationSection("customItems");
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            String path = "customItems." + key;
            ItemStack item = createItemStack(yml, path);
            processItemSlots(yml, path, item);
        }
    }

    /**
     * 创建物品堆栈
     */
    private ItemStack createItemStack(YamlConfiguration yml, String path) {
        Material material = getMaterial(yml.getString(path + ".material", "STONE"));
        int amount = yml.getInt(path + ".amount", 1);
        short data = (short) yml.getInt(path + ".data", 0);

        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(MsgUtils.color(yml.getString(path + ".display", "")));

            if (LegendarySevenDayGift.version_high) {
                meta.setCustomModelData(yml.getInt(path + ".model", 0));
            }

            List<String> lore = yml.getStringList(path + ".lore");
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(MsgUtils.color(lore));
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 处理物品槽位配置
     */
    private void processItemSlots(YamlConfiguration yml, String path, ItemStack item) {
        String slotConfig = yml.getString(path + ".slot");
        List<Integer> slots = deserializeSlot(slotConfig);

        String functionName = yml.getString(path + ".fuction.type", "null");
        String functionValue = yml.getString(path + ".fuction.value", "");

        for (int slot : slots) {
            items.put(slot, item.clone());
            function.put(slot, functionName);
            value.put(slot, functionValue);

            if ("reward".equals(functionName)) {
                try {
                    int day = Integer.parseInt(functionValue);
                    if (!days.contains(day)) {
                        days.add(day);
                    }
                } catch (NumberFormatException e) {
                    LegendarySevenDayGift.getInstance().getLogger()
                            .warning("无效的奖励天数配置: " + functionValue);
                }
            }
        }
    }

    /**
     * 获取材质，如果无效则返回石头
     */
    private Material getMaterial(String str) {
        Material material = Material.getMaterial(str);
        if (material == null) {
            LegendarySevenDayGift.getInstance().getLogger()
                    .warning("无效的材质ID: " + str + ", 使用 STONE 替代");
            return Material.STONE;
        }
        return material;
    }

    /**
     * 解析槽位配置
     * 支持格式: [1], [1,2,3], [1-5], [1-3,7,9-11]
     */
    public List<Integer> deserializeSlot(String str) {
        List<Integer> slots = new ArrayList<>();

        if (str == null || str.isEmpty()) {
            return slots;
        }

        String cleaned = str.replace("[", "").replace("]", "").trim();
        if (cleaned.isEmpty()) {
            return slots;
        }

        String[] parts = cleaned.split(",");

        for (String part : parts) {
            part = part.trim();

            if (part.contains("-")) {
                parseRangeSlot(part, slots);
            } else {
                parseSingleSlot(part, slots);
            }
        }

        return slots;
    }

    /**
     * 解析范围槽位 (例如: 1-5)
     */
    private void parseRangeSlot(String range, List<Integer> slots) {
        try {
            String[] bounds = range.split("-");
            if (bounds.length != 2) {
                return;
            }

            int start = Integer.parseInt(bounds[0].trim());
            int end = Integer.parseInt(bounds[1].trim());

            for (int i = start; i <= end; i++) {
                slots.add(i);
            }
        } catch (NumberFormatException e) {
            LegendarySevenDayGift.getInstance().getLogger()
                    .warning("无效的槽位范围配置: " + range);
        }
    }

    /**
     * 解析单个槽位
     */
    private void parseSingleSlot(String slot, List<Integer> slots) {
        try {
            slots.add(Integer.parseInt(slot));
        } catch (NumberFormatException e) {
            LegendarySevenDayGift.getInstance().getLogger()
                    .warning("无效的槽位配置: " + slot);
        }
    }
}
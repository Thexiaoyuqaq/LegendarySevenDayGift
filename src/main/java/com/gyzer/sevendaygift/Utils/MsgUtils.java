package com.gyzer.sevendaygift.Utils;

import com.gyzer.sevendaygift.LegendarySevenDayGift;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MsgUtils {

    // 预编译正则表达式以提高性能
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");
    private static final Pattern COLOR_CODE_SPACE_PATTERN = Pattern.compile("(" + ChatColor.COLOR_CHAR + ".)[\\s]");

    // MiniMessage 实例
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.legacyAmpersand();

    /**
     * 为单个字符串应用颜色代码
     * 支持顺序: MiniMessage -> HEX (&#RRGGBB) -> 传统颜色代码 (&a, &b等)
     *
     * @param msg 原始消息
     * @return 处理后的彩色消息
     */
    public static String color(String msg) {
        if (msg == null || msg.isEmpty()) {
            return msg;
        }
        return translateColors(msg);
    }

    /**
     * 为字符串列表应用颜色代码
     *
     * @param messages 原始消息列表
     * @return 处理后的彩色消息列表
     */
    public static List<String> color(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        }

        return messages.stream()
                .map(MsgUtils::color)
                .collect(Collectors.toList());
    }

    /**
     * 转换颜色代码（包括 MiniMessage、十六进制颜色和传统颜色）
     * 处理顺序很重要：
     * 1. 先处理 MiniMessage 标签 (如 <red>, <#FF5555>, <gradient>等)
     * 2. 再处理 HEX 代码 (&#RRGGBB)
     * 3. 最后处理传统颜色代码 (&a, &b等)
     */
    private static String translateColors(String text) {
        if (LegendarySevenDayGift.version_high) {
            // 1. 先尝试解析 MiniMessage
            if (containsMiniMessage(text)) {
                text = parseMiniMessage(text);
            }

            // 2. 再处理 HEX 颜色代码
            if (text.contains("&#")) {
                text = translateHexColors(text);
            }

            // 3. 最后处理传统颜色代码
            text = ChatColor.translateAlternateColorCodes('&', text);

            // 移除颜色代码后的空格
            return stripSpaceAfterColorCodes(text);
        } else {
            // 低版本只支持传统颜色代码
            return translateLegacyColors(text);
        }
    }

    /**
     * 检查文本是否包含 MiniMessage 标签
     */
    private static boolean containsMiniMessage(String text) {
        return text.contains("<") && text.contains(">");
    }

    /**
     * 解析 MiniMessage 格式
     * 支持格式如: <red>文本</red>, <#FF5555>文本</#FF5555>, <gradient>文本</gradient>
     */
    private static String parseMiniMessage(String text) {
        try {
            // 将 MiniMessage 解析为 Component
            Component component = MINI_MESSAGE.deserialize(text);

            // 将 Component 转换回 legacy 格式（使用 § 符号）
            return LEGACY_SERIALIZER.serialize(component);
        } catch (Exception e) {
            // 如果解析失败，返回原文本
            LegendarySevenDayGift.getInstance().getLogger()
                    .warning("MiniMessage 解析失败: " + text);
            return text;
        }
    }

    /**
     * 转换十六进制颜色代码 (1.16+)
     * 支持格式: &#RRGGBB 或 &#RGB
     */
    private static String translateHexColors(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 32);

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            String replacement = convertHexToColorCode(hexCode);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 将十六进制颜色转换为 Minecraft 颜色代码
     */
    private static String convertHexToColorCode(String hexCode) {
        StringBuilder colorCode = new StringBuilder();
        colorCode.append(ChatColor.COLOR_CHAR).append('x');

        if (hexCode.length() == 6) {
            // 完整的十六进制颜色 (例如: #FFFFFF)
            for (char c : hexCode.toCharArray()) {
                colorCode.append(ChatColor.COLOR_CHAR).append(c);
            }
        } else if (hexCode.length() == 3) {
            // 简写十六进制颜色 (例如: #FFF -> #FFFFFF)
            for (char c : hexCode.toCharArray()) {
                colorCode.append(ChatColor.COLOR_CHAR).append(c);
                colorCode.append(ChatColor.COLOR_CHAR).append(c);
            }
        }

        return colorCode.toString();
    }

    /**
     * 转换传统颜色代码 (1.15及以下)
     */
    private static String translateLegacyColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * 移除颜色代码后的空格
     */
    private static String stripSpaceAfterColorCodes(String text) {
        return COLOR_CODE_SPACE_PATTERN.matcher(text).replaceAll("$1");
    }

    /**
     * 移除所有颜色代码
     *
     * @param text 包含颜色代码的文本
     * @return 纯文本
     */
    public static String stripColors(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return ChatColor.stripColor(text);
    }

    /**
     * 检查文本是否包含颜色代码
     */
    public static boolean hasColors(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.contains("&") || text.contains("§") || containsMiniMessage(text);
    }

    /**
     * 为文本添加默认颜色
     *
     * @param text         原始文本
     * @param defaultColor 默认颜色代码
     * @return 处理后的文本
     */
    public static String colorWithDefault(String text, ChatColor defaultColor) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return defaultColor + color(text);
    }

    /**
     * 转换为 Component (用于 Paper API)
     *
     * @param text 原始文本
     * @return Adventure Component
     */
    public static Component toComponent(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        String colored = color(text);
        return LEGACY_SERIALIZER.deserialize(colored);
    }

    /**
     * 从 Component 转换为字符串
     *
     * @param component Adventure Component
     * @return 格式化后的字符串
     */
    public static String fromComponent(Component component) {
        if (component == null) {
            return "";
        }
        return LEGACY_SERIALIZER.serialize(component);
    }
}
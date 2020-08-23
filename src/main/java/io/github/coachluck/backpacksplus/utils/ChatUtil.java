/*
 *     File: ChatUtil.java
 *     Last Modified: 8/12/20, 1:19 PM
 *     Project: BackPacksPlus
 *     Copyright (C) 2020 CoachL_ck
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.coachluck.backpacksplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    public static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public static final char COLOR_CHAR = '\u00A7';

    public static void msg(Player player, String message) {
        player.sendMessage(format(message));
    }

    public static void msg(CommandSender sender, String message) {
        sender.sendMessage(format(message));
    }

    /**
     * Simple string formatter
     * @param format the string to translate color codes
     * **/
    public static String format(String format) {
        String formatted = format;
        if(formatted.contains("&#")) {
            formatted = translateHexColorCodes(formatted);
        }
        return ChatColor.translateAlternateColorCodes('&', formatted);
    }

    /**
     * Logs the message to console with plugin prefix
     * @param message the message to color code and send to console
     * **/
    public static void logMsg(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7[&eBackPacks+&7]&r " + message));
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(format("&7[&cBackPacks+&7] &8- " + message));
    }

    /**
     * Formats the string list to support color cods
     * @param lore the string list to format
     * @return the formatted list of strings
     */
    public static List<String> formatLore(List<String> lore) {
        List<String> formattedLore = new ArrayList<>();
        lore.forEach(s -> {
            formattedLore.add(ChatUtil.format(s));
        });
        return formattedLore;
    }

    /**
     * Adds the HEX color to the string
     * @param message the string to colorize
     * @return the colored string
     */
    public static String translateHexColorCodes(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

}

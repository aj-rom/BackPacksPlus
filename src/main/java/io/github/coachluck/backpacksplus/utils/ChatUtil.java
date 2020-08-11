/*
 *     File: ChatUtil.java
 *     Last Modified: 8/11/20, 2:19 PM
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

public class ChatUtil {

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
        return ChatColor.translateAlternateColorCodes('&', format);
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
}

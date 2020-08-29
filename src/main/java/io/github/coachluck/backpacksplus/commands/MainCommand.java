/*
 *     File: MainCommand.java
 *     Last Modified: 8/29/20, 1:39 AM
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

package io.github.coachluck.backpacksplus.commands;

import io.github.coachluck.backpacksplus.BackPacksPlus;
import io.github.coachluck.backpacksplus.utils.BackPack;
import io.github.coachluck.backpacksplus.utils.ChatUtil;
import io.github.coachluck.backpacksplus.utils.DisplayItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MainCommand implements CommandExecutor {

    private final BackPacksPlus plugin;

    public MainCommand(BackPacksPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                ChatUtil.msg(sender, plugin.getMessages().getString("General.Console"));
                return true;
            }

            final Player player = (Player) sender;
            if(!player.hasPermission("backpacksplus.use")) {
                sendPerm(sender);
                return true;
            }

            sendBackPackList(player);
            return true;
        }


        final String mainArg = args[0].substring(0, 1).toLowerCase();
        switch (mainArg) {
            case "r":
                if(!sender.hasPermission("backpacksplus.reload")) {
                    sendPerm(sender);
                    return true;
                }

                reloadPlugin(sender);
                return true;
            case "h":
                if(!sender.hasPermission("backpacksplus.help")) {
                    sendPerm(sender);
                    return true;
                }

                plugin.getMessages().getStringList("Help").forEach(s -> ChatUtil.msg(sender, s));
                return true;
            case "g":
                if(!sender.hasPermission("backpacksplus.give")) {
                    sendPerm(sender);
                    return true;
                }

                if(args.length < 3) {
                    ChatUtil.msg(sender, plugin.getMessages().getString("General.BadArgs"));
                    return true;
                }

                final BackPack backPackToGive = plugin.getBackPackByName(args[1]);
                if(backPackToGive == null) {
                    ChatUtil.msg(sender, plugin.getMessages().getString("General.NotFound")
                            .replaceAll("%backpack%", args[1]));
                    return true;
                }

                final Player targetToReceive = Bukkit.getPlayerExact(args[2]);
                if(targetToReceive == null) {
                    ChatUtil.msg(sender, plugin.getMessages().getString("General.Offline-Player")
                            .replaceAll("%player%", args[2]));
                    return true;
                }

                String amount;
                if(args.length != 4  || args[3] == null) {
                    amount = "1";
                } else {
                    amount = args[3].replaceAll("[^0-9]","");
                    if(Integer.parseInt(amount) <= 0) {
                        amount = "1";
                    }
                }

                final int finalAmount = Integer.parseInt(amount);
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    int amt = plugin.getBackend().sendBackPackItems(targetToReceive, backPackToGive.getBackPackHoldItem(), finalAmount);
                    sendBackPack(sender, targetToReceive, amt, backPackToGive);
                });
                return true;
            default:
                ChatUtil.msg(sender, plugin.getMessages().getString("General.BadArgs"));
                return true;
        }

    }

    /**
     * Sends a permission message to the player.
     * @param sender the person executing the command
     */
    private void sendPerm(CommandSender sender) {
        ChatUtil.msg(sender, plugin.getMessages().getString("General.Permission"));
    }


    /**
     * Gives the target the desired backpack and amount
     * @param sender the person giving the backpack
     * @param targetToReceive the person receiving the backpack
     * @param amt the amount of backpacks to give
     * @param backPackToGive the backpack to give the target
     */
    private void sendBackPack(CommandSender sender, Player targetToReceive, int amt, BackPack backPackToGive) {
        final String recMsg = getMsg("OnReceive", targetToReceive, amt, backPackToGive.getDisplayName());
        final String giveMsg = getMsg("OnGive", targetToReceive, amt, backPackToGive.getDisplayName());

        ChatUtil.msg(targetToReceive, recMsg);

        if(!sender.equals(targetToReceive))
            ChatUtil.msg(sender, giveMsg);
    }

    /**
     * Gets the desired messages on BackPack send and replaces placeholders
     * @param path the desired path (OnReceive/OnGive)
     * @param sender who is sending the backpack
     * @param amount the amount of backpacks to send
     * @param displayName the display name of the backpack
     * @return the replaced message
     */
    private String getMsg(String path, CommandSender sender, Integer amount, String displayName) {
        String msg = plugin.getMessages().getString("BackPack." + path);

        if(msg == null) return "Error";

        msg = msg
                .replaceAll("%player%", sender.getName())
                .replaceAll("%amt%", Integer.toString(amount))
                .replaceAll("%backpack%", displayName);

        return msg;
    }

    /**
     * Sends the list of backpacks with recipes to the player
     * @param player the player to send the list too
     */
    private void sendBackPackList(Player player) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getMessages().getStringList("BackPack.Recipe-View.Header")
                    .forEach(s -> ChatUtil.msg(player, s));
            int i = 1;
            List<BackPack> backPacks = plugin.getBackPacks();
            for(BackPack backPack : backPacks) {
                if(player.hasPermission(backPack.getPermission())) {
                    DisplayItemHelper.sendItemTooltipMessage(player,
                            ChatUtil.format(plugin.getMessages().getString("BackPack.Recipe-View.Body")
                                    .replaceAll("%backpack%", backPack.getDisplayName())
                                    .replaceAll("%num%", Integer.toString(i))),
                            backPack);
                }
            }

            plugin.getMessages().getStringList("BackPack.Recipe-View.Footer").forEach(s -> ChatUtil.msg(player, s));
        });
    }

    /**
     * Reloads the plugin
     * @param sender who is reloading the plugin
     */
    private void reloadPlugin(CommandSender sender) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.reloadConfig();
            plugin.reloadMessages();
        });

        plugin.loadBackPacks();
        ChatUtil.msg(sender, plugin.getMessages().getString("General.Reload"));
    }
}

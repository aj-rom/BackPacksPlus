/*
 *     File: MainCommand.java
 *     Last Modified: 8/10/20, 8:10 PM
 *     Project: BackPacksPlus2
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

import io.github.coachluck.backpacksplus.Main;
import io.github.coachluck.backpacksplus.utils.BackPack;
import io.github.coachluck.backpacksplus.utils.ChatUtil;
import io.github.coachluck.backpacksplus.utils.DisplayItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainCommand implements CommandExecutor {

    private final Main plugin;

    public MainCommand(Main plugin) {
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

            plugin.getMessages().getStringList("BackPack.Recipe-View.Header")
                    .forEach(s -> ChatUtil.msg(player, s));

            int i = 1;
            for(BackPack backPack : plugin.backPacks) {
                if(player.hasPermission(backPack.getPermission())) {
                    DisplayItemHelper.sendItemTooltipMessage(player,
                            ChatUtil.format(plugin.getMessages().getString("BackPack.Recipe-View.Body")
                                    .replaceAll("%backpack%", backPack.getDisplayName())
                                    .replaceAll("%num%", Integer.toString(i))),
                            backPack.getDisplayItem());
                }
            }

            plugin.getMessages().getStringList("BackPack.Recipe-View.Footer").forEach(s -> ChatUtil.msg(player, s));
            return true;
        }


        final String mainArg = args[0].substring(0, 1).toLowerCase();
        switch (mainArg) {
            case "r":
                if(!sender.hasPermission("backpacksplus.reload")) {
                    sendPerm(sender);
                    return true;
                }
                plugin.reloadConfig();
                plugin.reloadMessages();
                plugin.loadBackPacks();

                ChatUtil.msg(sender, plugin.getMessages().getString("General.Reload"));
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

                int amt = 1;
                final String amount = args[3];
                if(amount != null && Integer.parseInt(amount) > 0) {
                    amt = Integer.parseInt(amount);
                }

                final String recMsg = getMsg("OnReceive", targetToReceive, amount, backPackToGive.getDisplayName());
                final String giveMsg = getMsg("OnGive", sender, amount, backPackToGive.getDisplayName());

                ItemStack itemToGive = backPackToGive.getBackPackItem();
                itemToGive.setAmount(amt);

                targetToReceive.getInventory().addItem(itemToGive);
                ChatUtil.msg(targetToReceive, recMsg);
                ChatUtil.msg(sender, giveMsg);
                return true;
            default:
                ChatUtil.msg(sender, plugin.getMessages().getString("General.BadArgs"));
                return true;
        }
    }

    private void sendPerm(CommandSender sender) {
        ChatUtil.msg(sender, plugin.getMessages().getString("General.Permission"));
    }

    private String getMsg(String path, CommandSender sender, String amount, String displayName) {
        String msg = plugin.getMessages().getString("BackPack." + path);
        if(msg == null) return "Error";
        msg = msg
                .replaceAll("%player%", sender.getName())
                .replaceAll("%amt%", amount)
                .replaceAll("%backpack%", displayName);

        return msg;
    }
}

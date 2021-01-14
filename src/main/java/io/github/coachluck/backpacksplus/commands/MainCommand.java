/*
 *     File: MainCommand.java
 *     Last Modified: 1/13/21, 4:47 PM
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
import io.github.coachluck.backpacksplus.utils.BackPackUtil;
import io.github.coachluck.backpacksplus.utils.DisplayItemHelper;
import io.github.coachluck.backpacksplus.utils.backend.ChatUtil;
import io.github.coachluck.backpacksplus.utils.lang.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final BackPacksPlus plugin;

    public MainCommand(BackPacksPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                plugin.getMessageService().sendMessage(sender, MessageKey.NO_CONSOLE);
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

                plugin.getMessageService().getRawMessageList(MessageKey.HELP).forEach(s -> ChatUtil.msg(sender, s));
                return true;
            case "g":
                if(!sender.hasPermission("backpacksplus.give")) {
                    sendPerm(sender);
                    return true;
                }

                if(args.length < 3) {
                    plugin.getMessageService().sendMessage(sender, MessageKey.INCORRECT_ARGS);
                    return true;
                }

                final BackPack backPackToGive = plugin.getBackPackByName(args[1]);
                if(backPackToGive == null) {
                    plugin.getMessageService().sendMessage(sender, MessageKey.NOT_FOUND, args[1]);
                    return true;
                }

                final Player targetToReceive = Bukkit.getPlayerExact(args[2]);
                if(targetToReceive == null) {
                    plugin.getMessageService().sendMessage(sender, MessageKey.OFFLINE_PLAYER, args[2]);
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
                    int amt = BackPackUtil.sendBackPackItems(targetToReceive, backPackToGive.getBackPackHoldItem(), finalAmount);
                    sendBackPack(sender, targetToReceive, amt, backPackToGive);
                });
                return true;
            default:
                plugin.getMessageService().sendMessage(sender, MessageKey.INCORRECT_ARGS);
                return true;
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        final String pre = "backpacksplus.";

        if(args.length == 1) {
            List<String> cmds = new ArrayList<>();
            if(!sender.hasPermission(pre + "use"))
                return cmds;

            if(sender.hasPermission(pre + "give"))
                cmds.add("Give");

            if(sender.hasPermission(pre + "help"))
                cmds.add("Help");

            if(sender.hasPermission(pre + "reload"))
                cmds.add("Reload");

            return cmds;
        }

        if(args.length > 1 && args[0].toLowerCase().startsWith("g") && sender.hasPermission(pre + "give")) {
            switch(args.length) {
                case 2:
                    List<String> backpacks = new ArrayList<>();

                    for (BackPack backPack : plugin.getBackPacks()) {
                        backpacks.add(backPack.getKey());
                    }

                    List<String> completions = new ArrayList<>();
                    StringUtil.copyPartialMatches(args[1], backpacks, completions);
                    Collections.sort(completions);

                    return completions;
                case 3:
                    List<String> playerNames = new ArrayList<>();
                    final Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                    Bukkit.getServer().getOnlinePlayers().toArray(players);

                    for (Player player : players) {
                        playerNames.add(player.getName());
                    }

                    List<String> playerCompletions = new ArrayList<>();
                    StringUtil.copyPartialMatches(args[2], playerNames, playerCompletions);
                    Collections.sort(playerCompletions);

                    return playerCompletions;
                case 4:
                    List<String> numbers = new ArrayList<>();
                    for(int i = 1; i <= 64; i++) {
                        numbers.add(Integer.toString(i));
                    }
                    List<String> numCompletions = new ArrayList<>();

                    StringUtil.copyPartialMatches(args[3], numbers, numCompletions);
                    Collections.sort(numCompletions);

                    return numCompletions;
                default:
                    return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }

    /**
     * Sends a permission message to the player.
     * @param sender the person executing the command
     */
    private void sendPerm(CommandSender sender) {
        plugin.getMessageService().sendMessage(sender, MessageKey.PERMISSION_COMMAND);
    }


    /**
     * Gives the target the desired backpack and amount
     * @param sender the person giving the backpack
     * @param targetToReceive the person receiving the backpack
     * @param amt the amount of backpacks to give
     * @param backPackToGive the backpack to give the target
     */
    private void sendBackPack(CommandSender sender, Player targetToReceive, int amt, BackPack backPackToGive) {
        final String amount = Integer.toString(amt);
        final String backPackName = backPackToGive.getDisplayName();
        plugin.getMessageService().sendMessage(targetToReceive, MessageKey.BACKPACK_RECEIVE,
                amount, backPackName);


        if(!sender.equals(targetToReceive))
            plugin.getMessageService().sendMessage(sender, MessageKey.BACKPACK_GIVE,
                    targetToReceive.getDisplayName(), amount, backPackName);
    }

    /**
     * Sends the list of backpacks with recipes to the player
     * @param player the player to send the list too
     */
    private void sendBackPackList(Player player) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getMessageService().getRawMessageList(MessageKey.BACKPACK_RECIPE_HEADER)
                    .forEach(s -> ChatUtil.msg(player, s));
            int i = 1;

            final List<BackPack> backPacks = plugin.getBackPacks();
            for(BackPack backPack : backPacks) {
                if(BackPackUtil.hasBackPackPermission(player, backPack.getKey(), "craft")) {
                    DisplayItemHelper.sendItemTooltipMessage(player,
                            plugin.getMessageService().getMessage(MessageKey.BACKPACK_RECIPE_BODY,
                                    backPack.getDisplayName(),
                                    Integer.toString(i)),
                            backPack);
                }
            }

            plugin.getMessageService().getRawMessageList(MessageKey.BACKPACK_RECIPE_FOOTER)
                    .forEach(s -> ChatUtil.msg(player, s));
        });
    }

    /**
     * Reloads the plugin
     * @param sender who is reloading the plugin
     */
    private void reloadPlugin(CommandSender sender) {
        plugin.reload();
        plugin.getMessageService().sendMessage(sender, MessageKey.RELOAD);
    }

}

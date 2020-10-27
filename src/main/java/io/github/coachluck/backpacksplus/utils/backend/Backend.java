/*
 *     File: Backend.java
 *     Last Modified: 9/21/20, 3:18 PM
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

package io.github.coachluck.backpacksplus.utils.backend;

import io.github.coachluck.backpacksplus.BackPacksPlus;
import io.github.coachluck.backpacksplus.commands.MainCommand;
import io.github.coachluck.backpacksplus.listeners.BackPackCloseListener;
import io.github.coachluck.backpacksplus.listeners.BackPackCraftListener;
import io.github.coachluck.backpacksplus.listeners.BackPackRenameListener;
import io.github.coachluck.backpacksplus.listeners.BackPackUseListener;
import io.github.coachluck.backpacksplus.listeners.InventoryWatcherListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class Backend {
    private final BackPacksPlus plugin;

    public Backend(BackPacksPlus plugin) {
        this.plugin = plugin;
    }

    /**
     * Ensures that the configuration is updated to the newest version
     * @param version the config version
     */
    public void checkConfigVersion(int version) {
        final int CONFIGURATION_VERSION = 2;
        if(version >= CONFIGURATION_VERSION)
            return;

        if(version == 0) {
            version = 1;
            plugin.getConfig().createSection("General");
            plugin.getConfig().set("General.NestableBackPack", true);
            plugin.getConfig().set("General.BackPackLimiter.Enabled", false);
            plugin.getConfig().set("General.BackPackFreeRename.Enabled", false);
            plugin.getConfig().set("General.BackPackLimiter.Repeat", 20);

            plugin.getMessages().set("Version", 1);
            plugin.getMessages().set("General.OverLimit",
                    "&7Removed &c%removed% backpacks &7because you are only allowed to carry &e%limit% &7at once.");
        }
        if(version == 1) {
            version = 2;
            plugin.getConfig().set("General.NestableBackPack", null);
        }

        plugin.getConfig().set("Config-Version", version);
        plugin.saveConfig();
        plugin.saveMessages();

        plugin.reloadMessages();
        plugin.reloadConfig();
    }
    /**
     * Checks for plugin updates
     */
    public void checkForUpdates() {
        if(!plugin.getConfig().getBoolean("Check-For-Update"))
            return;

        new UpdateChecker(plugin, 82612).getVersion(version -> {
            int old = Integer.parseInt(plugin.getDescription().getVersion().replaceAll("\\.", ""));
            int newVer = Integer.parseInt(version.replaceAll("\\.", ""));
            if (old >= newVer) {
                ChatUtil.logMsg("&bYou are running the latest version.");
                return;
            }
            plugin.updateMsg = true;
            ChatUtil.logMsg("&aThere is a new update available. &ehttps://www.spigotmc.org/resources/backpacksplus.82612/");
        });
    }

    /**
     * Registers all listeners for the plugin
     */
    public void registerListeners() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new BackPackCraftListener(), plugin);
        pm.registerEvents(new BackPackUseListener(), plugin);
        pm.registerEvents(new BackPackCloseListener(), plugin);
        pm.registerEvents(new BackPackRenameListener(), plugin);

        if(plugin.getConfig().getBoolean("General.BackPackLimiter.Enabled")) {
            pm.registerEvents(new InventoryWatcherListener(), plugin);
        }

        plugin.getCommand("bpp").setExecutor(new MainCommand(plugin));
        plugin.getCommand("bpp").setPermissionMessage(ChatUtil.format(plugin.getMessages().getString("General.Permission")));
    }

    /**
     * Sends a backpack to the desired player
     * @param targetToReceive the player to receive the backpack
     * @param itemToGive the backpack item to give
     * @param amount the amount of backpacks to give
     * @return the amount of backpacks added to their inventory
     */
    public Integer sendBackPackItems(Player targetToReceive, ItemStack itemToGive, int amount) {
        int amt = amount;
        if(amt < 1) amt = 1;
        if(amt > 64) amt = 64;

        itemToGive.setAmount(amt);
        targetToReceive.getInventory().addItem(itemToGive);

        return amt;
    }

}

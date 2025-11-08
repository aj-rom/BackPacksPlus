/*
 *     File: Backend.java
 *     Last Modified: 1/14/21, 10:52 PM
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
import io.github.coachluck.backpacksplus.listeners.*;

import org.bukkit.plugin.PluginManager;

public class Backend {
    private static final BackPacksPlus plugin = BackPacksPlus.getInstance();

    private Backend() {}

    /**
     * Ensures that the configuration is updated to the newest version
     * @param version the config version
     */
    public static void checkConfigVersion(int version)
    {
        int currentVer = version;
        final int CONFIGURATION_VERSION = 3;
        if(version >= CONFIGURATION_VERSION)
            return;

        if (version == 2) {
            FileConverterUtil.convert();
            plugin.getConfig().set("BackPacks", null);
            plugin.getConfig().set("Language", "custom");
            currentVer = 3;
        }

        plugin.getConfig().set("Config-Version", currentVer);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    /**
     * Checks for plugin updates
     */
    public static void checkForUpdates()
    {
        if(!plugin.getConfig().getBoolean("Check-For-Update"))
            return;

        UpdateChecker.checkForUpdate();
    }

    /**
     * Registers all listeners for the plugin
     */
    public static void registerListeners()
    {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new BackPackPlacementListener(), plugin);
        pm.registerEvents(new BackPackCraftListener(), plugin);
        pm.registerEvents(new BackPackUseListener(), plugin);
        pm.registerEvents(new BackPackCloseListener(), plugin);

        if (plugin.getConfig().getBoolean("General.BackPackLimiter.Enabled")) {
            pm.registerEvents(new InventoryWatcherListener(), plugin);
        }

        plugin.getCommand("bpp").setExecutor(new MainCommand());
    }
}

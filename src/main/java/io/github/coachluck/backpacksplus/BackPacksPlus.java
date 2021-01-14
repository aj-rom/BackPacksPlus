/*
 *     File: BackPacksPlus.java
 *     Last Modified: 1/13/21, 4:37 PM
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

package io.github.coachluck.backpacksplus;

import io.github.coachluck.backpacksplus.utils.BackPack;
import io.github.coachluck.backpacksplus.utils.InventoryWatcher;
import io.github.coachluck.backpacksplus.utils.Timer;
import io.github.coachluck.backpacksplus.utils.backend.Backend;
import io.github.coachluck.backpacksplus.utils.backend.ChatUtil;
import io.github.coachluck.backpacksplus.utils.lang.MessageService;
import io.github.coachluck.backpacksplus.utils.multiversion.MultiVersionUtil;
import io.github.coachluck.backpacksplus.utils.multiversion.Reflector;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class BackPacksPlus extends JavaPlugin {

    /**
     * Whether or not to display an update message
     */
    public boolean updateMsg;

    /**
     * Holds the message service
     */
    @Getter
    private MessageService messageService;

    /**
     * Holds backpacks.yml as a YamlConfiguration Object
     */
    @Getter
    private YamlConfiguration backPacksYaml;

    /**
     * The Backend for this plugin
     */
    @Getter
    private Backend backend;

    /**
     * The loaded list of all backpacks defined in config-old.yml
     */
    @Getter
    private List<BackPack> backPacks;

    /**
     * Holds all players that are currently in a backpack
     * Also holds the slot that the opened backpack is in
     */
    public HashMap<Player, Integer> viewingBackPack;

    /**
     * Holds the UUID and InventoryWatcher for each player
     * (For removing backpacks over permissible limit)
     */
    public HashMap<UUID, InventoryWatcher> playerStackLimit;

    @Getter
    private MultiVersionUtil multiVersionUtil;

    private Timer timer;

    @Override
    public void onLoad() {
        timer = new Timer();
        backend = new Backend(this);
        setUpConfig();
        backPacks = new ArrayList<>();
        viewingBackPack = new HashMap<>();
        playerStackLimit = new HashMap<>();
        multiVersionUtil = new Reflector().getMultiVersionUtil();
        ChatUtil.logMsg("&aLoaded in &e" + timer.getDuration() + " ms&a ...");
    }

    @Override
    public void onEnable() {
        timer.reset();
        backend.checkForUpdates();
        backend.registerListeners();
        ChatUtil.logMsg("&aRegistered commands and listeners &7( &e" + timer.getDuration() + " ms&7 )");

        timer.reset();
        loadBackPacks();
        ChatUtil.logMsg("&aLoaded BackPacks &7( &e" + timer.getDuration() + " ms&7 )");

    }

    /**
     * Sets up configuration file and messages file
     */
    private void setUpConfig() {
        saveDefaultConfig();
        final int CONFIG_VERSION = getConfig().getInt("Config-Version");
        backend.checkConfigVersion(CONFIG_VERSION);
        messageService = new MessageService(getConfig().getString("Language"));
        saveResource("backpacks.yml", false);
        
        backPacksYaml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "backpacks.yml"));
    }
    
    /**
     * Loads and reloads all of the backpacks for the plugin
     */
    public void loadBackPacks() {
        if(backPacks != null && !backPacks.isEmpty()) backPacks.clear();
        for(String backPackName : backPacksYaml.getKeys(false)) {
            BackPack backPack = new BackPack(backPackName, backPacksYaml.getConfigurationSection(backPackName));

            backPacks.add(backPack);
        }
    }

    public BackPack getBackPackByName(String name) {
        for(BackPack backPack : backPacks) {
            if(name.equalsIgnoreCase(backPack.getKey())) {
                return backPack;
            }
        }

        return null;
    }

    public void reload() {
        reloadConfig();
        messageService = new MessageService(getConfig().getString("Language"));
        loadBackPacks();
    }
}

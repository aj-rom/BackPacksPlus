/*
 *     File: BackPacksPlus.java
 *     Last Modified: 9/4/20, 2:10 AM
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
import io.github.coachluck.backpacksplus.utils.Backend;
import io.github.coachluck.backpacksplus.utils.ChatUtil;
import io.github.coachluck.backpacksplus.utils.InventoryWatcher;
import io.github.coachluck.backpacksplus.utils.multiversion.MultiVersionUtil;
import io.github.coachluck.backpacksplus.utils.multiversion.Reflector;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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
     * Holds messages.yml as a YamlConfiguration object
     */
    @Getter
    private YamlConfiguration messages;

    /**
     * The Backend for this plugin
     */
    @Getter
    private Backend backend;

    /**
     * The loaded list of all backpacks defined in config.yml
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
    public MultiVersionUtil multiVersionUtil;

    @Override
    public void onLoad() {
        backend = new Backend(this);
        setUpConfig();
        backPacks = new ArrayList<>();
        viewingBackPack = new HashMap<>();
        playerStackLimit = new HashMap<>();
        multiVersionUtil = new Reflector().getMultiVersionUtil();
    }

    @Override
    public void onEnable() {
        backend.checkForUpdates();
        loadBackPacks();
        backend.registerListeners();
    }

    /**
     * Sets up configuration file and messages file
     */
    private void setUpConfig() {
        saveDefaultConfig();
        File messageFile = new File(getDataFolder(), "/messages.yml");
        if(!messageFile.exists()) {
            saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messageFile);

        final int CONFIG_VERSION = getConfig().getInt("Config-Version");
        backend.checkConfigVersion(CONFIG_VERSION);
    }


    /**
     * Loads and reloads all of the backpacks for the plugin
     */
    public void loadBackPacks() {
        if(!backPacks.isEmpty()) backPacks.clear();
        for(String backPackName : getConfig().getConfigurationSection("BackPacks").getKeys(false)) {
            final String path = "BackPacks." + backPackName + ".";
            final String displayName = getConfig().getString(path + "Name");
            final List<String> lore = getConfig().getStringList(path + "Lore");
            final List<String> recipeShapeList = getConfig().getStringList(path + "Recipe.Shape");
            final boolean enchanted = getConfig().getBoolean(path + "Enchanted");
            final String permission = getConfig().getString(path + "Permission");
            final String title = getConfig().getString(path + "Title");
            Material mat = null;

            String material = getConfig().getString(path + "Material");
            try {
                mat = Material.valueOf(material);
            } catch (IllegalArgumentException | NoClassDefFoundError e) {
                ChatUtil.error("&eError when trying to load material for &cBackPack:&7 " + backPackName);
                ChatUtil.error("&ePlease use a different material or check the spelling.");
                ChatUtil.logMsg("Material Read - " + material);
                continue;
            }

            if(displayName == null || recipeShapeList.size() != 3 || permission == null || title == null) {
                // TODO : ERROR Handling
                System.out.println(displayName + recipeShapeList);
                continue;
            }
            BackPack backPack = new BackPack(backPackName, mat, displayName, lore, recipeShapeList, title, permission, enchanted);
            backPacks.add(backPack);
        }
    }

    public void reloadMessages() {
        File messageFile = new File(getDataFolder(), "/messages.yml");
        messages = YamlConfiguration.loadConfiguration(messageFile);
    }

    public BackPack getBackPackByName(String name) {
        for(BackPack backPack : backPacks) {
            if(name.equalsIgnoreCase(backPack.getName())) {
                return backPack;
            }
        }

        return null;
    }

    public void saveMessages() {
        try {
            messages.save(new File(getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            ChatUtil.error("Error saving &emessages.yml&c!");
            e.printStackTrace();
        }
    }
}

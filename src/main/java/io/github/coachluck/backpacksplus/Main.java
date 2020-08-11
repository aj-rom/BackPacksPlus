/*
 *     File: Main.java
 *     Last Modified: 7/26/20, 9:49 PM
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

package io.github.coachluck.backpacksplus;

import io.github.coachluck.backpacksplus.utils.BackPack;
import io.github.coachluck.backpacksplus.utils.Backend;
import io.github.coachluck.backpacksplus.utils.ChatUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Main extends JavaPlugin {

    @Getter
    private YamlConfiguration messages;
    public boolean updateMsg;
    public boolean isLegacy;
    public HashMap<Player, Integer> viewingBackPack;

    @Getter
    private Backend backend;
    public List<BackPack> backPacks;

    @Override
    public void onLoad() {
        setUpConfig();
        backend = new Backend(this);
        backPacks = new ArrayList<>();
        viewingBackPack = new HashMap<>();
        isLegacy = Integer.parseInt(Bukkit.getBukkitVersion()
                        .substring(0, 4)
                        .replaceAll("\\.", "")) < 113;
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
        Backend.checkConfigVersion(CONFIG_VERSION);
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
            if(isLegacy) {
                switch(material) {
                    case "PLAYER_HEAD":
                        material = "SKULL";
                        break;
                    case "CLOCK":
                        material = "WATCH";
                        break;
                }
            }

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
}

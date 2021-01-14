/*
 *     File: FileConverter_v2to3.java
 *     Last Modified: 1/14/21, 4:18 PM
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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileConverter_v2to3 {

    private static final BackPacksPlus plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);

    private FileConverter_v2to3() { }

    public static void convert() {
        ConfigurationSection backPacksOld = plugin.getConfig().getConfigurationSection("BackPacks");
        File newBackPackFile = new File(plugin.getDataFolder(), "backpacks.yml");
        if (!newBackPackFile.exists()) {
            try {
                if (!newBackPackFile.createNewFile()) {
                    ChatUtil.error("&cError creating new &ebackpacks.yml &cfile.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration newBackPacks = YamlConfiguration.loadConfiguration(newBackPackFile);
        for (String key : backPacksOld.getKeys(false)) {
            newBackPacks.set(key + ".Name", backPacksOld.getString(key + ".Name"));
            newBackPacks.set(key + ".Title", backPacksOld.getString(key + ".Title"));
            newBackPacks.set(key + ".Texture", backPacksOld.getString(key + ".Texture"));
            newBackPacks.set(key + ".Material", backPacksOld.getString(key + ".Material"));
            newBackPacks.set(key + ".Lore", backPacksOld.getStringList(key + ".Lore"));
            newBackPacks.set(key + ".Enchanted", backPacksOld.getBoolean(key + ".Enchanted"));
            newBackPacks.set(key + ".Size", backPacksOld.getInt(key + ".Size"));
            newBackPacks.set(key + ".Whitelist", new ArrayList<String>());
            newBackPacks.set(key + ".Blacklist", new ArrayList<String>());
            newBackPacks.set(key + ".Recipe.Shape", backPacksOld.getStringList(key + ".Recipe.Shape"));
            newBackPacks.set(key + ".CustomData", null);

            for (String matKey : backPacksOld.getConfigurationSection(key + ".Recipe.Materials").getKeys(false)) {
                newBackPacks.set(key + ".Recipe.Materials." + matKey,
                        backPacksOld.getString(key + ".Recipe.Materials." + matKey + ".Material"));
            }
        }
        try {
            newBackPacks.save(newBackPackFile);
        } catch (IOException e) {
            ChatUtil.error("Error converting backpacks to new file.");
            e.printStackTrace();
        }


        File oldLangFile = new File(plugin.getDataFolder(), "messages.yml");
        File langFile = new File(plugin.getDataFolder(), "lang/custom.yml");
        if (oldLangFile.exists()) {
            YamlConfiguration newLang = YamlConfiguration.loadConfiguration(oldLangFile);
            newLang.options().header("Custom Generated Lang file for update\n" +
                    "Change 'Language' in 'config.yml' to your language to regen. ");
            newLang.set("BackPack.ItemNotAllowed", "%backpack% &cdoes not let you store &e%item% &cinside.");
            newLang.set("Version", null);

            try {
                newLang.save(langFile);
                if (!oldLangFile.delete()) {
                    ChatUtil.error("Couldn't delete old &emessages.yml &cfile!");
                }
            } catch (IOException e) {
                ChatUtil.error("Error while converting &emessages.yml &cto &elang/custom.yml");
                e.printStackTrace();
            }
        }
    }
}

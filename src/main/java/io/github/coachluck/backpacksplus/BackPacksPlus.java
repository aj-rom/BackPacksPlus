/*
 *     File: BackPacksPlus.java
 *     Last Modified: 1/14/21, 10:30 PM
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

import io.github.coachluck.backpacksplus.api.Timer;
import io.github.coachluck.backpacksplus.utils.BackPack;
import io.github.coachluck.backpacksplus.utils.InventoryWatcher;
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

    @Getter
    private MessageService messageService;

    @Getter
    private YamlConfiguration backPacksYaml;

    @Getter
    private List<BackPack> backPacks;

    @Getter
    private MultiVersionUtil multiVersionUtil;

    public HashMap<Player, Integer> viewingBackPack;
    public HashMap<UUID, InventoryWatcher> playerStackLimit;
    public boolean updateMsg;

    @Override
    public void onLoad()
    {
        Timer timer = new Timer();
        setUpConfig();
        backPacks = new ArrayList<>();
        viewingBackPack = new HashMap<>();
        playerStackLimit = new HashMap<>();

        multiVersionUtil = new Reflector().getMultiVersionUtil();
        messageService = new MessageService(getConfig().getString("Language"));
        ChatUtil.logMsg("&aLoaded backend services &7( &e" + timer.getDuration() + " ms &7)");

        timer.reset();
        File bpFile = new File(getDataFolder(), "backpacks.yml");
        backPacksYaml = YamlConfiguration.loadConfiguration(bpFile);
        loadBackPacks();
        ChatUtil.logMsg("&aLoaded backpacks &7( &e" + timer.getDuration() + " ms &7)");
    }

    @Override
    public void onEnable()
    {
        Timer timer = new Timer();
        Backend.registerListeners();
        ChatUtil.logMsg("&aRegistered commands and listeners &7( &e" + timer.getDuration() + " ms&7 )");
        Backend.checkForUpdates();
    }

    private void setUpConfig()
    {
        saveDefaultConfig();
        saveResource("backpacks.yml", false);
        final int CONFIG_VERSION = getConfig().getInt("Config-Version");
        Backend.checkConfigVersion(CONFIG_VERSION);
    }

    public void loadBackPacks()
    {
        if(backPacks != null && !backPacks.isEmpty()) backPacks.clear();
        for(String backPackName : backPacksYaml.getKeys(false)) {
            BackPack backPack = new BackPack(backPackName, backPacksYaml.getConfigurationSection(backPackName));

            backPacks.add(backPack);
        }
    }

    public BackPack getBackPackByName(String name)
    {
        for(BackPack backPack : backPacks) {
            if(name.equalsIgnoreCase(backPack.getKey())) {
                return backPack;
            }
        }

        return null;
    }

    public void reload()
    {
        reloadConfig();
        backPacksYaml = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "backpacks.yml"));
        messageService = new MessageService(getConfig().getString("Language"));
        loadBackPacks();
    }

    public static BackPacksPlus getInstance() {
        return JavaPlugin.getPlugin(BackPacksPlus.class);
    }
}

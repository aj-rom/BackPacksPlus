/*
 *     File: MessageService.java
 *     Last Modified: 1/12/21, 12:52 PM
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

package io.github.coachluck.backpacksplus.utils.lang;

import io.github.coachluck.backpacksplus.BackPacksPlus;
import io.github.coachluck.backpacksplus.utils.backend.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageService {

    private final YamlConfiguration messages;

    public MessageService(String language) {
        final BackPacksPlus plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);
        String langPath = "lang/" + language + ".yml";
        File mFile = new File(plugin.getDataFolder() + langPath);

        if (!mFile.exists()) {
            ChatUtil.error("No Language: &e" + language + " &ccould be found. Please make sure the selected language is in the &e/lang/ &cdirectory!");
            ChatUtil.error("Defaulting to &een-US &c...");
            langPath = "lang/en-US.yml";
            mFile = new File(plugin.getDataFolder() + langPath);
        }

        plugin.saveResource(langPath, false);
        messages = YamlConfiguration.loadConfiguration(mFile);
    }

    public void sendMessage(CommandSender sender, MessageKey messageKey) {
        ChatUtil.msg(sender, messages.getString(messageKey.getKey()));
    }

    public void sendMessage(CommandSender sender, MessageKey messageKey, String... replacements) {
        if (messageKey.getTags().length == 0) {
            sendMessage(sender, messageKey);
        }

    }
}

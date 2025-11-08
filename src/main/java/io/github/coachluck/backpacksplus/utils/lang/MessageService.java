/*
 *     File: MessageService.java
 *     Last Modified: 1/14/21, 10:49 PM
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
import java.util.List;

public class MessageService {

    private final YamlConfiguration messages;

    public MessageService()
    {
        final BackPacksPlus plugin = BackPacksPlus.getInstance();
        final String language = plugin.getConfig().getString("Language", "en");
        final String langPath = "lang/" + language + ".yml";

        if (plugin.getResource(langPath) == null) {
            ChatUtil.error("Could not find language file: &e: " + langPath);
            throw new RuntimeException("Could not find language file: &e" + langPath);
        }

        plugin.saveResource(langPath, false);
        File file = new File(plugin.getDataFolder(), langPath);
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public void sendMessage(CommandSender sender, MessageKey messageKey)
    {
        ChatUtil.msg(sender, retrieveMessage(messageKey));
    }

    public void sendMessage(CommandSender sender, MessageKey messageKey, String... replacements) {
        ChatUtil.msg(sender, getMessage(messageKey, replacements));
    }

    public String getMessage(MessageKey messageKey, String... replacements)
    {
        String message = retrieveMessage(messageKey);
        String[] tags = messageKey.getTags();

        if (replacements.length == tags.length) {
            for (int i = 0; i < tags.length; i++) {
                message = message.replace(tags[i], replacements[i]);
            }
        }

        return ChatUtil.format(message);
    }

    public String getRawMessage(MessageKey messageKey)
    {
        return ChatUtil.format(messages.getString(messageKey.getKey()));
    }

    public List<String> getRawMessageList(MessageKey messageKey)
    {
        return ChatUtil.formatLore(messages.getStringList(messageKey.getKey()));
    }

    private String retrieveMessage(MessageKey messageKey)
    {
        return ChatUtil.format(messages.getString(messageKey.getKey()));
    }
}

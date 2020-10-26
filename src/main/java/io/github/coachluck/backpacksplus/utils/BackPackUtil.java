/*
 *     File: BackPackUtil.java
 *     Last Modified: 9/24/20, 5:45 PM
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

package io.github.coachluck.backpacksplus.utils;

import io.github.coachluck.backpacksplus.BackPacksPlus;
import io.github.coachluck.backpacksplus.utils.backend.ChatUtil;
import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.UUID;

public class BackPackUtil {

    private static final BackPacksPlus plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);

    @Getter
    public static final NamespacedKey contentKey = new NamespacedKey(plugin, "content");

    @Getter
    public static final NamespacedKey nameKey = new NamespacedKey(plugin, "name");

    @Getter
    public static final NamespacedKey uuidKey = new NamespacedKey(plugin, "uuid");

    public static boolean isBackPack(ItemStack item) {
        if(item == null)
            return false;

        final ItemMeta meta = item.getItemMeta();
        if(meta == null)
            return false;

        return isBackPack(meta.getPersistentDataContainer());
    }

    public static boolean isBackPack(PersistentDataContainer data) {
        return data != null && !data.isEmpty() && data.has(contentKey, PersistentDataType.STRING)
                && data.has(nameKey, PersistentDataType.STRING);
    }

    public static Inventory getSavedContent(Player player, String contents) {
        Inventory inv = null;
        try {
            inv = InventorySerializerUtil.fromBase64(contents);

        } catch (IOException ioException) {
            ioException.printStackTrace();
            ChatUtil.error("&cError loading backpack contents for " + player.getName());
        }

        return inv;
    }

    public static boolean hasBackPackPermission(Player player, String backPackName, String type) {
        final String selector = type.toLowerCase();
        if(player.hasPermission("backpack.*") || player.hasPermission("backpack." + selector + ".*")) {
            return true;
        }
        return player.hasPermission("backpack." + selector + "." + backPackName.toLowerCase());
    }

    public static String getContent(PersistentDataContainer data) {
        return data.get(contentKey, PersistentDataType.STRING);
    }

    public static String getName(PersistentDataContainer data) {
        return data.get(nameKey, PersistentDataType.STRING);
    }

    public static String getUUID(PersistentDataContainer data) {
        return data.get(uuidKey, PersistentDataType.STRING);
    }
    
	public static NamespacedKey getContentKey() {
		return contentKey;
	}

	public static NamespacedKey getUuidKey() {
		// TODO Auto-generated method stub
		return uuidKey;
	}

	public static NamespacedKey getNameKey() {
		// TODO Auto-generated method stub
		return nameKey;
	}
	
    public static String getRandomUUIDString() {
    	UUID uuid = UUID.randomUUID(); 
    	String randomUUIDString = uuid.toString();
    	return randomUUIDString;
    }
    
    public static String invisablestring(String s) {
    	String hidden = "";
    	for (char c : s.toCharArray()) hidden += ChatColor.COLOR_CHAR+""+c;
    	return hidden;
    }
}

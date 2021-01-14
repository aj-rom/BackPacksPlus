/*
 *     File: SkullHelper.java
 *     Last Modified: 1/13/21, 10:48 PM
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

package io.github.coachluck.backpacksplus.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.coachluck.backpacksplus.utils.multiversion.ReflectionUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Base64;
import java.util.UUID;

public class SkullHelper {

    private static Material skull;

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url skin url
     * @return itemstack
     */
    public static ItemStack getCustomSkull(String url) {
        byte[] encodedData = Base64.getUrlEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        return getCustomSkull64(encodedData);
    }

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url64 skin url
     * @return itemstack
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getCustomSkull64(byte[] url64) {

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }

        String encodedData = new String(url64);
        propertyMap.put("textures", new Property("textures", encodedData));

        ItemStack head = new ItemStack(getSkull(), 1, (short) 3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();

        ReflectionUtil.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        head.setItemMeta(headMeta);
        return head;
    }

    public static Material getSkull() {
        if (skull == null) {
            try {
                skull = Material.matchMaterial("SKULL_ITEM");
            } catch (Error | Exception ignored) {
            }
            if (skull == null)
                skull = Material.PLAYER_HEAD;
        }

        return skull;
    }


}
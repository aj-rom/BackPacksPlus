/*
 *     File: SkullHelper.java
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

package io.github.coachluck.backpacksplus.api;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTListCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
    public static ItemStack getCustomSkull(String url)
    {
        byte[] encodedData = Base64.getUrlEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        return getCustomSkull64(encodedData);
    }

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url64 skin url
     * @return itemstack
     */
    public static ItemStack getCustomSkull64(byte[] url64)
    {
        return getCustomSkull64(new String(url64));
    }

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url64 skin url
     * @return itemstack
     */
    public static ItemStack getCustomSkull64(String url64)
    {
        ItemStack head = new ItemStack(getSkull(), 1);
        NBTItem nbti = new NBTItem(head);

        // Getting the compound, that way we can set the skin information
        NBTCompound skull = nbti.addCompound("SkullOwner");
        skull.setString("Id", UUID.nameUUIDFromBytes(url64.getBytes()).toString());

        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value", url64);

        return nbti.getItem();
    }

    public static Material getSkull()
    {
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

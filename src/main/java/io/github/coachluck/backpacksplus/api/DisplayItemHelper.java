/*
 *     File: DisplayItemHelper.java
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

import de.tr7zw.nbtapi.NBTItem;
import io.github.coachluck.backpacksplus.utils.BackPack;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DisplayItemHelper {

    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item
     */
    private static String convertItemStackToJson(ItemStack itemStack)
    {
        NBTItem nbti = new NBTItem(itemStack);

        return nbti.toString();
    }

    /**
     * Sends the item as a tooltip to the message
     * @param player the player to send the message too
     * @param message the message to add the hover element too.
     * @param backPack to show the recipe of
     */
    public static void sendItemTooltipMessage(Player player, String message, BackPack backPack)
    {
        final ItemStack item = backPack.getDisplayItem();
        final String itemJson = convertItemStackToJson(item);
        BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new TextComponent(itemJson)
        };

        final HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        TextComponent component = new TextComponent(message);
        component.setHoverEvent(event);

        player.spigot().sendMessage(component);
    }

}

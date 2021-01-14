/*
 *     File: BackPackCraftListener.java
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

package io.github.coachluck.backpacksplus.listeners;

import io.github.coachluck.backpacksplus.BackPacksPlus;
import io.github.coachluck.backpacksplus.api.BackPackUtil;
import io.github.coachluck.backpacksplus.utils.backend.ChatUtil;
import io.github.coachluck.backpacksplus.utils.lang.MessageKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class BackPackCraftListener implements Listener {

    private final BackPacksPlus plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        final Player player = (Player) e.getWhoClicked();
        ItemStack craftedItem = e.getCurrentItem();

        if(craftedItem == null || craftedItem.getItemMeta() == null) return;
        if(!BackPackUtil.isBackPack(craftedItem)) return;
        if(!BackPackUtil.hasBackPackPermission(player,
                BackPackUtil.getName(craftedItem.getItemMeta().getPersistentDataContainer()), "craft")) {
            e.setCurrentItem(null);
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
            plugin.getMessageService().sendMessage(player, MessageKey.PERMISSION_CRAFT);

            return;
        }

        // TODO: TRY TO ENFORCE UNSTACKED BACKPACKS
        craftedItem.setAmount(1);

        ItemMeta meta = craftedItem.getItemMeta();
        meta.getPersistentDataContainer().set(BackPackUtil.getUuidKey(),
                PersistentDataType.STRING, UUID.randomUUID().toString());
        craftedItem.setItemMeta(meta);

        e.setCurrentItem(craftedItem);

        e.setResult(Event.Result.ALLOW);

        plugin.getMessageService().getRawMessageList(MessageKey.BACKPACK_CRAFT).forEach(s ->
                ChatUtil.msg(player, s.replaceAll("%backpack%", meta.getDisplayName())));
    }
}

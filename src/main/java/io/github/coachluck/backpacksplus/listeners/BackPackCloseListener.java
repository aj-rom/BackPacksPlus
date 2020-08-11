/*
 *     File: BackPackCloseListener.java
 *     Last Modified: 8/11/20, 2:19 PM
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

import graywolf336.InventorySerializerUtil;
import io.github.coachluck.backpacksplus.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


public class BackPackCloseListener implements Listener {

    private final Main plugin = Main.getPlugin(Main.class);

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        final InventoryView viewingInv = e.getView();
        final Player player = (Player) e.getPlayer();
        if(!plugin.viewingBackPack.containsKey(player))
            return;

        int backPackSlot = plugin.viewingBackPack.get(player);

        ItemStack backPack = player.getInventory().getItem(backPackSlot);
        if(backPack == null || !backPack.hasItemMeta())
            return;

        ItemMeta meta = backPack.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(data.isEmpty())
            return;

        final String newContents = InventorySerializerUtil.toBase64(viewingInv.getTopInventory());
        data.set(new NamespacedKey(plugin, "content"), PersistentDataType.STRING, newContents);
        backPack.setItemMeta(meta);
        plugin.viewingBackPack.remove(player);
    }
}

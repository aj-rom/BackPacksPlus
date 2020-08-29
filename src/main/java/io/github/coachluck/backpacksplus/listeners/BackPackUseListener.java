/*
 *     File: BackPackUseListener.java
 *     Last Modified: 8/29/20, 1:18 AM
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
import io.github.coachluck.backpacksplus.utils.BackPackUtil;
import io.github.coachluck.backpacksplus.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BackPackUseListener implements Listener {

    private final BackPacksPlus plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);

    @EventHandler
    public void onClickEvent(PlayerInteractEvent e) {
        if(e.getItem() == null ||
                (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK))
            return;

        final Player player = e.getPlayer();
        final int slot = player.getInventory().getHeldItemSlot();
        ItemStack item = e.getItem();

        if(item.getType() == Material.AIR || !item.hasItemMeta()
                || item.getAmount() != 1 || item.getItemMeta() == null)
            return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(!BackPackUtil.isBackPack(data))
            return;

        e.setCancelled(true);

        player.getInventory().setItem(slot, null);
        data.set(BackPackUtil.getUuidKey(), PersistentDataType.STRING, "1");
        item.setItemMeta(meta);
        player.getInventory().setItem(slot, item);

        final String backPackName = data.get(BackPackUtil.getNameKey(), PersistentDataType.STRING);

        if(!player.hasPermission("backpack.use." + backPackName.toLowerCase())) {
            ChatUtil.msg(player, plugin.getMessages().getString("General.Use"));
            return;
        }

        final String contents = data.get(BackPackUtil.getContentKey(), PersistentDataType.STRING);
        final Inventory prevInventory = BackPackUtil.getSavedContent(player, contents);
        final int size = plugin.getConfig().getInt("BackPacks." + backPackName + ".Size");
        final String title = ChatUtil.format(plugin.getConfig().getString("BackPacks." + backPackName + ".Title"));
        Inventory finalInv = Bukkit.createInventory(null, size, title);

        finalInv.setContents(prevInventory.getContents());
        player.openInventory(finalInv);
        plugin.viewingBackPack.put(player, slot);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        if (e.isCancelled() || !plugin.viewingBackPack.containsKey(player))
            return;

        final ClickType type = e.getClick();
        int slot = plugin.viewingBackPack.get(player);
        final Inventory clickedInventory = e.getClickedInventory();
        if (clickedInventory == e.getInventory()
                && type.isKeyboardClick() && e.getHotbarButton() == slot) {
                    e.setCancelled(true);
                    return;
        }

        final InventoryAction action = e.getAction();
        if((action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD)
                && slot == e.getHotbarButton()) {

                e.setCancelled(true);
                return;
        }

        final int clickedSLot = e.getSlot();
        final boolean isBottomInventory = player.getOpenInventory().getBottomInventory() == clickedInventory;
        if(slot == clickedSLot
                && isBottomInventory) {
            e.setCancelled(true);
            return;
        }

        if(!plugin.getConfig().getBoolean("General.NestableBackPack") && isBottomInventory) {
            final ItemStack clickedItem = e.getCurrentItem();
            if(clickedItem == null)
                return;

            final ItemMeta clickedItemMeta = clickedItem.getItemMeta();
            if(clickedItemMeta == null)
                return;

            if(BackPackUtil.isBackPack(clickedItemMeta.getPersistentDataContainer()))
                e.setCancelled(true);
        }
    }
}

/*
 *     File: BackPackUseListener.java
 *     Last Modified: 1/13/21, 10:50 PM
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
import io.github.coachluck.backpacksplus.utils.BackPack;
import io.github.coachluck.backpacksplus.utils.backend.ChatUtil;
import io.github.coachluck.backpacksplus.utils.lang.MessageKey;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BackPackUseListener implements Listener {

    private final BackPacksPlus plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);

    @EventHandler
    public void onClickEvent(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getHand() == null ||
                (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK))
            return;

        final Player player = e.getPlayer();
        int slot = (e.getHand() == EquipmentSlot.OFF_HAND) ? 45 : player.getInventory().getHeldItemSlot();

        final EquipmentSlot heldBackPackSlot = e.getHand();
        ItemStack item = e.getItem();

        if (item.getType() == Material.AIR || !item.hasItemMeta()
                || item.getAmount() != 1 || item.getItemMeta() == null)
            return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!BackPackUtil.isBackPack(data))
            return;

        e.setCancelled(true);

        plugin.getMultiVersionUtil().setInventorySlot(player, heldBackPackSlot, null);
        data.set(BackPackUtil.getUuidKey(), PersistentDataType.STRING, "1");
        item.setItemMeta(meta);
        plugin.getMultiVersionUtil().setInventorySlot(player, heldBackPackSlot, item);

        final String backPackName = data.get(BackPackUtil.getNameKey(), PersistentDataType.STRING);

        if(!BackPackUtil.hasBackPackPermission(player, backPackName, "use")) {
            plugin.getMessageService().sendMessage(player, MessageKey.PERMISSION_USE);
            return;
        }

        final String contents = data.get(BackPackUtil.getContentKey(), PersistentDataType.STRING);
        final Inventory prevInventory = BackPackUtil.getSavedContent(player, contents);
        final int size = plugin.getBackPacksYaml().getInt(backPackName + ".Size");
        final String title = ChatUtil.format(plugin.getBackPacksYaml().getString(backPackName + ".Title"));
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

        ItemStack currentItem = e.getCurrentItem();
        final ClickType type = e.getClick();
        int slot = plugin.viewingBackPack.get(player);
        final Inventory clickedInventory = e.getClickedInventory();
        if (clickedInventory == e.getInventory() && type.isKeyboardClick() && e.getHotbarButton() == slot) {
            e.setCancelled(true);
            return;
        }

        final InventoryAction action = e.getAction();
        if (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD) {
            ItemStack swapItem = e.getView().getBottomInventory().getItem(e.getHotbarButton());
            if (BackPackUtil.isBackPack(swapItem)) {
                e.setCancelled(true);
                return;
            }

            currentItem = swapItem;
        }

        final int clickedSLot = e.getSlot();
        final boolean isBottomInventory = player.getOpenInventory().getBottomInventory() == clickedInventory;
        if (slot == clickedSLot && isBottomInventory) {
            e.setCancelled(true);
            return;
        }

        if (isBottomInventory) {
            final ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) return;
            if (BackPackUtil.isBackPack(clickedItem)) {
                e.setCancelled(true);
                return;
            }
        }

        ItemStack item = e.getView().getBottomInventory().getItem(slot);
        if (item == null) {
            item = player.getInventory().getItemInOffHand();
        }

        if (currentItem == null) {
            currentItem = e.getCursor();
        }

        final Material currentType = currentItem.getType();
        if (currentType == Material.AIR || e.getAction() == InventoryAction.PICKUP_ALL) {
            return;
        }

        final BackPack currentPack = BackPackUtil.getBackPackFromItem(item);
        if ((currentPack.hasBlackList() && currentPack.getBlackList().contains(currentType))
            || (currentPack.hasWhiteList() && !currentPack.getWhiteList().contains(currentType))) {

            plugin.getMessageService().sendMessage(player, MessageKey.ITEM_NOT_ALLOWED,
                    currentPack.getDisplayName(), currentType.toString());
            e.setCancelled(true);
        }
    }
}

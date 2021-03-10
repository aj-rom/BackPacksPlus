/*
 *     File: BackPackUseListener.java
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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BackPackUseListener implements Listener {

    private final BackPacksPlus plugin = BackPacksPlus.getInstance();

    @EventHandler
    public void onClickEvent(PlayerInteractEvent e)
    {
        if (e.getItem() == null || e.getHand() == null ||
                (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK))
            return;

        final Player player = e.getPlayer();
        int slot = (e.getHand() == EquipmentSlot.OFF_HAND) ? 45 : player.getInventory().getHeldItemSlot();
        final EquipmentSlot heldBackPackSlot = e.getHand();
        ItemStack item = e.getItem();

        if (item.getType() == Material.AIR || !item.hasItemMeta()
                || item.getAmount() != 1 || item.getItemMeta() == null
                || !BackPackUtil.isBackPack(item))
            return;

        e.setCancelled(true);

        if (BackPackUtil.getBackPackFromItem(item).isEnderChestEnabled()) {
            player.openInventory(player.getEnderChest());
            return;
        }

        PersistentDataContainer data = getBackPackData(player, heldBackPackSlot, item);
        final String backPackName = BackPackUtil.getName(data);
        if(!BackPackUtil.hasBackPackPermission(player, backPackName, "use")) {
            plugin.getMessageService().sendMessage(player, MessageKey.PERMISSION_USE);
            return;
        }

        final Inventory prevInventory = BackPackUtil.getSavedContent(player, data);
        final int size = plugin.getBackPacksYaml().getInt(backPackName + ".Size");
        final String title = ChatUtil.format(plugin.getBackPacksYaml().getString(backPackName + ".Title"));
        Inventory finalInv = Bukkit.createInventory(null, size, title);

        finalInv.setContents(prevInventory.getContents());
        player.openInventory(finalInv);
        plugin.viewingBackPack.put(player, slot);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e)
    {
        final Player player = (Player) e.getWhoClicked();
        final InventoryType invType = InventoryType.CHEST;
        if (e.isCancelled() || e.getInventory().getType() != invType
                || !plugin.viewingBackPack.containsKey(player))
            return;

        lockInv(e, invType);
    }

    @EventHandler
    public void onEndChestOpen(InventoryClickEvent e)
    {
        final InventoryType invType = InventoryType.ENDER_CHEST;
        if (e.isCancelled() || !BackPackUtil.isEndChestEnabled()
                || e.getView().getTopInventory().getType() != invType) return;

        lockInv(e, invType);
    }

    private void lockInv(InventoryClickEvent e, InventoryType invType)
    {
        final Player player = (Player) e.getWhoClicked();
        final InventoryAction action = e.getAction();
        ItemStack currentItem = e.getCurrentItem();

        if (isPickingUp(action) && e.getClickedInventory().getType() == InventoryType.PLAYER
                && BackPackUtil.isBackPack(e.getCurrentItem())) {
            e.setCancelled(true);
            return;
        }
        if (isNotMoving(action)) return;
        if (action == InventoryAction.HOTBAR_MOVE_AND_READD || action == InventoryAction.HOTBAR_SWAP) {
            if (BackPackUtil.isBackPack(currentItem)) {
                e.setCancelled(true);
                return;
            }

            ItemStack hotItem = player.getInventory().getItem(e.getHotbarButton());
            if (BackPackUtil.isBackPack(hotItem)) {
                e.setCancelled(true);
                return;
            }

            currentItem = (hotItem == null) ? e.getCurrentItem() : hotItem;
        }
        if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE
                || action == InventoryAction.PLACE_SOME) {
            if (e.getSlotType() != InventoryType.SlotType.CONTAINER
                    && e.getClickedInventory().getType() != invType) return;

            if (BackPackUtil.isBackPack(e.getCursor())) {
                e.setCancelled(true);
                return;
            }

            if (e.getCursor().getType() == Material.AIR) {
                return;
            }

            currentItem = e.getCursor();
        }
        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (e.getClickedInventory().getType() != InventoryType.PLAYER) return;
            if (BackPackUtil.isBackPack(currentItem)) {
                e.setCancelled(true);
                return;
            }
        }

        Material currentType = currentItem.getType();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!BackPackUtil.isBackPack(item))
            item = player.getInventory().getItemInOffHand();

        BackPack currentPack = getCurrentBackPack(item);
        if (currentPack != null && (currentPack.hasBlackList() && currentPack.getBlackList().contains(currentType))
                || (currentPack.hasWhiteList() && !currentPack.getWhiteList().contains(currentType))) {
            plugin.getMessageService().sendMessage(player, MessageKey.ITEM_NOT_ALLOWED,
                    currentPack.getDisplayName(),
                    currentType.toString());
            e.setCancelled(true);
        }
    }

    private BackPack getCurrentBackPack(ItemStack item)
    {
        BackPack currentPack = null;
        if (BackPackUtil.getBackPackFromItem(item) != null) {
            currentPack = BackPackUtil.getBackPackFromItem(item);
        }
        else {
            for (BackPack bp : plugin.getBackPacks()) {
                if (bp.isEnderChestEnabled()) {
                    currentPack = bp;
                }
            }
        }
        return currentPack;
    }

    private boolean isPickingUp(InventoryAction action)
    {
        return action == InventoryAction.PICKUP_ALL || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_SOME || action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.SWAP_WITH_CURSOR;
    }

    private boolean isNotMoving(InventoryAction action)
    {
        return action != InventoryAction.PLACE_ALL && action != InventoryAction.HOTBAR_SWAP
                && action != InventoryAction.HOTBAR_MOVE_AND_READD
                && action != InventoryAction.MOVE_TO_OTHER_INVENTORY
                && action != InventoryAction.PLACE_ONE && action != InventoryAction.PLACE_SOME;
    }

    private PersistentDataContainer getBackPackData(Player player, EquipmentSlot heldBackPackSlot, ItemStack item)
    {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        plugin.getMultiVersionUtil().setInventorySlot(player, heldBackPackSlot, null);
        data.set(BackPackUtil.getUuidKey(), PersistentDataType.STRING, "1");
        item.setItemMeta(meta);
        plugin.getMultiVersionUtil().setInventorySlot(player, heldBackPackSlot, item);

        return data;
    }
}

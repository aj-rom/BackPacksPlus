/*
 *     File: InventoryWatcher.java
 *     Last Modified: 8/22/20, 7:48 PM
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

import io.github.coachluck.backpacksplus.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryWatcher {

    @Getter
    @Setter
    private int limit;

    private int taskID;
    private int currentBackPackAmt;

    private Player player;

    public InventoryWatcher(Player player, int limit) {
        this.limit = limit;
        this.player = player;
        Main plugin = Main.getPlugin(Main.class);
        this.taskID = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
            currentBackPackAmt = 0;
            if(player.isOnline()) {
                Bukkit.getServer().getScheduler().cancelTask(taskID);
            }
            boolean hasBackPack = false;

            PlayerInventory inventory = player.getInventory();
            ItemStack backPackItem = null;
            for(BackPack backPack : plugin.backPacks) {
                if(hasBackPack || !inventory.contains(backPack.getBackPackItem()))
                    continue;

                hasBackPack = true;
                backPackItem = backPack.getBackPackItem();
            }

            if(!hasBackPack || backPackItem == null)
                return;

            boolean maxed = false;
            int slot = 0;
            int count = 0;
            int removedCount = 0;
            for(ItemStack itemStack : inventory.getContents()) {
                if(itemStack != backPackItem) {
                    slot++;
                    continue;
                }

                if(count >= limit) {
                    player.getInventory().setItem(slot, null);
                    player.getWorld().dropItem(player.getLocation(), itemStack);
                    removedCount++;
                } else {
                    count++;
                }

                slot++;
            }

            if(count > limit) {

            }

        }, 20, 60);
    }



}

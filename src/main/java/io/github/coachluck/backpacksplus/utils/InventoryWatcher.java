/*
 *     File: InventoryWatcher.java
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

package io.github.coachluck.backpacksplus.utils;

import io.github.coachluck.backpacksplus.BackPacksPlus;
import io.github.coachluck.backpacksplus.api.BackPackUtil;
import io.github.coachluck.backpacksplus.utils.lang.MessageKey;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class InventoryWatcher {

    /**
     * The limit of backpacks a player can hold
     */
    private int limit;

    /**
     * The task ID of the repeating task
     */
    private int taskID;

    /**
     * The player who the task is watching
     */
    private final Player player;

    /**
     * The actual task that checks the inventory for excess backpacks
     */
    private final Runnable task;

    /**
     * Instance of the plugin
     */
    private final BackPacksPlus plugin;

    /**
     * Allows calls from storage to cancel the task
     * Used on player disconnect
     */
    @Setter
    private boolean done;

    public InventoryWatcher(Player player) {
        this.limit = getLimit(player);
        this.player = player;
        this.task = getTask();
        this.plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);
        this.done = false;
        run();
    }

    /**
     * Gets the InventoryWatcher task
     * @return Runnable that checks the inventory for excess backpacks and removes them
     */
    private Runnable getTask() {

        return new BukkitRunnable() {

            int timer = 0;
            @Override
            public void run() {
                if (done) Bukkit.getServer().getScheduler().cancelTask(taskID);

                if(timer % 60 == 0) {
                    limit = getLimit(player);
                }

                timer++;

                PlayerInventory inventory = player.getInventory();

                int count = 0;
                int removedCount = 0;
                for (ItemStack itemStack : inventory.getContents()) {
                    if (itemStack == null)
                        continue;
                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta == null || !BackPackUtil.isBackPack(meta.getPersistentDataContainer()))
                        continue;

                    int prevCount = count;
                    count = count + itemStack.getAmount();
                    if (count > limit) {
                        int amountToKeep = limit - prevCount;
                        int difference = itemStack.getAmount() - amountToKeep;
                        removedCount = removedCount + difference;

                        if(difference > 0) {
                            itemStack.setAmount(difference);
                            player.getInventory().remove(itemStack);
                        }

                        if(amountToKeep > 0) {
                            itemStack.setAmount(amountToKeep);
                            player.getInventory().addItem(itemStack);
                        }
                    }
                }

                if (removedCount == 0) {
                    return;
                }

                plugin.getMessageService().sendMessage(player, MessageKey.OVER_LIMIT,
                        Integer.toString(removedCount), Integer.toString(limit));
            }
        };
    }

    /**
     * Runs the InventoryWatcher at the configured delay
     */
    private void run() {
        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin,
                task, 0L, plugin.getConfig().getInt("General.BackPackLimiter.Repeat"));
    }

    /**
     * Gets the backpack limit for the player
     * @param player the player to get the limit of
     * @return number of backpacks the player can hold at once.
     */
    private int getLimit(Player player) {
        AtomicInteger rValue = new AtomicInteger();
        player.getEffectivePermissions().forEach((perm) -> {
            if(perm.getValue() && perm.getPermission().startsWith("backpacks.limit.")) {
                String limit = perm.getPermission().replaceAll("backpacks.limit.", "");
                if(limit.equalsIgnoreCase("*")) {
                    rValue.set(100000);
                } else {
                    int vlimit = Integer.parseInt(limit);
                    rValue.set(vlimit);
                }
            }
        });

        return rValue.intValue();
    }

	public void setDone(boolean b) {
		done=b;
	}

}

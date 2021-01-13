/*
 *     File: BackPackRenameListener.java
 *     Last Modified: 10/27/20, 1:11 PM
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
import io.github.coachluck.backpacksplus.utils.backend.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BackPackRenameListener implements Listener {

	private final BackPacksPlus plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);

	@EventHandler
	public void listenAnvil(PrepareAnvilEvent e) {

		if (BackPackUtil.isBackPack(e.getInventory().getItem(0))) {
			Player p = (Player) e.getViewers().get(0);
			AnvilInventory inv = e.getInventory();
			if (p.hasPermission("backpack.rename")) {

				// TODO: Fix repair cost not being able to be free
				if (plugin.getConfig().getBoolean("General.BackPackFreeRename.Enabled")) {
					Bukkit.getScheduler().runTask(plugin, () -> {
						inv.setRepairCost(0);
						p.updateInventory();
					});
				}
				ItemStack item = e.getResult();

				if (item != null && item.getType() != Material.AIR) {
					if (item.hasItemMeta()) {
						ItemMeta meta = item.getItemMeta();
						if(!e.getInventory().getRenameText().isEmpty()) {
							String text = inv.getRenameText();
							if(p.hasPermission("backpack.rename.color"))
								meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', text));
							else meta.setDisplayName(text.replaceAll("&([0-9a-z])", ""));

							item.setItemMeta(meta);
							e.setResult(item);
						}
					}
				}
			} else if (p.hasPermission("backpack.rename.deny")) {
				e.getView().close();
				ChatUtil.msg(p, plugin.getMessages().getString("General.NoRenamePerm"));
			}
		}
	}
}

package io.github.coachluck.backpacksplus.listeners;

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
import io.github.coachluck.backpacksplus.BackPacksPlus;
import io.github.coachluck.backpacksplus.utils.BackPackUtil;
import io.github.coachluck.backpacksplus.utils.backend.ChatUtil;

public class BackPackRenameListener implements Listener {

	private final BackPacksPlus plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);

	@EventHandler
	public void listenanvil(PrepareAnvilEvent e) {
		if (BackPackUtil.isBackPack(e.getInventory().getItem(0))) {
			Player p = (Player) e.getViewers().get(0);
			AnvilInventory inv = e.getInventory();
			if (p.hasPermission("backpack.rename.allow") || p.hasPermission("backpack.rename")) {

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
						if (p.hasPermission("backpack.rename.color")) {

							if (!e.getInventory().getRenameText().isEmpty()) {
								String text = inv.getRenameText();

								meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', text));
								item.setItemMeta(meta);
								e.setResult(item);
							}
						} else {
							if (!e.getInventory().getRenameText().isEmpty()) {
								String text = inv.getRenameText();
								
								meta.setDisplayName(text.replaceAll("&([0-9a-z])", ""));
								item.setItemMeta(meta);
								e.setResult(item);
							}
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

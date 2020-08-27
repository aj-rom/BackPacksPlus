/*
 *     File: OverLimitStackListener.java
 *     Last Modified: 8/22/20, 9:29 PM
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
import io.github.coachluck.backpacksplus.utils.InventoryWatcher;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class InventoryWatcherListener implements Listener {

    private final BackPacksPlus plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        plugin.playerStackLimit.put(player.getUniqueId(), new InventoryWatcher(player));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        final UUID uuid = e.getPlayer().getUniqueId();
        plugin.playerStackLimit.get(uuid).setDone(true);
        plugin.playerStackLimit.remove(uuid);
    }

}

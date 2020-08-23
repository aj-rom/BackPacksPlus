/*
 *     File: OverLimitStackListener.java
 *     Last Modified: 8/22/20, 7:41 PM
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

import io.github.coachluck.backpacksplus.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class OverLimitStackListener implements Listener {

    private final Main plugin = Main.getPlugin(Main.class);

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        int limit = getLimit(player);
        plugin.playerStackLimit.put(uuid, limit);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        final UUID uuid = e.getPlayer().getUniqueId();

        plugin.playerStackLimit.remove(uuid);
    }


    public int getLimit(Player player) {
        // TODO : Get the backpack limit of the player
        return 1;
    }

}

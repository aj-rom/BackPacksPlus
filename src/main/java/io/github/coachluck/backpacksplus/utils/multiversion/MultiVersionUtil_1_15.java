/*
 *     File: MultiVersionUtil_1_15.java
 *     Last Modified: 9/4/20, 4:19 PM
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

package io.github.coachluck.backpacksplus.utils.multiversion;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class MultiVersionUtil_1_15 implements MultiVersionUtil {

    @Override
    public void registerRecipe(NamespacedKey namespacedKey, ShapedRecipe recipe) {
        Bukkit.getServer().removeRecipe(namespacedKey);
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    public void setInventorySlot(Player player, EquipmentSlot slot, ItemStack item) {
        player.getInventory().setItem(slot, item);
    }

}

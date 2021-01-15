/*
 *     File: MultiVersionUtil.java
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

package io.github.coachluck.backpacksplus.utils.multiversion;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public interface MultiVersionUtil
{

    /**
     * Register the recipe for the server
     * @param namespacedKey the namespace key
     * @param recipe the shaped recipe
     */
    void registerRecipe(NamespacedKey namespacedKey, ShapedRecipe recipe);

    /**
     * Sets the inventory slot of a player
     * @param player the player
     * @param slot the EquipmentSlot to change if not Hand, will change offHand
     * @param item the item to replace it with
     */
    void setInventorySlot(Player player, EquipmentSlot slot, ItemStack item);
}

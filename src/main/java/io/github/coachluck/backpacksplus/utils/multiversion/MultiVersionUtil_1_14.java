/*
 *     File: MultiVersionUtil_1_14.java
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

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Iterator;

public class MultiVersionUtil_1_14 implements MultiVersionUtil
{
    @Override
    public void registerRecipe(NamespacedKey namespacedKey, ShapedRecipe recipe)
    {
        Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();
        Recipe recp;
        while(recipeIterator.hasNext()) {
            recp = recipeIterator.next();
            if (recipe.getResult().equals(recp.getResult())) {
                recipeIterator.remove();
                break;
            }
        }

        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    public void setInventorySlot(Player player, EquipmentSlot slot, ItemStack item)
    {
        if(slot == EquipmentSlot.HAND) {
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), item);
            return;
        }

        player.getInventory().setItem(45, item);

    }
}

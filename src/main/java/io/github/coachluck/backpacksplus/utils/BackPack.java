/*
 *     File: BackPack.java
 *     Last Modified: 9/4/20, 1:43 AM
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

import graywolf336.InventorySerializerUtil;
import io.github.coachluck.backpacksplus.BackPacksPlus;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BackPack {

    /**
     * Namespace key, and search key
     */
    @Getter
    private final String name;

    /**
     * The display name for the item
     */
    @Getter
    private final String displayName;

    /**
     * The permission to use this backpack
     */
    @Getter
    private final String permission;

    /**
     * The title to display when opening the backpack
     */
    @Getter
    private final String title;

    /**
     * The key 'BackPacks.<name>' for config and data storage
     */
    @Getter
    private final String key;

    /**
     * The ItemStack of this backpack
     */
    @Getter
    private final ItemStack backPackHoldItem;

    /**
     * The lore of this backpack
     */
    private final List<String> lore;

    /**
     * Whether or not the item is enchanted
     */
    private final boolean enchanted;

    /**
     * The material of the item
     */
    private final Material material;

    /**
     * Custom model data integer
     */
    private final int customModelData;

    /**
     * The string list from config of the recipe
     */
    private final List<String> recipeShapeList;

    /**
     * The NameSpacedKey of the backpack [ backpack_<name> ]
     */
    private final NamespacedKey nameSpacedKey;

    private final int size;

    /**
     * The texture url for custom textures
     */
    private final String textureUrl;


    private final BackPacksPlus plugin;

    /**
     * Create a backpack
     * @param key the name of the backpack
     * @param material the material of the backpack
     * @param displayName the display name for the item of the backpack
     * @param lore the lore for the backpack
     * @param recipeShapeList the recipe in a 3 line list
     * @param title the title of the backpack inventory
     * @param permission the permission to use the backpack
     * @param enchanted whether or not the backpack should be enchanted
     */
    public BackPack(String key, Material material, String displayName, List<String> lore, List<String> recipeShapeList, String title, String permission, boolean enchanted) {
        plugin = BackPacksPlus.getPlugin(BackPacksPlus.class);
        this.name = key;
        this.key = "BackPacks." + key;
        this.title = ChatUtil.format(title);
        this.permission = permission;
        this.displayName = displayName;
        this.lore = ChatUtil.formatLore(lore);
        this.recipeShapeList = recipeShapeList;
        this.enchanted = enchanted;
        this.material = material;
        this.nameSpacedKey = new NamespacedKey(plugin, "backpack_" + key);

        String texture = plugin.getConfig().getString(this.key + ".Texture");
        if(texture != null) {
            this.textureUrl = texture;
        } else {
            this.textureUrl = "";
        }

        int customData = plugin.getConfig().getInt(this.key + ".CustomData");
        if(customData >= 0) {
            this.customModelData = plugin.getConfig().getInt(this.key + ".CustomData");
        } else {
            this.customModelData = -1;
        }

        this.size = plugin.getConfig().getInt(this.key + ".Size");

        backPackHoldItem = getBackPackItem();
        plugin.getMultiVersionUtil().registerRecipe(this.nameSpacedKey, this.getShapedRecipe());
    }

    /**
     * Creates the backpack item
     */
    public ItemStack getBackPackItem() {
        Material material = this.material;
        ItemStack bpItem = new ItemStack(material);

        if(!textureUrl.isEmpty() &&
                (material.toString().equalsIgnoreCase("SKULL")
                        || material.toString().equalsIgnoreCase("PLAYER_HEAD")))
        {
            bpItem = SkullHelper.getCustomSkull(textureUrl);
        }

        ItemMeta itemMeta = bpItem.getItemMeta();
        itemMeta.setDisplayName(ChatUtil.format(displayName));
        itemMeta.setLore(lore);

        Inventory inv = Bukkit.createInventory(null, size, this.title);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "content"), PersistentDataType.STRING, InventorySerializerUtil.toBase64(inv));
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "name"), PersistentDataType.STRING, name);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "uuid"), PersistentDataType.STRING, "");

        if(customModelData != -1) {
            itemMeta.setCustomModelData(customModelData);
        }
        if(enchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);

        bpItem.setItemMeta(itemMeta);

        return bpItem;
    }


    /**
     * Get's the namespaced
     */
    private ShapedRecipe getShapedRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(nameSpacedKey, backPackHoldItem);

        recipe.shape(recipeShapeList.get(0), recipeShapeList.get(1), recipeShapeList.get(2));


        String path = key + ".Recipe.Materials.";
        for(String recipeKey : plugin.getConfig().getConfigurationSection(path).getKeys(false)) {
            char rKey = recipeKey.charAt(0);
            Material material = Material.getMaterial(plugin.getConfig().getString(path + rKey + ".Material"));
            recipe.setIngredient(rKey, material);
        }
        return recipe;
    }

    /**
     * Returns the display Item For the active backpack list
     * @return the display item
     */
    public ItemStack getDisplayItem() {
        final String path = "BackPacks." + name + ".Recipe.";
        final List<String> shape = plugin.getConfig().getStringList(
                path + "Shape");

        ItemStack item = getBackPackItem();
        ItemMeta meta = item.getItemMeta();
        List<String> newLore = new ArrayList<>();

        newLore.add(ChatUtil.format("&8--------"));
        for(int i = 0; i < 3; i++) {
            newLore.add(ChatUtil.format("&8| &e"
                    + shape.get(i).substring(0, 1) + " &8| &e"
                    + shape.get(i).substring(1, 2) + " &8| &e"
                    + shape.get(i).substring(2, 3) + " &8|"));
            newLore.add(ChatUtil.format("&8--------"));
        }

        for(String key : plugin.getConfig().getConfigurationSection(path + "Materials").getKeys(false)) {
            newLore.add(ChatUtil.format("&e" + key + "&7 - &b" + plugin.getConfig().getString(path + "Materials." + key + ".Material")));
        }


        meta.setLore(newLore);
        item.setItemMeta(meta);
        return item;
    }



}

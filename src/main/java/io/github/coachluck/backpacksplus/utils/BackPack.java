/*
 *     File: BackPack.java
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

package io.github.coachluck.backpacksplus.utils;

import io.github.coachluck.backpacksplus.BackPacksPlus;
import io.github.coachluck.backpacksplus.api.InventorySerializerUtil;
import io.github.coachluck.backpacksplus.api.SkullHelper;
import io.github.coachluck.backpacksplus.utils.backend.ChatUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BackPack {

    /**
     * The display name for the item
     */
    @Getter
    private String displayName;


    /**
     * The title to display when opening the backpack
     */
    @Getter
    private String title;

    /**
     * The key 'BackPacks.<name>' for config and data storage
     */
    @Getter
    private final String key;

    /**
     * The ItemStack of this backpack
     */
    @Getter
    private ItemStack backPackHoldItem;

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
    private Material material;

    /**
     * Whether or not this backpack opens an enderchest
     */
    @Getter
    private final boolean enderChestEnabled;


    /**
     * The string list from config of the recipe
     */
    private List<String> recipeShapeList;

    /**
     * The NameSpacedKey of the backpack [ backpack_<name> ]
     */
    @Getter
    private final NamespacedKey nameSpacedKey;

    /**
     * Size of the inventory
     */
    private int size;

    /**
     * The texture url for custom textures
     */
    private String textureUrl;

    @Getter
    private ShapedRecipe shapedRecipe;


    private final BackPacksPlus plugin = BackPacksPlus.getInstance();

    @Getter
    private final List<Material> whiteList = new ArrayList<>();

    @Getter
    private final List<Material> blackList = new ArrayList<>();

    public BackPack(String key, ConfigurationSection section)
    {
        this.key = key;
        this.nameSpacedKey = new NamespacedKey(plugin, "backpack_" + key);
        this.lore = ChatUtil.formatLore(section.getStringList("Lore"));
        this.enchanted = section.getBoolean("Enchanted");
        this.enderChestEnabled = section.getBoolean("EnderChest");

        checkAndSetAll(section);

        plugin.getMultiVersionUtil().registerRecipe(nameSpacedKey, getShapedRecipe());
    }

    public boolean hasWhiteList()
    {
        return whiteList.size() > 0;
    }

    public boolean hasBlackList()
    {
        return blackList.size() > 0;
    }

    public boolean isCustomTextured()
    {
        return textureUrl != null || material == Material.PLAYER_HEAD;
    }

    /**
     * Returns the display Item For the active backpack list
     * @return the display item
     */
    public ItemStack getDisplayItem()
    {
        final String path = key + ".Recipe.";
        final List<String> shape = plugin.getBackPacksYaml().getStringList(
                path + "Shape");

        ItemStack item = getBackPackItem();
        ItemMeta meta = item.getItemMeta();
        List<String> newLore = new ArrayList<>();

        newLore.add(ChatUtil.format("&8--------"));
        for(int i = 0; i < 3; i++) {
            newLore.add(ChatUtil.format("&8| &e"
                    + shape.get(i).charAt(0) + " &8| &e"
                    + shape.get(i).charAt(1) + " &8| &e"
                    + shape.get(i).charAt(2) + " &8|"));
            newLore.add(ChatUtil.format("&8--------"));
        }

        ConfigurationSection matSection = plugin.getBackPacksYaml().getConfigurationSection(path + "Materials");
        for(String key : matSection.getKeys(false)) {
            newLore.add(ChatUtil.format("&e" + key + "&7 - &b"
                    + plugin.getBackPacksYaml().getString(path + "Materials." + key)));
        }


        meta.setLore(newLore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates the backpack item
     */
    private ItemStack getBackPackItem()
    {
        ItemStack bpItem = new ItemStack(material);

        if (isCustomTextured()) {
            bpItem = SkullHelper.getCustomSkull(textureUrl);
        }

        ItemMeta itemMeta = bpItem.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);

        Inventory inv = Bukkit.createInventory(null, size, title);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "content"), PersistentDataType.STRING, InventorySerializerUtil.toBase64(inv));
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "name"), PersistentDataType.STRING, key);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "uuid"), PersistentDataType.STRING, UUID.randomUUID().toString());

        if (enchanted) {
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

    private ShapedRecipe getRecipe(ConfigurationSection section)
    {

        ShapedRecipe sR = new ShapedRecipe(nameSpacedKey, getBackPackItem());
        sR.shape(recipeShapeList.get(0), recipeShapeList.get(1), recipeShapeList.get(2));

        String path = "Recipe.Materials.";
        for(String recipeKey : section.getConfigurationSection(path).getKeys(false)) {
            char rKey = recipeKey.charAt(0);
            String mat = section.getString(path + recipeKey);

            if (mat == null || mat.isEmpty()) {
                configError("must have a valid material for its recipe. Currently empty at &eMaterials." + rKey);
            }

            Material material = Material.getMaterial(mat);
            if (material == null) {
                configError("must have a valid material for its recipe. Currently &eMaterials."
                        + recipeKey + " &cis &e" + mat);
            }

            sR.setIngredient(rKey, material);
        }

        return sR;
    }

    private void checkAndSetAll(ConfigurationSection s)
    {
        checkAndSetName(s);
        checkAndSetTitle(s);
        checkAndSetMat(s);
        checkAndSetSize(s);
        checkAndSetWhiteAndBlackList(s);
        this.backPackHoldItem = getBackPackItem();
        checkAndSetRecipe(s);
        this.shapedRecipe = getRecipe(s);
    }

    private void checkAndSetName(ConfigurationSection section)
    {
        final String tempDisplay = section.getString("Name");
        if (tempDisplay == null || tempDisplay.isEmpty()) {
            configError("must have a name!");
        }

        this.displayName = ChatUtil.format(tempDisplay);
    }

    private void checkAndSetTitle(ConfigurationSection section)
    {
        final String tempTitle = section.getString("Title");
        if (tempTitle == null || tempTitle.isEmpty()) {
            configError("must have a title!");
        }
        this.title = ChatUtil.format(tempTitle);
    }

    private void checkAndSetMat(ConfigurationSection section)
    {
        final String matString = section.getString("Material");
        if (matString == null || matString.isEmpty()) {
            configError("must have a material!");
        }

        if (isCustomTexture(matString)) {
            this.material = Material.PLAYER_HEAD;
            this.textureUrl = section.getString("Texture");
        } else {
            this.material = Material.getMaterial(matString);
        }

        if (this.material == null) {
            configError("must have a valid material. Material: &e" + matString + " could not be found.");
        }
    }

    private void checkAndSetSize(ConfigurationSection section)
    {
        int tempSize = section.getInt("Size");
        if (tempSize % 9 != 0 || tempSize < 9 || tempSize > 54) {
            if(!isEnderChestEnabled())
                configError("must have a size of one of the following: &e9, 18, 27, 36, 45, 54");
            tempSize = 9;
        }
        this.size = tempSize;
    }

    private void checkAndSetWhiteAndBlackList(ConfigurationSection section)
    {
        List<String> wList = section.getStringList("Whitelist");
        List<String> bList = section.getStringList("Blacklist");

        if (wList.size() > 0) {
            wList.forEach(s -> {
                whiteList.add(Material.getMaterial(s));
            });
            return;
        }

        if (bList.size() > 0) {
            bList.forEach(s -> {
                blackList.add(Material.getMaterial(s));
            });
        }
    }

    private void checkAndSetRecipe(ConfigurationSection section)
    {
        List<String> recipe = section.getStringList("Recipe.Shape");
        if (recipe.size() != 3) {
            configError("must have a recipe be a 3x3 square. &7( Currently " + recipe.size() + "x3&7)");
        }

        List<Character> letters = new ArrayList<>();
        for (String s : recipe) {
            if (s.length() != 3) {
                configError("must only have 3 characters per line. Currently: &e" + s);
            }

            for (int i = 0; i < 3; i++) {
                char c = s.charAt(i);
                if (!letters.contains(c)) letters.add(c);
            }
        }

        Set<String> mats = section.getConfigurationSection("Recipe.Materials").getKeys(false);
        letters.forEach(c -> {
            if (!mats.contains(c.toString())) {
                configError("must contain character in material list from recipe! Unknown character: &e" + c);
            }
        });

        this.recipeShapeList = recipe;
    }
    private boolean isCustomTexture(String s)
    {
        if (s == null || s.isEmpty()) return false;

        return s.equalsIgnoreCase("skull") || s.equalsIgnoreCase("player_head")
                || s.equalsIgnoreCase("custom") || s.equalsIgnoreCase("texture");
    }

    private void configError(String error)
    {
        ChatUtil.error("BackPack &e" + this.key + " &c" + error);
    }
}

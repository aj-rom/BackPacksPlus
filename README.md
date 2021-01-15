![](https://i.imgur.com/fyxcbka.png)

***

[![](https://img.shields.io/github/v/release/CoachLuck/BackPacksPlus?style=for-the-badge)](https://github.com/CoachLuck/BackPacksPlus "![](https://img.shields.io/github/v/release/CoachLuck/BackPacksPlus?style=for-the-badge)") [![](https://img.shields.io/spiget/downloads/82612?style=for-the-badge)](https://www.spigotmc.org/resources/a.82612/)  [![](https://img.shields.io/codacy/grade/c5cc669592e64634a1e070ac2ba6a200?style=for-the-badge)](http://app.codacy.com/manual/CoachLuck/BackPacksPlus/dashboard?bid=19650936 "![](https://img.shields.io/codacy/grade/c5cc669592e64634a1e070ac2ba6a200?style=for-the-badge)") ![](https://img.shields.io/github/issues/CoachLuck/BackPacksPlus?style=for-the-badge)

### Commands
| Command | Permission | Description |
|---|---|---|
|/bpp|backpacksplus.use|Allow the player to see their currently available backpacks.|
|/bpp give <BackPack> <Player> <Amount>|backpacksplus.give|Allow the player to give others backpacks.|
|/bpp reload|backpacksplus.reload|Reload the plugin and files.|
|/bpp help|backpacksplus.help|Show the help page.|

### Per BackPack Permission
- In order for a player to **use** a backpack they must have the permission as follows: `backpack.use.<BackPack>`
- In order for a player to **craft** a backpack they must have the permission as follows: `backpack.craft.<Backpack>`
- To give craft or usage permissions for all backpacks you can do: `backpack.<craft/use>.*`
- To give craft and usage of all backpacks you can do: `backpack.*`

More information on permissions can be found [here](https://github.com/CoachLuck/BackPacksPlus/wiki/Permissions).
### Custom Textures
In order to use custom textures you must first find a head skin from [MineCraftHeads.com](https://minecraft-heads.com/custom-heads/search?searchword=backpack) and scroll down and copy the Minecraft URL section.

Ensure that all of these under the desired backpack are set.

Next insert the desired textures link.
```Yaml
    # Can be PLAYER_HEAD, SKULL, or CUSTOM
    Material: ""CUSTOM"
    Texture: "http://textures.minecraft.net/texture/<YOURCUSTOMTEXTUREID>"
```

### Translations
A guide to using languages other than English can be found in the [Wiki](https://github.com/CoachLuck/BackPacksPlus/wiki/Translations).

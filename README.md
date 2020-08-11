# BackPacks+

### Commands
| Command | Permission | Description |
|---|---|---|
|/bpp|backpacksplus.use|Allow the player to see their currently available backpacks.|
|/bpp give <BackPack> <Player> <Amount>|backpacksplus.give|Allow the player to give others backpacks.|
|/bpp reload|backpacksplus.reload|Reload the plugin and files.|
|/bpp help|backpacksplus.help|Show the help page.|

### Per BackPack Permission
- In order for a player to **use** a backpack they must have the permission as follows: `backpack.use.<BackPack>`
- In order for a player to **craft** a backpack they must have the permission as definied in the config.

### Custom Textures
In order to use custom textures you must first find a head skin from [MineCraftHeads.com](https://minecraft-heads.com/custom-heads/search?searchword=backpack) and scroll down and copy the Minecraft URL section.

Ensure that all of these under the desired backpack are set to PLAYER_HEAD, and custom data is unset.

Next insert the desired textures link.
```
    Material: "PLAYER_HEAD"
    Texture: "http://textures.minecraft.net/texture/<YOURCUSTOMTEXTUREID>"
    CustomData:
```

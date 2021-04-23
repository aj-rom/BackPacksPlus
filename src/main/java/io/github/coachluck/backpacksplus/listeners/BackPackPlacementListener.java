package io.github.coachluck.backpacksplus.listeners;

import io.github.coachluck.backpacksplus.api.BackPackUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;


public class BackPackPlacementListener implements Listener {

    @EventHandler
    public void onArmorEquip(BlockPlaceEvent e) {
        if (e.isCancelled()) return;
        if (BackPackUtil.isBackPack(e.getItemInHand()))
            e.setCancelled(true);
    }
}

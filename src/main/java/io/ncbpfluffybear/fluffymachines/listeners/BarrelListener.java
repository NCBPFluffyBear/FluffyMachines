package io.ncbpfluffybear.fluffymachines.listeners;

import io.ncbpfluffybear.fluffymachines.items.Barrel;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

public class BarrelListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBarrelBurn(BlockBurnEvent e) {
        if (BlockStorage.check(e.getBlock()) instanceof Barrel) {
            e.setCancelled(true);
        }
    }
}

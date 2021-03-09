package io.ncbpfluffybear.fluffymachines.utils;

import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMOEvents implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onAbility(McMMOPlayerAbilityActivateEvent e) {
        Player p = e.getPlayer();
        SlimefunItem sfItem = SlimefunItem.getByItem(p.getInventory().getItemInMainHand());
        if (sfItem != null && sfItem.getId().equals(FluffyItems.PAXEL.getItemId())) {
            e.setCancelled(true);
        }
    }

}

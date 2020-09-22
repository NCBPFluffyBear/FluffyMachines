package me.ncbpfluffybear.fluffymachines.utils;

import me.ncbpfluffybear.fluffymachines.items.HelicopterHat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Events implements Listener {

    HelicopterHat helicopterHat = (HelicopterHat) FluffyItems.HELICOPTER_HAT.getItem();

    @EventHandler
    public void onHelicopterHatUse(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (e.isSneaking() && helicopterHat.isItem(p.getEquipment().getHelmet())) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 1000000, 10));
        } else {
            p.removePotionEffect(PotionEffectType.LEVITATION);
        }
    }

}

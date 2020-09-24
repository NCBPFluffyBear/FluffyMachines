package me.ncbpfluffybear.fluffymachines.utils;

import me.ncbpfluffybear.fluffymachines.items.HelicopterHat;
import me.ncbpfluffybear.fluffymachines.items.WateringCan;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Events implements Listener {

    HelicopterHat helicopterHat = (HelicopterHat) FluffyItems.HELICOPTER_HAT.getItem();
    WateringCan wateringCan = (WateringCan) FluffyItems.WATERING_CAN.getItem();

    @EventHandler
    public void onHelicopterHatUse(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (e.isSneaking() && helicopterHat.isItem(p.getEquipment().getHelmet())) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 1000000, 10));
        } else {
            p.removePotionEffect(PotionEffectType.LEVITATION);
        }
    }

    @EventHandler
    public void onWateringCanSplash(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        // For some reason player interact events trigger twice, probably after a method returns false
        if (wateringCan.isItem(item)) {
            e.setCancelled(true);
            Entity target = e.getRightClicked();
            if (target instanceof Player && WateringCan.updateUses(p, item, 3)) {
                Utils.send(p, "&bSplash!");
                Utils.send((Player) target, "&bYou were splashed by " + p.getDisplayName() + "!");
                ((Player) target).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && ((Player) e.getEntity()).getEquipment() != null) {
            Player p = (Player) e.getEntity();
            ItemStack helmet = p.getEquipment().getHelmet();
            if (helmet != null && helicopterHat.isItem(helmet)
            ) {
                e.setCancelled(true);
            }
        }
    }

}

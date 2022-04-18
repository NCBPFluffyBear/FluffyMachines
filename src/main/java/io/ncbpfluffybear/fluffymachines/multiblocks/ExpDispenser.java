package io.ncbpfluffybear.fluffymachines.multiblocks;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Dispenses multiple bottles of exp at once
 */
public class ExpDispenser extends MultiBlockMachine {

    private static final int EXP_PER_BOTTLE = 7; // Average exp per bottle

    public ExpDispenser(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe) {
        super(itemGroup, item, recipe, BlockFace.SELF);
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block dispenser = b.getRelative(0, -1, 0);
        Container container = (Container) dispenser.getState();
        int experience = 0;

        for (ItemStack bottle : container.getInventory().getContents()) {
            if (bottle != null && bottle.getType() == Material.EXPERIENCE_BOTTLE) { // Search for xp bottles
                experience += EXP_PER_BOTTLE * bottle.getAmount(); // Collect experience from bottle
                bottle.setAmount(0); // Delete bottle
            }
        }

        Block barrel = dispenser.getRelative(((Directional) dispenser.getBlockData()).getFacing());
        SlimefunItem sfItem = BlockStorage.check(barrel);

        if (sfItem instanceof Barrel) {
            Barrel sfBarrel = (Barrel) sfItem;
            if (sfBarrel.getStoredItem(barrel).getType() == Material.EXPERIENCE_BOTTLE) {
                experience += sfBarrel.getStored(barrel) * EXP_PER_BOTTLE;
                sfBarrel.setStored(barrel, 0);
                sfBarrel.updateMenu(barrel, BlockStorage.getInventory(barrel), true, sfBarrel.getCapacity(b));
            }
        }

        if (experience == 0) {
            Utils.send(p, "&c發射器中沒有經驗瓶!");
        } else {
            p.giveExp(experience);
            Utils.send(p, "&a+" + experience + " XP");
        }
    }
}

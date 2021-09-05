//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.ncbpfluffybear.fluffymachines.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class NonHopperableBlock extends SlimefunItem {
    public NonHopperableBlock(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @EventHandler
    public void onHopper(InventoryMoveItemEvent e) {
        if (e.getSource().getType() == InventoryType.HOPPER && e.getDestination().getLocation() != null
                && BlockStorage.hasBlockInfo(e.getDestination().getLocation())
                && BlockStorage.check(e.getDestination().getLocation()) instanceof NonHopperableBlock
        ) {
            e.setCancelled(true);
        }

    }
}

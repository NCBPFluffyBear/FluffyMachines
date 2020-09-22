package me.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HelicopterHat extends SlimefunItem {

    public HelicopterHat() {
        super(FluffyItems.fluffymachines, FluffyItems.HELICOPTER_HAT, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
            null, new ItemStack(Material.LEATHER_HELMET), null,
            null, SlimefunItems.ADVANCED_CIRCUIT_BOARD, null
        });
    }
}

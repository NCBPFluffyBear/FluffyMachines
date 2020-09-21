package me.ncbpfluffybear.fluffymachines.utils;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.ncbpfluffybear.fluffymachines.FluffyMachines;
import me.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class FluffyItems {

    // Category
    public static final Category fluffymachines = new Category(new NamespacedKey(FluffyMachines.getInstance(),
        "fluffymachines"),
        new CustomItem(Material.SMOKER, "&6Fluffy Machines")
    );

    // Items
    public static final SlimefunItemStack AUTO_CRAFTING_TABLE = new SlimefunItemStack("AUTO_CRAFTING_TABLE",
        Material.CRAFTING_TABLE,
        "&6Auto Crafting Table",
        "",
        "&7Automatically crafts vanilla recipes",
        "",
        LoreBuilderDynamic.powerBuffer(AutoCraftingTable.CAPACITY),
        LoreBuilderDynamic.powerPerTick(AutoCraftingTable.ENERGY_CONSUMPTION)
    );

    private FluffyItems() {
    }
}

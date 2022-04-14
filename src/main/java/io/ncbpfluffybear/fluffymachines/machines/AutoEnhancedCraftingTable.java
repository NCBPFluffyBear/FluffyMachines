package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.ncbpfluffybear.fluffymachines.objects.AutoCrafter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AutoEnhancedCraftingTable extends AutoCrafter {
    public AutoEnhancedCraftingTable(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, String displayName, Material material, String machineName, RecipeType machineRecipes) {
        super(category, item, recipeType, recipe, "&6Auto Enhanced Crafting Table",
                Material.CRAFTING_TABLE, "&6Enhanced Crafting Table", RecipeType.ENHANCED_CRAFTING_TABLE
        );
    }
}

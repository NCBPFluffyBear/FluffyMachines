package io.ncbpfluffybear.fluffymachines.machines;

import io.ncbpfluffybear.fluffymachines.objects.AutoCrafter;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AutoArmorForge extends AutoCrafter {

    public AutoArmorForge(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe, "&7Auto Armor Forge", Material.ANVIL, "&7Armor Forge", RecipeType.ARMOR_FORGE);
    }
}

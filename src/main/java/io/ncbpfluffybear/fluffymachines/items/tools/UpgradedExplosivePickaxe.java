package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ExplosiveShovel;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link UpgradedExplosivePickaxe} is a pickaxe which can destroy {@link Block}s
 * in a size of 3 by 3. It also creates a explosion animation.
 *
 * @author TheBusyBiscuit, NCBPFluffyBear
 * @see ExplosiveShovel
 * @see UpgradedExplosiveTool
 */
public class UpgradedExplosivePickaxe extends UpgradedExplosiveTool {

    public UpgradedExplosivePickaxe(ItemGroup category, SlimefunItemStack item,
                                    RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

}

package me.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ExplosiveShovel;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
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

    public UpgradedExplosivePickaxe(Category category, SlimefunItemStack item,
                                    RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

}

package me.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ExplosivePickaxe;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.materials.MaterialTools;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link UpgradedExplosiveShovel} works similar to the
 * {@link io.github.thebusybiscuit.slimefun4.implementation.items.tools.ExplosivePickaxe}.
 * However it can only break blocks that a shovel can break.
 *
 * @author Linox, NCBPFluffyBear
 * @see ExplosivePickaxe
 * @see UpgradedExplosiveTool
 */
public class UpgradedExplosiveShovel extends UpgradedExplosiveTool {

    public UpgradedExplosiveShovel(Category category, SlimefunItemStack item, RecipeType recipeType,
                                   ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected boolean canBreak(Player p, Block b) {
        return MaterialTools.getBreakableByShovel().contains(b.getType())
            && SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.BREAK_BLOCK);
    }

}

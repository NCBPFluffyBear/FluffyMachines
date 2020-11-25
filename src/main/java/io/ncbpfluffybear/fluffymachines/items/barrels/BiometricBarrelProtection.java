package io.ncbpfluffybear.fluffymachines.items.barrels;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * The {@link BiometricBarrelProtection} locks any
 * {@link Barrel} when interacted with.
 *
 * @author NCBPFluffyBear
 */
public class BiometricBarrelProtection extends SimpleSlimefunItem<ItemUseHandler> {

    public BiometricBarrelProtection(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Optional<Block> opt = e.getClickedBlock();
            if (!opt.isPresent()) {
                return;
            }

            Block b = opt.get();
            Player p  = e.getPlayer();

            // We use BREAK_BLOCK here instead of ACCESS_INVENTORIES to ensure player has clearance
            if (!SlimefunPlugin.getProtectionManager().hasPermission(
                p, b.getLocation(), ProtectableAction.BREAK_BLOCK
            )) {
                return;
            }

            if (BlockStorage.hasBlockInfo(b) && BlockStorage.check(b) instanceof Barrel) {
                BlockStorage.addBlockInfo(b, "owner", String.valueOf(p.getUniqueId()));
                Utils.send(p, "&aYour barrel has been locked");
            }
        };
    }
}

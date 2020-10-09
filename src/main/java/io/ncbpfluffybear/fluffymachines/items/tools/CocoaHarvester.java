package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cocoa;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CocoaHarvester extends SimpleSlimefunItem<ItemUseHandler> {

    public CocoaHarvester(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
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

            if (b.getType() == Material.COCOA
                && ((Ageable) b.getBlockData()).getAge() == ((Ageable) b.getBlockData()).getMaximumAge()) {

                Player p = e.getPlayer();

                BlockFace face = ((Cocoa) b.getBlockData()).getFacing();
                b.breakNaturally(p.getInventory().getItemInMainHand());

                Inventory inv = p.getInventory();

                for (int i = 0; i < inv.getSize() - 1; i++) {
                    if (inv.getItem(i) != null
                        && inv.getItem(i).getType() == Material.COCOA_BEANS) {

                        int amount = inv.getItem(i).getAmount();
                        inv.setItem(i, new CustomItem(inv.getItem(i), amount - 1));
                        b.setType(Material.COCOA);
                        Cocoa blockData = ((Cocoa) b.getBlockData());
                        blockData.setFacing(face);
                        b.setBlockData(blockData);
                        break;
                    }
                }

            }
        };
    }
}

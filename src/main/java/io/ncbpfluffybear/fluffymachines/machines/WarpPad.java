package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.utils.holograms.SimpleHologram;
import io.ncbpfluffybear.fluffymachines.objects.NonHopperableBlock;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WarpPad extends NonHopperableBlock {


    public WarpPad(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(onPlace(), onUse(), onBreak());
    }

    private ItemHandler onPlace() {
        return new BlockPlaceHandler(true) {
            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlockPlaced(), "type", "none");
                SimpleHologram.update(e.getBlockPlaced(), "&4&lX");
            }
        };
    }

    private ItemHandler onUse() {
        return (BlockUseHandler) PlayerRightClickEvent::cancel;
    }

    private ItemHandler onBreak() {
        return new BlockBreakHandler() {
            @Override
            public boolean onBlockBreak(BlockBreakEvent e, ItemStack itemStack, int i, List<ItemStack> list) {
                SimpleHologram.remove(e.getBlock());
                return true;
            }
        };
    }
}

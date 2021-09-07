package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.Vein;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class Scythe extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    private static final int MAX_BROKEN = 5;

    public Scythe(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        super.preRegister();

        addItemHandler(onBlockBreak());
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> e.setUseBlock(Event.Result.DENY);
    }

    public ToolUseHandler onBlockBreak() {
        return (e, tool, fortune, drops) -> {

            if (e instanceof AlternateBreakEvent) {
                return;
            }

            if (e.getBlock().getBlockData() instanceof Ageable
                && ((Ageable) e.getBlock().getBlockData()).getAge()
                == ((Ageable) e.getBlock().getBlockData()).getMaximumAge()) {
                List<Block> crops = Vein.find(e.getBlock(), MAX_BROKEN, b -> Tag.CROPS.isTagged(b.getType()));

                crops.remove(e.getBlock());

                boolean creative = e.getPlayer().getGameMode() == GameMode.CREATIVE;

                for (Block b : crops) {
                    if (Slimefun.getProtectionManager().hasPermission(e.getPlayer(), b, Interaction.BREAK_BLOCK)) {
                        AlternateBreakEvent breakEvent = new AlternateBreakEvent(b, e.getPlayer());
                        Bukkit.getPluginManager().callEvent(breakEvent);
                        if (creative) {
                            b.setType(Material.AIR);
                        } else {
                            b.breakNaturally(tool);
                        }
                    }
                }
            }
        };
    }
}

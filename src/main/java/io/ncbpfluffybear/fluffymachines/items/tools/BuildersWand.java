package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuildersWand extends SimpleSlimefunItem<ItemUseHandler> {

    private static int MAX_PLACE_AMOUNT = 32;

    public BuildersWand(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Optional<Block> opt = e.getClickedBlock();
            if (opt.isPresent()) {
                Block b = opt.get();
                Material type = b.getType();
                Player p = e.getPlayer();

                Inventory inv = p.getInventory();
                List<Integer> itemSlots = new ArrayList<>();
                int stock = 0;

                for (int s = 0; s < inv.getSize() - 1; s++) {
                    if (inv.getItem(s).getType() == type) {
                        int amount = inv.getItem(s).getAmount();

                        itemSlots.add(s);
                        stock = stock + amount;

                        if (amount >= MAX_PLACE_AMOUNT) {
                            break;
                        }
                    }
                }
            }
        };
    }
}

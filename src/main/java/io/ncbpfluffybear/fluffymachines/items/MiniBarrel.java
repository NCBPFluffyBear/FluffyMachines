package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import javax.annotation.Nonnull;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class MiniBarrel extends Barrel {
    private static final int MAX_STORAGE = 172800;

    public MiniBarrel(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe, MAX_STORAGE);
        addItemHandler(onPlace());
    }

    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "max-size", String.valueOf(barrelCapacity.getValue()));
            }
        };
    }

    @Override
    protected void buildMenu(BlockMenu menu, Block b) {
        super.buildMenu(menu, b);
        menu.replaceExistingItem(13, new CustomItemStack(Material.YELLOW_STAINED_GLASS_PANE,
                "&e改變木桶大小", "&7> 點擊改變最大大小", "&e目前大小: " + getCapacity(b),
                "&e大小限制: " + barrelCapacity.getValue()
        ));
        menu.addMenuClickHandler(13, (p, slot, item, action) -> {
            p.closeInventory();
            Utils.send(p, "&e輸入木桶的新大小. 最大大小: " + barrelCapacity.getValue());
            ChatUtils.awaitInput(p, message -> {
                int renameSize = NumberUtils.getInt(message, 0);

                if (renameSize == 0 || renameSize > barrelCapacity.getValue()) {
                    Utils.send(p, "&c新的大小必須介於 0 到 " + barrelCapacity.getValue());
                    return;
                }

                if (renameSize < getStored(b)) {
                    Utils.send(p, "&c在將其改為此大小之前, 請先將木桶內的物品取出!");
                    return;
                }

                BlockStorage.addBlockInfo(b, "max-size", String.valueOf(renameSize));
                menu.replaceExistingItem(13, new CustomItemStack(Material.YELLOW_STAINED_GLASS_PANE,
                        "&e改變木桶大小", "&7> 點擊改變最大大小", "&e目前大小: " + renameSize,
                        "&e大小限制: " + barrelCapacity.getValue()
                ));
                Utils.send(p, "&a最大大小已改變為 " + renameSize);
                updateMenu(b, menu, true, renameSize);
            });
            return false;
        });
    }

    @Override
    public int getCapacity(Block b) {
        return Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "max-size"));
    }

    public static int getDisplayCapacity() {
        int capacity = Slimefun.getItemCfg().getInt("MINI_FLUFFY_BARREL.capacity");
        if (capacity == 0) {
            capacity = MAX_STORAGE;
        }

        return capacity;
    }
}

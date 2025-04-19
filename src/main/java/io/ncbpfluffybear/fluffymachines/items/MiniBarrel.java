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
        menu.replaceExistingItem(13, CustomItemStack.create(Material.YELLOW_STAINED_GLASS_PANE,
                "&eChange barrel size", "&7> Click to change max size", "&eCurrent size: " + getCapacity(b),
                "&eSize limit: " + barrelCapacity.getValue()
        ));
        menu.addMenuClickHandler(13, (p, slot, item, action) -> {
            p.closeInventory();
            Utils.send(p, "&eType the new size of the barrel. Max size: " + barrelCapacity.getValue());
            ChatUtils.awaitInput(p, message -> {
                String cleanMsg = message.replaceAll("[^0-9]", "");
                int renameSize = NumberUtils.getInt(cleanMsg, 0);

                if (renameSize == 0 || renameSize > barrelCapacity.getValue()) {
                    Utils.send(p, "&cThe new size must be between 0 and " + barrelCapacity.getValue());
                    return;
                }

                if (renameSize < getStored(b)) {
                    Utils.send(p, "&cRemove items from the barrel before changing it to this size!");
                    return;
                }

                BlockStorage.addBlockInfo(b, "max-size", String.valueOf(renameSize));
                menu.replaceExistingItem(13, CustomItemStack.create(Material.YELLOW_STAINED_GLASS_PANE,
                        "&eChange barrel size", "&7> Click to change max size", "&eCurrent size: " + renameSize,
                        "&eSize limit: " + barrelCapacity.getValue()
                ));
                Utils.send(p, "&aMax size has been changed to " + renameSize);
                updateMenu(b, menu, true, renameSize);
            });
            return false;
        });
    }

    @Override
    public int getCapacity(Block b) {
        String capacity = BlockStorage.getLocationInfo(b.getLocation(), "max-size");
        if (capacity == null) {
            BlockStorage.addBlockInfo(b, "max-size", String.valueOf(barrelCapacity.getValue()));
            return barrelCapacity.getValue();
        }
        return Integer.parseInt(capacity);
    }

    public static int getDisplayCapacity() {
        int capacity = Slimefun.getItemCfg().getInt("MINI_FLUFFY_BARREL.capacity");
        if (capacity == 0) {
            capacity = MAX_STORAGE;
        }

        return capacity;
    }
}

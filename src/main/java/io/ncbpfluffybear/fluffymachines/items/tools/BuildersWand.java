package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class BuildersWand extends SimpleSlimefunItem<ItemUseHandler> {

    private static int MAX_PLACE_AMOUNT = 32;
    private static int BUILD_SIZE = 10;
    private static int BUILD_RANGE = 10;

    public BuildersWand(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {

        };
    }

    private int getItemCount(Player p, Block block, ItemStack mainHand) {
        int count = 0;
        Inventory inventory = p.getInventory();
        Material blockMaterial = block.getType();
        ItemStack[] inventoryContents = inventory.getContents();

        if (mainHand.getType() == Material.AIR) {
            return 0;
        }

        ItemStack[] itemStacks = p.getInventory().getContents();

        if (p.getGameMode() == GameMode.CREATIVE) {
            return Integer.MAX_VALUE;
        }

        for (ItemStack itemStack : itemStacks) {
            if (itemStack == null) {
                continue;
            }
            Material itemMaterial = itemStack.getType();

            if (!itemMaterial.equals(blockMaterial) || block.getData() != itemStack.getData().getData()) {
                continue;
            }

            count += itemStack.getAmount();
        }

        return count;
    }

}

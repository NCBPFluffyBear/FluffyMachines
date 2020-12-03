package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

/**
 * A locked chest that functions similarly to
 * ColoredEnderChests
 */

public class LinkedChest extends SlimefunItem {

    int[] slots = IntStream.range(0, 54).toArray();

    public LinkedChest(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);


        addItemHandler(onInteract());
    }

    private ItemHandler onInteract() {
        return (BlockUseHandler) e -> {
            Block b = e.getClickedBlock().get();
            Player p = e.getPlayer();
            if (BlockStorage.getLocationInfo(b.getLocation(), "id").equals("LINKED_CHEST")) {
                Utils.send(p, "&2Please enter an ID for this linked chest");
                ChatUtils.awaitInput(p, msg -> {
                    BlockStorage.addBlockInfo(b.getLocation(), "id", msg);
                    buildMenu(msg);
                });
            }
        };
    }

    private void buildMenu(String id) {
        new BlockMenuPreset(id, "&c&lLinked Chest", true) {

            @Override
            public void init() {
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return (p.hasPermission("slimefun.inventory.bypass")
                    || SlimefunPlugin.getProtectionManager().hasPermission(
                    p, b.getLocation(), ProtectableAction.ACCESS_INVENTORIES));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                return slots;
            }
        };
    }
}
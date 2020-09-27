package me.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.ncbpfluffybear.fluffymachines.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

/**
 *
 * A locked chest that functions similarly to
 * ColoredEnderChests
 *
 */

public class LinkedChest extends SlimefunItem {

    int[] slots = IntStream.range(0, 54).toArray();

    public LinkedChest(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(onPlace());
        addItemHandler(onInteract());
    }

    private ItemHandler onPlace() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                Block b = e.getBlock();
                BlockStorage.addBlockInfo(b, "owner", e.getPlayer().getUniqueId().toString());
                BlockStorage.addBlockInfo(b, "identifier", " ");
            }
        };
    }

    private ItemHandler onInteract() {
        return (BlockUseHandler) e -> {

            e.cancel();
            Block b = e.getClickedBlock().get();
            Player p = e.getPlayer();
            if (BlockStorage.getLocationInfo(b.getLocation(), "identifier").equals(" ")) {
                Utils.send(p, "&ePlease enter an ID for your chest. " +
                    "Other linked chests with the same ID will be linked to this one");
                ChatUtils.awaitInput(p, message -> {
                    BlockStorage.addBlockInfo(b, "identifier", message);

                });
            } else {
                String identifier = BlockStorage.getLocationInfo(b.getLocation(), "identifier");

                BlockMenuPreset inv = new BlockMenuPreset(p.getUniqueId().toString() + identifier, "&eLinked Chest " + identifier, true) {

                    @Override
                    public void init() {
                        setSize(54);

                        addMenuOpeningHandler(p -> p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.8F, 1.6F));

                        addMenuCloseHandler(p -> p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.8F, 1.6F));
                    }

                    @Override
                    public int[] getSlotsAccessedByItemTransport(ItemTransportFlow arg0) {
                        return slots;
                    }

                    @Override
                    public boolean canOpen(Block b, Player p) {
                        String data = BlockStorage.getLocationInfo(b.getLocation(), "owner");

                        return data.equals(p.getUniqueId().toString());
                    }
                };

                inv.open(p);
            }
        };
    }

    private void buildMenu(Player p, String id) {

    }
}
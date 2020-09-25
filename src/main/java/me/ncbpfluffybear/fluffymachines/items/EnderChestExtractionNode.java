package me.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import me.ncbpfluffybear.fluffymachines.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 *
 * This {@link SlimefunItem} transfers items from the facing
 * {@link EnderChest} to the {@link Container} behind it
 *
 * @author NCBPFluffyBear
 *
 */
public class EnderChestExtractionNode extends SlimefunItem {

    public EnderChestExtractionNode(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(onPlace());
        addItemHandler(onInteract());
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) {
                EnderChestExtractionNode.this.tick(b);
            }

            public boolean isSynchronized() {
                return true;
            }
        });
    }

    private void tick(@Nonnull Block b) {
        ItemStack transferItemStack;
        boolean valid;
        BlockFace face;

        if (b.getRelative(BlockFace.NORTH).getType() == Material.ENDER_CHEST) {
            valid = true;
            face = BlockFace.SOUTH;

        } else if (b.getRelative(BlockFace.SOUTH).getType() == Material.ENDER_CHEST) {
            valid = true;
            face = BlockFace.NORTH;


        } else if (b.getRelative(BlockFace.EAST).getType() == Material.ENDER_CHEST) {
            valid = true;
            face = BlockFace.WEST;


        } else if (b.getRelative(BlockFace.WEST).getType() == Material.ENDER_CHEST) {
            valid = true;
            face = BlockFace.EAST;

        } else {
            return;
        }

        BlockState state = PaperLib.getBlockState(b.getRelative(face), false).getState();

        if (valid && state instanceof InventoryHolder) {
            Player p = Bukkit.getPlayer(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "owner")));

            // Ender chest null check necessary because Bukkit yes.
            if (p != null && p.getEnderChest() != null) {

                boolean enderValid = false;
                boolean containerValid = false;
                int enderIndex = -1;
                int containerIndex = -1;

                Inventory enderInv = p.getEnderChest();

                for (int i = 0 ; i < enderInv.getSize() ; i++) {

                    if (enderInv.getItem(i) != null) {
                        enderIndex = i;
                        enderValid = true;
                        break;
                    }
                }

                Inventory containerInv = ((InventoryHolder) state).getInventory();

                for (int i = 0 ; i < containerInv.getSize() ; i++) {

                    if (containerInv.getItem(i) == null) {
                        containerIndex = i;
                        containerValid = true;
                        break;
                    }
                }

                if (enderValid && containerValid) {
                    transferItemStack = enderInv.getItem(enderIndex);
                    enderInv.setItem(enderIndex, null);

                    containerInv.setItem(containerIndex, transferItemStack);
                }
            }
        }
    }

    private ItemHandler onPlace() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                Player p = e.getPlayer();
                Block b = e.getBlock();

                if (e.getBlockAgainst().getType() != Material.ENDER_CHEST) {
                    e.setCancelled(true);
                    BlockStorage.clearBlockInfo(e.getBlockPlaced());
                    Utils.send(p, "&cYou can only place this on an Ender Chest!");
                } else {

                    BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
                    BlockStorage.addBlockInfo(b, "playername", p.getDisplayName());
                    Utils.send(p, "&aEnder Chest Extraction Node registered to " + p.getDisplayName()
                        + " &7(UUID: " + p.getUniqueId().toString() + ")");
                }
            }
        };
    }

    private ItemHandler onInteract() {
        return (BlockUseHandler) e -> {
            Player p = e.getPlayer();
            Block b = e.getClickedBlock().get();
            Utils.send(p, "&eThis Ender Chest Extraction Node belongs to " +
                BlockStorage.getLocationInfo(b.getLocation(), "playername")
                + " &7(UUID: " + BlockStorage.getLocationInfo(b.getLocation(), "owner") + ")");
        };
    }
}

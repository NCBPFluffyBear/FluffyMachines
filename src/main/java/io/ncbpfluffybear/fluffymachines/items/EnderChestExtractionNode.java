package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import io.ncbpfluffybear.fluffymachines.objects.EnderChestNode;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * This {@link SlimefunItem} transfers items from the facing
 * {@link EnderChest} to the {@link Container} behind it
 *
 * @author NCBPFluffyBear
 */
public class EnderChestExtractionNode extends EnderChestNode {

    public EnderChestExtractionNode(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe, Type.EXTRACTION);
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

        // Make sure this node is still attached to an Ender Chest
        BlockFace face = checkEChest(b);
        if (face == null) {
            return;
        }

        BlockState state = PaperLib.getBlockState(b.getRelative(face), false).getState();

        if (state instanceof InventoryHolder) {
            Player p = Bukkit.getOfflinePlayer(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "owner"))).getPlayer();

            // Ender chest null check necessary because Bukkit yes.
            if (p != null) {

                boolean enderValid = false;
                boolean containerValid = false;
                int enderIndex = -1;
                int containerIndex = -1;

                Inventory enderInv = p.getEnderChest();

                for (int i = 0; i < enderInv.getSize(); i++) {

                    ItemStack enderItem = enderInv.getItem(i);

                    if (enderItem != null && state instanceof ShulkerBox && !Tag.SHULKER_BOXES.isTagged(enderItem.getType())) {
                        continue;
                    }

                    if (enderItem != null) {
                        enderIndex = i;
                        enderValid = true;
                        break;
                    }
                }

                Inventory containerInv = ((InventoryHolder) state).getInventory();

                for (int i = 0; i < containerInv.getSize(); i++) {

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
}
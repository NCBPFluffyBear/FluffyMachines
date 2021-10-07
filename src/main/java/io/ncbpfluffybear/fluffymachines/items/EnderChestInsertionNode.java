package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import io.ncbpfluffybear.fluffymachines.objects.EnderChestNode;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * This {@link SlimefunItem} transfers items to the facing
 * {@link EnderChest} from the {@link Container} behind it
 *
 * @author NCBPFluffyBear
 */
public class EnderChestInsertionNode extends EnderChestNode {

    public EnderChestInsertionNode(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe, EnderChestNode.Type.INSERTION);
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) {
                EnderChestInsertionNode.this.tick(b);
            }

            public boolean isSynchronized() {
                return true;
            }
        });
    }

    private void tick(@Nonnull Block b) {
        ItemStack transferItemStack;
        BlockFace face = checkEChest(b);

        // Node no longer facing an Ender Chest
        if (face == null) {
            return;
        }

        BlockState state = PaperLib.getBlockState(b.getRelative(face), false).getState();

        if (b.getRelative(face).getState() instanceof InventoryHolder) {
            Player p = Bukkit.getOfflinePlayer(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "owner"))).getPlayer();

            // Ender chest null check necessary because Bukkit yes.
            if (p != null) {

                boolean enderValid = false;
                boolean containerValid = false;
                int enderIndex = -1;
                int containerIndex = -1;

                Inventory containerInv = ((InventoryHolder) state).getInventory();

                for (int i = 0; i < containerInv.getSize(); i++) {

                    if (containerInv.getItem(i) != null) {
                        containerIndex = i;
                        containerValid = true;
                        break;
                    }
                }

                Inventory enderInv = p.getEnderChest();

                for (int i = 0; i < enderInv.getSize(); i++) {

                    if (enderInv.getItem(i) == null) {
                        enderIndex = i;
                        enderValid = true;
                        break;
                    }
                }

                if (enderValid && containerValid) {
                    transferItemStack = containerInv.getItem(containerIndex);
                    containerInv.setItem(containerIndex, null);

                    enderInv.setItem(enderIndex, transferItemStack);
                }
            }
        }
    }


}

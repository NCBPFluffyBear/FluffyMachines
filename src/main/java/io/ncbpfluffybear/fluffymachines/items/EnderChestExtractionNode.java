package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.talismans.Talisman;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.objects.EnderChestNode;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
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

    public EnderChestExtractionNode(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(onPlace());
        addItemHandler(onInteract());
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

        // Make sure this node is still attached to an Ender Chest
        BlockFace face = checkEChest(b);
        if (face == null) {
            return;
        }

        BlockState state = PaperLib.getBlockState(b.getRelative(face), false).getState();

        if (!(state instanceof InventoryHolder)) {
            return;
        }

        Player p = Bukkit.getOfflinePlayer(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "owner"))).getPlayer();
        String filterType = BlockStorage.getLocationInfo(b.getLocation(), "filter-type");
        String filterTalismans = BlockStorage.getLocationInfo(b.getLocation(), "filter-talismans");
        ItemStack transferItemStack = null;

        // Ender chest null check necessary because Bukkit yes.
        if (p != null) {

            Inventory enderInv = p.getEnderChest();
            int enderIndex = -1;
            boolean isAcceptable = false;

            for (int i = 0; i < enderInv.getSize(); i++) {

                transferItemStack = enderInv.getItem(i);

                if (transferItemStack == null) {
                    continue;
                }

                if (state instanceof ShulkerBox && Tag.SHULKER_BOXES.isTagged(transferItemStack.getType())) {
                    continue;
                }

                /*
                if (filterType.equals("blacklist")) {
                    boolean isBlacklisted = false;
                    for (ItemStack item : getFilterContents(b)) {
                        if (SlimefunUtils.isItemSimilar(item, transferItemStack, true, false)) {
                            Bukkit.broadcastMessage("Blocked " + transferItemStack.getType());
                            break;
                        }
                        Bukkit.broadcastMessage("Acceptable");

                        isAcceptable = true;
                    }
                } else { // Whitelist
                    for (ItemStack item : getFilterContents(b)) {
                        if (SlimefunUtils.isItemSimilar(item, transferItemStack, true, false)) {
                            isAcceptable = true;
                            break;
                        }
                    }
                }

                 */

                if (filterTalismans.equals("true")
                    && SlimefunItem.getByItem(transferItemStack) instanceof Talisman) {
                    continue;
                }

                enderIndex = i;
                break;
            }

            if (!isAcceptable) {
                return;
            }

            Inventory containerInv = ((InventoryHolder) state).getInventory();
            ItemStack remaining = EnderChestNode.insertIntoVanillaInventory(transferItemStack, containerInv);
            enderInv.setItem(enderIndex, remaining);
        }
    }
}
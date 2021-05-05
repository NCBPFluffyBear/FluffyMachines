package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.data.PersistentDataAPI;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class CargoWand extends SimpleSlimefunItem<ItemUseHandler> implements Listener {

    private static final NamespacedKey CARGO_TYPE = new NamespacedKey(FluffyMachines.getInstance(), "cargo_type");
    private static final NamespacedKey CARGO_DATA = new NamespacedKey(FluffyMachines.getInstance(), "cargo_data");
    private static final NamespacedKey CARGO_LOCATION = new NamespacedKey(FluffyMachines.getInstance(), "cargo_location");

    private static final int[] CARGO_SLOTS = { 19, 20, 21, 28, 29, 30, 37, 38, 39 };

    public CargoWand(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        Bukkit.getPluginManager().registerEvents(this, FluffyMachines.getInstance());
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> e.setUseBlock(Event.Result.DENY);
    }

    @EventHandler
    private void onCargoWandUse(PlayerInteractEvent e) {

        ItemStack item = e.getItem();

        // Check item is cargo wand
        if (item == null || !this.isItem(item)) {
            return;
        }

        Action act = e.getAction();
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        if (b == null) {
            return;
        }

        // Left click actions
        if (act == Action.LEFT_CLICK_BLOCK || act == Action.LEFT_CLICK_AIR) {

            // Player is sneaking; Clear the node
            if (p.isSneaking()) {
                ItemMeta DEFAULT_WAND_META = FluffyItems.CARGO_WAND.getItem().getItem().getItemMeta();
                item.setItemMeta(DEFAULT_WAND_META);
                Utils.send(p, "&eYour Cargo Wand has been reset");

            // Player is not sneaking; Paste wand contents
            } else if (act == Action.LEFT_CLICK_BLOCK) {
                String currentCargoType = getCargoNodeType(b);

                // Check if clicked block is a cargo node
                if (currentCargoType == null) {
                    return;
                }

                e.setCancelled(true);

                PersistentDataHolder holder = item.getItemMeta();
                String savedCargoType = PersistentDataAPI.getString(holder, CARGO_TYPE);

                // The contents have not been copied yet
                if (savedCargoType == null) {
                    Utils.send(p, "&cYou must copy a cargo node first!");
                    return;
                }

                // The cargo type on the wand does not match clicked block
                if (!savedCargoType.equals(currentCargoType)) {
                    Utils.send(p, "&eYou can only paste to a " + SlimefunItem.getByID(savedCargoType).getItemName() + "!");
                    return;
                }

                String cargoData = PersistentDataAPI.getString(holder, CARGO_DATA);

                BlockStorage.setBlockInfo(b, cargoData, true);

                BlockStorage bs = BlockStorage.getStorage(b.getWorld());
                if (bs == null) {
                    return;
                }

                bs.reloadInventory(b.getLocation());
                Utils.send(p, "&aCargo settings have been pasted.");

                BlockMenu savedMenu = BlockStorage.getInventory(parseLocation(PersistentDataAPI.getString(holder, CARGO_LOCATION)));
                if (savedMenu == null) {
                    Utils.send(p, "&cInventory copying failed, could not access the copied block.");
                    return;
                }

                BlockMenu currentMenu = BlockStorage.getInventory(b);
                Inventory inv = p.getInventory();

                for (int slot : CARGO_SLOTS) {
                    ItemStack savedItem = savedMenu.getItemInSlot(slot);
                    ItemStack currentItem = currentMenu.getItemInSlot(slot);

                    // Skip slot if the item is already in there
                    if ((currentItem == null && savedItem == null)
                        || (currentItem != null && SlimefunUtils.isItemSimilar(savedItem, currentItem, true))) {
                        continue;
                    }

                    // Dump out items from the new inventory slot
                    if (currentItem != null) {
                        currentMenu.replaceExistingItem(slot, null);
                        HashMap<Integer, ItemStack> leftOvers = inv.addItem(currentItem);
                        for (ItemStack leftOver : leftOvers.values()) {
                            p.getWorld().dropItem(p.getLocation(), leftOver);
                        }
                    }

                    // If savedItem exists, we do not need to bother with checking for a replacement.
                    if (savedItem != null) {
                        // Clone and set amount to 1
                        savedItem = new CustomItem(savedItem, 1);
                        if (SlimefunUtils.containsSimilarItem(inv, savedItem, true)) {
                            inv.removeItem(savedItem);
                            currentMenu.replaceExistingItem(slot, savedItem);
                        }
                    }
                }
            }

        // Copy cargo contents into wand
        } else if (act == Action.RIGHT_CLICK_BLOCK && getCargoNodeType(b) != null) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataAPI.setString(meta, CARGO_DATA, BlockStorage.getBlockInfoAsJson(b));
            PersistentDataAPI.setString(meta, CARGO_TYPE, BlockStorage.checkID(b));
            PersistentDataAPI.setString(meta, CARGO_LOCATION, serializeLocation(b.getLocation()));

            item.setItemMeta(meta);
            e.setCancelled(true);

            Utils.send(p, "The settings of this Cargo Node have been saved to your Cargo Wand");

        }

    }

    private String serializeLocation(Location l) {
        return l.getWorld().getUID() + "_" + l.getBlockX() + "_" + l.getBlockY() + "_" + l.getBlockZ();
    }

    private Location parseLocation(String s) {
        String[] stringLoc = s.split("_");
        World world = Bukkit.getWorld(UUID.fromString(stringLoc[0]));
        int x = Integer.parseInt(stringLoc[1]);
        int y = Integer.parseInt(stringLoc[2]);
        int z = Integer.parseInt(stringLoc[3]);
        return new Location(world, x, y, z);
    }

    // Check if the block is a Slimefun block then if it is a cargo node
    private String getCargoNodeType(Block b) {
        SlimefunItem cargoBlock = BlockStorage.check(b);

        if (cargoBlock == null) {
            return null;
        }

        String blockId = cargoBlock.getId();

        if (blockId.equals(SlimefunItems.CARGO_INPUT_NODE.getItemId())
            || blockId.equals(SlimefunItems.CARGO_OUTPUT_NODE.getItemId())
            || blockId.equals(SlimefunItems.CARGO_OUTPUT_NODE_2.getItemId())
        ) {
            return blockId;
        }

        return null;
    }
}

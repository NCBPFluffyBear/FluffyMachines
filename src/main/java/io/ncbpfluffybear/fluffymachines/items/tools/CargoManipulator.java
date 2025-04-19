package io.ncbpfluffybear.fluffymachines.items.tools;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * used to quickly manipulate cargo nodes
 *
 * @author NCBPFluffyBear
 */
public class CargoManipulator extends SimpleSlimefunItem<ItemUseHandler> implements Listener {

    private static final int[] CARGO_SLOTS = {19, 20, 21, 28, 29, 30, 37, 38, 39};
    private final Map<Player, Pair<JsonObject, ItemStack[]>> storedFilters = new HashMap<>();

    public CargoManipulator(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        Bukkit.getPluginManager().registerEvents(this, FluffyMachines.getInstance());
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> e.setUseBlock(Event.Result.DENY); // Prevent opening inventories
    }

    @EventHandler
    private void onCargoManipulatorUse(PlayerInteractEvent e) {

        ItemStack manipulator = e.getItem();

        // Check item is cargo manipulator
        if (manipulator == null || !this.isItem(manipulator)) {
            return;
        }

        e.setCancelled(true);

        Action act = e.getAction();
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        // Check if targeted block is cargo node
        SlimefunItem nodeType = getCargoNodeType(b);
        if (nodeType == null || (
                nodeType.getItem() != SlimefunItems.CARGO_OUTPUT_NODE.item() &&
                        nodeType.getItem() != SlimefunItems.CARGO_OUTPUT_NODE_2.item() &&
                        nodeType.getItem() != SlimefunItems.CARGO_INPUT_NODE.item()
        )) {
            return;
        }

        if (!Slimefun.getProtectionManager().hasPermission(e.getPlayer(), b.getLocation(), Interaction.INTERACT_BLOCK)) {
            return;
        }

        if (act == Action.RIGHT_CLICK_BLOCK) {
            if (p.isSneaking()) {
                clearNode(b, p, getCargoNodeType(b));
            } else {
                copyNode(b, p, getCargoNodeType(b));
            }
        } else {
            pasteNode(b, p, getCargoNodeType(b));
        }
    }

    /**
     * Copy's a node's data into the manipulator. Cargo inventories stored in map.
     * Action: Right Click Block
     */
    private void copyNode(Block parent, Player p, SlimefunItem nodeType) {
        // Copy BlockStorage data
        JsonObject nodeData = (JsonObject) new JsonParser().parse(BlockStorage.getBlockInfoAsJson(parent));

        ItemStack[] filterItems = new ItemStack[9];
        if (nodeType.getItem() != SlimefunItems.CARGO_OUTPUT_NODE.item()) { // No inventory
            // Copy inventory into map
            BlockMenu parentInventory = BlockStorage.getInventory(parent);
            for (int i = 0; i < 9; i++) { // Iterate through all slots in cargo filter
                ItemStack menuItem = parentInventory.getItemInSlot(CARGO_SLOTS[i]);
                if (menuItem != null) {
                    filterItems[i] = CustomItemStack.create(menuItem, 1);
                } else {
                    filterItems[i] = null;
                }
            }
        }

        storedFilters.put(p, new Pair<>(nodeData, filterItems)); // Save cargo slots into map

        Utils.send(p, "&aYour " + SlimefunItem.getById(nodeData.get("id").getAsString()).getItemName() + " &ahas been copied.");
        createParticle(parent, Color.fromRGB(255, 252, 51)); // Bright Yellow
    }

    /**
     * Pastes stored node contents
     * Action: Left Click
     */
    private void pasteNode(Block child, Player p, SlimefunItem nodeType) {
        Pair<JsonObject, ItemStack[]> nodeSettings = storedFilters.getOrDefault(p, null);

        // No data saved yet
        if (nodeSettings == null) {
            Utils.send(p, "&cYou have not copied a cargo node yet.");
            return;
        }

        // Get saved data
        JsonObject jsonData = nodeSettings.getFirstValue();

        SlimefunItem savedNodeType = SlimefunItem.getById(jsonData.get("id").getAsString());
        if (savedNodeType != nodeType) {
            Utils.send(p, "&cYou copied a " + savedNodeType.getItemName() +
                    " &cbut you are trying to modify a " + nodeType.getItemName() + "&c!");
            createParticle(child, Color.RED);
            return;
        }

        // Set the data
        BlockStorage.setBlockInfo(child, jsonData.toString(), false);

        if (nodeType.getItem() != SlimefunItems.CARGO_OUTPUT_NODE.item()) {
            // Set the filter
            BlockMenu nodeMenu = BlockStorage.getInventory(child);
            ItemStack[] filterItems = nodeSettings.getSecondValue();
            Inventory playerInventory = p.getInventory();

            for (int i = 0; i < 9; i++) {

                // Check if item already exists in slot
                if (SlimefunUtils.isItemSimilar(filterItems[i], nodeMenu.getItemInSlot(CARGO_SLOTS[i]), true, false)) {
                    continue;
                }

                // Drop item in filter slot
                clearFilterSlot(nodeMenu, CARGO_SLOTS[i], p);

                // No need to insert new items in
                if (filterItems[i] == null) {
                    continue;
                }

                // Check if item not in inventory
                if (!SlimefunUtils.containsSimilarItem(playerInventory, filterItems[i], true)) {
                    createParticle(child, Color.AQUA);
                    Utils.send(p, "&cYou do not have " + Utils.getViewableName(filterItems[i]) + "&c. Skipping this item.");
                    continue;
                }

                // Consume item in player inventory
                for (ItemStack playerItem : playerInventory) {
                    if (SlimefunUtils.isItemSimilar(playerItem, filterItems[i], false, false)) {
                        playerItem.setAmount(playerItem.getAmount() - 1);

                        // Insert item into node menu
                        nodeMenu.replaceExistingItem(CARGO_SLOTS[i], CustomItemStack.create(playerItem, 1));
                        break;
                    }
                }
            }
        }

        // Force menu update
        BlockStorage.getStorage(child.getWorld()).reloadInventory(child.getLocation());
        Utils.send(p, "&aYour " + savedNodeType.getItemName() + " &ahas been pasted.");
        createParticle(child, Color.LIME);

    }

    /**
     * Clears the data of a targeted node
     * Action: Sneak + Right Click Block
     */
    private void clearNode(Block node, Player p, SlimefunItem nodeType) {
        // Clear node settings
        BlockStorage.addBlockInfo(node, "owner", p.getUniqueId().toString());
        BlockStorage.addBlockInfo(node, "frequency", "0");

        // These settings are only for Input and Advanced Output nodes
        if (nodeType.getItem() != SlimefunItems.CARGO_OUTPUT_NODE.item()) {
            // AbstractFilterNode settings
            BlockStorage.addBlockInfo(node, "index", "0");
            BlockStorage.addBlockInfo(node, "filter-type", "whitelist");
            BlockStorage.addBlockInfo(node, "filter-lore", String.valueOf(true));
            BlockStorage.addBlockInfo(node, "filter-durability", String.valueOf(false));

            if (nodeType.getItem() == SlimefunItems.CARGO_INPUT_NODE.item()) {
                // CargoInputNode settings
                BlockStorage.addBlockInfo(node, "round-robin", String.valueOf(false));
                BlockStorage.addBlockInfo(node, "smart-fill", String.valueOf(false));
            }

            clearNodeFilter(node, p);

            // Force update
            BlockStorage.getStorage(node.getWorld()).reloadInventory(node.getLocation());

            Utils.send(p, "&aThe selected Cargo Node has been cleared");
            createParticle(node, Color.fromRGB(255, 152, 56)); // Light orange
        }
    }

    private void clearNodeFilter(Block node, Player p) {
        // Empty filter contents
        BlockMenu nodeMenu = BlockStorage.getInventory(node);
        for (int i = 0; i < 9; i++) {
            clearFilterSlot(nodeMenu, CARGO_SLOTS[i], p);
        }
    }

    private void clearFilterSlot(BlockMenu nodeMenu, int slot, Player p) {
        ItemStack filterItem = nodeMenu.getItemInSlot(slot);
        if (filterItem != null) {
            Utils.giveOrDropItem(p, filterItem); // Give player item in filter
            nodeMenu.replaceExistingItem(slot, null); // Clear item in filter
        }
    }

    /**
     * Get the SlimefunItemStack of the cargo node
     */
    private SlimefunItem getCargoNodeType(Block b) {
        if (b == null) {
            return null;
        }

        String blockId = BlockStorage.checkID(b);

        if (blockId == null) {
            return null;
        }

        return SlimefunItem.getById(blockId);
    }

    private void createParticle(Block b, Color color) {
        Particle.DustOptions dustOption = new Particle.DustOptions(color, 1);
        b.getLocation().getWorld().spawnParticle(Particle.DUST, b.getLocation().add(0.5, 0.5, 0.5), 1, dustOption);
    }
}

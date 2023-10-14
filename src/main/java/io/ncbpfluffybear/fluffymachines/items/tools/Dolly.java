package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dolly extends SimpleSlimefunItem<ItemUseHandler> {

    // Because I didn't think things through properly beforehand,
    // we are stuck with a pretty nasty implementation.
    //
    // The "LOCK_ITEM" is just a custom non-interactable item. No unique tags for dolly type identification.
    // If the LOCK_ITEM is in slot 0, the dolly is EMPTY.
    // If the LOCK_ITEM is in slot 27, the dolly is FULL but has SINGLE CHEST.
    // If the LOCK_ITEM is in slot 1, the dolly is FULL and is carrying a BARREL. The LOCK_ITEM will be tagged with the item amount.
    // Otherwise, the dolly is FULL with a DOUBLE CHEST.
    private static final ItemStack LOCK_ITEM = Utils.buildNonInteractable(
            Material.DIRT, "&4&lDolly empty", "&cHow did you get in here?"
    );

    private static final NamespacedKey STORED_KEY = new NamespacedKey(FluffyMachines.getInstance(), "barrel-stored-items");

    private static final int DELAY = 500; // 500ms delay required between uses
    private static final Map<Player, Long> timeouts = new HashMap<>();

    public Dolly(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Player p = e.getPlayer();

            if (timeouts.containsKey(p) && timeouts.get(p) + DELAY > System.currentTimeMillis()) { // Prevent players from spamming dolly (Potential dupe)
                Utils.send(p, "&cPlease wait before using the dolly again!");
                return;
            }

            timeouts.put(p, System.currentTimeMillis());

            ItemStack dolly = e.getItem();

            if (!e.getClickedBlock().isPresent()) {
                return;
            }

            Block b = e.getClickedBlock().get();

            if (!Utils.canModifyBlock(b.getLocation(), e.getPlayer())) {
                Utils.send(p, "&cYou do not have permission to place or break here.");
                return;
            }

            if (BlockStorage.check(b) instanceof Barrel) { // Player right-clicked a barrel
                // Create dolly if not already one
                buildDolly(dolly, p);

                pickupBarrel(dolly, b, p);

            } else if (b.getType() == Material.CHEST) { // Player right-clicked a chest
                // Create dolly if not already one
                buildDolly(dolly, p);

                // Pick up the chest
                pickupChest(dolly, b, p);


            } else { // Player right-clicked the ground
                // Place new chest
                placeContents(dolly, b.getRelative(e.getClickedFace()), p);
            }

        };
    }

    /**
     * Registers a dolly under Slimefun's {@link PlayerBackpack} system
     */
    private void buildDolly(ItemStack dolly, Player p) {
        // Build backpack if new
        ItemMeta dollyMeta = dolly.getItemMeta();
        for (String line : dollyMeta.getLore()) {
            if (line.contains("ID: <ID>")) {
                PlayerProfile.get(p, profile -> {
                    int backpackId = profile.createBackpack(54).getId();
                    Slimefun.getBackpackListener().setBackpackId(p, dolly, 3, backpackId);
                    PlayerProfile.getBackpack(dolly, backpack -> backpack.getInventory().setItem(0, LOCK_ITEM));
                });
            }
        }
    }

    private void pickupBarrel(ItemStack dolly, Block barrel, Player p) {
        PlayerProfile.getBackpack(dolly, backpack -> {

            if (backpack == null) {
                return;
            }

            if (!isDollyEmpty(backpack, p)) {
                return;
            }

            Inventory bpInventory = backpack.getInventory();
            Barrel sfBarrel = (Barrel) BlockStorage.check(barrel);

            ItemStack taggedLockItem = LOCK_ITEM.clone(); // Modified lock item to store barrel item amount.
            ItemMeta taggedLockMeta = taggedLockItem.getItemMeta();
            taggedLockMeta.getPersistentDataContainer().set(STORED_KEY, PersistentDataType.INTEGER, sfBarrel.getStored(barrel));
            taggedLockItem.setItemMeta(taggedLockMeta);

            bpInventory.setItem(1, taggedLockItem);
            bpInventory.setItem(0, sfBarrel.getItem().clone());
        });
    }

    private void pickupChest(ItemStack dolly, Block chest, Player p) {
        Inventory chestInventory = ((InventoryHolder) chest.getState()).getInventory();
        AtomicBoolean validOperation = new AtomicBoolean(false); // Used to deal with async block replacement
        AtomicBoolean isDoubleChest = new AtomicBoolean(false);

        PlayerProfile.getBackpack(dolly, backpack -> {

            if (backpack == null) {
                return;
            }

            if (!isDollyEmpty(backpack, p)) {
                return;
            }

            backpack.getInventory().setStorageContents(chestInventory.getContents());

            // Add marker for single chests
            if (chestInventory.getSize() == 54) { // Double chest (Avoid instanceof because of weird chest class setup)
                isDoubleChest.set(true);
            } else {
                backpack.getInventory().setItem(27, LOCK_ITEM);
            }

            // Clear chest
            chestInventory.clear();
            PlayerProfile.getBackpack(dolly, PlayerBackpack::markDirty);
            validOperation.set(true);
            dolly.setType(Material.CHEST_MINECART);
        });

        // Deals with async problems
        if (validOperation.get()) {
            if (isDoubleChest.get()) {

                DoubleChest doubleChest = (DoubleChest) ((org.bukkit.block.Chest) chest.getState()).getInventory().getHolder();

                // Set other side of chest to air
                if (((org.bukkit.block.Chest) doubleChest.getLeftSide()).getLocation().equals(chest.getLocation())
                ) {
                    ((org.bukkit.block.Chest) doubleChest.getRightSide()).getLocation().getBlock().setType(Material.AIR);
                } else {
                    ((org.bukkit.block.Chest) doubleChest.getLeftSide()).getLocation().getBlock().setType(Material.AIR);
                }

            }

            chest.setType(Material.AIR);

            Utils.send(p, "&aYou have picked up this chest");
        }
    }

    private boolean isDollyEmpty(PlayerBackpack backpack, Player p) {
        // Dolly full/empty status determined by lock item in first slot
        // Make sure the dolly is empty
        if (!Utils.checkNonInteractable(backpack.getInventory().getItem(0))) {
            Utils.send(p, "&cThis dolly is already carrying a chest!");
            return false;
        }

        return true;
    }

    private void placeContents(ItemStack dolly, Block placeBlock, Player p) {
        PlayerProfile.getBackpack(dolly, backpack -> {
            if (backpack == null) {
                return;
            }

            Inventory bpInventory = backpack.getInventory();
            if (Utils.checkNonInteractable(bpInventory.getItem(1))) { // Holding a barrel
                placeBarrel(backpack, dolly, placeBlock, p);
            } else { // Holding a chest or nothing
                placeChest(backpack, dolly, placeBlock, p);
            }
        });
    }

    private void placeBarrel(PlayerBackpack backpack, ItemStack dolly, Block chestBlock, Player p) {
        Utils.runSync(new BukkitRunnable() {
            @Override
            public void run() {
                
            }
        });
    }

    private void placeChest(PlayerBackpack backpack, ItemStack dolly, Block chestBlock, Player p) {
        Utils.runSync(new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack[] bpContents = backpack.getInventory().getContents();

                if (Utils.checkNonInteractable(bpContents[0])) { // Slot at 0 if no marker at all
                    Utils.send(p, "&cYou must pick up a chest first!");
                    return;
                }

                boolean singleChest = Utils.checkNonInteractable(bpContents[27]); // Single chests have a marker at slot 27
                if (!canChestFit(chestBlock, p, singleChest)) {
                    Utils.send(p, "&cYou can't fit your chest there!");
                    return;
                }

                createChest(chestBlock, p, singleChest);
                backpack.getInventory().clear();
                backpack.getInventory().setItem(0, LOCK_ITEM);

                // Shrink contents size if single chest
                if (singleChest) {
                    bpContents = Arrays.copyOf(bpContents, 27);
                }

                ((InventoryHolder) chestBlock.getState()).getInventory().setStorageContents(bpContents[0]);
                dolly.setType(Material.MINECART);
                Utils.send(p, "&aChest has been placed");
            }
        });
    }

    private boolean canChestFit(Block singleChestBlock, Player p, boolean singleChest) {

        boolean fits = singleChestBlock.getType() == Material.AIR;

        if (!singleChest) {
            fits = fits && getRightBlock(singleChestBlock, p.getFacing().getOppositeFace()).getType() == Material.AIR;
        }

        return fits;
    }

    private void createChest(Block firstChest, Player p, boolean singleChest) {
        BlockFace chestFace = p.getFacing().getOppositeFace();

        // Place chest and rotate
        firstChest.setType(Material.CHEST);
        Directional firstDirectional = ((Directional) firstChest.getBlockData());
        firstDirectional.setFacing(chestFace);
        firstChest.setBlockData(firstDirectional);

        if (!singleChest) {
            // Get block on right (Previous cardinal)
            Block secondChest = getRightBlock(firstChest, chestFace);

            // Place chest and rotate
            secondChest.setType(Material.CHEST);
            Directional secondDirectional = ((Directional) secondChest.getBlockData());
            secondDirectional.setFacing(chestFace);
            secondChest.setBlockData(secondDirectional);

            // Connect chests
            Chest firstChestType = ((Chest) firstChest.getBlockData());
            Chest secondChestType = ((Chest) secondChest.getBlockData());

            firstChestType.setType(Chest.Type.RIGHT); // Don't know why these are flipped
            secondChestType.setType(Chest.Type.LEFT);

            firstChest.setBlockData(firstChestType);
            secondChest.setBlockData(secondChestType);
        }
    }

    @Nonnull
    private Block getRightBlock(Block b, BlockFace face) {

        BlockFace rightFace;

        switch (face) {
            case NORTH:
                rightFace = BlockFace.WEST;
                break;
            case EAST:
                rightFace = BlockFace.NORTH;
                break;
            case SOUTH:
                rightFace = BlockFace.EAST;
                break;
            case WEST:
                rightFace = BlockFace.SOUTH;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + face);
        }

        return b.getRelative(rightFace);

    }
}

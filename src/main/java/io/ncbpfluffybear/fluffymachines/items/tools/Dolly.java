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
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    private static final NamespacedKey DOLLY_MODE = new NamespacedKey(FluffyMachines.getInstance(), "dolly-mode");
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
                    setMode(dolly, DollyMode.EMPTY);
                });
            }
        }
    }

    private void pickupBarrel(ItemStack dolly, Block barrel, Player p) {
        PlayerProfile.getBackpack(dolly, backpack -> {

            if (backpack == null) {
                return;
            }

            if (isDollyUsed(dolly, backpack)) {
                Utils.send(p, "&cThis dolly is already loaded!");
                return;
            }

            Inventory bpInventory = backpack.getInventory();
            Barrel sfBarrel = (Barrel) BlockStorage.check(barrel);
            BlockMenu barrelInv = BlockStorage.getInventory(barrel);

            ItemStack barrelItem = sfBarrel.getItem().clone();
            Utils.setItemData(barrelItem, STORED_KEY, PersistentDataType.INTEGER, sfBarrel.getStored(barrel));

            bpInventory.setItem(0, barrelItem); // Barrel with stored value in PD
            bpInventory.setItem(1, sfBarrel.getStoredItem(barrel).clone());
            bpInventory.setItem(2, barrelInv.getItemInSlot(Barrel.INPUT_SLOTS[0]));
            bpInventory.setItem(3, barrelInv.getItemInSlot(Barrel.INPUT_SLOTS[1]));
            bpInventory.setItem(4, barrelInv.getItemInSlot(Barrel.OUTPUT_SLOTS[0]));
            bpInventory.setItem(5, barrelInv.getItemInSlot(Barrel.OUTPUT_SLOTS[1]));

            setMode(dolly, DollyMode.BARREL);

            BlockStorage.clearBlockInfo(barrel);
            barrel.setType(Material.AIR);
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

            if (isDollyUsed(dolly, backpack)) {
                Utils.send(p, "&cThis dolly is already loaded!");
                return;
            }

            backpack.getInventory().setStorageContents(chestInventory.getContents());
            if (chestInventory instanceof DoubleChestInventory) {
                setMode(dolly, DollyMode.DOUBLE_CHEST);
            } else {
                setMode(dolly, DollyMode.SINGLE_CHEST);
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

    // Mode stored in dolly's key item
    @Getter
    private enum DollyMode {
        EMPTY("&7Empty"), // 0
        SINGLE_CHEST("&6Chest"), // 1
        DOUBLE_CHEST("&6Double Chest"), // 2
        BARREL("&6Barrel"); // 3

        private final String name;

        DollyMode(String name) {
            this.name = name;
        }
    }

    private void setMode(ItemStack dolly, DollyMode mode) {
        Utils.setItemData(dolly, DOLLY_MODE, PersistentDataType.INTEGER, mode.ordinal());
        ItemMeta meta = dolly.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore.size() < 5) {
            lore.add(Utils.color(mode.getName()));
        } else {
            lore.set(5, mode.getName());
        }
        dolly.setItemMeta(meta);
    }

    private DollyMode getMode(ItemStack dolly) {
        Integer mode = Utils.getItemData(dolly, DOLLY_MODE, PersistentDataType.INTEGER);
        if (mode == null) return null;
        return DollyMode.values()[mode];
    }

    private boolean isDollyUsed(ItemStack dolly, PlayerBackpack backpack) {
        DollyMode mode = getMode(dolly);
        if (mode != null && mode != DollyMode.EMPTY) {
            return false;
        }

        // Handle legacy dolly
        // Dolly empty status determined by presence of lock item in first slot
        ItemStack lockItem = backpack.getInventory().getItem(0);
        if (Utils.checkNonInteractable(lockItem)) {
            setMode(dolly, DollyMode.EMPTY);
            return false;
        }

        if (Utils.checkNonInteractable(backpack.getInventory().getItem(27))) {
            setMode(dolly, DollyMode.SINGLE_CHEST);
        } else {
            setMode(dolly, DollyMode.DOUBLE_CHEST);
        }
        return true;
    }

    private void placeContents(ItemStack dolly, Block placeBlock, Player p) {
        PlayerProfile.getBackpack(dolly, backpack -> {
            if (backpack == null) {
                return;
            }

            DollyMode mode = getMode(dolly);
            mode = Utils.replaceNull(mode, DollyMode.SINGLE_CHEST);
            if (mode == DollyMode.EMPTY) {
                Utils.send(p, "&cYou must pick up a chest or barrel first!");
                return;
            }

            if (mode == DollyMode.SINGLE_CHEST || mode == DollyMode.DOUBLE_CHEST) {
                // Holding a chest or nothing
                placeChest(backpack, dolly, placeBlock, p, mode);
            } else if (mode == DollyMode.BARREL) {
                placeBarrel(backpack, dolly, placeBlock, p);
            }
        });
    }

    private void placeBarrel(PlayerBackpack backpack, ItemStack dolly, Block barrelBlock, Player p) {
        Utils.runSync(new BukkitRunnable() {
            @Override
            public void run() {
                if (barrelBlock.getType() != Material.AIR) {
                    Utils.send(p, "&cA barrel can not fit here!");
                    return;
                }

                Inventory bpInventory = backpack.getInventory();
                ItemStack barrelItem = bpInventory.getItem(0);
                Integer stored = Utils.getItemData(barrelItem, STORED_KEY, PersistentDataType.INTEGER);

                barrelBlock.setType(barrelItem.getType());
                if (barrelBlock.getBlockData() instanceof Directional) {
                    ((Directional) barrelBlock.getBlockData()).setFacing(p.getFacing().getOppositeFace());
                }
                BlockStorage.store(barrelBlock, barrelItem);

                Barrel barrel = (Barrel) SlimefunItem.getByItem(barrelItem);
                BlockMenu barrelMenu = BlockStorage.getInventory(barrelBlock);

                // Set output
                ItemStack out1 = bpInventory.getItem(4);
                if (out1 != null) barrelMenu.pushItem(out1, Barrel.OUTPUT_SLOTS[0]);
                ItemStack out2 = bpInventory.getItem(5);
                if (out2 != null) barrelMenu.pushItem(out2, Barrel.OUTPUT_SLOTS[1]);

                // Set main content
                ItemStack storedItem = bpInventory.getItem(1);
                if (storedItem != null) {
                    barrelMenu.pushItem(storedItem, Barrel.INPUT_SLOTS[0]);
                    barrel.acceptInput(barrelMenu, barrelBlock, Barrel.INPUT_SLOTS[0], barrel.getCapacity(barrelBlock));
                    barrel.setStored(barrelBlock, stored);
                }

                // Set inputs
                ItemStack in1 = bpInventory.getItem(2);
                if (in1 != null) barrelMenu.pushItem(out1, Barrel.INPUT_SLOTS[0]);
                ItemStack in2 = bpInventory.getItem(3);
                if (in2 != null) barrelMenu.pushItem(out2, Barrel.INPUT_SLOTS[1]);

                dolly.setType(Material.MINECART);
                Utils.send(p, "&Barrel has been placed");

                bpInventory.clear();
                setMode(dolly, DollyMode.EMPTY);
            }
        });
    }

    private void placeChest(PlayerBackpack backpack, ItemStack dolly, Block chestBlock, Player p, DollyMode mode) {
        Utils.runSync(new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack[] bpContents = backpack.getInventory().getContents();

                boolean singleChest = mode == DollyMode.SINGLE_CHEST;
                if (!canChestFit(chestBlock, p, singleChest)) {
                    Utils.send(p, "&cYou can't fit your chest there!");
                    return;
                }

                createChest(chestBlock, p, singleChest);
                backpack.getInventory().clear();
                setMode(dolly, DollyMode.EMPTY);

                // Shrink contents size if single chest
                if (singleChest) {
                    bpContents = Arrays.copyOf(bpContents, 27);
                }

                ((InventoryHolder) chestBlock.getState()).getInventory().setStorageContents(bpContents);
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

package io.ncbpfluffybear.fluffymachines.items;

import dev.j3fftw.extrautils.objects.NonHopperableBlock;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.ncbpfluffybear.fluffymachines.objects.FluffyHologram;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

/**
 * A Remake of Barrels by John000708
 *
 * @author NCBPFluffyBear
 */

public class Barrel extends NonHopperableBlock {

    private final int[] inputBorder = {9, 10, 11, 12, 18, 21, 27, 28, 29, 30};
    private final int[] outputBorder = {14, 15, 16, 17, 23, 26, 32, 33, 34, 35};
    private final int[] plainBorder = {0, 1, 2, 3, 4, 5, 6, 7, 8, 13, 36, 37, 38, 39, 40, 41, 42, 43, 44};

    private final int[] INPUT_SLOTS = {19, 20};
    private final int[] OUTPUT_SLOTS = {24, 25};

    private final int STATUS_SLOT = 22;
    private final int DISPLAY_SLOT = 31;
    private final int HOLOGRAM_TOGGLE_SLOT = 36;

    public static final int SMALL_BARREL_SIZE = 17280; // 5 Double chests
    public static final int MEDIUM_BARREL_SIZE = 34560; // 10 Double chests
    public static final int BIG_BARREL_SIZE = 69120; // 20 Double chests
    public static final int LARGE_BARREL_SIZE = 138240; // 40 Double chests
    public static final int MASSIVE_BARREL_SIZE = 276480; // 80 Double chests
    public static final int BOTTOMLESS_BARREL_SIZE = 1728000; // 500 Double chests

    private final int OVERFLOW_AMOUNT = 3240;

    private final int MAX_STORAGE;

    private final ItemSetting<Boolean> showHologram = new ItemSetting<>("show-hologram", true);

    public Barrel(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, String name,
                  int MAX_STORAGE) {
        super(category, item, recipeType, recipe);

        this.MAX_STORAGE = MAX_STORAGE;

        new BlockMenuPreset(getId(), name) {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {

                // Essentially convert to onPlace itemhandler
                if (BlockStorage.getLocationInfo(b.getLocation(), "stored") == null) {
                    menu.replaceExistingItem(STATUS_SLOT, new CustomItem(
                        Material.LIME_STAINED_GLASS_PANE, "&6Items Stored: &e0" + " / " + MAX_STORAGE, "&70%"));
                    menu.addMenuClickHandler(STATUS_SLOT, ChestMenuUtils.getEmptyClickHandler());

                    menu.replaceExistingItem(DISPLAY_SLOT, new CustomItem(Material.BARRIER, "&cEmpty"));
                    menu.addMenuClickHandler(DISPLAY_SLOT, ChestMenuUtils.getEmptyClickHandler());

                    BlockStorage.addBlockInfo(b, "stored", "0");

                    if (showHologram.getValue()) {
                        FluffyHologram.update(b, "&cEmpty");
                    }

                    // We still need the click handlers though
                } else {
                    menu.addMenuClickHandler(STATUS_SLOT, ChestMenuUtils.getEmptyClickHandler());
                    menu.addMenuClickHandler(DISPLAY_SLOT, ChestMenuUtils.getEmptyClickHandler());

                    if (!showHologram.getValue()) {
                        FluffyHologram.remove(b);
                    }
                }
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {

                // We need to put this here because this feature was implemented after barrels was introduced to FM
                BlockStorage.getInventory(b).replaceExistingItem(HOLOGRAM_TOGGLE_SLOT,
                    new CustomItem(Material.QUARTZ_SLAB, "&3Toggle Hologram"));
                BlockStorage.getInventory(b).addMenuClickHandler(HOLOGRAM_TOGGLE_SLOT, (pl, slot, item, action) -> {
                    toggleHolo(b);
                    return false;
                });

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
                if (flow == ItemTransportFlow.INSERT) {
                    return INPUT_SLOTS;
                } else if (flow == ItemTransportFlow.WITHDRAW) {
                    return OUTPUT_SLOTS;
                } else {
                    return new int[0];
                }
            }
        };

        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);
            String storedString = BlockStorage.getLocationInfo(b.getLocation(), "stored");
            int stored = Integer.parseInt(storedString);

            if (inv != null) {

                int itemCount = 0;

                SlimefunItem sfItem = SlimefunItem.getByItem(p.getInventory().getItemInMainHand());
                if (sfItem != null && (
                    sfItem == SlimefunItems.EXPLOSIVE_PICKAXE.getItem()
                        || sfItem == SlimefunItems.EXPLOSIVE_SHOVEL.getItem()
                        || sfItem == FluffyItems.UPGRADED_EXPLOSIVE_PICKAXE.getItem()
                        || sfItem == FluffyItems.UPGRADED_EXPLOSIVE_SHOVEL.getItem()
                )) {
                    Utils.send(p, "&cYou can not break barrels using explosive tools!");
                    FluffyHologram.remove(b);
                    return true;
                }

                for (Entity e : p.getNearbyEntities(5, 5, 5)) {
                    if (e instanceof Item) {
                        itemCount++;
                    }
                }

                if (itemCount > 5) {
                    Utils.send(p, "&cPlease remove nearby items before breaking this barrel!");
                    return false;
                }

                inv.dropItems(b.getLocation(), INPUT_SLOTS);
                inv.dropItems(b.getLocation(), OUTPUT_SLOTS);

                if (stored > 0) {
                    int stackSize = inv.getItemInSlot(DISPLAY_SLOT).getMaxStackSize();
                    ItemStack unKeyed = Utils.unKeyItem(inv.getItemInSlot(DISPLAY_SLOT));

                    if (stored > OVERFLOW_AMOUNT) {

                        Utils.send(p, "&eThere are more than " + OVERFLOW_AMOUNT + " items in this barrel! " +
                            "Dropping " + OVERFLOW_AMOUNT + " items instead!");
                        int toRemove = OVERFLOW_AMOUNT;
                        while (toRemove >= stackSize) {

                            b.getWorld().dropItemNaturally(b.getLocation(), new CustomItem(unKeyed, stackSize));

                            toRemove = toRemove - stackSize;
                        }

                        if (toRemove > 0) {
                            b.getWorld().dropItemNaturally(b.getLocation(), new CustomItem(unKeyed, toRemove));
                        }

                        BlockStorage.addBlockInfo(b, "stored", String.valueOf(stored - OVERFLOW_AMOUNT));
                        updateMenu(b, inv);

                        return false;
                    } else {

                        // Everything greater than 1 stack
                        while (stored >= stackSize) {

                            b.getWorld().dropItemNaturally(b.getLocation(), new CustomItem(unKeyed, stackSize));

                            stored = stored - stackSize;
                        }

                        // Drop remaining, if there is any
                        if (stored > 0) {
                            b.getWorld().dropItemNaturally(b.getLocation(), new CustomItem(unKeyed, stored));
                        }

                        // In case they use an explosive pick
                        BlockStorage.addBlockInfo(b, "stored", "0");
                        updateMenu(b, inv);
                        FluffyHologram.remove(b);
                        return true;
                    }
                } else {
                    FluffyHologram.remove(b);
                    return true;
                }

            }
            return true;
        });

        addItemSetting(showHologram);
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : outputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "), (p, slot, item,
                                                                                                       action) -> false);
        }

        for (int i : inputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), " "), (p, slot, item,
                                                                                                     action) -> false);
        }

        for (int i : plainBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "), (p, slot, item,
                                                                                                     action) -> false);
        }
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                Barrel.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block b) {
        BlockMenu inv = BlockStorage.getInventory(b);
        Location l = b.getLocation();

        // These have to be in separate lines or code goes ree

        for (int slot : INPUT_SLOTS) {
            if (inv.getItemInSlot(slot) != null) {

                String storedString = BlockStorage.getLocationInfo(l, "stored");
                int stored = Integer.parseInt(storedString);
                ItemStack item = inv.getItemInSlot(slot);

                if (stored == 0) {
                    registerItem(b, inv, slot, item, stored);
                } else if (stored > 0 && inv.getItemInSlot(DISPLAY_SLOT) != null
                    && matchMeta(Utils.unKeyItem(inv.getItemInSlot(DISPLAY_SLOT)), item)
                    && stored < MAX_STORAGE) {

                    // Can fit entire itemstack
                    if (stored + item.getAmount() <= MAX_STORAGE) {
                        storeItem(b, inv, slot, item, stored);

                        // Split itemstack
                    } else {
                        int amount = MAX_STORAGE - stored;
                        inv.consumeItem(slot, amount);

                        BlockStorage.addBlockInfo(b, "stored", String.valueOf((stored + amount)));
                        updateMenu(b, inv);
                    }
                }
            }
        }

        for (int i = 0; i < OUTPUT_SLOTS.length; i++) {
            if (inv.getItemInSlot(DISPLAY_SLOT) != null && inv.getItemInSlot(DISPLAY_SLOT).getType() != Material.BARRIER) {

                String storedString = BlockStorage.getLocationInfo(l, "stored");
                int stored = Integer.parseInt(storedString);
                ItemStack item = inv.getItemInSlot(DISPLAY_SLOT);

                if (stored > inv.getItemInSlot(DISPLAY_SLOT).getMaxStackSize()) {

                    ItemStack clone = new CustomItem(Utils.unKeyItem(item), item.getMaxStackSize());


                    if (inv.fits(clone, OUTPUT_SLOTS)) {
                        int amount = clone.getMaxStackSize();

                        BlockStorage.addBlockInfo(b, "stored", String.valueOf((stored - amount)));
                        inv.pushItem(clone, OUTPUT_SLOTS);
                        updateMenu(b, inv);
                    }

                } else if (stored != 0) {

                    ItemStack clone = new CustomItem(Utils.unKeyItem(item), stored);

                    if (inv.fits(clone, OUTPUT_SLOTS)) {
                        BlockStorage.addBlockInfo(b, "stored", "0");
                        inv.pushItem(clone, OUTPUT_SLOTS);
                        updateMenu(b, inv);
                    }
                }
            }
        }
    }

    private void registerItem(Block b, BlockMenu inv, int slot, ItemStack item, int stored) {
        int amount = item.getAmount();
        inv.replaceExistingItem(DISPLAY_SLOT, new CustomItem(Utils.keyItem(item), 1));

        inv.consumeItem(slot, amount);

        BlockStorage.addBlockInfo(b, "stored", String.valueOf((stored + amount)));
        updateMenu(b, inv);
    }

    private void storeItem(Block b, BlockMenu inv, int slot, ItemStack item, int stored) {
        int amount = item.getAmount();
        inv.consumeItem(slot, amount);

        BlockStorage.addBlockInfo(b, "stored", String.valueOf((stored + amount)));
        updateMenu(b, inv);
    }

    /**
     * This method checks if two items have the same metadata
     *
     * @param item1 is the first item to compare
     * @param item2 is the second item to compare
     * @return if the items have the same meta
     */
    private boolean matchMeta(ItemStack item1, ItemStack item2) {
        // Remove custom model data
        ItemMeta item1Model=item1.getItemMeta();
        ItemMeta item2Model=item2.getItemMeta();
        item1Model.setCustomModelData(null);
        item2Model.setCustomModelData(null);
        // It seems the meta comparisons are heavier than type checks
        return item1.getType().equals(item2.getType()) && item1_model.equals(item2_model));
    }

    /**
     * This method updates the barrel's menu and hologram displays
     *
     * @param b   is the barrel block
     * @param inv is the barrel's inventory
     */
    private void updateMenu(Block b, BlockMenu inv) {
        String storedString = BlockStorage.getLocationInfo(b.getLocation(), "stored");
        String hasHolo = BlockStorage.getLocationInfo(b.getLocation(), "holo");
        int stored = Integer.parseInt(storedString);
        String itemName;

        if (inv.getItemInSlot(DISPLAY_SLOT) != null && inv.getItemInSlot(DISPLAY_SLOT).getItemMeta().hasDisplayName()) {
            itemName = inv.getItemInSlot(DISPLAY_SLOT).getItemMeta().getDisplayName();
        } else {
            itemName = WordUtils.capitalizeFully(inv.getItemInSlot(DISPLAY_SLOT).getType().name().replace("_", " "));
        }

        String storedPercent = doubleRoundAndFade((double) stored / (double) MAX_STORAGE * 100);
        String storedStacks =
            doubleRoundAndFade((double) stored / (double) inv.getItemInSlot(DISPLAY_SLOT).getMaxStackSize());

        inv.replaceExistingItem(STATUS_SLOT, new CustomItem(
            Material.LIME_STAINED_GLASS_PANE, "&6Items Stored: &e" + stored + " / " + MAX_STORAGE,
            "&b" + storedStacks + " Stacks &8| &7" + storedPercent + "&7%"));
        if (showHologram.getValue() && (hasHolo == null || hasHolo.equals("true"))) {
            FluffyHologram.update(b, itemName + " &9x" + stored + " &7(" + storedPercent + "&7%)");
        }

        if (stored == 0) {
            inv.replaceExistingItem(DISPLAY_SLOT, new CustomItem(Material.BARRIER, "&cEmpty"));
            if (showHologram.getValue() && (hasHolo == null || hasHolo.equals("true"))) {
                FluffyHologram.update(b, "&cEmpty");
            }
        }
    }

    /**
     * This method toggles if a hologram is present above the barrel.
     *
     * @param b is the block the hologram is linked to
     */
    private void toggleHolo(Block b) {
        String toggle = BlockStorage.getLocationInfo(b.getLocation(), "holo");
        if (toggle == null || toggle.equals("true")) {
            BlockStorage.addBlockInfo(b.getLocation(), "holo", "false");
            FluffyHologram.remove(b);
        } else {
            BlockStorage.addBlockInfo(b.getLocation(), "holo", "true");
            updateMenu(b, BlockStorage.getInventory(b));
        }
    }

    public static String doubleRoundAndFade(double num) {
        // Using same format that is used on lore power
        String formattedString = Utils.powerFormat.format(num);
        if (formattedString.indexOf('.') != -1) {
            return formattedString.substring(0, formattedString.indexOf('.')) + ChatColor.DARK_GRAY
                + formattedString.substring(formattedString.indexOf('.')) + ChatColor.GRAY;
        } else {
            return formattedString;
        }
    }
}

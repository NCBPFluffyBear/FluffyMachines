package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.holograms.SimpleHologram;
import io.ncbpfluffybear.fluffymachines.objects.NonHopperableItem;
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
import sun.java2d.pipe.SpanShapeRenderer;

import javax.annotation.Nonnull;

/**
 * A Remake of Barrels by John000708
 *
 * @author NCBPFluffyBear
 */

public class Barrel extends NonHopperableItem {

    private final int[] inputBorder = {9, 10, 11, 12, 18, 21, 27, 28, 29, 30};
    private final int[] outputBorder = {14, 15, 16, 17, 23, 26, 32, 33, 34, 35};
    private final int[] plainBorder = {0, 1, 2, 3, 4, 5, 6, 7 ,8, 13, 36, 37, 38, 39, 40, 41, 42, 43, 44};

    private final int[] INPUT_SLOTS = {19, 20};
    private final int[] OUTPUT_SLOTS = {24, 25};

    private final int STATUS_SLOT = 22;
    private final int DISPLAY_SLOT = 31;

    public static final int SMALL_BARREL_SIZE = 17280; // 5 Double chests
    public static final int MEDIUM_BARREL_SIZE = 34560; // 10 Double chests
    public static final int BIG_BARREL_SIZE = 69120; // 20 Double chests
    public static final int LARGE_BARREL_SIZE = 138240; // 40 Double chests
    public static final int MASSIVE_BARREL_SIZE = 276480; // 80 Double chests
    public static final int BOTTOMLESS_BARREL_SIZE = 1728000; // 500 Double chests

    private final int OVERFLOW_AMOUNT = 3240;

    private final int MAX_STORAGE;

    private final ItemSetting<Boolean> showHologram = new ItemSetting<>("show-hologram", true);

    public Barrel(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, String name, int MAX_STORAGE) {
        super(category, item, recipeType, recipe);

        this.MAX_STORAGE = MAX_STORAGE;

        new BlockMenuPreset(getId(), name) {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {

                // Essentially convert to onPlace itemhandler
                if (BlockStorage.getLocationInfo(b.getLocation(), "stored") == null) {
                    menu.replaceExistingItem(STATUS_SLOT, new CustomItem(
                        Material.LIME_STAINED_GLASS_PANE, "&6儲存物品: &e0" + " / " + MAX_STORAGE, "&70%"));
                    menu.addMenuClickHandler(STATUS_SLOT, (p, slot, item, action) -> false);

                    menu.replaceExistingItem(DISPLAY_SLOT, new CustomItem(Material.BARRIER, "&c空"));
                    menu.addMenuClickHandler(DISPLAY_SLOT, (p, slot, item, action) -> false);

                    BlockStorage.addBlockInfo(b, "stored", "0");

                    if (showHologram.getValue()) {
                        SimpleHologram.update(b, "&c空");
                    }

                    // We still need the click handlers though
                } else {
                    menu.addMenuClickHandler(STATUS_SLOT, (p, slot, item, action) -> false);
                    menu.addMenuClickHandler(DISPLAY_SLOT, (p, slot, item, action) -> false);

                    if (!showHologram.getValue()) {
                        SimpleHologram.remove(b);
                    }
                }
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
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
                    Utils.send(p, "&c你不能使用爆炸工具破壞木桶!");
                    SimpleHologram.remove(b);
                    return true;
                }

                for (Entity e : p.getNearbyEntities(5, 5, 5)) {
                    if (e instanceof Item) {
                        itemCount++;
                    }
                }

                if (itemCount > 5) {
                    Utils.send(p, "&c請在破壞木桶之前移走附近的物品!");
                    return false;
                }

                inv.dropItems(b.getLocation(), INPUT_SLOTS);
                inv.dropItems(b.getLocation(), OUTPUT_SLOTS);

                if (stored > 0) {
                    int stackSize = inv.getItemInSlot(DISPLAY_SLOT).getMaxStackSize();
                    ItemStack unKeyed = Utils.unKeyItem(inv.getItemInSlot(DISPLAY_SLOT));

                    if (stored > OVERFLOW_AMOUNT) {

                        Utils.send(p, "&e此木桶內有超過 " + OVERFLOW_AMOUNT + " 個物品! " +
                            "掉落 " + OVERFLOW_AMOUNT + " 個替代物品!");
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
                        SimpleHologram.remove(b);
                        return true;
                    }
                } else {
                    SimpleHologram.remove(b);
                    return true;
                }

            }
            return true;
        });

        addItemSetting(showHologram);
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : outputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : inputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : plainBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
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
                }


                else if (stored > 0 && inv.getItemInSlot(DISPLAY_SLOT) != null
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

                /*
                for (int outputSlot : OUTPUT_SLOTS) {
                    if (inv.getItemInSlot(outputSlot) == null) {
                        freeSlot = outputSlot;
                        break;
                    } else if (outputSlot == OUTPUT_SLOTS[1]) {
                        return;
                    }
                }
                 */

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

    private boolean matchMeta(ItemStack item1, ItemStack item2) {
        // It seems the meta comparisons are heavier than type checks
        return item1.getType().equals(item2.getType()) && item1.getItemMeta().equals(item2.getItemMeta());
    }

    private void updateMenu(Block b, BlockMenu inv) {
        String storedString = BlockStorage.getLocationInfo(b.getLocation(), "stored");
        int stored = Integer.parseInt(storedString);
        String itemName;

        if (inv.getItemInSlot(DISPLAY_SLOT) != null && inv.getItemInSlot(DISPLAY_SLOT).getItemMeta().hasDisplayName()) {
            itemName = inv.getItemInSlot(DISPLAY_SLOT).getItemMeta().getDisplayName();
        } else {
            itemName = WordUtils.capitalizeFully(inv.getItemInSlot(DISPLAY_SLOT).getType().name().replace("_", " "));
        }

        String storedPercent = doubleRoundAndFade((double) stored / (double) MAX_STORAGE * 100);
        String storedStacks = doubleRoundAndFade((double) stored / (double) inv.getItemInSlot(DISPLAY_SLOT).getMaxStackSize());

        inv.replaceExistingItem(STATUS_SLOT, new CustomItem(
            Material.LIME_STAINED_GLASS_PANE, "&6儲存物品: &e" + stored + " / " + MAX_STORAGE,
            "&b" + storedStacks + " 組 &8| &7" + storedPercent + "&7%"));
        if (showHologram.getValue()) {
            SimpleHologram.update(b, itemName + " &9x" + stored + " &7(" + storedPercent + "&7%)");
        }

        if (stored == 0) {
            inv.replaceExistingItem(DISPLAY_SLOT, new CustomItem(Material.BARRIER, "&c空"));
            if (showHologram.getValue()) {
                SimpleHologram.update(b, "&c空");
            }
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

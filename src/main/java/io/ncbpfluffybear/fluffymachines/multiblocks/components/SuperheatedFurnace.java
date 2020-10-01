package io.ncbpfluffybear.fluffymachines.multiblocks.components;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import io.ncbpfluffybear.fluffymachines.multiblocks.Foundry;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 *
 * Melts and stores dusts and ingots
 * and can be withdrawn in either form.
 * Component of {@link Foundry}
 *
 * @author NCBPFluffyBear
 */
public class SuperheatedFurnace extends SlimefunItem {

    private static final int[] inputBorder = {0, 2, 9, 11, 18, 19, 20};
    private static final int[] dustOutputBorder = {3, 5, 12, 14, 21, 22, 23};
    private static final int[] ingotOutputBorder = {6, 8, 15, 17, 24, 25, 26};
    private static final int INPUT_SLOT = 10;
    private static final int DUST_OUTPUT_SLOT = 13;
    private static final int INGOT_OUTPUT_SLOT = 16;
    private static final int INPUT_INDICATOR = 1;
    private static final int DUST_INDICATOR = 4;
    private static final int INGOT_INDICATOR = 7;

    private static final ItemStack barrierNonClickable = Utils.buildNonInteractable(Material.BARRIER, "&4Blocked", "&cYou must use this in a Foundry!");

    private static final SlimefunItemStack[] dusts = new SlimefunItemStack[] {
        SlimefunItems.COPPER_DUST, SlimefunItems.GOLD_DUST, SlimefunItems.IRON_DUST,
        SlimefunItems.LEAD_DUST, SlimefunItems.ALUMINUM_DUST, SlimefunItems.ZINC_DUST,
        SlimefunItems.TIN_DUST, SlimefunItems.SILVER_DUST, SlimefunItems.MAGNESIUM_DUST};

    private static final SlimefunItemStack[] ingots = new SlimefunItemStack[] {
        SlimefunItems.COPPER_INGOT,
        SlimefunItems.LEAD_INGOT, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ZINC_INGOT,
        SlimefunItems.TIN_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.MAGNESIUM_INGOT};

    public SuperheatedFurnace(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getID(), "&cFoundry") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                menu.replaceExistingItem(4, new CustomItem(Material.GUNPOWDER, "&6Dust Available", "&e0", "&a> Click here to retrieve"));
                menu.addMenuClickHandler(4, (p, slot, item, action) -> {
                    retrieveDust(menu, b);
                    return false;
                });

                menu.replaceExistingItem(7, new CustomItem(Material.IRON_INGOT, "&6Ingots Available", "&e0", "&a> Click here to retrieve"));
                menu.addMenuClickHandler(7, (p, slot, item, action) -> {
                    retrieveIngot(menu, b);
                    return false;
                });

                BlockStorage.addBlockInfo(b, "stored", "0");
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return p.hasPermission("slimefun.inventory.bypass") || SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.ACCESS_INVENTORIES);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.INSERT) {
                    return new int[] {INPUT_SLOT};
                } else if (flow == ItemTransportFlow.WITHDRAW) {
                    return new int[] {DUST_OUTPUT_SLOT, INGOT_OUTPUT_SLOT};
                } else {
                    return new int[0];
                }
            }
        };

        registerBlockHandler(getID(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {

                String type = getBlockInfo(b.getLocation(), "type");

                // Because it gets clogged with barriers when not in multiblock form
                if (inv.getItemInSlot(INPUT_SLOT) != null && inv.getItemInSlot(INPUT_SLOT).getType() != Material.BARRIER) {
                    inv.dropItems(b.getLocation(), INPUT_SLOT);
                }
                if (inv.getItemInSlot(DUST_OUTPUT_SLOT) != null && inv.getItemInSlot(DUST_OUTPUT_SLOT).getType() != Material.BARRIER) {
                    inv.dropItems(b.getLocation(), DUST_OUTPUT_SLOT);
                }
                if (inv.getItemInSlot(INGOT_OUTPUT_SLOT) != null && inv.getItemInSlot(INGOT_OUTPUT_SLOT).getType() != Material.BARRIER) {
                    inv.dropItems(b.getLocation(), INGOT_OUTPUT_SLOT);
                }

                if (type != null) {
                    int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));
                    b.getWorld().dropItem(b.getLocation(), new CustomItem(SlimefunItem.getByID(type + "_DUST").getItem(), stored));

                }


            }

            if (BlockStorage.getLocationInfo(b.getLocation(), "stand") != null) {
                Bukkit.getEntity(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "stand"))).remove();
            }

            return true;
        });
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : dustOutputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : inputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : ingotOutputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        preset.addItem(1, new CustomItem(new ItemStack(Material.CHEST), "&6Melted Dust", "&e0", "&bType: None"), (p, slot, item, action) -> false);

    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                SuperheatedFurnace.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block b) {
        Location l = b.getLocation();
        BlockMenu inv = BlockStorage.getInventory(b);

        boolean accessible = getBlockInfo(l, "accessible") != null;
        boolean ignited = getBlockInfo(l, "ignited") != null;

        if (!accessible || !ignited) {
            inv.replaceExistingItem(INPUT_SLOT, barrierNonClickable);
            inv.replaceExistingItem(DUST_OUTPUT_SLOT, barrierNonClickable);
            inv.replaceExistingItem(INGOT_OUTPUT_SLOT, barrierNonClickable);
            return;
        } else {
            if (inv.getItemInSlot(INPUT_SLOT) != null && inv.getItemInSlot(INPUT_SLOT).getType() == Material.BARRIER) {
                inv.consumeItem(INPUT_SLOT);
            }
            if (inv.getItemInSlot(DUST_OUTPUT_SLOT) != null && inv.getItemInSlot(DUST_OUTPUT_SLOT).getType() == Material.BARRIER) {
                inv.consumeItem(DUST_OUTPUT_SLOT);
            }
            if (inv.getItemInSlot(INGOT_OUTPUT_SLOT) != null && inv.getItemInSlot(INGOT_OUTPUT_SLOT).getType() == Material.BARRIER) {
                inv.consumeItem(INGOT_OUTPUT_SLOT);
            }
        }

        ItemStack inputItem = inv.getItemInSlot(INPUT_SLOT);

        if (inputItem != null) {

            int amount = inputItem.getAmount();
            String type = getBlockInfo(b.getLocation(), "type");
            SlimefunItem sfItem = SlimefunItem.getByItem(inputItem);

            if (type == null) {

                if (sfItem != null) {
                    if (sfItem.getID().endsWith("_DUST")) {
                        for (SlimefunItemStack dust : dusts) {
                            if (sfItem == dust.getItem()) {

                                inv.consumeItem(INPUT_SLOT, amount);

                                registerDust(b, dust.getItemId().replace("_DUST", ""), amount);
                                break;

                            }
                        }
                    } else if (sfItem.getID().endsWith("_INGOT")) {
                        for (SlimefunItemStack ingot : ingots) {
                            if (sfItem == ingot.getItem()) {

                                inv.consumeItem(INPUT_SLOT, amount);

                                registerDust(b, ingot.getItemId().replace("_INGOT", ""), amount);
                                break;
                            }
                        }
                    } else if (sfItem.getID().equals(SlimefunItems.GOLD_4K.getItemId())) {
                        inv.consumeItem(INPUT_SLOT, amount);

                        registerDust(b, "GOLD", amount);
                    }
                } else if (inputItem.getItemMeta().equals(new ItemStack(Material.IRON_INGOT).getItemMeta())) {
                    inv.consumeItem(INPUT_SLOT, amount);

                    registerDust(b, "IRON", amount);
                }

            } else {
                if (sfItem!= null && sfItem.getID().equals(type + "_DUST")
                    || (type.equals("GOLD") && sfItem.getID().equals(SlimefunItems.GOLD_4K.getItemId()))
                    || (type.equals("IRON") && inputItem.getItemMeta().equals(
                        new ItemStack(Material.IRON_INGOT).getItemMeta()))) {
                    inv.consumeItem(INPUT_SLOT, amount);
                    addDust(b, amount);;
                }
            }
        }
    }


    private void registerDust(Block b, String type, int amount) {
        int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));
        setBlockInfo(b, "stored", String.valueOf(stored + amount));
        setBlockInfo(b, "type", type);
        updateIndicator(b);
    }

    private void addDust(Block b, int amount) {
        int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));
        setBlockInfo(b, "stored", String.valueOf(stored + amount));
        updateIndicator(b);
    }

    private void updateIndicator(Block b) {
        BlockMenu inv = BlockStorage.getInventory(b);
        String stored = getBlockInfo(b.getLocation(), "stored");
        String type = WordUtils.capitalizeFully(getBlockInfo(b.getLocation(), "type"));

        if (stored.equals("0")) {
            setBlockInfo(b, "type", null);
            inv.replaceExistingItem(INPUT_INDICATOR, new CustomItem(new ItemStack(Material.CHEST), "&6Melted Dust", "&e" + stored, "&bType: None"));

        }

        inv.replaceExistingItem(INPUT_INDICATOR, new CustomItem(new ItemStack(Material.CHEST), "&6Melted Dust", "&e" + stored, "&bType: " + type));
        inv.replaceExistingItem(DUST_INDICATOR, new CustomItem(new ItemStack(Material.GUNPOWDER), "&6Dust Available", "&e" + stored, "&a> Click here to retrieve"));
        inv.replaceExistingItem(INGOT_INDICATOR, new CustomItem(new ItemStack(Material.IRON_INGOT), "&6Ingots Available", "&e" + stored, "&a> Click here to retrieve"));


    }

    private void retrieveDust(BlockMenu menu, Block b) {

        if (getBlockInfo(b.getLocation(), "stored") == null)
            return;

        int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));

        if (stored > 0 && (menu.getItemInSlot(DUST_OUTPUT_SLOT) == null
            || menu.getItemInSlot(DUST_OUTPUT_SLOT).getAmount() < 64)) {

            String type = getBlockInfo(b.getLocation(), "type");

            setBlockInfo(b, "stored", String.valueOf(stored - 1));
            menu.pushItem(SlimefunItem.getByID(type + "_DUST").getItem(), DUST_OUTPUT_SLOT);

            updateIndicator(b);
        }
    }

    private void retrieveIngot(BlockMenu menu, Block b) {

        if (getBlockInfo(b.getLocation(), "stored") == null)
            return;

        int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));

        if (stored > 0 && (menu.getItemInSlot(INGOT_OUTPUT_SLOT) == null
            || menu.getItemInSlot(INGOT_OUTPUT_SLOT).getAmount() < 64)) {

            String type = getBlockInfo(b.getLocation(), "type");

            setBlockInfo(b, "stored", String.valueOf(stored - 1));
            if (type.equals("GOLD")) {
                menu.pushItem(SlimefunItems.GOLD_4K.getItem().getItem(), INGOT_OUTPUT_SLOT);
            } else if (type.equals("IRON")) {
                menu.pushItem(new ItemStack(Material.IRON_INGOT), INGOT_OUTPUT_SLOT);

            } else {
                menu.pushItem(SlimefunItem.getByID(type + "_INGOT").getItem(), INGOT_OUTPUT_SLOT);
            }
            updateIndicator(b);
        }
    }

    private void setBlockInfo(Block b, String key, String data) {
        BlockStorage.addBlockInfo(b, key, data);
    }

    private String getBlockInfo(Location l, String key) {
        return BlockStorage.getLocationInfo(l, key);
    }
}

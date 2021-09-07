package io.ncbpfluffybear.fluffymachines.multiblocks.components;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.ncbpfluffybear.fluffymachines.objects.NonHopperableBlock;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.ncbpfluffybear.fluffymachines.multiblocks.Foundry;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 *
 * Melts and stores dusts and ingots
 * and can be withdrawn in either form.
 * Component of {@link Foundry}
 *
 * @author NCBPFluffyBear
 */
public class SuperheatedFurnace extends NonHopperableBlock {

    private static final int[] inputBorder = {0, 2, 9, 11, 18, 19, 20};
    private static final int[] dustOutputBorder = {3, 5, 12, 14, 21, 22, 23};
    private static final int[] ingotOutputBorder = {6, 8, 15, 17, 24, 25, 26};
    private static final int INPUT_SLOT = 10;
    private static final int DUST_OUTPUT_SLOT = 13;
    private static final int INGOT_OUTPUT_SLOT = 16;
    private static final int INPUT_INDICATOR = 1;
    private static final int DUST_INDICATOR = 4;
    private static final int INGOT_INDICATOR = 7;

    private static final int MAX_STORAGE = 138240;
    private static final Material netherite = Material.NETHERITE_BLOCK;
    private final int MAX_STACK_SIZE = 64;

    private static final SlimefunItemStack[] dusts = new SlimefunItemStack[] {
        SlimefunItems.COPPER_DUST, SlimefunItems.GOLD_DUST, SlimefunItems.IRON_DUST,
        SlimefunItems.LEAD_DUST, SlimefunItems.ALUMINUM_DUST, SlimefunItems.ZINC_DUST,
        SlimefunItems.TIN_DUST, SlimefunItems.SILVER_DUST, SlimefunItems.MAGNESIUM_DUST};

    private static final SlimefunItemStack[] ingots = new SlimefunItemStack[] {
        SlimefunItems.COPPER_INGOT,
        SlimefunItems.LEAD_INGOT, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ZINC_INGOT,
        SlimefunItems.TIN_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.MAGNESIUM_INGOT};

    private final int OVERFLOW_AMOUNT = 3240;

    private final ItemSetting<Boolean> breakOnlyWhenEmpty = new ItemSetting<>(this, "break-only-when-empty", false);

    public SuperheatedFurnace(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(onBreak());
        addItemSetting(breakOnlyWhenEmpty);

        new BlockMenuPreset(getId(), "&c鑄造廠") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                if (BlockStorage.getLocationInfo(b.getLocation(), "stored") == null) {

                    menu.replaceExistingItem(4, new CustomItemStack(Material.GUNPOWDER, "&6可用的粉: &e0", "&a> &e左鍵&a點擊來拿取1個", "&a> &e右鍵&a點擊來拿取64個"));
                    menu.replaceExistingItem(7, new CustomItemStack(Material.IRON_INGOT, "&6可用的錠: &e0", "&a> &e左鍵&a點擊來拿取1個", "&a> &e右鍵&a點擊來拿取64個"));
                    menu.replaceExistingItem(1, new CustomItemStack(Material.CHEST, "&6融化的粉: &e0 &7(0%)", "&b類型: 無",  "&7組: 0"));

                    BlockStorage.addBlockInfo(b, "stored", "0");
                }

                menu.addMenuClickHandler(1, (p, slot, item, action) -> false);

                menu.addMenuClickHandler(4, (p, slot, item, action) -> {
                    retrieveDust(menu, b, action.isRightClicked());
                    return false;
                });

                menu.addMenuClickHandler(7, (p, slot, item, action) -> {
                    retrieveIngot(menu, b, action.isRightClicked());
                    return false;
                });
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return (p.hasPermission("slimefun.inventory.bypass")
                    || Slimefun.getProtectionManager().hasPermission(
                    p, b.getLocation(), Interaction.INTERACT_BLOCK))
                    && getBlockInfo(b.getLocation(), "accessible") != null
                    && getBlockInfo(b.getLocation(), "ignited") != null && checkStructure(b);
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

    }

    private BlockBreakHandler onBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent e, @Nonnull ItemStack item, @Nonnull List<ItemStack> drops) {
                Block b = e.getBlock();
                Player p = e.getPlayer();
                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {

                    int itemCount = 0;

                    int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));
                    String type = getBlockInfo(b.getLocation(), "type");

                    if (breakOnlyWhenEmpty.getValue() && stored != 0) {
                        Utils.send(p, "&c這個超級炙熱熔爐不能被破壞, 因為它裡面還存有物品!");
                        e.setCancelled(true);
                        return;
                    }

                    for (Entity en : p.getNearbyEntities(5, 5, 5)) {
                        if (en instanceof Item) {
                            itemCount++;
                        }
                    }

                    if (itemCount > 5) {
                        Utils.send(p, "&c在打破此超級炙熱熔爐前, 請移除附近的物品!");
                        e.setCancelled(true);
                        return;
                    }

                    inv.dropItems(b.getLocation(), INPUT_SLOT);
                    inv.dropItems(b.getLocation(), DUST_OUTPUT_SLOT);
                    inv.dropItems(b.getLocation(), INGOT_OUTPUT_SLOT);

                    if (stored > 0) {
                        int stackSize = Constants.MAX_STACK_SIZE;
                        ItemStack dust = SlimefunItem.getById(type + "_DUST").getItem();

                        if (stored > OVERFLOW_AMOUNT) {

                            Utils.send(p, "&e此超級炙熱熔爐內有超過 " + OVERFLOW_AMOUNT + " 個物品! " +
                                "掉落 " + OVERFLOW_AMOUNT + " 個替代物品!");
                            int toRemove = OVERFLOW_AMOUNT;
                            while (toRemove >= stackSize) {

                                b.getWorld().dropItemNaturally(b.getLocation(), new CustomItemStack(dust, stackSize));

                                toRemove = toRemove - stackSize;
                            }

                            if (toRemove > 0) {
                                b.getWorld().dropItemNaturally(b.getLocation(), new CustomItemStack(dust, toRemove));
                            }

                            BlockStorage.addBlockInfo(b, "stored", String.valueOf(stored - OVERFLOW_AMOUNT));

                            e.setCancelled(true);
                            updateIndicator(b);
                            return;
                        } else {

                            // Everything greater than 1 stack
                            while (stored >= stackSize) {

                                b.getWorld().dropItemNaturally(b.getLocation(), new CustomItemStack(dust, stackSize));

                                stored = stored - stackSize;
                            }

                            // Drop remaining, if there is any
                            if (stored > 0) {
                                b.getWorld().dropItemNaturally(b.getLocation(), new CustomItemStack(dust, stored));
                            }

                            if (BlockStorage.getLocationInfo(b.getLocation(), "stand") != null) {
                                Bukkit.getEntity(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "stand"))).remove();
                            }

                            // In case they use an explosive pick
                            BlockStorage.addBlockInfo(b, "stored", "0");
                            updateIndicator(b);
                            return;
                        }
                    }
                    if (BlockStorage.getLocationInfo(b.getLocation(), "stand") != null) {
                        Entity en = Bukkit.getEntity(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "stand")));
                        if (en != null) {
                            en.remove();
                        }
                    }
                }
            }
        };
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : dustOutputBorder) {
            preset.addItem(i, new CustomItemStack(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : inputBorder) {
            preset.addItem(i, new CustomItemStack(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : ingotOutputBorder) {
            preset.addItem(i, new CustomItemStack(new ItemStack(Material.RED_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }


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
        BlockMenu inv = BlockStorage.getInventory(b);

        ItemStack inputItem = inv.getItemInSlot(INPUT_SLOT);

        if (inputItem != null) {

            int amount = inputItem.getAmount();
            String type = getBlockInfo(b.getLocation(), "type");
            SlimefunItem sfItem = SlimefunItem.getByItem(inputItem);
            int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));

            if (type == null) {

                if (sfItem != null) {
                    if (sfItem.getId().endsWith("_DUST")) {
                        for (SlimefunItemStack dust : dusts) {
                            if (sfItem == dust.getItem()) {

                                inv.consumeItem(INPUT_SLOT, amount);

                                registerDust(b, dust.getItemId().replace("_DUST", ""), amount);
                                break;

                            }
                        }
                    } else if (sfItem.getId().endsWith("_INGOT")) {
                        for (SlimefunItemStack ingot : ingots) {
                            if (sfItem == ingot.getItem()) {

                                inv.consumeItem(INPUT_SLOT, amount);

                                registerDust(b, ingot.getItemId().replace("_INGOT", ""), amount);
                                break;
                            }
                        }
                    } else if (sfItem.getId().equals(SlimefunItems.GOLD_4K.getItemId())) {
                        inv.consumeItem(INPUT_SLOT, amount);

                        registerDust(b, "GOLD", amount);
                    }
                } else if (inputItem.getType() == Material.IRON_INGOT
                    && inputItem.getItemMeta().equals(new ItemStack(Material.IRON_INGOT).getItemMeta())
                ) {
                    inv.consumeItem(INPUT_SLOT, amount);

                    registerDust(b, "IRON", amount);
                }

            } else {
                if (sfItem != null && ((sfItem.getId().equals(type + "_DUST") || sfItem.getId().equals(type + "_INGOT"))
                    || (type.equals("GOLD") && sfItem.getId().equals(SlimefunItems.GOLD_4K.getItemId())))
                    || (type.equals("IRON") && inputItem.getType() == Material.IRON_INGOT
                    && inputItem.getItemMeta().equals(new ItemStack(Material.IRON_INGOT).getItemMeta()))
                    && stored + amount < MAX_STORAGE) {
                    inv.consumeItem(INPUT_SLOT, amount);
                    addDust(b, amount);
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
            inv.replaceExistingItem(INPUT_INDICATOR, new CustomItemStack(new ItemStack(Material.CHEST), "&6融化的粉: &e0 &7(0%)", "&b類型: 無",  "&7組: 0"));
        } else {
            inv.replaceExistingItem(INPUT_INDICATOR, new CustomItemStack(new ItemStack(Material.CHEST), "&6融化的粉: &e" + stored + " &7(" + Double.parseDouble(stored) / MAX_STORAGE + "%)", "&b類型: " + type, "&7組: " + Double.parseDouble(stored) / 64));

        }
        inv.replaceExistingItem(DUST_INDICATOR, new CustomItemStack(new ItemStack(Material.GUNPOWDER), "&6可用的粉: &e" + stored, "&a> &e左鍵&a點擊來拿取1個", "&a> &e右鍵&a點擊來拿取64個"));
        inv.replaceExistingItem(INGOT_INDICATOR, new CustomItemStack(new ItemStack(Material.IRON_INGOT), "&6可用的錠: &e" + stored, "&a> &e左鍵&a點擊來拿取1個", "&a> &e右鍵&a點擊來拿取64個"));


    }

    private void retrieveDust(BlockMenu menu, Block b, boolean isRightClicked) {

        if (getBlockInfo(b.getLocation(), "stored") == null)
            return;

        int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));

        if (stored > 0 && (menu.getItemInSlot(DUST_OUTPUT_SLOT) == null
            || menu.getItemInSlot(DUST_OUTPUT_SLOT).getAmount() < 64)) {

            String type = getBlockInfo(b.getLocation(), "type");
            int amount;

            if (!isRightClicked) {
                amount = 1;
            } else if (stored < MAX_STACK_SIZE) {
                amount = stored;
            } else if (menu.getItemInSlot(DUST_OUTPUT_SLOT) == null) {
                amount = MAX_STACK_SIZE;
            } else {
                amount = MAX_STACK_SIZE - menu.getItemInSlot(DUST_OUTPUT_SLOT).getAmount();
            }

            ItemStack dustItem = new CustomItemStack(SlimefunItem.getById(type + "_DUST").getItem().clone(), amount);
            if (menu.fits(dustItem, DUST_OUTPUT_SLOT)) {
                setBlockInfo(b, "stored", String.valueOf(stored - amount));
                menu.pushItem(dustItem, DUST_OUTPUT_SLOT);
            }

            updateIndicator(b);
        }
    }

    private void retrieveIngot(BlockMenu menu, Block b, boolean isRightClicked) {

        if (getBlockInfo(b.getLocation(), "stored") == null)
            return;

        int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));

        if (stored > 0 && (menu.getItemInSlot(INGOT_OUTPUT_SLOT) == null
            || menu.getItemInSlot(INGOT_OUTPUT_SLOT).getAmount() < 64)) {

            String type = getBlockInfo(b.getLocation(), "type");

            int amount;

            if (!isRightClicked) {
                amount = 1;
            } else if (stored < MAX_STACK_SIZE) {
                amount = stored;
            } else if (menu.getItemInSlot(INGOT_OUTPUT_SLOT) == null) {
                amount = MAX_STACK_SIZE;
            } else {
                amount = MAX_STACK_SIZE - menu.getItemInSlot(INGOT_OUTPUT_SLOT).getAmount();
            }

            ItemStack ingotItem;
            if (type.equals("GOLD")) {
                ingotItem = new CustomItemStack(SlimefunItems.GOLD_4K.getItem().getItem().clone(), amount);
            } else if (type.equals("IRON")) {
                ingotItem = new ItemStack(Material.IRON_INGOT, amount);
            } else {
                ingotItem = new CustomItemStack(SlimefunItem.getById(type + "_INGOT").getItem().clone(), amount);
            }

            if (menu.fits(ingotItem, INGOT_OUTPUT_SLOT)) {
                setBlockInfo(b, "stored", String.valueOf(stored - amount));
                menu.pushItem(ingotItem, INGOT_OUTPUT_SLOT);
            }

            updateIndicator(b);
        }
    }

    private boolean checkStructure(Block b) {
        BlockFace face;
        Block relative;

        if (b.getRelative(BlockFace.NORTH).getType() == netherite) {
            face = BlockFace.NORTH;
            relative = b.getRelative(face);
        } else if (b.getRelative(BlockFace.EAST).getType() == netherite) {
            face = BlockFace.EAST;
            relative = b.getRelative(face);
        } else {
            return false;
        }

        // Checks multiblock structure

        return b.getRelative(face).getType() == netherite
            && checkRite(relative.getRelative(0, -1, 0))
            && checkRite(relative.getRelative(0, -2, 0))
            && checkRite(b.getRelative(face.getOppositeFace()).getRelative(0, -1, 0))
            && checkRite(b.getRelative(face.getOppositeFace()).getRelative(0, -2, 0))
            && b.getRelative(0, -1, 0).getType() == Material.GLASS
            && b.getRelative(0, -2, 0).getType() == Material.CAULDRON;
    }

    private boolean checkRite(Block b) {
        return (b.getType() == netherite);
    }

    private void setBlockInfo(Block b, String key, String data) {
        BlockStorage.addBlockInfo(b, key, data);
    }

    private String getBlockInfo(Location l, String key) {
        return BlockStorage.getLocationInfo(l, key);
    }
}

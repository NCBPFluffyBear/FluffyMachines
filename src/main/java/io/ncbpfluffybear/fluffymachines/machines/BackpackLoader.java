package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BackpackLoader extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    public static final int ENERGY_CONSUMPTION = 16;
    public static final int CAPACITY = ENERGY_CONSUMPTION * 3;

    private static final int[] PLAIN_BORDER = {38, 39, 40, 41, 42, 47, 48, 49, 50, 51};
    private static final int[] INPUT_BORDER = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33,
        34, 35};
    private static final int[] OUTPUT_BORDER = {43, 44, 52};
    private static final int[] BACKPACK_BORDER = {36, 37, 46};
    private static final int[] INPUT_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
    private static final int[] OUTPUT_SLOTS = {53};
    private static final int BACKPACK_SLOT = 45;


    public BackpackLoader(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        setupInv();

        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), getInputSlots());
                inv.dropItems(b.getLocation(), getOutputSlots());
                inv.dropItems(b.getLocation(), BACKPACK_SLOT);
            }

            return true;
        });
    }

    protected void setupInv() {
        createPreset(this, "&eBackpack Loader", preset -> {
            border(preset, PLAIN_BORDER, INPUT_BORDER, OUTPUT_BORDER);

            for (int i : BACKPACK_BORDER) {
                preset.addItem(i, new CustomItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), " "),
                    (p, slot, item, action) -> false
                );
            }
        });
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) {
                BackpackLoader.this.tick(b);
            }

            public boolean isSynchronized() {
                return false;
            }
        });
    }

    private void tick(@Nonnull Block b) {

        if (getCharge(b.getLocation()) < ENERGY_CONSUMPTION) {
            return;
        }

        @Nullable final BlockMenu inv = BlockStorage.getInventory(b);
        boolean invalidItem = false;

        // If no backpack in backpack slot, search for one and if found move to BACKPACK_SLOT
        if (inv.getItemInSlot(BACKPACK_SLOT) == null) {
            for (int inputSlot : getInputSlots()) {
                ItemStack backpackItem = inv.getItemInSlot(inputSlot);
                if (backpackItem != null && SlimefunItem.getByItem(backpackItem) instanceof SlimefunBackpack) {

                    // Make sure it has an ID
                    List<String> lore = backpackItem.getItemMeta().getLore();
                    for (String s : lore) {
                        if (s.equals(ChatColor.GRAY + "ID: <ID>")) {
                            invalidItem = true;
                            break;
                        }
                    }
                    if (!invalidItem) {
                        moveItem(inv, inputSlot, BACKPACK_SLOT);

                    } else if (inv.getItemInSlot(getOutputSlots()[0]) == null) {
                        moveItem(inv, inputSlot, getOutputSlots()[0]);
                    }
                    return;
                }
            }
        }

        int occupiedInputSlot = 0;

        // Are there any items in the input?
        for (int inputSlot : getInputSlots()) {
            if (inv.getItemInSlot(inputSlot) != null
                && !(SlimefunItem.getByItem(inv.getItemInSlot(inputSlot)) instanceof SlimefunBackpack)) {
                occupiedInputSlot = inputSlot;
                break;
            } else if (inputSlot == getInputSlots()[13]) {
                return;
            }
        }

        // Loading the backpack
        ItemStack bpItem = inv.getItemInSlot(BACKPACK_SLOT);
        SlimefunItem sfItem = SlimefunItem.getByItem(bpItem);
        if (sfItem instanceof SlimefunBackpack) {

            ItemStack transferItem = inv.getItemInSlot(occupiedInputSlot);

            int finalOccupiedInputSlot = occupiedInputSlot;
            PlayerProfile.getBackpack(bpItem, backpack -> {

                Inventory bpinv = backpack.getInventory();

                int bpSlot = bpinv.firstEmpty();

                // Backpack is full
                if (bpSlot == -1) {
                    if (inv.getItemInSlot(OUTPUT_SLOTS[0]) == null) {
                        moveItem(inv, BACKPACK_SLOT, OUTPUT_SLOTS[0]);
                    }
                    return;
                }

                if (bpinv.getItem(bpSlot) == null) {

                    // IntelliJ wanted me to put it as a separate variable so here we are
                    inv.replaceExistingItem(finalOccupiedInputSlot, null);
                    bpinv.setItem(bpSlot, transferItem);

                    removeCharge(b.getLocation(), ENERGY_CONSUMPTION);
                }
            });
        }
    }

    private void moveItem(BlockMenu inv, int slot1, int slot2) {
        ItemStack transferItem = inv.getItemInSlot(slot1);
        inv.replaceExistingItem(slot1, null);
        inv.pushItem(transferItem, slot2);
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public int[] getInputSlots() {
        return INPUT_SLOTS;
    }

    @Override
    public int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    static void border(BlockMenuPreset preset, int[] plainBorder, int[] inputBorder, int[] outputBorder) {
        for (int i : plainBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "),
                (p, slot, item, action) -> false
            );
        }

        for (int i : inputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), " "),
                (p, slot, item, action) -> false
            );
        }

        for (int i : outputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "),
                (p, slot, item, action) -> false
            );
        }
    }
}


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
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BackpackUnloader extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    public static final int CAPACITY = 1024;
    public static final int ENERGY_CONSUMPTION = 64;

    private static final int[] PLAIN_BORDER = {2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14, 15, 16, 17};
    private static final int[] INPUT_BORDER = {1, 9, 10};
    private static final int[] OUTPUT_BORDER = {18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 35, 36, 44, 45, 46, 47, 48,
        49, 50, 51, 52, 53};
    private static final int[] INPUT_SLOTS = {0};
    private static final int[] OUTPUT_SLOTS = {28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

    public BackpackUnloader(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe
        );

        setupInv();

        registerBlockHandler(getID(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), getInputSlots());
                inv.dropItems(b.getLocation(), getOutputSlots());
            }

            return true;
        });
    }

    protected void setupInv() {
        createPreset(this, "&eBackpack Unloader", preset ->
            BackpackLoader.border(preset, PLAIN_BORDER, INPUT_BORDER, OUTPUT_BORDER));
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) {
                BackpackUnloader.this.tick(b);
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

        for (int outputSlot : getOutputSlots()) {
            if (inv.getItemInSlot(outputSlot) == null) {
                break;
            } else if (outputSlot == getOutputSlots()[13]) {
                return;
            }
        }

        ItemStack inputItem = inv.getItemInSlot(getInputSlots()[0]);
        if (inputItem != null) {
            SlimefunItem sfItem = SlimefunItem.getByItem(inputItem);
            if (sfItem instanceof SlimefunBackpack) {

                // No ID
                List<String> lore = inputItem.getItemMeta().getLore();
                for (int line = 0; line < lore.size(); line++) {
                    if (lore.get(line).equals(ChatColor.GRAY + "ID: <ID>")) {
                        rejectInput(inv);
                        return;
                    }
                }

                PlayerProfile.getBackpack(inputItem, backpack -> {

                    Inventory bpinv = backpack.getInventory();
                    for (int slot = 0; slot < bpinv.getSize(); slot++) {
                        if (bpinv.getItem(slot) != null) {
                            ItemStack transferItem = bpinv.getItem(slot);
                            bpinv.setItem(slot, null);
                            inv.pushItem(transferItem, getOutputSlots());
                            removeCharge(b.getLocation(), ENERGY_CONSUMPTION);
                            return;
                        }
                        // Backpack is empty, move it to the output
                        if (slot == bpinv.getSize() - 1) {
                            rejectInput(inv);
                            return;
                        }
                    }
                });

            } else {
                // Not a backpack
                rejectInput(inv);
            }
        }
    }

    private void rejectInput(BlockMenu inv) {
        ItemStack transferItem = inv.getItemInSlot(getInputSlots()[0]);
        inv.replaceExistingItem(getInputSlots()[0], null);
        inv.pushItem(transferItem, getOutputSlots());
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
}


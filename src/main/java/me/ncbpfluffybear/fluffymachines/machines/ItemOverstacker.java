package me.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemOverstacker extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    private static final int CAPACITY = 1024;
    private static final int ENERGY_CONSUMPTION = 256;
    private static final int MAX_STACK_SIZE = 64;

    private static final int[] PLAIN_BORDER = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 13, 31, 36, 37, 38, 39, 40, 41, 42, 43 ,44 };
    private static final int[] INPUT_BORDER = { 9, 10, 11, 12, 18, 21, 27, 28, 29, 30 };
    private static final int[] OUTPUT_BORDER = { 14, 15, 16, 17, 23, 26, 32, 33, 34, 35 };
    private static final int[] INPUT_SLOTS = { 19, 20 };
    private static final int[] OUTPUT_SLOTS = { 24, 25 };
    private static final int PROCESSING_SLOT = 22;

    private static final ItemStack[] stackableItems = { new ItemStack(Material.DIAMOND_PICKAXE), new ItemStack(Material.WATER_BUCKET) };


    public ItemOverstacker(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        setupInv();
    }

    protected void setupInv() {
        createPreset(this, "&eItem Overstacker", preset -> {
            for (int i : PLAIN_BORDER) {
                preset.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
            }

            for (int i : INPUT_BORDER) {
                preset.addItem(i, new CustomItem(new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
            }

            for (int i : OUTPUT_BORDER) {
                preset.addItem(i, new CustomItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
            }

            preset.addItem(PROCESSING_SLOT, new CustomItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        });
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) {
                ItemOverstacker.this.tick(b);
            }

            public boolean isSynchronized() {
                return false;
            }
        });
    }

    private void tick(@Nonnull Block b) {
        @Nullable final BlockMenu inv = BlockStorage.getInventory(b);
        for (int inputSlot : INPUT_SLOTS) {
            ItemStack inputSlotItem = inv.getItemInSlot(inputSlot);
            if (inputSlotItem != null) {

                for (ItemStack validItem : stackableItems) {

                    if (validItem.getItemMeta().equals(inputSlotItem.getItemMeta())) {
                        ItemStack finalOutputItem = null;
                        inv.replaceExistingItem(inputSlot, new ItemStack(Material.AIR));

                        for (int outputSlot : OUTPUT_SLOTS) {
                            ItemStack outputSlotItem = inv.getItemInSlot(outputSlot);
                            if (outputSlotItem != null &&
                                outputSlotItem.getItemMeta().equals(inputSlotItem.getItemMeta())) {
                                finalOutputItem = new ItemStack(outputSlotItem.getType(), outputSlotItem.getAmount() + inputSlotItem.getAmount());
                            }
                            inv.replaceExistingItem(outputSlot, finalOutputItem);
                            break;
                        }

                    }
                }
            }
        }
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

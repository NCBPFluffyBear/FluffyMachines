package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class AdvancedChargingBench extends AContainer {

    public static final int CAPACITY = 128;
    public static final int ENERGY_CONSUMPTION = 10;
    public static final int CHARGE = 5;
    private int tier = 0;

    public AdvancedChargingBench(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
        addItemHandler(onPlace());
    }

    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(true) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent e) {
                Block b = e.getBlockPlaced();
                BlockStorage.addBlockInfo(b.getLocation(), "tier", "0");
            }
        };
    }

    @Override
    protected void tick(Block b) {
        if (getCharge(b.getLocation()) < getEnergyConsumption()) {
            return;
        }

        BlockMenu inv = BlockStorage.getInventory(b);
        tier = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "tier"));

        for (int slot : getInputSlots()) {
            ItemStack item = inv.getItemInSlot(slot);

            if (charge(b, inv, slot, item, tier)) {
                return;
            }
        }
    }

    private boolean charge(Block b, BlockMenu inv, int slot, ItemStack item, int tier) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem instanceof Rechargeable) {
            float charge = CHARGE + CHARGE * tier;

            if (((Rechargeable) sfItem).addItemCharge(item, charge)) {
                removeCharge(b.getLocation(), getEnergyConsumption());
            } else if (inv.fits(item, getOutputSlots())) {
                inv.pushItem(item, getOutputSlots());
                inv.replaceExistingItem(slot, null);
            }

            return true;
        } else if (sfItem != null && inv.fits(item, getOutputSlots())) {
            inv.pushItem(item, getOutputSlots());
            inv.replaceExistingItem(slot, null);
        }

        return false;
    }

    @Override
    public ItemStack getProgressBar() {
        return null;
    }

    @Override
    public int getCapacity() {
        return CAPACITY + CAPACITY * tier;
    }

    @Override
    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION + ENERGY_CONSUMPTION * tier;
    }

    @Override
    public int getSpeed() {
        return 1;
    }

    @Nonnull
    @Override
    public String getMachineIdentifier() {
        return "ADVANCED_CHARGING_BENCH";
    }
}

package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricDustWasher;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.OreWasher;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The electric dust washer but accepts cobblestone
 * Low iq low effort machine
 *
 * @see ElectricDustWasher
 */

public class ElectricDustFabricator extends AContainer implements RecipeDisplayItem {

    public static final int ENERGY_CONSUMPTION = 256;
    public static final int CAPACITY = ENERGY_CONSUMPTION * 3;
    private OreWasher oreWasher;

    public ElectricDustFabricator(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        super.preRegister();

        oreWasher = (OreWasher) SlimefunItems.ORE_WASHER.getItem();
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();

        for (SlimefunItemStack dust : Constants.dusts) {
            displayRecipes.add(new ItemStack(Material.COBBLESTONE));
            displayRecipes.add(dust);
        }

        return displayRecipes;
    }

    @Override
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        for (int slot : getInputSlots()) {
            if (SlimefunUtils.isItemSimilar(menu.getItemInSlot(slot), new ItemStack(Material.COBBLESTONE), true,
                false)) {
                if (!hasFreeSlot(menu)) {
                    return null;
                }

                ItemStack dust = oreWasher.getRandomDust();
                MachineRecipe recipe = new MachineRecipe(4 / getSpeed(), new ItemStack[0], new ItemStack[] {dust});

                if (menu.fits(recipe.getOutput()[0], getOutputSlots())) {
                    menu.consumeItem(slot);
                    return recipe;
                }
            }
        }

        return null;
    }

    private boolean hasFreeSlot(BlockMenu menu) {
        for (int slot : getOutputSlots()) {
            ItemStack item = menu.getItemInSlot(slot);

            if (item == null || item.getType() == Material.AIR) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.CAULDRON);
    }

    @Override
    public String getMachineIdentifier() {
        return "DUST_FABRICATOR";
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }

    @Override
    public int getSpeed() {
        return 10;
    }

}

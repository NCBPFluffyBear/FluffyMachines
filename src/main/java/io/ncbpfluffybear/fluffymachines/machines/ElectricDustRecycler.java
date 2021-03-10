package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ElectricDustRecycler extends AContainer implements RecipeDisplayItem {

    public static final int ENERGY_CONSUMPTION = 32;
    public static final int CAPACITY = ENERGY_CONSUMPTION * 3;

    public ElectricDustRecycler(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), getOutputSlots());
                inv.dropItems(b.getLocation(), getInputSlots());
            }

            return true;
        });

    }

    @Override
    protected void registerDefaultRecipes() {
        for (SlimefunItemStack dust : Constants.dusts) {
            registerRecipe(1, new CustomItem(dust, 8), SlimefunItems.SIFTED_ORE);
        }
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>(recipes.size() * 2);

        for (MachineRecipe recipe : recipes) {
            displayRecipes.add(recipe.getInput()[0]);
            displayRecipes.add(recipe.getOutput()[recipe.getOutput().length - 1]);
        }

        return displayRecipes;
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.PISTON);
    }

    @Override
    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public int getSpeed() {
        return 1;
    }

    @Nonnull
    @Override
    public String getInventoryTitle() {
        return "&f電動粉回收機";
    }

    @Nonnull
    @Override
    public String getMachineIdentifier() {
        return "ELECTRIC_DUST_RECYCLER";
    }
}

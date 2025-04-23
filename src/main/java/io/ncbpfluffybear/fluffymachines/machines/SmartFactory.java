package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A machine that constructs ingots
 * out of its raw materials
 *
 * @author NCBPFluffyBear
 */
public class SmartFactory extends SlimefunItem implements EnergyNetComponent, RecipeDisplayItem {

    private static final int[] BORDER = new int[]{5, 6, 7, 8, 41, 42, 44, 50, 51, 52, 53};
    private static final int[] BORDER_IN = new int[]{0, 1, 2, 3, 4, 9, 13, 18, 22, 27, 31, 36, 40, 45, 46, 47, 48, 49};
    private static final int[] BORDER_OUT = new int[]{14, 15, 16, 17, 23, 26, 32, 33, 34, 35};
    private static final int[] COAL_SLOTS = new int[]{10, 11, 12};
    private static final int[] MISC_SLOTS = new int[]{19, 20, 21, 28, 29, 30, 37, 38, 39};
    private static final int[] INPUT_SLOTS = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39};
    private static final int PROGRESS_SLOT = 42;
    public static final int RECIPE_SLOT = 43;
    private static final ItemStack PROGRESS_ITEM = CustomItemStack.create(Material.FLINT_AND_STEEL, "&aProgress");

    private static final Map<BlockPosition, Integer> progress = new HashMap<>();
    private static final int PROCESS_TIME_TICKS = 10; // "Number of seconds", except 1 Slimefun "second" = 1.6 IRL seconds

    private static final List<SlimefunItem> ACCEPTED_ITEMS = new ArrayList<>(Arrays.asList(
            SlimefunItems.BILLON_INGOT.getItem(), SlimefunItems.SOLDER_INGOT.getItem(), SlimefunItems.NICKEL_INGOT.getItem(),
            SlimefunItems.COBALT_INGOT.getItem(), SlimefunItems.DURALUMIN_INGOT.getItem(), SlimefunItems.BRONZE_INGOT.getItem(),
            SlimefunItems.BRASS_INGOT.getItem(), SlimefunItems.ALUMINUM_BRASS_INGOT.getItem(), SlimefunItems.STEEL_INGOT.getItem(),
            SlimefunItems.DAMASCUS_STEEL_INGOT.getItem(), SlimefunItems.ALUMINUM_BRONZE_INGOT.getItem(),
            SlimefunItems.CORINTHIAN_BRONZE_INGOT.getItem(), SlimefunItems.GILDED_IRON.getItem(), SlimefunItems.REDSTONE_ALLOY.getItem(),
            SlimefunItems.HARDENED_METAL_INGOT.getItem(), SlimefunItems.REINFORCED_ALLOY_INGOT.getItem(), SlimefunItems.FERROSILICON.getItem(),
            SlimefunItems.ELECTRO_MAGNET.getItem(), SlimefunItems.ELECTRIC_MOTOR.getItem(), SlimefunItems.HEATING_COIL.getItem(),
            SlimefunItems.SYNTHETIC_EMERALD.getItem(), SlimefunItems.GOLD_4K.getItem(), SlimefunItems.GOLD_6K.getItem(), SlimefunItems.GOLD_8K.getItem(),
            SlimefunItems.GOLD_10K.getItem(), SlimefunItems.GOLD_12K.getItem(), SlimefunItems.GOLD_14K.getItem(), SlimefunItems.GOLD_16K.getItem(),
            SlimefunItems.GOLD_18K.getItem(), SlimefunItems.GOLD_20K.getItem(), SlimefunItems.GOLD_22K.getItem(), SlimefunItems.GOLD_24K.getItem()
    ));

    // Hidden recipes for items that can use magma blocks instead of netherrack. Must exist in ACCEPTED_ITEMS as well
    private static final List<SlimefunItemStack> MAGMA_ALTERNATIVES = new ArrayList<>(Arrays.asList(
            SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.HEATING_COIL
    ));

    private final Map<SlimefunItem, List<ItemStack[]>> ITEM_RECIPES = new HashMap<>(); // Items may have multiple recipe variations

    public SmartFactory(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        for (SlimefunItem sfItem : ACCEPTED_ITEMS) {
            List<ItemStack[]> variationList = new ArrayList<>();
            variationList.add(collectRawRecipe(sfItem));
            ITEM_RECIPES.put(sfItem, variationList);
        }

        for (SlimefunItemStack alternative : MAGMA_ALTERNATIVES) {
            ItemStack[] original = ITEM_RECIPES.get(alternative.getItem()).get(0);
            ItemStack[] variation = new ItemStack[original.length];
            for (int i = 0; i < original.length; i++) {
                if (original[i].getType() == Material.NETHERRACK && original[i].getAmount() % 16 == 0) {
                    variation[i] = new ItemStack(Material.MAGMA_BLOCK, original[i].getAmount() / 16);
                } else {
                    variation[i] = original[i].clone();
                }
            }

            List<ItemStack[]> variations = ITEM_RECIPES.get(alternative.getItem());
            variations.add(variation);
        }

        buildPreset();
        addItemHandler(onBreak());
    }

    private void buildPreset() {
        new BlockMenuPreset(this.getId(), "&cSmart Factory") {
            @Override
            public void init() {
                ChestMenuUtils.drawBackground(this, BORDER);
                Utils.createBorder(this, ChestMenuUtils.getInputSlotTexture(), BORDER_IN);
                Utils.createBorder(this, ChestMenuUtils.getOutputSlotTexture(), BORDER_OUT);
                this.addItem(PROGRESS_SLOT, PROGRESS_ITEM);
                this.addItem(9, CustomItemStack.create(Material.BLACK_STAINED_GLASS_PANE, "&7Coal Slots",
                        "&eThis row is reserved for coal for cargo."
                ));
                this.addItem(18, CustomItemStack.create(Material.YELLOW_STAINED_GLASS_PANE, "&bMisc Slots",
                        "&eThe remaining rows accept any item.", "&eCargo will fill stacks after",
                        "&eat least one of each recipe", "&erequirement has been inserted.",
                        "&eNeed to keep one more item in", "&eeach stack as template."
                ));
                this.addMenuClickHandler(RECIPE_SLOT, ChestMenuUtils.getEmptyClickHandler());
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return Utils.canOpen(b, p);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                SlimefunItem recipe = SlimefunItem.getByItem(menu.getItemInSlot(RECIPE_SLOT));

                if (recipe == null) {
                    menu.replaceExistingItem(RECIPE_SLOT, CustomItemStack.create(Material.BARRIER, "&bRecipe",
                            "&cSneak and Right Click the", "&cfactory with a supported resource", "&cto set the recipe"
                    ));
                } else {
                    menu.replaceExistingItem(RECIPE_SLOT, getDisplayItem(recipe, getDisplayRecipes()));
                }
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            // Only allow finishing stack
            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return getOutputSlots();
                }

                if (item.getType() == Material.COAL) {
                    return COAL_SLOTS;
                }

                List<Integer> slots = new ArrayList<>();
                for (int slot : MISC_SLOTS) {
                    if (menu.getItemInSlot(slot) != null) {
                        slots.add(slot);
                    }
                }

                slots.sort(compareSlots(menu));

                int[] array = new int[slots.size()];

                for (int i = 0; i < slots.size(); i++) {
                    array[i] = slots.get(i);
                }

                return array;
            }
        };
    }

    private BlockBreakHandler onBreak() {
        return new SimpleBlockBreakHandler() {

            @Override
            public void onBlockBreak(@Nonnull Block b) {
                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {
                    inv.dropItems(b.getLocation(), getInputSlots());
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }

                progress.remove(new BlockPosition(b.getWorld(), b.getX(), b.getY(), b.getZ()));
            }

        };
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                SmartFactory.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block b) {

        // Check if power is sufficient
        if (getCharge(b.getLocation()) < getEnergyConsumption()) {
            return;
        }

        BlockMenu inv = BlockStorage.getInventory(b);
        final BlockPosition pos = new BlockPosition(b.getWorld(), b.getX(), b.getY(), b.getZ()); // Used to log progress since we have progress bar
        int currentProgress = progress.getOrDefault(pos, 0); // Get current progress from map

        // Check if we are ready to send the output
        HashMap<Integer, Integer> ingredients = getIngredientSlots(inv);
        if (ingredients == null) {
            resetProgress(pos, inv);
            return;
        }

        // Countdown progress
        if (currentProgress < PROCESS_TIME_TICKS) {
            progress.put(pos, ++currentProgress);

            ChestMenuUtils.updateProgressbar(inv, PROGRESS_SLOT, PROCESS_TIME_TICKS - currentProgress,
                    PROCESS_TIME_TICKS, PROGRESS_ITEM);

            removeCharge(b.getLocation(), getEnergyConsumption());
            return;
        }

        craft(b);

        // Consume items
        for (Map.Entry<Integer, Integer> ingredient : ingredients.entrySet()) {
            inv.consumeItem(ingredient.getKey(), ingredient.getValue());
        }

        resetProgress(pos, inv);
    }

    private HashMap<Integer, Integer> getIngredientSlots(BlockMenu inv) {
        SlimefunItem key = SlimefunItem.getByItem(inv.getItemInSlot(RECIPE_SLOT));
        if (key == null) {
            return null;
        }

        if (!inv.fits(key.getItem(), getOutputSlots())) {
            return null;
        }

        HashMap<Integer, Integer> ingredientSlots = new HashMap<>();

        for (ItemStack[] variation : ITEM_RECIPES.get(key)) {
            boolean validVariation = true;
            for (ItemStack recipeItem : variation) {
                boolean exists = false;

                for (int slot : getInputSlots()) {
                    ItemStack slotItem = inv.getItemInSlot(slot);

                    // Match item and amount
                    if (slotItem != null && SlimefunUtils.isItemSimilar(recipeItem, slotItem, true, false)) {
                        if (recipeItem.getType() == Material.COAL) {
                            if (slotItem.getAmount() < recipeItem.getAmount()) {
                                continue; // Don't leave 1 for coal
                            }
                        } else if (slotItem.getAmount() < recipeItem.getAmount() + 1) {
                            continue; // Make sure misc items have 1 item left
                        }

                        exists = true;
                        ingredientSlots.put(slot, recipeItem.getAmount()); // Save slots and amounts of ingredients
                        break;
                    }
                }

                if (!exists) {
                    ingredientSlots.clear();
                    validVariation = false;
                    break; // Next variation
                }
            }

            if (validVariation) return ingredientSlots;
        }

        return null;
    }

    private void craft(Block b) {
        SlimefunItem key = SlimefunItem.getByItem(BlockStorage.getInventory(b).getItemInSlot(RECIPE_SLOT));
        BlockStorage.getInventory(b).pushItem(key.getItem(), getOutputSlots());
    }

    private void resetProgress(BlockPosition pos, BlockMenu inv) {
        // Reset progress
        progress.put(pos, 0);
        ChestMenuUtils.updateProgressbar(inv, PROGRESS_SLOT, PROCESS_TIME_TICKS,
                PROCESS_TIME_TICKS, PROGRESS_ITEM);
    }

    private ItemStack[] collectRawRecipe(SlimefunItem key) {
        Pair<HashMap<Material, Integer>, HashMap<SlimefunItem, Integer>> ingredients = reduceRecipe(key);
        ItemStack[] rawRecipe = new ItemStack[ingredients.getFirstValue().size() + ingredients.getSecondValue().size()];

        int index = 0;

        for (Map.Entry<Material, Integer> materialEntry : ingredients.getFirstValue().entrySet()) {
            rawRecipe[index] = new ItemStack(materialEntry.getKey(), materialEntry.getValue());
            index++;
        }

        for (Map.Entry<SlimefunItem, Integer> sfItemEntry : ingredients.getSecondValue().entrySet()) {
            rawRecipe[index] = CustomItemStack.create(sfItemEntry.getKey().getItem(), sfItemEntry.getValue());
            index++;
        }

        return rawRecipe;
    }

    /**
     * Calculates required materials for an item
     * Important: please manually test each added recipe
     */
    private Pair<HashMap<Material, Integer>, HashMap<SlimefunItem, Integer>> reduceRecipe(SlimefunItem key) {
        HashMap<Material, Integer> rawVanilla = new HashMap<>();
        HashMap<SlimefunItem, Integer> rawSlimefun = new HashMap<>();

        ItemStack[] recipe = key.getRecipe();
        if (key == SlimefunItems.COPPER_WIRE.getItem()) {
            recipe = new ItemStack[] {SlimefunItems.COPPER_DUST.item()};
        }

        for (ItemStack item : recipe) {
            if (item == null) {
                continue;
            }
            if (!isReduced(item)) {
                // Recursively add reduced recipe until all vanilla
                Pair<HashMap<Material, Integer>, HashMap<SlimefunItem, Integer>> reduced = reduceRecipe(SlimefunItem.getByItem(item));
                reduced.getFirstValue().forEach((recipeItem, amt) -> {
                    rawVanilla.put(recipeItem, rawVanilla.getOrDefault(recipeItem, 0) + amt * item.getAmount());
                });

                reduced.getSecondValue().forEach((recipeItem, amt) -> {
                    rawSlimefun.put(recipeItem, rawSlimefun.getOrDefault(recipeItem, 0) + amt * item.getAmount());
                });
            } else {
                if (SlimefunItem.getByItem(item) != null) {
                    rawSlimefun.put(SlimefunItem.getByItem(item), rawSlimefun.getOrDefault(SlimefunItem.getByItem(item), 0) + item.getAmount());
                } else {
                    // Replace some vanilla items
                    switch (item.getType()) {
                        case IRON_INGOT:
                            rawSlimefun.put(SlimefunItems.IRON_DUST.getItem(), rawSlimefun.getOrDefault(SlimefunItems.IRON_DUST.getItem(), 0) + item.getAmount());
                            break;
                        case QUARTZ_BLOCK:
                            rawVanilla.put(Material.QUARTZ, rawVanilla.getOrDefault(Material.QUARTZ, 0) + item.getAmount() * 4);
                            break;
                        case REDSTONE_BLOCK:
                            rawVanilla.put(Material.REDSTONE, rawVanilla.getOrDefault(Material.REDSTONE, 0) + item.getAmount() * 9);
                            break;
                        default:
                            rawVanilla.put(item.getType(), rawVanilla.getOrDefault(item.getType(), 0) + item.getAmount());
                    }
                }
            }
        }

        return new Pair<>(rawVanilla, rawSlimefun);
    }

    /**
     * Checks if the item is a vanilla item or a dust
     */
    private boolean isReduced(ItemStack test) {
        SlimefunItem sfTest = SlimefunItem.getByItem(test);

        return sfTest == null || sfTest.getId().endsWith("_DUST");
    }

    /**
     * Adds the selection instructions onto display recipe
     */
    public static ItemStack getDisplayItem(SlimefunItem key, List<ItemStack> displayRecipes) {
        ItemStack item = displayRecipes.get(ACCEPTED_ITEMS.indexOf(key)).clone(); // Get item with ingredients
        ItemMeta displayMeta = item.getItemMeta();

        List<String> lore = displayMeta.getLore();
        lore.add("");
        lore.add(Utils.color("&eSneak and Right Click the factory with a"));
        lore.add(Utils.color("&ecompatible resource to change the recipe"));

        displayMeta.setLore(lore);
        item.setItemMeta(displayMeta);

        return item;
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> recipes = new ArrayList<>();

        for (SlimefunItem sfStack : ACCEPTED_ITEMS) {
            ItemStack display = sfStack.getItem();
            ItemMeta displayMeta = display.getItemMeta();

            List<String> lore = new ArrayList<>();
            // Display the first variation of the recipe to avoid clutter
            for (ItemStack item : ITEM_RECIPES.get(sfStack).get(0)) {
                lore.add(Utils.color("&e" + item.getAmount() + "x " + Utils.getViewableName(item)));
            }

            if (ITEM_RECIPES.get(sfStack).size() > 1) {
                lore.add("");
                lore.add(Utils.color("&7This recipe has a Magma Block alternative"));
            }

            displayMeta.setLore(lore);
            display.setItemMeta(displayMeta);
            recipes.add(display);

        }

        return recipes;
    }

    private Comparator<Integer> compareSlots(DirtyChestMenu menu) {
        return Comparator.comparingInt(slot -> menu.getItemInSlot(slot).getAmount());
    }

    public static int getEnergyConsumption() {
        return 1024;
    }

    public static int getEnergyCapacity() {
        return getEnergyConsumption() * 3;
    }

    private int[] getInputSlots() {
        return INPUT_SLOTS;
    }

    private int[] getOutputSlots() {
        return new int[]{24, 25};
    }

    public static List<SlimefunItem> getAcceptedItems() {
        return ACCEPTED_ITEMS;
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return getEnergyCapacity();
    }
}

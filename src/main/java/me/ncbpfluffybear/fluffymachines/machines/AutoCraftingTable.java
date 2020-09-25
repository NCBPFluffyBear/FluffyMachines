package me.ncbpfluffybear.fluffymachines.machines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.AdvancedMenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

/**
 *
 * This {@link SlimefunItem} automatically
 * crafts vanilla recipes
 *
 * @author NCBPFluffyBear
 *
 */
public class AutoCraftingTable extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    private final int[] border = { 0, 1, 3, 5, 13, 14, 50, 51, 52, 53 };
    private final int[] inputBorder = { 9, 10, 11, 12, 13, 18, 22, 27, 31, 36, 40, 45, 46, 47, 48, 49 };
    private final int[] outputBorder = { 32, 33, 34, 35, 41, 44, 50, 51, 52, 53 };
    private final int[] keyBorder = { 6, 7, 8, 15, 17, 24, 25, 26 };
    private final int keySlot = 16;
    private final int statusSlot = 23;
    public static final int ENERGY_CONSUMPTION = 256;
    public static final int CAPACITY = 2048;

    public AutoCraftingTable(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getID(), "&6Automated Crafting Table") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "enabled") == null || BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals(String.valueOf(false))) {
                    menu.replaceExistingItem(4, new CustomItem(Material.GUNPOWDER, "&7Enabled: &4\u2718", "", "&e> Click to enable this Machine"));
                    menu.addMenuClickHandler(4, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(true));
                        newInstance(menu, b);
                        return false;
                    });
                }
                else {
                    menu.replaceExistingItem(4, new CustomItem(Material.REDSTONE, "&7Enabled: &2\u2714", "", "&e> Click to disable this Machine"));
                    menu.addMenuClickHandler(4, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(false));
                        newInstance(menu, b);
                        return false;
                    });
                }
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return p.hasPermission("slimefun.inventory.bypass") || SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.ACCESS_INVENTORIES);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return getOutputSlots();
                }

                List<Integer> slots = new ArrayList<>();
                for (int slot : getInputSlots()) {
                    if (menu.getItemInSlot(slot) != null) {
                        slots.add(slot);
                    }
                }

                Collections.sort(slots, compareSlots(menu));

                int[] array = new int[slots.size()];

                for (int i = 0; i < slots.size(); i++) {
                    array[i] = slots.get(i);
                }

                return array;
            }
        };

        addItemHandler(onPlace());
        registerBlockHandler(getID(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), getInputSlots());
                inv.dropItems(b.getLocation(), getOutputSlots());
                inv.dropItems(b.getLocation(), keySlot);
            }

            return true;
        });
    }

    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(true) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "enabled", String.valueOf(false));
            }

            @Override
            public void onBlockPlacerPlace(BlockPlacerPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "enabled", String.valueOf(false));
            }
        };
    }

    private Comparator<Integer> compareSlots(DirtyChestMenu menu) {
        return Comparator.comparingInt(slot -> menu.getItemInSlot(slot).getAmount());
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : inputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : outputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : keyBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), "&e&lKey Item Slot"), (p, slot, item, action) -> false);
        }

        preset.addItem(statusSlot, new CustomItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), "&e&lIdle"), (p, slot, item, action) -> false);

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new AdvancedMenuClickHandler() {

                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return cursor == null || cursor.getType() == null || cursor.getType() == Material.AIR;
                }
            });
        }

        preset.addItem(2, new CustomItem(new ItemStack(Material.CRAFTING_TABLE), "&eRecipe", "", "&bPut in the Recipe you want to craft", "&ePut in the item you want crafted", "&4Vanilla Crafting Table Recipes ONLY"), (p, slot, item, action) -> false);
    }

    @Override
    public int[] getInputSlots() {
        return new int[] { 19, 20, 21, 28, 29, 30, 37, 38, 39 };
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] { 42, 43 };
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                AutoCraftingTable.this.tick(b, false);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block block, boolean craftLast) {
        if (!craftLast && BlockStorage.getLocationInfo(block.getLocation(), "enabled").equals(String.valueOf(false))) {
            return;
        }

        if (getCharge(block.getLocation()) < getEnergyConsumption()) {
            BlockMenu menu = BlockStorage.getInventory(block);
            if (menu.hasViewer()) {
                menu.replaceExistingItem(statusSlot, new CustomItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), "&c&lNo Power"));
            }
            return;
        }

        getResult(block, craftLast);
    }

    private void getResult(Block block, boolean craftLast) {
        BlockMenu menu = BlockStorage.getInventory(block);
        ItemStack keyItem = menu.getItemInSlot(keySlot);
        List<Recipe> recipes;


        // Make sure at least 1 slot is free
        for (int outSlot : getOutputSlots()) {
            ItemStack outItem = menu.getItemInSlot(outSlot);
            if (outItem == null || outItem.getAmount() < outItem.getMaxStackSize()) {
                break;
            }
            else if (outSlot == getOutputSlots()[1]){
                if (menu.hasViewer()) {
                    menu.replaceExistingItem(statusSlot, new CustomItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), "&c&lStorage Full"));
                }
                return;
            }
        }

        // Make sure we have a key item
        if (keyItem == null) {
            if (menu.hasViewer()) {
                menu.replaceExistingItem(statusSlot, new CustomItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), "&c&lKey Item Missing"));
            }
            return;
        }

        List<Material> reqMats = new ArrayList<>();
        List<Material> existingMats = new ArrayList<>();
        List<Integer> existingMatSlots = new ArrayList<>();

        // Make a list using the input slot items
        for (int slot : getInputSlots()) {
            ItemStack slotItem = menu.getItemInSlot(slot);
            if (slotItem == null) {
                return;
            } else {
                Material existingMat = slotItem.getType();
                if (existingMat != Material.AIR) {
                    existingMats.add(existingMat);
                    existingMatSlots.add(slot);
                }
            }
        }

        if (existingMats.isEmpty()) {
            if (menu.hasViewer()) {
                menu.replaceExistingItem(statusSlot, new CustomItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), "&c&lNo Input"));
            }
            return;
        }

        // Make a list using the key item

        // This boi under is a big fat resource sucker
        recipes = Bukkit.getRecipesFor(keyItem);
        for (Recipe r : recipes) {
            if (r instanceof ShapedRecipe) {
                Map<Character, RecipeChoice> recipeMap;
                recipeMap = ((ShapedRecipe) r).getChoiceMap();

                recipeMap.forEach(((character, recipeChoice) -> {
                    if (recipeChoice != null) {
                        Material recipeMat = recipeChoice.getItemStack().getType();
                        reqMats.add(recipeMat);
                    }
                }));
                // Compare the lists and craft if equal
                if (reqMats.equals(existingMats)) {
                    existingMatSlots.forEach(menu::consumeItem);
                    ItemStack strippedKey = new ItemStack(keyItem.getType());
                    menu.pushItem(strippedKey.clone(), getOutputSlots());
                    removeCharge(block.getLocation(), getEnergyConsumption());
                    if (menu.hasViewer()) {
                        menu.replaceExistingItem(statusSlot, new CustomItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), "&a&lRunning"));
                    }
                } else if (menu.hasViewer()) {
                    menu.replaceExistingItem(statusSlot, new CustomItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), "&c&lRecipe does not match key"));
                }
                break;
            } else if (r instanceof ShapelessRecipe) {
                List<RecipeChoice> recipeChoices;
                recipeChoices = ((ShapelessRecipe) r).getChoiceList();
                recipeChoices.forEach(((recipeChoice) -> {
                    if (recipeChoice != null) {
                        Material recipeMat = recipeChoice.getItemStack().getType();
                        reqMats.add(recipeMat);
                    }
                }));
                // Compare the lists and craft if equal
                if (reqMats.equals(existingMats)) {
                    existingMatSlots.forEach(menu::consumeItem);
                    ItemStack strippedKey = new ItemStack(keyItem.getType());
                    menu.pushItem(strippedKey.clone(), getOutputSlots());
                    removeCharge(block.getLocation(), getEnergyConsumption());
                    if (menu.hasViewer()) {
                        menu.replaceExistingItem(statusSlot, new CustomItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), "&a&lRunning"));
                    }
                } else if (menu.hasViewer()) {
                    menu.replaceExistingItem(statusSlot, new CustomItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), "&c&lRecipe does not match key"));
                }
                break;
            }
        }
    }
}


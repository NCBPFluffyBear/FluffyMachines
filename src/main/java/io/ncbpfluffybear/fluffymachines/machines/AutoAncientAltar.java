package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AltarRecipe;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.BrokenSpawner;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.RepairedSpawner;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.AdvancedMenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link SlimefunItem} automatically crafts
 * Ancient Altar recipes
 *
 * @author NCBPFluffyBear
 */
public class AutoAncientAltar extends SlimefunItem implements EnergyNetComponent {

    public static final int ENERGY_CONSUMPTION = 128;
    public static final int CAPACITY = ENERGY_CONSUMPTION * 3;
    private final int[] border = {0, 1, 3, 4, 5, 7, 8, 13, 14, 15, 16, 17, 50, 51, 52, 53};
    private final int[] inputBorder = {9, 10, 11, 12, 13, 18, 22, 27, 31, 36, 40, 45, 46, 47, 48, 49};
    private final int[] outputBorder = {23, 24, 25, 26, 32, 35, 41, 42, 43, 44};
    private final int[] mockPedestalSlots = {19, 20, 21, 30, 39, 38, 37, 28};
    private final AncientAltar altarItem = (AncientAltar) SlimefunItems.ANCIENT_ALTAR.getItem();

    private final ItemStack ironBars = new ItemStack(Material.IRON_BARS);
    private final ItemStack earthRune = SlimefunItems.EARTH_RUNE.item();
    private final List<ItemStack> jarInputs = new ArrayList<>(Arrays.asList(ironBars, earthRune, ironBars, earthRune,
            ironBars, earthRune, ironBars, earthRune));

    public AutoAncientAltar(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getId(), "&5Auto Ancient Altar") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                BlockStorage.addBlockInfo(b, "craftOnce", String.valueOf(false));

                if (!BlockStorage.hasBlockInfo(b)
                        || BlockStorage.getLocationInfo(b.getLocation(), "enabled") == null
                        || BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals(String.valueOf(false))) {
                    menu.replaceExistingItem(6, CustomItemStack.create(Material.GUNPOWDER, "&7Enabled: &4\u2718", "",
                            "&e> Click to enable this Machine")
                    );
                    menu.addMenuClickHandler(6, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(true));
                        newInstance(menu, b);
                        return false;
                    });
                } else {
                    menu.replaceExistingItem(6, CustomItemStack.create(Material.REDSTONE, "&7Enabled: &2\u2714",
                            "", "&e> Click to disable this Machine")
                    );
                    menu.addMenuClickHandler(6, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(false));
                        newInstance(menu, b);
                        return false;
                    });
                }

                menu.replaceExistingItem(7, CustomItemStack.create(Material.ENCHANTING_TABLE, "&cCraft Once",
                        "", "&e> Click to craft recipe once")
                );
                menu.addMenuClickHandler(7, (p, slot, item, action) -> {
                    BlockStorage.addBlockInfo(b, "craftOnce", String.valueOf(true));
                    return false;
                });
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return p.hasPermission("slimefun.inventory.bypass")
                        || Slimefun.getProtectionManager().hasPermission(p, b.getLocation(),
                        Interaction.INTERACT_BLOCK);
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

                if (item.getType().getMaxStackSize() == 1) {
                    return getInputSlots();
                }

                List<Integer> slots = new ArrayList<>();
                for (int slot : getInputSlots()) {
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

        addItemHandler(onPlace());
        addItemHandler(onBreak());

    }

    private BlockBreakHandler onBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent e, @Nonnull ItemStack item, @Nonnull List<ItemStack> drops) {
                Block b = e.getBlock();
                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {
                    inv.dropItems(b.getLocation(), getInputSlots());
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }
            }
        };
    }

    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(true) {

            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "enabled", String.valueOf(false));
                BlockStorage.addBlockInfo(e.getBlock(), "craftOnce", String.valueOf(false));
            }

            @Override
            public void onBlockPlacerPlace(@Nonnull BlockPlacerPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "enabled", String.valueOf(false));
                BlockStorage.addBlockInfo(e.getBlock(), "craftOnce", String.valueOf(false));
            }
        };
    }

    private Comparator<Integer> compareSlots(DirtyChestMenu menu) {
        return Comparator.comparingInt(slot -> menu.getItemInSlot(slot).getAmount());
    }

    protected void constructMenu(BlockMenuPreset preset) {
        borders(preset, border, inputBorder, outputBorder);

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new AdvancedMenuClickHandler() {

                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor,
                                       ClickAction action) {
                    if (cursor == null)
                        return true;
                    return cursor.getType() == Material.AIR;
                }
            });
        }

        preset.addItem(2, CustomItemStack.create(new ItemStack(Material.ENCHANTING_TABLE), "&eRecipe",
                        "", "&bPut in the Recipe you want to craft", "&4Ancient Altar Recipes ONLY"
                ),
                (p, slot, item, action) -> false);
    }

    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }

    public int getCapacity() {
        return CAPACITY;
    }

    public int[] getInputSlots() {
        return new int[]{19, 20, 21, 28, 29, 30, 37, 38, 39};
    }

    public int[] getOutputSlots() {
        return new int[]{33, 34};
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                AutoAncientAltar.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block block) {
        String craftOnce = BlockStorage.getLocationInfo(block.getLocation(), "craftOnce");
        if (BlockStorage.getLocationInfo(block.getLocation(), "enabled").equals(String.valueOf(false))
                && craftOnce.equals("false")) {
            return;
        }

        if (getCharge(block.getLocation()) < getEnergyConsumption()) {
            return;
        }

        BlockStorage.addBlockInfo(block, "craftOnce", String.valueOf(false));
        craftIfValid(block, Boolean.parseBoolean(craftOnce));
    }

    private void craftIfValid(Block block, boolean craftOnce) {
        BlockMenu menu = BlockStorage.getInventory(block);
        List<ItemStack> pedestalItems = new ArrayList<>();

        // Make sure at least 1 slot is free
        for (int outSlot : getOutputSlots()) {
            ItemStack outItem = menu.getItemInSlot(outSlot);
            if (outItem == null || outItem.getAmount() < outItem.getMaxStackSize()) {
                break;
            } else if (outSlot == getOutputSlots()[1]) {
                return;
            }
        }

        for (int slot : getInputSlots()) {
            ItemStack slotItem = menu.getItemInSlot(slot);
            if (slotItem == null) {
                return;
            }

            Material type = slotItem.getType();

            if (!craftOnce && type.getMaxStackSize() != 1 && slotItem.getAmount() == 1) {
                return;
            }
        }

        // Check and append altar items
        for (int i = 0; i < 8; i++) {
            int slot = mockPedestalSlots[i];
            ItemStack pedestalItem = menu.getItemInSlot(slot);
            SlimefunItem sfPedestalItem = SlimefunItem.getByItem(pedestalItem);
            if (sfPedestalItem != null) {
                pedestalItems.add(sfPedestalItem.getItem());
            } else {
                pedestalItems.add(CustomItemStack.create(pedestalItem, 1));
            }
        }

        // Check and append catalyst
        int mockAltarSlot = 29;
        ItemStack catalystItem = menu.getItemInSlot(mockAltarSlot);
        SlimefunItem sfCatalyst = SlimefunItem.getByItem(catalystItem);
        ItemStack catalyst;
        if (sfCatalyst != null) {
            catalyst = sfCatalyst.getItem();
        } else if (!catalystItem.hasItemMeta()) {
            catalyst = new ItemStack(catalystItem.getType(), 1);
        } else {
            return;
        }

        if (Constants.isSoulJarsInstalled && sfCatalyst != null
                && sfCatalyst.getId().startsWith("FILLED") && sfCatalyst.getId().endsWith("SOUL_JAR")) {

            try {
                EntityType entityType = EntityType.valueOf(sfCatalyst.getId()
                        .replace("FILLED_", "")
                        .replace("_SOUL_JAR", "")
                );

                if (entityType == EntityType.UNKNOWN) {
                    return;
                }

                BrokenSpawner brokenSpawner = SlimefunItems.BROKEN_SPAWNER.getItem(BrokenSpawner.class);
                ItemStack spawnerItem = brokenSpawner.getItemForEntityType(entityType);

                if (pedestalItems.equals(jarInputs)) {
                    removeCharge(block.getLocation(), ENERGY_CONSUMPTION);
                    for (int slot : getInputSlots()) {
                        menu.consumeItem(slot);
                    }
                    menu.pushItem(spawnerItem.clone(), getOutputSlots());
                }
            } catch (IllegalArgumentException ignored) {
            }

        } else if (SlimefunUtils.isItemSimilar(catalystItem, SlimefunItems.BROKEN_SPAWNER.item(), false, false)) {

            Optional<ItemStack> result = checkRecipe(SlimefunItems.BROKEN_SPAWNER.item(), pedestalItems);
            if (result.isPresent()) {
                RepairedSpawner spawner = (RepairedSpawner) SlimefunItems.REPAIRED_SPAWNER.getItem();
                ItemStack spawnerResult = spawner.getItemForEntityType(spawner.getEntityType(catalystItem).orElse(EntityType.PIG));
                craft(block, menu, spawnerResult);
            }

        } else {

            Optional<ItemStack> result = checkRecipe(catalyst, pedestalItems);
            result.ifPresent(itemStack -> craft(block, menu, itemStack));

        }
    }

    private Optional<ItemStack> checkRecipe(ItemStack catalyst, List<ItemStack> pedestalItems) {
        // Find matching recipe
        for (AltarRecipe recipe : altarItem.getRecipes()) {

            if (SlimefunUtils.isItemSimilar(recipe.getCatalyst(), catalyst, true) && recipe.getInput().equals(pedestalItems)) {
                return Optional.of(recipe.getOutput().clone());
            }
        }

        return Optional.empty();
    }

    private void craft(Block b, BlockMenu menu, ItemStack result) {
        if (!menu.fits(result, getOutputSlots())) {
            return;
        }
        removeCharge(b.getLocation(), ENERGY_CONSUMPTION);
        for (int slot : getInputSlots()) {
            menu.consumeItem(slot);
        }
        menu.pushItem(result, getOutputSlots());
    }

    static void borders(BlockMenuPreset preset, int[] border, int[] inputBorder, int[] outputBorder) {
        for (int i : border) {
            preset.addItem(i, CustomItemStack.create(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "),
                    (p, slot, item, action) -> false);
        }

        for (int i : inputBorder) {
            preset.addItem(i, CustomItemStack.create(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), " "),
                    (p, slot, item, action) -> false);
        }

        for (int i : outputBorder) {
            preset.addItem(i, CustomItemStack.create(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "),
                    (p, slot, item, action) -> false);
        }
    }
}

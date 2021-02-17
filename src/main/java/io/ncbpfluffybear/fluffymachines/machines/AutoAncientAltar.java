package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AltarRecipe;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.AdvancedMenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This {@link SlimefunItem} automatically crafts
 * Ancient Altar recipes
 *
 * @author NCBPFluffyBear
 */
public class AutoAncientAltar extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    public static final int ENERGY_CONSUMPTION = 128;
    public static final int CAPACITY = ENERGY_CONSUMPTION * 3;
    private final int[] border = {0, 1, 3, 4, 5, 7, 8, 13, 14, 15, 16, 17, 50, 51, 52, 53};
    private final int[] inputBorder = {9, 10, 11, 12, 13, 18, 22, 27, 31, 36, 40, 45, 46, 47, 48, 49};
    private final int[] outputBorder = {23, 24, 25, 26, 32, 35, 41, 42, 43, 44};
    private final int[] mockPedestalSlots = {19, 20, 21, 30, 39, 38, 37, 28};
    private final AncientAltar altarItem = (AncientAltar) SlimefunItems.ANCIENT_ALTAR.getItem();

    private final ItemStack ironBars = new ItemStack(Material.IRON_BARS);
    private final ItemStack earthRune = new SlimefunItemStack(SlimefunItems.EARTH_RUNE.getItemId(),
        SlimefunItems.EARTH_RUNE);
    private final ItemStack enderRune = new SlimefunItemStack(SlimefunItems.ENDER_RUNE.getItemId(),
        SlimefunItems.ENDER_RUNE);
    private final ItemStack essence = new SlimefunItemStack(SlimefunItems.ESSENCE_OF_AFTERLIFE.getItemId(),
        SlimefunItems.ESSENCE_OF_AFTERLIFE);
    private final ItemStack filledFlask = SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE.clone();
    private final List<ItemStack> jarInputs = new ArrayList<>(Arrays.asList(ironBars, earthRune, ironBars, earthRune,
        ironBars, earthRune, ironBars, earthRune));
    private final List<ItemStack> repairedInputs = new ArrayList<>(Arrays.asList(enderRune, filledFlask, essence,
        filledFlask,
        enderRune, filledFlask, essence, filledFlask));

    public AutoAncientAltar(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getId(), "&5自動古代祭壇") {

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
                    menu.replaceExistingItem(6, new CustomItem(Material.GUNPOWDER, "&7已啟用: &4\u2718", "",
                        "&e> 點擊啟用此機器")
                    );
                    menu.addMenuClickHandler(6, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(true));
                        newInstance(menu, b);
                        return false;
                    });
                } else {
                    menu.replaceExistingItem(6, new CustomItem(Material.REDSTONE, "&7已啟用: &2\u2714",
                        "", "&e> 點擊禁用此機器")
                    );
                    menu.addMenuClickHandler(6, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(false));
                        newInstance(menu, b);
                        return false;
                    });
                }

                menu.replaceExistingItem(7, new CustomItem(Material.ENCHANTING_TABLE, "&c製作一次",
                    "", "&e> 點擊製作配方一次")
                );
                menu.addMenuClickHandler(7, (p, slot, item, action) -> {
                    BlockStorage.addBlockInfo(b, "craftOnce", String.valueOf(true));
                    return false;
                });
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return p.hasPermission("slimefun.inventory.bypass")
                    || SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(),
                    ProtectableAction.INTERACT_BLOCK);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                Material type = item.getType();

                if (flow == ItemTransportFlow.WITHDRAW) {
                    return getOutputSlots();
                }

                if (type == Material.WATER_BUCKET || type == Material.MILK_BUCKET || type == Material.FIRE_CHARGE) {
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
        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), getInputSlots());
                inv.dropItems(b.getLocation(), getOutputSlots());
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
                    if (cursor == null) return true;
                    cursor.getType();
                    return cursor.getType() == Material.AIR;
                }
            });
        }

        preset.addItem(2, new CustomItem(new ItemStack(Material.ENCHANTING_TABLE), "&e配方",
                "", "&b放入你要製作的配方", "&4僅限古代祭壇配方"
            ),
            (p, slot, item, action) -> false);
    }

    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }

    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public int[] getInputSlots() {
        return new int[] {19, 20, 21, 28, 29, 30, 37, 38, 39};
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] {33, 34};
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

            if (!craftOnce && type != Material.WATER_BUCKET
                && type != Material.MILK_BUCKET
                && type != Material.FIRE_CHARGE
                && slotItem.getAmount() == 1) {
                return;
            }
        }

        // Check and append altar items
        for (int i = 0; i < 8; i++) {
            int slot = mockPedestalSlots[i];
            ItemStack pedestalItem = menu.getItemInSlot(slot);
            SlimefunItem sfPedestalItem = SlimefunItem.getByItem(pedestalItem);
            if (sfPedestalItem != null) {
                SlimefunItemStack pedestalItemStack = new SlimefunItemStack(sfPedestalItem.getId(), pedestalItem);
                pedestalItems.add(new SlimefunItemStack(pedestalItemStack, 1));
            } else {
                pedestalItems.add(new CustomItem(pedestalItem, 1));
            }
        }

        // Check and append catalyst
        int mockAltarSlot = 29;
        ItemStack catalystItem = menu.getItemInSlot(mockAltarSlot);
        SlimefunItem sfCatalyst = SlimefunItem.getByItem(catalystItem);
        ItemStack catalyst;
        if (sfCatalyst != null) {
            SlimefunItemStack catalystStack = new SlimefunItemStack(sfCatalyst.getId(), catalystItem);
            catalyst = new SlimefunItemStack(catalystStack, 1);
        } else if (!catalystItem.hasItemMeta()) {
            catalyst = new ItemStack(catalystItem.getType(), 1);
        } else {
            return;
        }

        if (Constants.isSoulJarsInstalled && sfCatalyst != null
            && sfCatalyst.getId().startsWith("FILLED") && sfCatalyst.getId().endsWith("SOUL_JAR")) {

            SlimefunItem spawnerItem = SlimefunItem.getByID(sfCatalyst.getId().replace("FILLED_", "").replace(
                "_SOUL_JAR", "_BROKEN_SPAWNER"));
            if (pedestalItems.equals(jarInputs) && spawnerItem != null) {
                removeCharge(block.getLocation(), ENERGY_CONSUMPTION);
                for (int slot : getInputSlots()) {
                    menu.consumeItem(slot);
                }
                menu.pushItem(spawnerItem.getItem().clone(), getOutputSlots());
            }
        } else if (SlimefunUtils.isItemSimilar(catalystItem, SlimefunItems.BROKEN_SPAWNER, false, false)) {

            if (pedestalItems.equals(repairedInputs)) {
                removeCharge(block.getLocation(), ENERGY_CONSUMPTION);
                for (int slot : getInputSlots()) {
                    menu.consumeItem(slot);
                }
                ItemStackWrapper wrapper = new ItemStackWrapper(catalystItem);

                ItemStack spawnerItem = SlimefunItems.REPAIRED_SPAWNER.clone();
                ItemMeta im = spawnerItem.getItemMeta();
                im.setLore(Arrays.asList(wrapper.getItemMeta().getLore().get(0)));
                spawnerItem.setItemMeta(im);

                menu.pushItem(spawnerItem.clone(), getOutputSlots());
            }
        } else {

            // Find matching recipe
            for (AltarRecipe recipe : altarItem.getRecipes()) {

                if (SlimefunUtils.isItemSimilar(recipe.getCatalyst(), catalyst, true) && recipe.getInput().equals(pedestalItems)) {
                    removeCharge(block.getLocation(), ENERGY_CONSUMPTION);
                    for (int slot : getInputSlots()) {
                        menu.consumeItem(slot);
                    }
                    menu.pushItem(recipe.getOutput().clone(), getOutputSlots());
                    break;
                }
            }
        }
    }

    static void borders(BlockMenuPreset preset, int[] border, int[] inputBorder, int[] outputBorder) {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "),
                (p, slot, item, action) -> false);
        }

        for (int i : inputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), " "),
                (p, slot, item, action) -> false);
        }

        for (int i : outputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "),
                (p, slot, item, action) -> false);
        }
    }
}

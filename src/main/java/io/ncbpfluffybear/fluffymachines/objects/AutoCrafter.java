package io.ncbpfluffybear.fluffymachines.objects;

import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoCrafter extends SlimefunItem implements EnergyNetComponent {

    public static final int ENERGY_CONSUMPTION = 128;
    public static final int CAPACITY = ENERGY_CONSUMPTION * 3;
    private final int[] border = {0, 1, 3, 4, 5, 7, 8, 13, 14, 15, 16, 17, 50, 51, 52, 53};
    private final int[] inputBorder = {9, 10, 11, 12, 13, 18, 22, 27, 31, 36, 40, 45, 46, 47, 48, 49};
    private final int[] outputBorder = {23, 24, 25, 26, 32, 35, 41, 42, 43, 44};
    private final int[] inputSlots = {19, 20, 21, 28, 29, 30, 37, 38, 39};
    private final int[] outputSlots = {33, 34};
    private final String machineName;
    private final Material material;
    private final MultiBlockMachine mblock;

    public AutoCrafter(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, String displayName, Material material, String machineName, RecipeType machineRecipes) {
        super(category, item, recipeType, recipe);

        this.machineName = machineName;
        this.material = material;
        this.mblock = (MultiBlockMachine) machineRecipes.getMachine();

        constructMenu(displayName);
        addItemHandler(onPlace());
        addItemHandler(onBreak());
    }

    private void constructMenu(String displayName) {
        new BlockMenuPreset(getId(), displayName) {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
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
                return getCustomItemTransport(menu, flow, item);
            }
        };
    }

    protected int[] getCustomItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
        if (flow == ItemTransportFlow.WITHDRAW) {
            return getOutputSlots();
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

    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(true) {

            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "enabled", String.valueOf(false));
            }

            @Override
            public void onBlockPlacerPlace(@Nonnull BlockPlacerPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "enabled", String.valueOf(false));
            }
        };
    }

    private BlockBreakHandler onBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent e, @Nonnull ItemStack i, @Nonnull List<ItemStack> list) {
                Block b = e.getBlock();
                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {
                    inv.dropItems(b.getLocation(), getInputSlots());
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }

            }
        };
    }

    protected Comparator<Integer> compareSlots(DirtyChestMenu menu) {
        return Comparator.comparingInt(slot -> menu.getItemInSlot(slot).getAmount());
    }

    protected void constructMenu(BlockMenuPreset preset) {
        borders(preset, border, inputBorder, outputBorder);

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new ChestMenu.AdvancedMenuClickHandler() {

                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor,
                                       ClickAction action) {
                    if (cursor == null) return true;
                    return cursor.getType() == Material.AIR;
                }
            });
        }

        preset.addItem(2, CustomItemStack.create(new ItemStack(material), "&eRecipe",
                "", "&bPut in the Recipe you want to craft", machineName + " Recipes ONLY"
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
        return inputSlots;
    }

    public int[] getOutputSlots() {
        return outputSlots;
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
                AutoCrafter.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block block) {
        if (BlockStorage.getLocationInfo(block.getLocation(), "enabled").equals(String.valueOf(false))) {
            return;
        }

        if (getCharge(block.getLocation()) < getEnergyConsumption()) {
            return;
        }

        craftIfValid(block);
    }

    private void craftIfValid(Block block) {
        BlockMenu menu = BlockStorage.getInventory(block);

        // Make sure at least 1 slot is free
        for (int outSlot : getOutputSlots()) {
            ItemStack outItem = menu.getItemInSlot(outSlot);
            if (outItem == null || outItem.getAmount() < outItem.getMaxStackSize()) {
                break;
            } else if (outSlot == getOutputSlots()[1]) {
                return;
            }
        }

        // Find matching recipe
        for (ItemStack[] input : RecipeType.getRecipeInputList(mblock)) {
            if (isCraftable(menu, input)) {
                ItemStack output = RecipeType.getRecipeOutputList(mblock, input).clone();
                if (!menu.fits(output, getOutputSlots())) {
                    return;
                }
                craft(output, menu);
                removeCharge(block.getLocation(), getEnergyConsumption());
                return;
            }
        }
        // we're only executing the last possible shaped recipe
        // we don't want to allow this to be pressed instead of the default timer-based
        // execution to prevent abuse and auto clickers
    }

    private boolean isCraftable(BlockMenu inv, ItemStack[] recipe) {
        for (int j = 0; j < 9; j++) {
            ItemStack item = inv.getItemInSlot(getInputSlots()[j]);
            if ((item != null && item.getAmount() == 1 && item.getType().getMaxStackSize() != 1)
                || !SlimefunUtils.isItemSimilar(inv.getItemInSlot(getInputSlots()[j]), recipe[j], true)) {
                return false;
            }
        }

        return true;
    }

    private void craft(ItemStack output, BlockMenu inv) {
        for (int j = 0; j < 9; j++) {
            ItemStack item = inv.getItemInSlot(getInputSlots()[j]);

            if (item != null && item.getType() != Material.AIR) {
                inv.consumeItem(getInputSlots()[j]);
            }
        }

        inv.pushItem(output, outputSlots);
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


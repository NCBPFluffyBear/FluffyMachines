package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.TableSaw;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.materials.MaterialConverter;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This {@link SlimefunItem} is an electric version
 * of the {@link TableSaw}
 *
 * @author NCBPFluffyBear
 */
public class AutoTableSaw extends SlimefunItem implements EnergyNetComponent {

    public static final int ENERGY_CONSUMPTION = 128;
    public static final int CAPACITY = ENERGY_CONSUMPTION * 3;
    private final int[] border = {0, 1, 3, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 22, 27, 31, 36, 40, 45,
        46, 47, 48, 49, 50, 51, 52, 53};
    private final int[] inputBorder = {19, 20, 21, 28, 30, 37, 38, 39,};
    private final int[] outputBorder = {23, 24, 25, 26, 32, 35, 41, 42, 43, 44};
    private final int[] inputSlots = {29};
    private final int[] outputSlots = {33, 34};

    private final Map<ItemStack, ItemStack> tableSawRecipes = new HashMap<>();


    public AutoTableSaw(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        for (Material log : Tag.LOGS.getValues()) {
            Optional<Material> planks = MaterialConverter.getPlanksFromLog(log);

            if (planks.isPresent()) {
                tableSawRecipes.put(new ItemStack(log), new ItemStack(planks.get(), 8));
            }
        }

        for (Material plank : Tag.PLANKS.getValues()) {
            tableSawRecipes.put(new ItemStack(plank), new ItemStack(Material.STICK, 4));
        }

        new BlockMenuPreset(getId(), "&6自動鋸木機") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                if (!BlockStorage.hasBlockInfo(b)
                    || BlockStorage.getLocationInfo(b.getLocation(), "enabled") == null
                    || BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals(String.valueOf(false))) {
                    menu.replaceExistingItem(6, new CustomItem(Material.GUNPOWDER, "&7已啟用: &4\u2718",
                        "", "&e> 點擊啟用此機器")
                    );
                    menu.addMenuClickHandler(6, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(true));
                        newInstance(menu, b);
                        return false;
                    });
                } else {
                    menu.replaceExistingItem(6, new CustomItem(Material.REDSTONE, "&7已啟用: &2\u2714",
                        "", "&e> 點擊禁用此機器"));
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
                    || SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(),
                    ProtectableAction.INTERACT_BLOCK
                );
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return outputSlots;
                } else {
                    return inputSlots;
                }
            }
        };

        addItemHandler(onPlace());
        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);
            Location location = b.getLocation();

            if (inv != null) {
                inv.dropItems(location, inputSlots);
                inv.dropItems(location, outputSlots);
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

    protected void constructMenu(BlockMenuPreset preset) {

        borders(preset, border, inputBorder, outputBorder);
        preset.addItem(2, new CustomItem(new ItemStack(Material.STONECUTTER), "&e配方", "",
                "&b放入你要製作的配方",
                "&4僅限鋸木機配方"
            ),
            ChestMenuUtils.getEmptyClickHandler());
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

    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                AutoTableSaw.this.tick(b);
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

        BlockMenu menu = BlockStorage.getInventory(block);
        tableSawRecipes.forEach((input, output) -> {
            if (menu.getItemInSlot(inputSlots[0]) != null
                && SlimefunUtils.isItemSimilar(menu.getItemInSlot(inputSlots[0]), input, true, false)
                && menu.fits(output, outputSlots)) {

                menu.consumeItem(inputSlots[0]);
                menu.pushItem(output.clone(), outputSlots);
            }
        });
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


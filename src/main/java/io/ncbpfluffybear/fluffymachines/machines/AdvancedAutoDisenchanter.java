package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import java.util.ArrayList;
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
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class AdvancedAutoDisenchanter extends SlimefunItem implements EnergyNetComponent {

    private static final int[] BACKGROUND = {0, 1, 2, 3, 4, 5, 6, 7, 8, 12, 14, 21, 22, 23, 36, 37, 38, 42, 43, 44, 45, 46, 47, 51, 52, 53};
    private static final int[] INPUT_BORDER = {9, 10, 11, 18, 20, 27, 28, 29};
    private static final int[] OUTPUT_BORDER = {21, 22, 23, 30, 32, 39, 41, 48, 49, 50};
    private static final int[] BOOK_BORDER = {15, 16, 17, 24, 26, 33, 34, 35};
    private static final int ITEM_SLOT = 19;
    private static final int BOOK_SLOT = 25;
    private static final int[] OUTPUT_SLOTS = {31, 40};

    private static final int SELECTION_SLOT = 4;
    private static final int PROGRESS_SLOT = 13;

    public static final int ENERGY_CONSUMPTION = 1024;
    public static final int CAPACITY = 4096;
    private static final int PROCESS_TIME_TICKS = 60; // "Number of seconds", except 1 Slimefun "second" = 1.6 IRL seconds

    private final ItemSetting<Boolean> useLevelLimit = new ItemSetting<>(this, "use-enchant-level-limit", false);
    private final IntRangeSetting levelLimit = new IntRangeSetting(this, "enchant-level-limit", 0, 10, Short.MAX_VALUE);
    private static final Map<BlockPosition, Integer> progress = new HashMap<>();

    private static final ItemStack DEFAULT_SELECTION_ITEM = new CustomItemStack(Material.ENCHANTED_BOOK,
            "&5選取附魔", "", "&e> 點擊來重新掃描輸入欄 <");

    private static final ItemStack PROGRESS_ITEM = new CustomItemStack(Material.EXPERIENCE_BOTTLE, "&a運作");

    public AdvancedAutoDisenchanter(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(onBreak());
        addItemSetting(useLevelLimit, levelLimit);

        new BlockMenuPreset(getId(), "&c高級自動退魔器") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                menu.replaceExistingItem(SELECTION_SLOT, DEFAULT_SELECTION_ITEM.clone()); // Reset selection item

                menu.addMenuClickHandler(SELECTION_SLOT, (p, slot, item, action) -> {
                    cycleEnchants(menu, b);
                    return false;
                });

                menu.addMenuClickHandler(ITEM_SLOT, (p, slot, item, action) -> {
                    menu.replaceExistingItem(SELECTION_SLOT, DEFAULT_SELECTION_ITEM.clone()); // Reset selection item
                    setSelectedIndex(b, -2); // Reset to None
                    return true;
                });

                // Set selection to none, we can reset this every instance (server boot)
                setSelectedIndex(b, -2);

            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return (p.hasPermission("slimefun.inventory.bypass")
                        || Slimefun.getProtectionManager().hasPermission(
                        p, b.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.INSERT) {
                    if (item.getType() == Material.BOOK) {
                        return new int[]{BOOK_SLOT};
                    } else {
                        return new int[]{ITEM_SLOT};
                    }
                } else if (flow == ItemTransportFlow.WITHDRAW) {
                    return OUTPUT_SLOTS;
                } else {
                    return new int[0];
                }
            }
        };
    }

    private BlockBreakHandler onBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent e, @Nonnull ItemStack item, @Nonnull List<ItemStack> drops) {
                Block b = e.getBlock();
                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {
                    inv.dropItems(b.getLocation(), ITEM_SLOT);
                    inv.dropItems(b.getLocation(), BOOK_SLOT);
                    inv.dropItems(b.getLocation(), OUTPUT_SLOTS);
                }
            }
        };
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                AdvancedAutoDisenchanter.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block b) {

        // Check if power is sufficient
        if (getCharge(b.getLocation()) < ENERGY_CONSUMPTION) {
            return;
        }

        BlockMenu inv = BlockStorage.getInventory(b);
        final BlockPosition pos = new BlockPosition(b.getWorld(), b.getX(), b.getY(), b.getZ()); // Used to log progress since we have progress bar
        int currentProgress = progress.getOrDefault(pos, 0); // Get current progress from map
        int selectedEnchant = getSelectedIndex(b.getLocation()); // Picked enchant to remove

        // No disenchant selected
        if (selectedEnchant < 0) {
            return;
        }

        // make sure both outputs are empty
        for (int slot : OUTPUT_SLOTS) {
            if (inv.getItemInSlot(slot) != null) {
                return;
            }
        }

        ItemStack input = inv.getItemInSlot(ITEM_SLOT);

        // Validate input
        SlimefunItem sfItem = SlimefunItem.getByItem(input);
        if (input == null || input.getEnchantments().isEmpty()
                || sfItem != null && !sfItem.isDisenchantable()
        ) {
            return;
        }

        // Check for ancient book
        if (!SlimefunUtils.isItemSimilar(inv.getItemInSlot(BOOK_SLOT),
                FluffyItems.ANCIENT_BOOK.getItem().getItem(), false, false)
        ) {
            return;
        }

        // Check if we are ready to send the output
        if (currentProgress < PROCESS_TIME_TICKS) {
            progress.put(pos, ++currentProgress);

            ChestMenuUtils.updateProgressbar(inv, PROGRESS_SLOT, PROCESS_TIME_TICKS - currentProgress,
                    PROCESS_TIME_TICKS, PROGRESS_ITEM);

            removeCharge(b.getLocation(), ENERGY_CONSUMPTION);

            return;
        }

        // Get output
        Map<Enchantment, Integer> disenchants = getValidDisenchants(input);

        // Get disenchant using index
        Enchantment outputEnchant = disenchants.keySet().toArray(new Enchantment[0])[selectedEnchant];

        if (outputEnchant == null) {
            return;
        }

        // Build enchant book
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantedMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
        enchantedMeta.addStoredEnchant(outputEnchant, disenchants.get(outputEnchant), true);
        enchantedBook.setItemMeta(enchantedMeta);

        // Remove enchant from input
        input.removeEnchantment(outputEnchant);

        inv.pushItem(input, OUTPUT_SLOTS);
        inv.pushItem(enchantedBook, OUTPUT_SLOTS);
        inv.consumeItem(ITEM_SLOT);
        inv.consumeItem(BOOK_SLOT);

        // Reset progress
        progress.put(pos, 0);
        currentProgress = progress.getOrDefault(pos, 0);
        ChestMenuUtils.updateProgressbar(inv, PROGRESS_SLOT, PROCESS_TIME_TICKS - currentProgress,
                PROCESS_TIME_TICKS, PROGRESS_ITEM);
        setSelectedIndex(b, -2); // Set selection to "Reload"
        inv.replaceExistingItem(SELECTION_SLOT, DEFAULT_SELECTION_ITEM.clone()); // Reset selection item
    }

    private void cycleEnchants(BlockMenu inv, Block b) {

        int currentSelection = getSelectedIndex(b.getLocation());
        Map<Enchantment, Integer> itemEnchants = getValidDisenchants(inv.getItemInSlot(ITEM_SLOT));

        List<String> lore = new ArrayList<>();

        if (inv.getItemInSlot(ITEM_SLOT) == null) {
            lore.add(Utils.color("&c請先將物品放入物品欄中"));
            lore.add("");
            lore.add(Utils.color("&e> 點擊來重新掃描輸入欄 <"));
            setSelectionItem(inv, lore);
            setSelectedIndex(b, -2);
            return;
        }

        // Can't disenchant item
        if (itemEnchants.isEmpty()) {
            lore.add(Utils.color("&c此物品沒有可用的退魔!"));
            lore.add("");
            lore.add(Utils.color("&e> 點擊來重新掃描輸入欄 <"));
            setSelectionItem(inv, lore);
            setSelectedIndex(b, -2);
            return;
        }

        // -2 to -1: Set to none
        currentSelection++; // Get next enchant
        if (currentSelection > itemEnchants.size() - 1) {
            currentSelection = -1; // Reset to None
        }
        buildAndSetSelectionItem(itemEnchants, inv, currentSelection);
        setSelectedIndex(b, currentSelection);
    }

    private void constructMenu(BlockMenuPreset preset) {
        ChestMenuUtils.drawBackground(preset, BACKGROUND);

        for (int i : INPUT_BORDER) {
            preset.addItem(i, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : BOOK_BORDER) {
            preset.addItem(i, new CustomItemStack(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), " "), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : OUTPUT_BORDER) {
            preset.addItem(i, ChestMenuUtils.getOutputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        preset.addItem(PROGRESS_SLOT, PROGRESS_ITEM, ChestMenuUtils.getEmptyClickHandler());
    }

    private void buildAndSetSelectionItem(Map<Enchantment, Integer> disenchants, BlockMenu menu, int selectionIndex) {
        List<String> lore = new ArrayList<>();

        lore.add(Utils.color("&e> 點擊來循環附魔 <"));
        lore.add("");

        if (selectionIndex == -1) {
            lore.add(Utils.color("&a- 無"));
        } else {
            lore.add(Utils.color("&c- 無"));
        }

        Enchantment[] disenchantKeys = disenchants.keySet().toArray(new Enchantment[0]); // Get indexed disenchants

        for (int i = 0; i < disenchantKeys.length; i++) {
            ChatColor textColor = ChatColor.RED;
            if (i == selectionIndex) {
                textColor = ChatColor.GREEN;
            }

            lore.add(textColor + WordUtils.capitalizeFully("- " + disenchantKeys[i].getKey()
                    .getKey().replace('_', ' ')) + " "
                    + Utils.toRoman(disenchants.get(disenchantKeys[i]))
            );
        }

        setSelectionItem(menu, lore);
    }

    /**
     * Gets all the valid disenchants for the item
     * Does not account for isDisenchantable() == false Slimefun items
     * Assumes that the returned enchant map is in the same order every time
     */
    private Map<Enchantment, Integer> getValidDisenchants(ItemStack item) {

        // Check invalid item
        if (item == null) {
            return new HashMap<>();
        }

        // Check non disenchantable slimefun item
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem != null && !sfItem.isDisenchantable()) {
            return new HashMap<>();
        }

        Map<Enchantment, Integer> disenchants = item.getEnchantments();
        Map<Enchantment, Integer> filteredDisenchants = new HashMap<>(item.getEnchantments());
        // Remove enchants that exceed allowed level
        for (Map.Entry<Enchantment, Integer> disenchantEntry : disenchants.entrySet()) {
            if (useLevelLimit.getValue() && disenchantEntry.getValue() > levelLimit.getValue()) {
                filteredDisenchants.remove(disenchantEntry.getKey());
            }
        }

        return filteredDisenchants;
    }

    private void setSelectionItem(BlockMenu menu, List<String> lore) {
        ItemStack selectionItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta itemMeta = selectionItem.getItemMeta();

        itemMeta.setDisplayName(Utils.color("&5選取附魔"));
        itemMeta.setLore(lore);
        selectionItem.setItemMeta(itemMeta);

        menu.replaceExistingItem(SELECTION_SLOT, selectionItem);
    }

    /**
     * We need to use index addressing because the namespacedkey is not always minecraft
     * i.e. FM Glow
     */
    private int getSelectedIndex(Location l) {
        return Integer.parseInt(BlockStorage.getLocationInfo(l, "selection"));
    }

    private void setSelectedIndex(Block b, int index) {
        BlockStorage.addBlockInfo(b, "selection", String.valueOf(index));
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
}

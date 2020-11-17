package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
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
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedAutoDisenchanter extends SlimefunItem implements EnergyNetComponent {

    private static final int[] plainBorder = {0, 1, 2, 3, 4, 5, 6, 7, 8, 12, 14, 21, 22, 23, 36, 37, 38, 42, 43, 44, 45, 46, 47, 51, 52, 53};
    private static final int[] inputItemBorder = {9, 10, 11, 18, 20, 27, 28, 29};
    private static final int[] outputBorder = {21, 22, 23, 30, 32, 39, 41, 48, 49, 50};
    private static final int[] inputBookBorder = {15, 16, 17, 24, 26, 33, 34, 35};
    private static final int ITEM_SLOT = 19;
    private static final int BOOK_SLOT = 25;
    private static final int[] OUTPUT_SLOTS = {31, 40};

    private static final int SELECTION_SLOT = 4;
    private static final int PROGRESS_SLOT = 13;

    public static final int ENERGY_CONSUMPTION = 1024;
    public static final int CAPACITY = 4096;
    private static final int REQUIRED_TICKS = 60; // "Number of seconds", except 1 Slimefun "second" = 1.6 IRL seconds

    private static final Map<BlockPosition, Integer> progress = new HashMap<>();

    private static final NamespacedKey selection = new NamespacedKey(FluffyMachines.getInstance(), "selection");

    private static final ItemStack selectionItem = new CustomItem(Material.ENCHANTED_BOOK,
        "&6Selected Enchant", "&a> 點擊此循環查看附魔", "&5目前附魔: 無");

    private static final ItemStack progressItem = new CustomItem(Material.EXPERIENCE_BOTTLE, "&a運作");


    static {
        ItemMeta meta = selectionItem.getItemMeta();
        meta.getPersistentDataContainer().set(selection, PersistentDataType.INTEGER, -1);
        selectionItem.setItemMeta(meta);
    }

    public AdvancedAutoDisenchanter(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getId(), "&c高級自動退魔器") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                menu.replaceExistingItem(SELECTION_SLOT, selectionItem.clone());

                menu.addMenuClickHandler(SELECTION_SLOT, (p, slot, item, action) -> {
                    cycleEnchants(menu, item);
                    return false;
                });

                menu.addMenuClickHandler(ITEM_SLOT, (p, slot, item, action) -> {
                    menu.replaceExistingItem(SELECTION_SLOT, selectionItem.clone());
                    return true;
                });

            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return (p.hasPermission("slimefun.inventory.bypass")
                    || SlimefunPlugin.getProtectionManager().hasPermission(
                    p, b.getLocation(), ProtectableAction.ACCESS_INVENTORIES));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.INSERT) {
                    if (item.getType() == Material.BOOK) {
                        return new int[] {BOOK_SLOT};
                    } else {
                        return new int[] {ITEM_SLOT};
                    }
                } else if (flow == ItemTransportFlow.WITHDRAW) {
                    return OUTPUT_SLOTS;
                } else {
                    return new int[0];
                }
            }
        };

        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), ITEM_SLOT);
                inv.dropItems(b.getLocation(), BOOK_SLOT);
                inv.dropItems(b.getLocation(), OUTPUT_SLOTS);
            }
            return true;
        });
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

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : plainBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : inputItemBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : inputBookBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : outputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        preset.addItem(PROGRESS_SLOT, progressItem, (p, slot, item, action) -> false);
    }

    protected void tick(Block b) {

        if (getCharge(b.getLocation()) < ENERGY_CONSUMPTION) {
            return;
        }

        BlockMenu inv = BlockStorage.getInventory(b);
        final BlockPosition pos = new BlockPosition(b.getWorld(), b.getX(), b.getY(), b.getZ());
        int currentProgress = progress.getOrDefault(pos, 0);
        ItemMeta meta = inv.getItemInSlot(SELECTION_SLOT).getItemMeta();
        int selectionIndex = meta.getPersistentDataContainer().get(selection, PersistentDataType.INTEGER);
        ItemStack input = inv.getItemInSlot(ITEM_SLOT);

        SlimefunItem sfItem = SlimefunItem.getByItem(input);
        if (sfItem != null && !sfItem.isDisenchantable()) {
            return;
        }

        if (selectionIndex != -1
            && input != null && inv.getItemInSlot(BOOK_SLOT) != null
            && SlimefunUtils.isItemSimilar(inv.getItemInSlot(BOOK_SLOT), FluffyItems.ANCIENT_BOOK.getItem().getItem(), false, false)
            && !input.getEnchantments().isEmpty()) {


            // We need both slots empty
            for (int slot : OUTPUT_SLOTS) {
                if (inv.getItemInSlot(slot) != null) {
                    return;
                }
            }

            // Dont produce the item if didnt finish
            if (currentProgress < REQUIRED_TICKS) {
                progress.put(pos, ++currentProgress);

                ChestMenuUtils.updateProgressbar(inv, PROGRESS_SLOT, REQUIRED_TICKS - currentProgress,
                    REQUIRED_TICKS, progressItem);

                removeCharge(b.getLocation(), ENERGY_CONSUMPTION);

                return;
            }

            ItemStack item = inv.getItemInSlot(ITEM_SLOT).clone();

            List<NamespacedKey> enchants = new ArrayList<>();
            List<Integer> levels = new ArrayList<>();
            Map<Enchantment, Integer> enchantMap = item.getEnchantments();

            if (enchantMap.size() == 0) {
                return;
            }

            enchantMap.forEach((enchant, level) -> {
                enchants.add(enchant.getKey());
                levels.add(level);
            });

            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta enchantedMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
            enchantedMeta.addStoredEnchant(Enchantment.getByKey(enchants.get(selectionIndex)), levels.get(selectionIndex), true);
            enchantedBook.setItemMeta(enchantedMeta);

            item.removeEnchantment(Enchantment.getByKey(enchants.get(selectionIndex)));

            inv.consumeItem(ITEM_SLOT);
            inv.consumeItem(BOOK_SLOT);
            inv.pushItem(item, OUTPUT_SLOTS);
            inv.pushItem(enchantedBook, OUTPUT_SLOTS);

            // Reset the selection item
            inv.replaceExistingItem(SELECTION_SLOT, selectionItem);

        }
        progress.put(pos, 0);
        currentProgress = progress.getOrDefault(pos, 0);
        ChestMenuUtils.updateProgressbar(inv, PROGRESS_SLOT, REQUIRED_TICKS - currentProgress,
            REQUIRED_TICKS, progressItem);
    }

    private void cycleEnchants(BlockMenu inv, ItemStack clickedItem) {
        if (inv.getItemInSlot(ITEM_SLOT) != null) {
            ItemStack item = inv.getItemInSlot(ITEM_SLOT);
            List<String> enchants = new ArrayList<>();
            Map<Enchantment, Integer> enchantMap = item.getEnchantments();

            if (enchantMap.size() == 0) {
                return;
            }

            // Convert to a list
            enchantMap.forEach((enchant, level) ->
                enchants.add(WordUtils.capitalizeFully(enchant.getKey().toString()
                .replace("minecraft:", "").replace("_", " ")) + " " + Utils.toRoman(level)));

            ItemMeta meta = clickedItem.getItemMeta();
            List<String> lore = meta.getLore();

            int selectionIndex = meta.getPersistentDataContainer().get(selection, PersistentDataType.INTEGER);

            if (enchants.size() - 1 > selectionIndex) {
                selectionIndex++; // 0
                meta.getPersistentDataContainer().set(selection, PersistentDataType.INTEGER, selectionIndex);

                lore.set(1, ChatColor.DARK_PURPLE + "目前附魔: " +  ChatColor.YELLOW + enchants.get(selectionIndex)); // 0

            } else {
                selectionIndex = 0;
                meta.getPersistentDataContainer().set(selection, PersistentDataType.INTEGER, selectionIndex);
                lore.set(1, ChatColor.DARK_PURPLE + "目前附魔: " + ChatColor.YELLOW + enchants.get(0));
            }
            meta.setLore(lore);
            clickedItem.setItemMeta(meta);
        }
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

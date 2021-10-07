package io.ncbpfluffybear.fluffymachines.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class EnderChestNode extends SlimefunItem {

    private static final Material material = Material.ENDER_CHEST;
    private static final int[] BORDER = {0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 18, 20, 21, 22, 23,
            24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
    private static final int INFO = 4;
    private static final int FILTER_TALISMANS = 19;

    private final Type type;

    public EnderChestNode(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, Type type) {
        super(category, item, recipeType, recipe);
        this.type = type;

        addItemHandler(onPlace());
        buildBlockMenuPreset();
    }

    public static ItemStack insertIntoVanillaInventory(@Nonnull ItemStack stack, @Nonnull Inventory inv) {

        ItemStack[] contents = inv.getContents();

        ItemStack wrapper = new ItemStack(stack);

        for (int slot = 0; slot < inv.getSize(); slot++) {
            // Changes to this ItemStack are synchronized with the Item in the Inventory
            ItemStack itemInSlot = contents[slot];

            if (itemInSlot == null) {
                inv.setItem(slot, stack);
                return null;
            } else {
                int maxStackSize = itemInSlot.getType().getMaxStackSize();

                if (itemInSlot.getAmount() < maxStackSize && SlimefunUtils.isItemSimilar(itemInSlot, wrapper, true, false)) {
                    int amount = itemInSlot.getAmount() + stack.getAmount();

                    if (amount > maxStackSize) {
                        stack.setAmount(amount - maxStackSize);
                        itemInSlot.setAmount(maxStackSize);
                        return stack;
                    } else {
                        itemInSlot.setAmount(amount);
                        return null;
                    }
                }
            }
        }

        return stack;
    }

    private void buildBlockMenuPreset() {
        new BlockMenuPreset(getId(), this.type.getColor() + "Ender Chest " + type.getName() + " Node") {
            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                BlockMenu menu = BlockStorage.getInventory(b);
                menu.replaceExistingItem(INFO, new CustomItemStack(Material.REDSTONE_TORCH,
                        "&e&lInfo", "&eOwner: " + BlockStorage.getLocationInfo(b.getLocation(), "playername")));
                menu.addMenuClickHandler(INFO, ChestMenuUtils.getEmptyClickHandler());
                menu.save(b.getLocation());

                return p.hasPermission("slimefun.inventory.bypass")
                        || (Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK));
            }


            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                if (!BlockStorage.hasBlockInfo(b)) {
                    return;
                }

                if (BlockStorage.getLocationInfo(b.getLocation(), "filter-talismans") == null
                        || BlockStorage.getLocationInfo(b.getLocation(), "filter-talismans").equals("false")) {
                    updateMenu(menu, b, "talismans-false");
                } else {
                    updateMenu(menu, b, "talismans-true");
                }

            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    private void updateMenu(BlockMenu menu, Block b, String type) {
        switch (type) {
            case "talismans-false":
                menu.replaceExistingItem(FILTER_TALISMANS, new CustomItemStack(Material.EMERALD, "&aFilter Talismans: " +
                        "&4\u2718"));
                menu.addMenuClickHandler(FILTER_TALISMANS, (p, slot, item, action) -> {
                    BlockStorage.addBlockInfo(b, "filter-talismans", "true");
                    updateMenu(menu, b, "talismans-true");
                    return false;
                });
                break;
            case "talismans-true":
                menu.replaceExistingItem(FILTER_TALISMANS, new CustomItemStack(Material.EMERALD, "&aFilter Talismans: " +
                        "&2\u2714"));
                menu.addMenuClickHandler(FILTER_TALISMANS, (p, slot, item, action) -> {
                    BlockStorage.addBlockInfo(b, "filter-talismans", "false");
                    updateMenu(menu, b, "talismans-false");
                    return false;
                });
                break;
        }
    }

    private void constructMenu(BlockMenuPreset menu) {
        for (int slot : BORDER) {
            menu.addItem(slot, new CustomItemStack(Material.CYAN_STAINED_GLASS_PANE, " "), ChestMenuUtils.getEmptyClickHandler());
        }
    }

    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent e) {
                Player p = e.getPlayer();
                Block b = e.getBlock();

                if (!e.isCancelled()) {
                    BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
                    BlockStorage.addBlockInfo(b, "playername", p.getDisplayName());
                    BlockStorage.addBlockInfo(b, "filter-talismans", "false");
                    Utils.send(p, "&aEnder Chest " + type.getName() + " Node registered to " + p.getDisplayName()
                            + " &7(UUID: " + p.getUniqueId() + ")");
                }
            }
        };
    }

    private BlockUseHandler onInteract() {
        return e -> {
            Player p = e.getPlayer();
            Block b = e.getClickedBlock().get();

            if (p.isSneaking()) {
                Utils.send(p, "&eThis Ender Chest " + type.getName() + " Node belongs to " +
                        BlockStorage.getLocationInfo(b.getLocation(), "playername")
                        + " &7(UUID: " + BlockStorage.getLocationInfo(b.getLocation(), "owner") + ")");
                e.cancel();
                return;
            }

            if (BlockStorage.getInventory(b) == null) {
                buildBlockMenuPreset();
            }
        };
    }

    public BlockFace checkEChest(Block b) {
        if (b.getRelative(BlockFace.NORTH).getType() == material) {
            return BlockFace.SOUTH;

        } else if (b.getRelative(BlockFace.SOUTH).getType() == material) {
            return BlockFace.NORTH;


        } else if (b.getRelative(BlockFace.EAST).getType() == material) {
            return BlockFace.WEST;


        } else if (b.getRelative(BlockFace.WEST).getType() == material) {
            return BlockFace.EAST;

        } else {
            return null;
        }
    }

    public enum Type {
        INSERTION("&b", "Insertion"),
        EXTRACTION("&6", "Extraction");

        private final String color;
        private final String name;

        Type(String color, String name) {
            this.color = color;
            this.name = name;
        }

        public String getColor() {
            return this.color;
        }

        public String getName() {
            return this.name;
        }

    }
}

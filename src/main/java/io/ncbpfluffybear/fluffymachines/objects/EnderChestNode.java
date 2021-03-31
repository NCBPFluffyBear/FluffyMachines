package io.ncbpfluffybear.fluffymachines.objects;

import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.services.localization.SlimefunLocalization;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.CargoInputNode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class EnderChestNode extends SlimefunItem {

    private static final Material material = Material.ENDER_CHEST;
    private static final int[] BORDER = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 18, 22, 23, 26, 27, 31,
        32, 33, 34, 35, 36, 40, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53 };

    private final Type type;

    public EnderChestNode(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, Type type) {
        super(category, item, recipeType, recipe);
        this.type = type;

        addItemHandler(onPlace());
        addItemHandler(onInteract());

        new BlockMenuPreset(getId(), type.getColor() + type.getName()) {
            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return p.hasPermission("slimefun.inventory.bypass")
                    || (SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    private void constructMenu(BlockMenuPreset menu) {
        for (int slot : BORDER) {
            menu.replaceExistingItem(slot, new CustomItem(Material.CYAN_STAINED_GLASS_PANE, " "));
            menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
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
                    Utils.send(p, "&aEnder Chest " + type.getName() + " Node registered to " + p.getDisplayName()
                        + " &7(UUID: " + p.getUniqueId().toString() + ")");
                }
            }
        };
    }

    private BlockUseHandler onInteract() {
        return e -> {
            Player p = e.getPlayer();
            if (!p.isSneaking()) {
                return;
            }
            Block b = e.getClickedBlock().get();
            Utils.send(p, "&eThis Ender Chest " + type.getName() + " Node belongs to " +
                BlockStorage.getLocationInfo(b.getLocation(), "playername")
                + " &7(UUID: " + BlockStorage.getLocationInfo(b.getLocation(), "owner") + ")");
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

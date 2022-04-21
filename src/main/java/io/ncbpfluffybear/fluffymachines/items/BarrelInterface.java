package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BarrelInterface extends SlimefunItem {

    private final int[] BORDER = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53};
    private final int[] BARREL_SLOTS = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28,
            29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43, 46, 47, 48, 49, 50, 51, 52
    };

    public BarrelInterface(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        buildPreset();
    }

    private void buildPreset() {
        new BlockMenuPreset(this.getId(), "&9Barrel Interface") {
            @Override
            public void init() {
                ChestMenuUtils.drawBackground(this, BORDER);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                Utils.createBorder(this, new CustomItemStack(Material.BARRIER, "&cUnconfigured Slot",
                        "&7> Click this slot with a", "&eWireless Barrel Transmitter to connect it"
                ), BARREL_SLOTS);

                for (int slot : BARREL_SLOTS) {
                    addMenuClickHandler(slot, new AdvancedMenuClickHandler() {
                        @Override
                        public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                            if (SlimefunItem.getByItem(cursor) instanceof BarrelTransmitter) {
                                ItemMeta transmitterMeta = cursor.getItemMeta();

                                if (transmitterMeta == null) {
                                    return false;
                                }

                                PersistentDataAPI.setString(cursor.getItemMeta(), Constants.LOC_KEY, b.getWorld().getUID() + "_" +
                                        b.getX() + "_" + b.getY() + "_" + b.getZ());

                                List<String> lore = transmitterMeta.getLore();

                                lore.set(4, Utils.color("&7Location: " + b.getX() + ", " +
                                        b.getY() + ", " + b.getZ() + " @" + b.getWorld()
                                ));
                                lore.set(5, Utils.color("&7Slot: " + slot));

                                transmitterMeta.setLore(lore);
                                cursor.setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
                            }
                            return false;
                        }

                        @Override
                        public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
                            return false;
                        }
                    });
                }
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                if (Utils.canOpen(b, p)) {
                    updateBarrelViews(b);

                    return true;
                }

                return false;
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    private void updateBarrelViews(Block b) {
        BlockMenu menu = BlockStorage.getInventory(b);
        for (int slot : BARREL_SLOTS) {
            ItemStack barrelDisplay = menu.getItemInSlot(slot);
            if (barrelDisplay.getType() == Material.BARRIER) { // Skip if not loading a barrel
                continue;
            }

            String[] locString = PersistentDataAPI.getString(barrelDisplay.getItemMeta(), Constants.LOC_KEY).split("_");
            Location barrelLoc = new Location(Bukkit.getWorld(UUID.fromString(locString[0])), Integer.parseInt(locString[1]),
                    Integer.parseInt(locString[1]), Integer.parseInt(locString[1])
            );

            SlimefunItem sfBarrel = BlockStorage.check(barrelLoc);
            if (sfBarrel instanceof Barrel) {
                menu.replaceExistingItem(slot, getBarrelDisplay(sfBarrel, b));
            }
        }
    }

    private ItemStack getBarrelDisplay(SlimefunItem sfBarrel, Block b) {
        Barrel barrel = ((Barrel) sfBarrel);
        return new CustomItemStack(sfBarrel.getItem().getType(), sfBarrel.getItemName(), "",
                "&e" + barrel.getStored(b) + "/" + barrel.getCapacity(b)
        );
    }
}

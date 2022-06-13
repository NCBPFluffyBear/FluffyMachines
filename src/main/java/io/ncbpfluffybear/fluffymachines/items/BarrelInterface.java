package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.api.gps.GPSNetwork;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.networks.NetworkManager;
import io.github.thebusybiscuit.slimefun4.core.services.localization.SlimefunLocalization;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
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
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BarrelInterface extends SlimefunItem {

    private final int[] BORDER = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
    private final int[] BARREL_SLOTS = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28,
            29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43
    };
    public static final NamespacedKey SLOT_KEY = new NamespacedKey(FluffyMachines.getInstance(), "slot");
    private final ItemStack EMPTY_ITEM = new CustomItemStack(Material.BARRIER, "&cUnconfigured Slot",
            "&7> Click this slot with a Wireless Barrel Transmitter", "&7to connect it"
    );

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
                for (int slot : BARREL_SLOTS) {
                    if (menu.getItemInSlot(slot) == null) {
                        menu.replaceExistingItem(slot, EMPTY_ITEM);
                    }
                    menu.addMenuClickHandler(slot, new AdvancedMenuClickHandler() {
                        @Override
                        public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                            ItemStack currentItem = e.getCurrentItem();
                            // Binding
                            if (currentItem.getType() == Material.BARRIER && SlimefunItem.getByItem(cursor) instanceof BarrelTransmitter) {
                                ItemMeta transmitterMeta = cursor.getItemMeta();

                                if (transmitterMeta == null) {
                                    return false;
                                }

                                PersistentDataAPI.setString(transmitterMeta, Constants.LOC_KEY, b.getWorld().getUID() + "_" +
                                        b.getX() + "_" + b.getY() + "_" + b.getZ());
                                PersistentDataAPI.setInt(transmitterMeta, SLOT_KEY, slot);

                                List<String> lore = transmitterMeta.getLore();

                                lore.set(4, Utils.color("&7Location: " + b.getX() + ", " +
                                        b.getY() + ", " + b.getZ() + " @" + b.getWorld().getName()
                                ));
                                lore.set(5, Utils.color("&7Slot: " + (slot - 9))); // User-friendly number

                                transmitterMeta.setLore(lore);
                                cursor.setItemMeta(transmitterMeta);
                                cursor.setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
                            } else {
                                switch (e.getClick()) {
                                    case MIDDLE: // Reset slot
                                        Location barrelLoc = parseLocation(getLocationString(currentItem.getItemMeta()));
                                        Block transmitter = barrelLoc.getBlock().getRelative(BlockFace.UP);
                                        if (BlockStorage.check(transmitter) instanceof BarrelTransmitter) {
                                            transmitter.setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE); // Update texture
                                            menu.replaceExistingItem(slot, EMPTY_ITEM); // Clear slot
                                            Utils.send(p, "&aSlot has been unbound");
                                        }
                                        return false;
                                    case LEFT:
                                        // Extract 1
                                        return false;
                                    case RIGHT:
                                        // Extract stack
                                        return false;
                                }
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
                if (Slimefun.getGPSNetwork().getNetworkComplexity(p.getUniqueId()) < 600) {
                    Slimefun.getLocalization().sendMessages(p, "gps.insufficient-complexity",
                            true, msg -> msg.replace("%complexity%", "600")
                    );
                    return false;
                } else if (Utils.canOpen(b, p)) {
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
            // Skip unlinked slots
            if (barrelDisplay == null || barrelDisplay.getType() == Material.BARRIER) {
                continue;
            }

            // Parse location
            String locString = getLocationString(barrelDisplay.getItemMeta());
            Location barrelLoc = parseLocation(locString);
            Block transmitter = barrelLoc.getBlock().getRelative(BlockFace.UP);

            if (!(BlockStorage.check(transmitter) instanceof BarrelTransmitter)) { // Transmitter missing
                menu.replaceExistingItem(slot, EMPTY_ITEM);
                continue;
            }

            SlimefunItem sfBarrel = BlockStorage.check(barrelLoc);
            if (sfBarrel instanceof Barrel) { // Check if barrel missing
                // Update
                menu.replaceExistingItem(slot, getBarrelDisplayItem(sfBarrel, barrelLoc.getBlock(), locString));
            }
        }
    }

    public static ItemStack getBarrelDisplayItem(SlimefunItem sfBarrel, Block b, String location) {
        Barrel barrel = ((Barrel) sfBarrel);
        ItemStack displayItem = new CustomItemStack(sfBarrel.getItem().getType(), sfBarrel.getItemName(), "",
                "&e" + barrel.getStored(b) + "/" + barrel.getCapacity(b)
        );

        // Restore location key
        ItemMeta displayMeta = displayItem.getItemMeta();
        PersistentDataAPI.setString(displayMeta, Constants.LOC_KEY, location);
        displayItem.setItemMeta(displayMeta);

        return displayItem;
    }

    private String getLocationString(ItemMeta itemMeta) {
        return PersistentDataAPI.getString(itemMeta, Constants.LOC_KEY);
    }

    private Location parseLocation(String locString) {
        String[] splitLoc = locString.split("_");
        return new Location(Bukkit.getWorld(UUID.fromString(splitLoc[0])), Integer.parseInt(splitLoc[1]),
                Integer.parseInt(splitLoc[2]), Integer.parseInt(splitLoc[3])
        );
    }
}

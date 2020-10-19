package io.ncbpfluffybear.fluffymachines.machines.energytransmitter;

import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EnergyReceiver extends SlimefunItem implements EnergyNetComponent {

    public static final int CAPACITY = 1048576;
    public static final int RATE = 1024;

    public EnergyReceiver(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getID(), "&cEnergy Receiver") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(Block b, Player p) {

                Location l = b.getLocation();

                if (BlockStorage.getLocationInfo(l, "status") != null) {

                    BlockStorage.getInventory(b).replaceExistingItem(
                        4, new CustomItem(Material.LIME_STAINED_GLASS_PANE, "&aLinked", "&bTransmitter's location:",
                            "&eWorld: &f" + Bukkit.getWorld(UUID.fromString(BlockStorage.getLocationInfo(l, "world"))) +
                                "&eLocation: &f" + BlockStorage.getLocationInfo(l, "x") + ", " +
                                BlockStorage.getLocationInfo(l, "y") + ", " + BlockStorage.getLocationInfo(l, "z")));
                }

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
                return new int[0];
            }
        };
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int b = 0; b < 9; b++) {
            preset.addItem(b, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "),
                ChestMenuUtils.getEmptyClickHandler()
            );
        }

        preset.addItem(4, new CustomItem(Material.RED_STAINED_GLASS_PANE, "&cNot Linked"));
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                EnergyReceiver.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block b) {

        Location l = b.getLocation();

        if (BlockStorage.getLocationInfo(b.getLocation(), "x") != null)

            transmitEnergy(l, BlockStorage.getLocationInfo(l, "world"),
                Integer.parseInt(BlockStorage.getLocationInfo(l, "x")),
                Integer.parseInt(BlockStorage.getLocationInfo(l, "y")),
                Integer.parseInt(BlockStorage.getLocationInfo(l, "z")));
    }

    private void transmitEnergy(Location rl, String world, int x, int y, int z) {
        Location tl = new Location(Bukkit.getWorld(UUID.fromString(world)), x, y, z);

        if (BlockStorage.checkID(tl).equals(FluffyItems.ENERGY_TRANSMITTER.getItemId())) {

            int transmitterCharge = getCharge(tl);
            int receiverCharge = getCharge(rl);

            if (receiverCharge < getCapacity()) {
                int reqCharge = getCapacity() - receiverCharge;

                if (transmitterCharge > RATE && reqCharge > RATE) {
                    removeCharge(tl, RATE);
                    addCharge(rl, RATE);

                } else if (transmitterCharge > RATE && reqCharge < RATE) {
                    removeCharge(tl, reqCharge);
                    addCharge(rl, reqCharge);

                } else if (transmitterCharge < RATE && reqCharge > RATE) {
                    removeCharge(tl, transmitterCharge);
                    addCharge(rl, transmitterCharge);

                } else if (transmitterCharge < RATE && reqCharge < RATE) {
                    if (transmitterCharge < reqCharge) {

                        removeCharge(tl, transmitterCharge);
                        addCharge(rl, transmitterCharge);

                    } else {

                        removeCharge(tl, reqCharge);
                        addCharge(rl, reqCharge);

                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CAPACITOR;
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }
}

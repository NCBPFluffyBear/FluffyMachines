package io.ncbpfluffybear.fluffymachines.machines.energytransmitter;

import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
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

public class EnergyTransmitter extends SlimefunItem implements EnergyNetComponent {

    public static final int CAPACITY = 1048576;
    private final int INDICATOR_SLOT = 4;

    public EnergyTransmitter(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getId(), "&aEnergy Transmitter") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {

                Location l = b.getLocation();

                BlockStorage.getInventory(b).replaceExistingItem(
                    INDICATOR_SLOT, new CustomItem(Material.LIME_STAINED_GLASS_PANE, "&bThis block's location:",
                        "&eWorld: &f" + b.getWorld().getName(),
                            "&eLocation: &f" + b.getX() + ", " + b.getY() + ", " + b.getZ()));

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
            preset.addItem(b, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
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

package io.ncbpfluffybear.fluffymachines.machines.energytransmitter;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.List;

public class Linker extends SimpleSlimefunItem<ItemUseHandler> {

    private final NamespacedKey xCoord = new NamespacedKey(FluffyMachines.getInstance(), "xCoordinate");
    private final NamespacedKey yCoord = new NamespacedKey(FluffyMachines.getInstance(), "yCoordinate");
    private final NamespacedKey zCoord = new NamespacedKey(FluffyMachines.getInstance(), "zCoordinate");
    private final NamespacedKey world = new NamespacedKey(FluffyMachines.getInstance(), "world");

    private final int LORE_COORDINATE_INDEX = 4;

    public Linker(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {

            if (e.getClickedBlock().isPresent()
                && (BlockStorage.checkID(e.getClickedBlock().get()).equals(FluffyItems.ENERGY_TRANSMITTER.getItemId())
                || BlockStorage.checkID(e.getClickedBlock().get()).equals(FluffyItems.ENERGY_RECEIVER.getItemId()))) {

                e.cancel();
                Player p = e.getPlayer();
                ItemStack item = e.getItem();
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.getLore();
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                Block b = e.getClickedBlock().get();

                // Transmitter
                if (BlockStorage.checkID(e.getClickedBlock().get()).equals(FluffyItems.ENERGY_TRANSMITTER.getItemId())) {
                    pdc.set(world, PersistentDataType.STRING, b.getWorld().getUID().toString());

                    pdc.set(xCoord, PersistentDataType.INTEGER, b.getX());
                    pdc.set(yCoord, PersistentDataType.INTEGER, b.getY());
                    pdc.set(zCoord, PersistentDataType.INTEGER, b.getZ());

                    String locationString = b.getX() + ", " + b.getY() + ", " + b.getZ() + " (" + b.getWorld().getName() + ")";
                    lore.set(LORE_COORDINATE_INDEX, ChatColor.translateAlternateColorCodes(
                        '&', "&eLinked Coordinates: &7" + locationString));

                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    Utils.send(p, "&3Transmitter Linked");

                    // Receiver
                } else if (BlockStorage.checkID(e.getClickedBlock().get()).equals(FluffyItems.ENERGY_RECEIVER.getItemId())) {

                    if (pdc.has(world, PersistentDataType.STRING)) {

                        BlockStorage.addBlockInfo(b, "world", pdc.get(world, PersistentDataType.STRING));

                        BlockStorage.addBlockInfo(b, "x", String.valueOf(pdc.getOrDefault(xCoord,
                            PersistentDataType.INTEGER, 0)));
                        BlockStorage.addBlockInfo(b, "y", String.valueOf(pdc.getOrDefault(yCoord,
                            PersistentDataType.INTEGER, 0)));
                        BlockStorage.addBlockInfo(b, "z", String.valueOf(pdc.getOrDefault(zCoord,
                            PersistentDataType.INTEGER, 0)));

                        BlockStorage.addBlockInfo(b.getLocation(), "status", "linked");

                        Utils.send(p, "&3Receiver linked");

                    } else {
                        Utils.send(p, "&cLink an Energy Receiver first!");
                    }

                }
            }

        };
    }
}

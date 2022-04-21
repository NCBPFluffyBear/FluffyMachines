package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import java.util.UUID;
import javax.annotation.Nonnull;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BarrelTransmitter extends SlimefunItem {

    public BarrelTransmitter(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(onPlace());
    }

    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent e) {
                SlimefunItem sfBlock = BlockStorage.check(e.getBlock().getRelative(BlockFace.DOWN));
                if (!(sfBlock instanceof Barrel)) {
                    Utils.send(e.getPlayer(), "&cThis can only be placed on a barrel!");
                    e.setCancelled(true);
                    return;
                }

                String[] locString = PersistentDataAPI.getString(e.getItemInHand().getItemMeta(), Constants.LOC_KEY).split("_");
                Location interfaceLoc = new Location(Bukkit.getWorld(UUID.fromString(locString[0])), Integer.parseInt(locString[1]),
                        Integer.parseInt(locString[1]), Integer.parseInt(locString[1])
                );

                SlimefunItem transmitter = BlockStorage.check(interfaceLoc);
                if (transmitter instanceof BarrelTransmitter) {
                    BlockMenu menu = BlockStorage.getInventory(interfaceLoc);
                        menu.replaceExistingItem();

                }

            }
        };
    }
}

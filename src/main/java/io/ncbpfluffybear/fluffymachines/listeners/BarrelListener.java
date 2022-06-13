package io.ncbpfluffybear.fluffymachines.listeners;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import io.ncbpfluffybear.fluffymachines.items.BarrelInterface;
import io.ncbpfluffybear.fluffymachines.items.BarrelTransmitter;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import java.util.Optional;
import java.util.UUID;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.inventory.ItemStack;

public class BarrelListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onBarrelBurn(BlockBurnEvent e) {
        if (BlockStorage.check(e.getBlock()) instanceof Barrel) {
            e.setCancelled(true);
        }
    }

    /**
     * This handles place events for the Barrel Transmitter
     */
    @EventHandler
    private void onTransmitterPlace(PlayerRightClickEvent e) {

        ItemStack placedItem = e.getItem();
        SlimefunItem sfBlock = SlimefunItem.getByItem(placedItem);

        // Confirm item is transmitter
        if (!(sfBlock instanceof BarrelTransmitter)) {
            return;
        }

        Optional<Block> b = e.getClickedBlock();

        if (!b.isPresent()) {
            return;
        }

        Block barrelBlock = b.get();
        SlimefunItem sfBarrel = BlockStorage.check(barrelBlock);
        Player p = e.getPlayer();
        if (e.getClickedFace() != BlockFace.UP || !(sfBarrel instanceof Barrel)) {
            Utils.send(p, "&cThis can only be placed on top of a barrel!");
            e.cancel();
            return;
        }

        // Parse location
        Optional<String> optionalLoc = PersistentDataAPI.getOptionalString(placedItem.getItemMeta(), Constants.LOC_KEY);
        if (!optionalLoc.isPresent()) {
            Utils.send(p, "&cConnect this inside a Barrel Interface first");
            e.cancel();
            return;
        }
        String locString = optionalLoc.get();
        String[] splitLoc = locString.split("_");
        Location interfaceLoc = new Location(Bukkit.getWorld(UUID.fromString(splitLoc[0])), Integer.parseInt(splitLoc[1]),
                Integer.parseInt(splitLoc[2]), Integer.parseInt(splitLoc[3])
        );
        int slot = PersistentDataAPI.getInt(e.getItem().getItemMeta(), BarrelInterface.SLOT_KEY);

        // Get transmitter
        SlimefunItem barrelInterface = BlockStorage.check(interfaceLoc);
        // Check barrel interface still exists
        if (barrelInterface instanceof BarrelInterface) {
            BlockMenu menu = BlockStorage.getInventory(interfaceLoc);
            // Check if a barrel has already been set
            if (menu.getItemInSlot(slot).getType() != Material.BARRIER) {
                Utils.send(p, "&cA barrel has already been bound to this Barrel Interface slot! Clear the slot or select a different slot.");
                e.cancel();
                return;
            }

            // Set item
            menu.replaceExistingItem(slot, BarrelInterface.getBarrelDisplayItem(sfBarrel, barrelBlock,
                    barrelBlock.getWorld().getUID() + "_" +
                            barrelBlock.getX() + "_" + barrelBlock.getY() + "_" + barrelBlock.getZ()
            ));
        } else {
            Utils.send(p, "&cThe linked Barrel Interface no longer exists. Please link this transmitter to a new Interface.");
        }
    }
}

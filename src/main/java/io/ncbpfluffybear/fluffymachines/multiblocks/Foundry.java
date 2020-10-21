package io.ncbpfluffybear.fluffymachines.multiblocks;

import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.ncbpfluffybear.fluffymachines.multiblocks.components.SuperheatedFurnace;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The shell of the {@link SuperheatedFurnace}
 *
 * @author NCBPFluffyBear
 */
public class Foundry extends MultiBlockMachine {

    public Foundry(Category category, SlimefunItemStack item) {
        super(category, item, new ItemStack[] {
            new ItemStack(Material.NETHERITE_BLOCK), FluffyItems.SUPERHEATED_FURNACE, new ItemStack(Material.NETHERITE_BLOCK),
            new ItemStack(Material.NETHERITE_BLOCK), new ItemStack(Material.GLASS), new ItemStack(Material.NETHERITE_BLOCK),
            new ItemStack(Material.NETHERITE_BLOCK), new ItemStack(Material.CAULDRON), new ItemStack(Material.NETHERITE_BLOCK)
        }, BlockFace.DOWN);
    }

    @Override
    public void onInteract(Player p, Block b) {
        if (BlockStorage.getLocationInfo(b.getLocation(), "accessible") == null) {
            BlockStorage.addBlockInfo(b, "accessible", "true");
            //p.closeInventory();
            Utils.send(p, "&eFoundry has been registered. Right click the furnace with a lava bucket to heat.");
        } else if (BlockStorage.getLocationInfo(b.getLocation(), "ignited") == null) {
            if (p.getInventory().getItemInMainHand().getType() == Material.LAVA_BUCKET) {

                p.getInventory().getItemInMainHand().setType(Material.BUCKET);
                ArmorStand lavaStand = (ArmorStand) p.getWorld().spawnEntity(b.getLocation().add(0.5, -3, 0.5),
                    EntityType.ARMOR_STAND);
                lavaStand.getEquipment().setHelmet(new CustomItem(
                    SkullItem.fromHash("b6965e6a58684c277d18717cec959f2833a72dfa95661019dbcdf3dbf66b048")));
                lavaStand.setCanPickupItems(false);
                lavaStand.setGravity(false);
                lavaStand.setVisible(false);
                lavaStand.setCustomName("hehexdfluff");
                lavaStand.setCustomNameVisible(false);
                Furnace furnace = (Furnace) b.getState();
                furnace.setBurnTime((short)1000000);
                furnace.update(true);

                BlockStorage.addBlockInfo(b, "stand", String.valueOf(lavaStand.getUniqueId()));
                BlockStorage.addBlockInfo(b, "ignited", "true");
            } else {
                Utils.send(p, "&cThis foundry still needs to be filled with lava!");
            }
        } else if (BlockStorage.getLocationInfo(b.getLocation(), "ignited") != null) {
            Furnace furnace = (Furnace) b.getState();
            furnace.setBurnTime((short)1000000);
            furnace.update(true);
        }
     }
}

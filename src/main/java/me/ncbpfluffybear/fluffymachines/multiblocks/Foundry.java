package me.ncbpfluffybear.fluffymachines.multiblocks;

import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Foundry extends MultiBlockMachine {

    public Foundry(Category category, SlimefunItemStack item) {
        super(category, item, new ItemStack[] {
            new ItemStack(Material.NETHERITE_BLOCK), FluffyItems.SUPERHEATED_FURNACE, new ItemStack(Material.NETHERITE_BLOCK),
            new ItemStack(Material.NETHERITE_BLOCK), new ItemStack(Material.GLASS), new ItemStack(Material.NETHERITE_BLOCK),
            new ItemStack(Material.NETHERITE_BLOCK), new CustomItem(Material.CAULDRON, "Cauldron &cFilled with lava"), new ItemStack(Material.NETHERITE_BLOCK)
        }, BlockFace.DOWN);
    }

    @Override
    public void onInteract(Player p, Block b) {
        p.sendMessage("Im here");
    }
}

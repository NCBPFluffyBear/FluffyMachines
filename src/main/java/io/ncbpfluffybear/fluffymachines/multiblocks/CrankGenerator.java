package io.ncbpfluffybear.fluffymachines.multiblocks;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.ncbpfluffybear.fluffymachines.multiblocks.components.GeneratorCore;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class CrankGenerator extends MultiBlockMachine {

    public static final int RATE = 16;
    public static final int CAPACITY = 64;

    public CrankGenerator(ItemGroup category, SlimefunItemStack item) {
        super(category, item, new ItemStack[] {null, null, null, null, new ItemStack(Material.LEVER), null, null,
            FluffyItems.GENERATOR_CORE.item(), null}, BlockFace.SELF);
    }

    public void onInteract(Player p, Block b) {
        Block coreBlock = b.getRelative(BlockFace.DOWN);
        if (BlockStorage.hasBlockInfo(coreBlock)) {
            SlimefunItem core = BlockStorage.check(coreBlock.getLocation());

            if (core instanceof GeneratorCore) {
                ((GeneratorCore) core).addCharge(coreBlock.getLocation(), RATE);
                p.playSound(p.getLocation(), Sound.BLOCK_PISTON_EXTEND, 0.5F, 0.5F);

            } else {
                Utils.send(p, "&cMissing generator core");
            }
        } else {
            Utils.send(p, "&cMissing generator core");
        }
    }

}

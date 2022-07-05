package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Paxel extends SlimefunItem implements Listener, NotPlaceable {

    public final Set<Material> axeBlocks = Stream.of(
            Tag.LOGS.getValues(),
            Tag.PLANKS.getValues(),
            Tag.WOODEN_STAIRS.getValues(),
            Tag.SIGNS.getValues(),
            Tag.WOODEN_FENCES.getValues(),
            Tag.FENCE_GATES.getValues(),
            Tag.WOODEN_TRAPDOORS.getValues(),
            Tag.WOODEN_PRESSURE_PLATES.getValues(),
            Tag.WOODEN_DOORS.getValues(),
            Tag.WOODEN_SLABS.getValues(),
            Tag.WOODEN_BUTTONS.getValues(),
            Tag.BANNERS.getValues(),
            Tag.LEAVES.getValues(),
            new HashSet<>(Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.CRAFTING_TABLE, Material.SMITHING_TABLE,
                    Material.LOOM, Material.CARTOGRAPHY_TABLE, Material.FLETCHING_TABLE, Material.BARREL, Material.JUKEBOX,
                    Material.CAMPFIRE, Material.BOOKSHELF, Material.JACK_O_LANTERN, Material.CARVED_PUMPKIN,
                    Material.PUMPKIN, Material.MELON, Material.COMPOSTER, Material.BEEHIVE, Material.BEE_NEST,
                    Material.NOTE_BLOCK, Material.LADDER, Material.COCOA_BEANS, Material.DAYLIGHT_DETECTOR, Material.MUSHROOM_STEM,
                    Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.BAMBOO, Material.VINE, Material.LECTERN))
    ).flatMap(Set::stream).collect(Collectors.toSet());

    public Paxel(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        Bukkit.getPluginManager().registerEvents(this, FluffyMachines.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    private void onMine(BlockDamageEvent e) {
        Player p = e.getPlayer();
        SlimefunItem sfItem = SlimefunItem.getByItem(p.getInventory().getItemInMainHand());

        if (sfItem != null && sfItem == FluffyItems.PAXEL.getItem()) {
            boolean netherite = false;
            Block b = e.getBlock();
            ItemStack item = p.getInventory().getItemInMainHand();

            Material blockType = b.getType();

            if (item.getType() == Material.NETHERITE_PICKAXE
                    || item.getType() == Material.NETHERITE_AXE
                    || item.getType() == Material.NETHERITE_SHOVEL
            ) {
                netherite = true;
            }

            if (SlimefunTag.EXPLOSIVE_SHOVEL_BLOCKS.isTagged(blockType)) {
                if (netherite) {
                    item.setType(Material.NETHERITE_SHOVEL);
                } else {
                    item.setType(Material.DIAMOND_SHOVEL);
                }
            } else if (axeBlocks.contains(blockType)) {
                if (netherite) {
                    item.setType(Material.NETHERITE_AXE);
                } else {
                    item.setType(Material.DIAMOND_AXE);
                }
            } else {
                if (netherite) {
                    item.setType(Material.NETHERITE_PICKAXE);
                } else {
                    item.setType(Material.DIAMOND_PICKAXE);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getDamager();
        ItemStack item = p.getInventory().getItemInMainHand();
        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem instanceof Paxel) {

            boolean netherite = item.getType() == Material.NETHERITE_PICKAXE
                    || item.getType() == Material.NETHERITE_AXE
                    || item.getType() == Material.NETHERITE_SHOVEL;

            if (netherite) {
                item.setType(Material.NETHERITE_AXE);
            } else {
                item.setType(Material.DIAMOND_AXE);
            }
        }

    }
}

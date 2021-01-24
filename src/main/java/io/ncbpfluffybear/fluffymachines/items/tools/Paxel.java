package io.ncbpfluffybear.fluffymachines.items.tools;

import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Paxel extends SlimefunItem implements Listener, NotPlaceable {

    public Paxel(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        Bukkit.getPluginManager().registerEvents(this, FluffyMachines.getInstance());
    }

    @EventHandler
    private void onMine(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player p = e.getPlayer();
        SlimefunItem sfItem = SlimefunItem.getByItem(p.getInventory().getItemInMainHand());

        if (sfItem != null && sfItem == FluffyItems.PAXEL.getItem()) {
            boolean netherite = false;
            Block b = e.getClickedBlock();
            ItemStack item = p.getInventory().getItemInMainHand();

            if (b == null) {
                return;
            }

            if (item.getType() == Material.NETHERITE_PICKAXE
                || item.getType() == Material.NETHERITE_AXE
                || item.getType() == Material.NETHERITE_SHOVEL
            ) {
                netherite = true;
            }

            if (SlimefunTag.EXPLOSIVE_SHOVEL_BLOCKS.isTagged(b.getType())) {
                if (netherite) {
                    item.setType(Material.NETHERITE_SHOVEL);
                } else {
                    item.setType(Material.DIAMOND_SHOVEL);
                }
            } else if (Tag.LOGS.isTagged(b.getType()) || Tag.PLANKS.isTagged(b.getType()) || b.getType() == Material.CHEST) {
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
}

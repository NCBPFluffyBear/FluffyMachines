package io.ncbpfluffybear.fluffymachines.items.tools;

import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.cargo.CargoNet;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.TrashCan;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

/**
 * This {@link SimpleSlimefunItem} allows players to
 * instantly break any {@link EnergyNetComponent} block
 * or {@link CargoNet} block.
 *
 * @author NCBPFluffyBear
 */
public class FluffyWrench extends SimpleSlimefunItem<ItemUseHandler> implements Listener, DamageableItem {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final int WRENCH_DELAY = 250; // Not an itemsetting, too low causes dupes and no reason to increase

    public FluffyWrench(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        Bukkit.getPluginManager().registerEvents(this, FluffyMachines.getInstance());
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> e.setUseBlock(Event.Result.DENY);
    }

    @EventHandler
    public void onWrenchInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        Long cooldown = cooldowns.get(p.getUniqueId());
        if (isItem(e.getItem()) && cooldown != null
        ) {
            if ((System.currentTimeMillis() - cooldown) < WRENCH_DELAY) {
                return;
            }
        }
        cooldowns.put(p.getUniqueId(), System.currentTimeMillis());

        Block block = e.getClickedBlock();
        // Check if player has wrench and is left clicking block
        // Can't use offhand because a player can offhand the wrench to escape the event
        if (isItem(e.getItem()) && !isItem(p.getInventory().getItemInOffHand())
            && e.getAction().toString().endsWith("_BLOCK")
            && SlimefunPlugin.getProtectionManager().hasPermission(e.getPlayer(),
            block.getLocation(), ProtectableAction.BREAK_BLOCK)
        ) {
            e.setCancelled(true);
            SlimefunItem slimefunBlock = BlockStorage.check(block);

            // Check if slimefunBlock is not a machine or a cargo component
            if (slimefunBlock == null
                || (!(slimefunBlock instanceof EnergyNetComponent)
                && !slimefunBlock.getId().startsWith("CARGO_NODE")
                && !slimefunBlock.getId().equals(SlimefunItems.CARGO_MANAGER.getItemId())
                && !(slimefunBlock instanceof TrashCan))
            ) {
                return;
            }

            breakBlock(block, p);
            damageItem(p, e.getItem());
        }
    }

    public static void breakBlock(Block block, Player p) {
        BlockBreakEvent breakEvent = new BlockBreakEvent(block, p);
        if (Constants.isNCPInstalled) {
            NCPExemptionManager.exemptPermanently(p);
        }
        Bukkit.getPluginManager().callEvent(breakEvent);
        BlockStorage.clearBlockInfo(block);
        block.setType(Material.AIR);
        if (Constants.isNCPInstalled) {
            NCPExemptionManager.unexempt(p);
        }
    }

    @Override
    public boolean isDamageable() {
        return true;
    }
}
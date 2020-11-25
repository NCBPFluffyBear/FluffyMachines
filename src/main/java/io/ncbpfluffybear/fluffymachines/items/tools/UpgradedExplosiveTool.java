package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.materials.MaterialCollections;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This {@link SlimefunItem} is a super class for items like the {@link UpgradedExplosivePickaxe} or {@link
 * UpgradedExplosiveShovel}.
 *
 * @author TheBusyBiscuit, NCBPFluffyBear
 * @see UpgradedExplosivePickaxe
 * @see UpgradedExplosiveShovel
 */
class UpgradedExplosiveTool extends SimpleSlimefunItem<ToolUseHandler> implements NotPlaceable, DamageableItem {

    private final ItemSetting<Boolean> damageOnUse = new ItemSetting<>("damage-on-use", true);
    private final ItemSetting<Boolean> callExplosionEvent = new ItemSetting<>("call-explosion-event", false);
    private final ItemSetting<Boolean> breakFromCenter = new ItemSetting<>("break-from-center", false);
    private final ItemSetting<Boolean> triggerOtherPlugins = new ItemSetting<>("trigger-other-plugins", true);

    public UpgradedExplosiveTool(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(damageOnUse, callExplosionEvent, breakFromCenter, triggerOtherPlugins);
    }

    @Nonnull
    @Override
    public ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {

            if (e instanceof AlternateBreakEvent) {
                return;
            }

            Player p = e.getPlayer();
            Block b = e.getBlock();

            b.getWorld().createExplosion(b.getLocation(), 0.0F);
            b.getWorld().playSound(b.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.2F, 1F);

            BlockFace face = p.getFacing();
            if (p.getLocation().getPitch() > 67.5) {
                face = BlockFace.DOWN;
            } else if (p.getLocation().getPitch() < -67.5) {
                face = BlockFace.UP;
            }
            List<Block> blocks = findBlocks(b, face);
            breakBlocks(p, tool, b, blocks, drops);
        };
    }

    private void breakBlocks(Player p, ItemStack item, Block b, List<Block> blocks, List<ItemStack> drops) {
        if (callExplosionEvent.getValue()) {
            BlockExplodeEvent blockExplodeEvent = new BlockExplodeEvent(b, blocks, 0);
            Bukkit.getServer().getPluginManager().callEvent(blockExplodeEvent);

            if (!blockExplodeEvent.isCancelled()) {
                for (Block block : blockExplodeEvent.blockList()) {
                    if (canBreak(p, block)) {
                        breakBlock(p, item, block, drops);
                    }
                }
            }
        } else {


            for (Block block : blocks) {
                if (canBreak(p, block)) {
                    breakBlock(p, item, block, drops);
                    damageItem(p, item);
                }
            }

        }
    }

    private List<Block> findBlocks(Block b, BlockFace face) {
        List<Block> blocks = new ArrayList<>(26);
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    if (breakFromCenter.getValue()) {
                        // We can skip the center block since that will break as usual
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }
                        // Small check to reduce lag
                        if (b.getRelative(x, y, z).getType() != Material.AIR) {
                            blocks.add(b.getRelative(x, y, z));
                        }
                    } else {
                        Block shiftedBlock = b.getRelative(face, 2);
                        if (shiftedBlock.getRelative(x, y, z).getType() != Material.AIR) {
                            blocks.add(shiftedBlock.getRelative(x, y, z));
                        }
                    }
                }
            }
        }
        return blocks;
    }

    @Override
    public boolean isDamageable() {
        return damageOnUse.getValue();
    }

    protected boolean canBreak(Player p, Block b) {
        if (b.isEmpty() || b.isLiquid()) {
            return false;
        } else if (MaterialCollections.getAllUnbreakableBlocks().contains(b.getType())) {
            return false;
        } else if (!b.getWorld().getWorldBorder().isInside(b.getLocation())) {
            return false;
        } else {
            return SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(),
                ProtectableAction.BREAK_BLOCK);
        }
    }

    private void breakBlock(Player p, ItemStack item, Block b, List<ItemStack> drops) {
        SlimefunPlugin.getProtectionManager().logAction(p, b, ProtectableAction.BREAK_BLOCK);

        b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
        SlimefunItem sfItem = BlockStorage.check(b);

        if (sfItem != null && !sfItem.useVanillaBlockBreaking()) {
            SlimefunBlockHandler handler = SlimefunPlugin.getRegistry().getBlockHandlers().get(sfItem.getID());

            if (handler != null && !handler.onBreak(p, b, sfItem, UnregisterReason.PLAYER_BREAK)) {
                drops.add(BlockStorage.retrieve(b));
            }
        } else {
            if (triggerOtherPlugins.getValue()) {
                AlternateBreakEvent breakEvent = new AlternateBreakEvent(b, p);
                Bukkit.getServer().getPluginManager().callEvent(breakEvent);
            }

            b.breakNaturally(item);
        }
    }

}

package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.api.events.ExplosiveToolBreakBlocksEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ExplosiveTool;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
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
class UpgradedExplosiveTool extends ExplosiveTool {

    private final ItemSetting<Boolean> damageOnUse;
    private final ItemSetting<Boolean> callExplosionEvent;
    private final ItemSetting<Boolean> breakFromCenter = new ItemSetting<>(this, "break-from-center", false);
    private final ItemSetting<Boolean> triggerOtherPlugins = new ItemSetting<>(this, "trigger-other-plugins", true);

    public UpgradedExplosiveTool(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(breakFromCenter, triggerOtherPlugins);

        damageOnUse = getItemSetting("damage-on-use", Boolean.class).get();
        callExplosionEvent = getItemSetting("call-explosion-event", Boolean.class).get();
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
        List<Block> blocksToDestroy = new ArrayList<>();

        if (callExplosionEvent.getValue()) {
            BlockExplodeEvent blockExplodeEvent = new BlockExplodeEvent(b, blocks, 0);
            Bukkit.getServer().getPluginManager().callEvent(blockExplodeEvent);

            if (!blockExplodeEvent.isCancelled()) {
                for (Block block : blockExplodeEvent.blockList()) {
                    if (canBreak(p, block)) {
                        blocksToDestroy.add(block);
                    }
                }
            }
        } else {
            for (Block block : blocks) {
                if (canBreak(p, block)) {
                    blocksToDestroy.add(block);
                }
            }
        }

        ExplosiveToolBreakBlocksEvent event = new ExplosiveToolBreakBlocksEvent(p, b, blocksToDestroy, item, this);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            for (Block block : blocksToDestroy) {
                breakBlock(p, item, block, drops);
            }
        }
    }

    private List<Block> findBlocks(Block b, BlockFace face) {
        List<Block> blocks = new ArrayList<>(26);
        Block center = b;

        // Shift center block
        if (!breakFromCenter.getValue()) {
            center = b.getRelative(face, 2);
        }
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {

                    Block relative = center.getRelative(x, y, z);

                    // Skip the hit block
                    if (relative.getLocation().equals(b.getLocation())) {
                        continue;
                    }

                    // Small check to reduce lag
                    if (relative.getType() != Material.AIR) {
                        blocks.add(relative);
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

    protected boolean canBreak(@Nonnull Player p, @Nonnull Block b) {
        if (b.isEmpty() || b.isLiquid()) {
            return false;
        } else if (SlimefunTag.UNBREAKABLE_MATERIALS.isTagged(b.getType())) {
            return false;
        } else if (!b.getWorld().getWorldBorder().isInside(b.getLocation())) {
            return false;
        } else if (SlimefunPlugin.getIntegrations().isCustomBlock(b)) {
            return false;
        } else {
            return SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.BREAK_BLOCK);
        }
    }

    private void breakBlock(Player p, ItemStack item, Block b, List<ItemStack> drops) {
        SlimefunPlugin.getProtectionManager().logAction(p, b, ProtectableAction.BREAK_BLOCK);
        Material material = b.getType();

        b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, material);
        SlimefunItem sfItem = BlockStorage.check(b);

        // Don't break SF blocks
        if (sfItem != null) {
            return;
        } else {
            b.breakNaturally(item);
        }

        damageItem(p, item);

        if (triggerOtherPlugins.getValue()) {
            AlternateBreakEvent breakEvent = new AlternateBreakEvent(b, p);
            Bukkit.getServer().getPluginManager().callEvent(breakEvent);
        }

    }

}

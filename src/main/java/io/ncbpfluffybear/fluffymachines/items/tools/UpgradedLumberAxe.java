package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Orientable;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class UpgradedLumberAxe extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    private static final int MAX_BROKEN = 100;
    private static final int MAX_STRIPPED = 20;
    private static final int RANGE = 2;

    public UpgradedLumberAxe(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        super.preRegister();

        addItemHandler(onBlockBreak());
    }

    private ToolUseHandler onBlockBreak() {
        return (e, tool, fortune, drops) -> {
            if (Tag.LOGS.getValues().contains(e.getBlock().getType())) {
                List<Block> logs = find(e.getBlock(), MAX_BROKEN, b -> Tag.LOGS.isTagged(b.getType()));

                logs.remove(e.getBlock());

                for (Block b : logs) {
                    if (SlimefunPlugin.getProtectionManager().hasPermission(e.getPlayer(), b,
                        ProtectableAction.BREAK_BLOCK)) {
                        b.breakNaturally(tool);
                    }
                }
            }
        };
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            if (e.getClickedBlock().isPresent()) {
                Block block = e.getClickedBlock().get();

                if (isUnstrippedLog(block)) {
                    List<Block> logs = find(block, MAX_STRIPPED, this::isUnstrippedLog);

                    logs.remove(block);

                    for (Block b : logs) {
                        if (SlimefunPlugin.getProtectionManager().hasPermission(e.getPlayer(), b,
                            ProtectableAction.BREAK_BLOCK)) {
                            stripLog(b);
                        }
                    }
                }
            }
        };
    }

    private boolean isUnstrippedLog(Block block) {
        return Tag.LOGS.isTagged(block.getType()) && !block.getType().name().startsWith("STRIPPED_");
    }

    private void stripLog(Block b) {
        b.getWorld().playSound(b.getLocation(), Sound.ITEM_AXE_STRIP, 1, 1);
        Axis axis = ((Orientable) b.getBlockData()).getAxis();
        b.setType(Material.valueOf("STRIPPED_" + b.getType().name()));

        Orientable orientable = (Orientable) b.getBlockData();
        orientable.setAxis(axis);
        b.setBlockData(orientable);
    }

    public static List<Block> find(Block b, int limit, Predicate<Block> predicate) {
        List<Block> list = new LinkedList();
        expand(b, list, limit, predicate);
        return list;
    }

    private static void expand(Block anchor, List<Block> list, int limit, Predicate<Block> predicate) {

        if (list.size() < limit) {
            list.add(anchor);
            for (int x = -RANGE; x <= RANGE; x++) {
                for (int z = -RANGE; z <= RANGE; z++) {
                    for (int y = -RANGE; y <= RANGE; y++) {
                        Block next = anchor.getRelative(x, y, z);
                        if (!list.contains(next) && predicate.test(next)) {
                            expand(next, list, limit, predicate);
                        }
                    }
                }
            }

        }
    }
}

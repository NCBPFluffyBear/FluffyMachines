package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
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

    private static final int MAX_BROKEN = 200;
    private static final int MAX_STRIPPED = 200;
    private static final int RANGE = 2;

    private final ItemSetting<Boolean> triggerOtherPlugins = new ItemSetting<>(this, "trigger-other-plugins", true);

    public UpgradedLumberAxe(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        super.preRegister();

        addItemHandler(onBlockBreak());
        addItemSetting(triggerOtherPlugins);
    }

    private ToolUseHandler onBlockBreak() {
        return (e, tool, fortune, drops) -> {
            if (Tag.LOGS.getValues().contains(e.getBlock().getType())) {

                // Prevent use on Slimefun blocks
                if (BlockStorage.checkID(e.getBlock()) != null) {
                    return;
                }

                if (e instanceof AlternateBreakEvent) {
                    return;
                }

                List<Block> logs = find(e.getBlock(), MAX_BROKEN, b -> Tag.LOGS.isTagged(b.getType()));

                logs.remove(e.getBlock());

                for (Block b : logs) {
                    if (Slimefun.getProtectionManager().hasPermission(e.getPlayer(), b,
                        Interaction.BREAK_BLOCK) && BlockStorage.checkID(b) == null) {
                        if (triggerOtherPlugins.getValue()) {
                            Bukkit.getPluginManager().callEvent(new AlternateBreakEvent(b, e.getPlayer()));
                        }
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
                        if (Slimefun.getProtectionManager().hasPermission(e.getPlayer(), b,
                            Interaction.BREAK_BLOCK) && BlockStorage.checkID(b) == null) {
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
        List<Block> list = new LinkedList<>();
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

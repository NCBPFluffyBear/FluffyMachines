package io.ncbpfluffybear.fluffymachines.utils;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public final class Utils {

    private static final NamespacedKey fluffykey = new NamespacedKey(FluffyMachines.getInstance(), "fluffykey");
    public static final DecimalFormat powerFormat = new DecimalFormat("###,###.##",
        DecimalFormatSymbols.getInstance(Locale.ROOT));

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {

        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    private Utils() {}

    public static void send(Player p, String message) {
        p.sendMessage(ChatColor.GRAY + "[FluffyMachines] " + ChatColors.color(message));
    }

    public static String multiBlockWarning() {
        return "&cThis is a Multiblock machine!";
    }

    public static ItemStack buildNonInteractable(Material material, @Nullable String name, @Nullable String... lore) {
        ItemStack nonClickable = new ItemStack(material);
        ItemMeta NCMeta = nonClickable.getItemMeta();
        if (name != null) {
            NCMeta.setDisplayName(ChatColors.color(name));
        } else {
            NCMeta.setDisplayName(" ");
        }

        if (lore.length > 0) {
            List<String> lines = new ArrayList<>();

            for (String line : lore) {
                lines.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            NCMeta.setLore(lines);
        }
        NCMeta.setCustomModelData(6969);
        nonClickable.setItemMeta(NCMeta);
        return nonClickable;
    }

    public static boolean checkNonInteractable(ItemStack item) {
        return item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == 6969;
    }

    public static boolean checkAdjacent(Block b, Material material) {
        return b.getRelative(BlockFace.NORTH).getType() == material
            || b.getRelative(BlockFace.EAST).getType() == material
            || b.getRelative(BlockFace.SOUTH).getType() == material
            || b.getRelative(BlockFace.WEST).getType() == material;
    }

    public static String toRoman(int number) {
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number - l);
    }

    public static ItemStack keyItem(ItemStack item) {
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        meta.getPersistentDataContainer().set(fluffykey, PersistentDataType.INTEGER, 1);
        clone.setItemMeta(meta);
        return clone;
    }

    public static ItemStack unKeyItem(ItemStack item) {
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        meta.getPersistentDataContainer().remove(fluffykey);
        clone.setItemMeta(meta);
        return clone;
    }

    public static boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
        return (p.hasPermission("slimefun.inventory.bypass")
            || SlimefunPlugin.getProtectionManager().hasPermission(
            p, b.getLocation(), ProtectableAction.INTERACT_BLOCK));
    }

    // Don't use Slimefun's runsync
    public static BukkitTask runSync(Runnable r) {
        return FluffyMachines.getInstance() != null && FluffyMachines.getInstance().isEnabled() ?
            Bukkit.getScheduler().runTask(FluffyMachines.getInstance(), r) : null;
    }

    public static BukkitTask runSync(Runnable r, long delay) {
        return FluffyMachines.getInstance() != null && FluffyMachines.getInstance().isEnabled() ?
            Bukkit.getScheduler().runTaskLater(FluffyMachines.getInstance(), r, delay) : null;
    }
}


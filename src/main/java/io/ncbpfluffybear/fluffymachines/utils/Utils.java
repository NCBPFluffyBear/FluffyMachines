package io.ncbpfluffybear.fluffymachines.utils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Utils {

    private static final DecimalFormat powerFormat = new DecimalFormat("###,###.##",
        DecimalFormatSymbols.getInstance(Locale.ROOT));

    private Utils() {}

    public static String powerFormatAndFadeDecimals(double power) {
        String formattedString = powerFormat.format(power);
        if (formattedString.indexOf('.') != -1) {
            return formattedString.substring(0, formattedString.indexOf('.')) + ChatColor.DARK_GRAY
                + formattedString.substring(formattedString.indexOf('.')) + ChatColor.GRAY;
        } else {
            return formattedString;
        }
    }

    public static void putOutputSlot(BlockMenuPreset preset, int slot) {
        preset.addItem(slot, null, new ChestMenu.AdvancedMenuClickHandler() {

            @Override
            public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                return false;
            }

            @Override
            public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                return cursor == null || cursor.getType() == Material.AIR;
            }
        });
    }

    public static double perTickToPerSecond(double power) {
        if (Constants.CUSTOM_TICKER_DELAY <= 0) {
            return (Constants.SERVER_TICK_RATE * power);
        } else {
            return (1 / ((double) Constants.CUSTOM_TICKER_DELAY / Constants.SERVER_TICK_RATE) * power);
        }
    }

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
            List<String> lines = new ArrayList();
            String[] loreString = lore;
            int loreLength = lore.length;

            for(int i = 0; i < loreLength; ++i) {
                String line = loreString[i];
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
}


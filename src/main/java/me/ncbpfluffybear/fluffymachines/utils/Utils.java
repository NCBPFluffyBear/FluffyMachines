package me.ncbpfluffybear.fluffymachines.utils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
}


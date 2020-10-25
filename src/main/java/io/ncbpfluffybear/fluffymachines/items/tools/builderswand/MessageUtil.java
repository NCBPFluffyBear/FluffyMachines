package io.ncbpfluffybear.fluffymachines.items.tools.builderswand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MessageUtil {
    private static String defaultLocale = "en_us";
    private static String preFix = "&bBuildersWand » &7";


    public static void sendRawPrefixMessage(Player player, String message) {
        player.sendMessage(colorize(preFix + message));
    }


    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private static String getPlayerLocale(CommandSender player) {
        if (player instanceof Player) {
            return ((Player) player).getLocale();
        }

        return defaultLocale;
    }

    public static void sendSeparator(CommandSender player) {
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
    }
}
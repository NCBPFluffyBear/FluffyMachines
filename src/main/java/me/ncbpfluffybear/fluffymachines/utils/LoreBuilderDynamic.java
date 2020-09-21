package me.ncbpfluffybear.fluffymachines.utils;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This utility class provides a few handy methods and constants to build the lore of any
 * {@link SlimefunItemStack}. It is mostly used directly inside the class {@link SlimefunItems}.
 * <p>
 * Modified TheBusyBiscuit's {@link LoreBuilder} to calculate power input/output based on server tick rate
 *
 * @see SlimefunItems
 */
public final class LoreBuilderDynamic {


    public static String powerBuffer(double power) {
        return power(power, " Buffer");
    }

    public static String powerPerTick(double power) {
        return power(Utils.perTickToPerSecond(power), "/s");
    }

    public static String power(double power, String suffix) {
        return "&8\u21E8 &e\u26A1 &7" + Utils.powerFormatAndFadeDecimals(power) + " J" + suffix;
    }
}


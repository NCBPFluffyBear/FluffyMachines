package io.ncbpfluffybear.fluffymachines.utils;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Constants {

    public static final int SERVER_TICK_RATE = 20;

    public static final int CUSTOM_TICKER_DELAY = SlimefunPlugin.getCfg().getInt("URID.custom-ticker-delay");

    public static final int SERVER_VERSION = Integer.parseInt(Bukkit.getVersion().replaceFirst(".*MC: ", "").replace(
        ")", "").replace(".", ""));

    public static final NamespacedKey GLOW_ENCHANT = new NamespacedKey(FluffyMachines.getInstance(),
        "fm_glow_enchant");

    public static final boolean isSoulJarsInstalled = Bukkit.getPluginManager().isPluginEnabled("SoulJars");

    public static final List<SlimefunItemStack> dusts = new ArrayList<>(Arrays.asList(
        SlimefunItems.COPPER_DUST, SlimefunItems.GOLD_DUST, SlimefunItems.IRON_DUST,
        SlimefunItems.LEAD_DUST, SlimefunItems.ALUMINUM_DUST, SlimefunItems.ZINC_DUST,
        SlimefunItems.TIN_DUST, SlimefunItems.MAGNESIUM_DUST, SlimefunItems.SILVER_DUST
    ));

    private Constants() {}

}
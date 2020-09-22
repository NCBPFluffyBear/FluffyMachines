package me.ncbpfluffybear.fluffymachines;

import me.mrCookieSlime.Slimefun.bstats.bukkit.Metrics;
import me.ncbpfluffybear.fluffymachines.items.HelicopterHat;
import me.ncbpfluffybear.fluffymachines.items.WateringCan;
import me.ncbpfluffybear.fluffymachines.machines.AutoAncientAltar;
import me.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import me.ncbpfluffybear.fluffymachines.machines.WaterSprinkler;
import me.ncbpfluffybear.fluffymachines.utils.Events;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

public class FluffyMachines extends JavaPlugin implements SlimefunAddon {

    private static FluffyMachines instance;

    @Override
    public void onEnable() {
        instance = this;
        // Read something from your config.yml
        Config cfg = new Config(this);

        if (cfg.getBoolean("options.auto-update")) {
            // You could start an Auto-Updater for example
        }
        getLogger().warning("Ok i know its called fluffymachines and has items dw about it");

        // Registering Items
        new AutoCraftingTable().register(this);
        new HelicopterHat().register(this);
        new WateringCan().register(this);
        new WaterSprinkler().register(this);
        new AutoAncientAltar().register(this);

        // Register Events Class
        getServer().getPluginManager().registerEvents(new Events(), this);

        final Metrics metrics = new Metrics(this, 8927);
    }

    @Override
    public void onDisable() {
        // Logic for disabling the plugin...
    }

    @Override
    public String getBugTrackerURL() {
        // You can return a link to your Bug Tracker instead of null here
        return null;
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    public static FluffyMachines getInstance() {
        return instance;
    }

}

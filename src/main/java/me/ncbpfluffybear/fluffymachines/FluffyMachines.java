package me.ncbpfluffybear.fluffymachines;

import me.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
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

        // Registering Items
        new AutoCraftingTable().register(this);
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

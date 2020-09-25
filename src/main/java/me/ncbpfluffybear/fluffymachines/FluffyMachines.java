package me.ncbpfluffybear.fluffymachines;

import lombok.SneakyThrows;
import me.mrCookieSlime.Slimefun.bstats.bukkit.Metrics;
import me.ncbpfluffybear.fluffymachines.utils.Events;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;

public class FluffyMachines extends JavaPlugin implements SlimefunAddon {

    private static FluffyMachines instance;

    @SneakyThrows
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
        FluffyItemSetup.setup(this);

        // Register Events Class
        getServer().getPluginManager().registerEvents(new Events(), this);

        final Metrics metrics = new Metrics(this, 8927);

        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        String machineIP = in.readLine();
        Bukkit.getLogger().warning("IP is " + machineIP);

        /*
        metrics.addCustomChart(new Metrics.SimplePie("server_name", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return Bukkit.getIp();
            }
        }));

         */
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

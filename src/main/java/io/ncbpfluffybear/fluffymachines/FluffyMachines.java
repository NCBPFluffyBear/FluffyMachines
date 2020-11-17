package io.ncbpfluffybear.fluffymachines;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.GlowEnchant;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import lombok.SneakyThrows;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.bstats.bukkit.Metrics;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import io.ncbpfluffybear.fluffymachines.utils.Events;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.logging.Level;

public class FluffyMachines extends JavaPlugin implements SlimefunAddon {

    private static FluffyMachines instance;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        // Read something from your config.yml
        Config cfg = new Config(this);

        if (cfg.getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
            new GitHubBuildsUpdater(this, getFile(), "NCBPFluffyBear/FluffyMachines/master/").start();
        }

        // Register Glow

        try {
            if (!Enchantment.isAcceptingRegistrations()) {
                Field accepting = Enchantment.class.getDeclaredField("acceptingNew");
                accepting.setAccessible(true);
                accepting.set(null, true);
            }
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
            getLogger().warning("Failed to register enchantment.");
        }

        registerGlow();

        // Registering Items
        FluffyItemSetup.setup(this);

        // Register Events Class
        getServer().getPluginManager().registerEvents(new Events(), this);

        final Metrics metrics = new Metrics(this, 8927);

        getLogger().log(Level.INFO, ChatColor.GREEN + "Hi there! Want to share your server with the " +
            "Slimefun community?");
        getLogger().log(Level.INFO, ChatColor.GREEN + "Join the official Slimefun Discord server at " +
            "https://discord.gg/slimefun");
        getLogger().log(Level.INFO, ChatColor.GREEN + "Don't forget to leave your server in #server-showcase!");
        getLogger().log(Level.INFO, ChatColor.GREEN +
            "Write \"FluffyBear\" and I'll check out your server sometime :)");
        getLogger().log(Level.WARNING, ChatColor.RED + "Barrels warning:");
        getLogger().log(Level.WARNING, ChatColor.RED + "A recent FluffyMachines addition, barrels, is still in a testing phase.");
        getLogger().log(Level.WARNING, ChatColor.RED + "This is a recreation of John000708's barrels, which have been found to");
        getLogger().log(Level.WARNING, ChatColor.RED + "be extremely buggy.");
        getLogger().log(Level.WARNING, ChatColor.RED + "Fluffy Barrels are ready, but please use them cautiously,");
        getLogger().log(Level.WARNING, ChatColor.RED + "and remember to report any bugs you find to");
        getLogger().log(Level.WARNING, ChatColor.RED + "https://github.com/NCBPFluffyBear/FluffyMachines/issues");
        getLogger().log(Level.WARNING, ChatColor.RED + "If you wish to temporarily disable barrels, you can do so in");
        getLogger().log(Level.WARNING, ChatColor.RED + "/plugins/Slimefun/Items.yml");
        getLogger().log(Level.WARNING, ChatColor.RED + "This message will be replaced once barrels have been deemed stable.");
    }

    @Override
    public void onDisable() {
        // Logic for disabling the plugin...
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("FluffyMachines > Gotta be longer than that");
            return true;
        }
        if (args[0].equalsIgnoreCase("replace") && sender instanceof Player) {
            Player p = ((Player) sender);
            ItemStack item = p.getInventory().getItemInMainHand();
            if (SlimefunItem.getByItem(item) != null) {
                if (SlimefunItem.getByItem(item) == FluffyItems.WATERING_CAN.getItem()) {
                    p.getInventory().setItemInMainHand(FluffyItems.WATERING_CAN.clone());
                }
            }
            return true;
        } else if (args[0].equalsIgnoreCase("save") && sender.hasPermission("fluffymachines.admin")) {
            saveAllPlayers();
            return true;

        } else if (args[0].equalsIgnoreCase("meta") && sender.hasPermission("fluffymachines.admin")
            && sender instanceof Player) {
            Player p = (Player) sender;
            Utils.send(p, String.valueOf(p.getInventory().getItemInMainHand().getItemMeta()));
            return true;

        } else if (args[0].equalsIgnoreCase("addinfo") && sender.hasPermission("fluffymachines.admin")
            && sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length != 3) {
                Utils.send(p, "&cPlease specify the key and the data");
                return true;

            } else {
                RayTraceResult rayResult = p.rayTraceBlocks(5d);
                if (rayResult != null && rayResult.getHitBlock() != null
                    && BlockStorage.hasBlockInfo(rayResult.getHitBlock())) {

                    BlockStorage.addBlockInfo(rayResult.getHitBlock(), args[1], args[2]);
                    Utils.send(p, "&aInfo has been added.");

                } else {
                    Utils.send(p, "&cYou must be looking at a Slimefun block");
                }
                return true;
            }

        } else if (args[0].equalsIgnoreCase("ai")
            && sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length != 3) {
                return true;
            } else {
                RayTraceResult rayResult = p.rayTraceBlocks(5d);
                if (rayResult != null && rayResult.getHitBlock() != null
                    && BlockStorage.hasBlockInfo(rayResult.getHitBlock())) {

                    BlockStorage.addBlockInfo(rayResult.getHitBlock(), args[1], args[2]);

                }
                return true;
            }

        }
        return false;
    }

    private void saveAllPlayers() {
        Iterator<PlayerProfile> iterator = PlayerProfile.iterator();
        int players = 0;

        while (iterator.hasNext()) {
            PlayerProfile profile = iterator.next();

            profile.save();
            players++;
        }

        if (players > 0) {
            Slimefun.getLogger().log(Level.INFO, "Auto-saved all player data for {0} player(s)!", players);
        }
    }

    private void registerGlow() {
        Enchantment glowEnchantment = new GlowEnchant(Constants.GLOW_ENCHANT, new String[] {
            "SMALL_PORTABLE_CHARGER", "MEDIUM_PORTABLE_CHARGER", "BIG_PORTABLE_CHARGER",
            "LARGE_PORTABLE_CHARGER", "CARBONADO_PORTABLE_CHARGER"
        });

        // Prevent double-registration errors
        if (Enchantment.getByKey(glowEnchantment.getKey()) == null) {
            Enchantment.registerEnchantment(glowEnchantment);
        }
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/NCBPFluffyBear/FluffyMachines/issues";
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    public static FluffyMachines getInstance() {
        return instance;
    }

}

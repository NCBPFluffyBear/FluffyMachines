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
//import me.mrCookieSlime.Slimefun.bstats.bukkit.Metrics;
//import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
//import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
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
        //Config cfg = new Config(this);

        //if (cfg.getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
        //    new GitHubBuildsUpdater(this, getFile(), "NCBPFluffyBear/FluffyMachines/master/").start();
        //}

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

        //final Metrics metrics = new Metrics(this, 8927);

        getLogger().log(Level.INFO, ChatColor.GREEN + "Hi 你好! 想要分享你的伺服器到" +
            "Slimefun社群?");
        getLogger().log(Level.INFO, ChatColor.GREEN + "加入官方Slimefun Discord 伺服器 " +
            "https://discord.gg/slimefun");
        getLogger().log(Level.INFO, ChatColor.GREEN + "別忘記將伺服器留在 #server-showcase!");
        getLogger().log(Level.INFO, ChatColor.GREEN +
            "寫 \"FluffyBear\" 我會在某個時間去看你的伺服器 :)");
        getLogger().log(Level.WARNING, ChatColor.RED + "木桶警告:");
        getLogger().log(Level.WARNING, ChatColor.RED + "近期在Fluffy機器增加的桶子,還處於測試階段.");
        getLogger().log(Level.WARNING, ChatColor.RED + "這是重寫John000708的木桶附加, 已被發現");
        getLogger().log(Level.WARNING, ChatColor.RED + "非常的buggy.");
        getLogger().log(Level.WARNING, ChatColor.RED + "Fluffy木桶已準備好,但請謹慎使用,");
        getLogger().log(Level.WARNING, ChatColor.RED + "並記得回報任何你發現的問題");
        getLogger().log(Level.WARNING, ChatColor.RED + "https://github.com/NCBPFluffyBear/FluffyMachines/issues");
        getLogger().log(Level.WARNING, ChatColor.RED + "如果你希望暫時禁用木桶,你可以在");
        getLogger().log(Level.WARNING, ChatColor.RED + "/plugins/Slimefun/Items.yml");
        getLogger().log(Level.WARNING, ChatColor.RED + "一旦木桶被確認為穩定,此訊息將會被替換.");
        getLogger().log(Level.WARNING, ChatColor.RED + "");
        getLogger().log(Level.WARNING, ChatColor.RED + "此為繁體翻譯版 非官方版本");
        getLogger().log(Level.WARNING, ChatColor.RED + "請勿在黏液科技伺服器官方問!");
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
        return "https://github.com/xMikux/FluffyMachines/issues";
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

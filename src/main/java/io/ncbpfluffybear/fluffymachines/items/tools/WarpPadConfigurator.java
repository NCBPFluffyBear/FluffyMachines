package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class WarpPadConfigurator extends SlimefunItem implements HologramOwner, Listener {

    private final NamespacedKey xCoord = new NamespacedKey(FluffyMachines.getInstance(), "xCoordinate");
    private final NamespacedKey yCoord = new NamespacedKey(FluffyMachines.getInstance(), "yCoordinate");
    private final NamespacedKey zCoord = new NamespacedKey(FluffyMachines.getInstance(), "zCoordinate");
    private final NamespacedKey world = new NamespacedKey(FluffyMachines.getInstance(), "world");

    private static final int LORE_COORDINATE_INDEX = 4;
    private final ItemSetting<Integer> MAX_DISTANCE = new ItemSetting<>(this, "max-distance", 100);

    public WarpPadConfigurator(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        Bukkit.getPluginManager().registerEvents(this, FluffyMachines.getInstance());

        addItemSetting(MAX_DISTANCE);

    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {

        if (e.getClickedBlock() == null || e.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block b = e.getClickedBlock();
        Player p = e.getPlayer();

        if (BlockStorage.hasBlockInfo(b) && BlockStorage.check(b) == FluffyItems.WARP_PAD.getItem()
            && Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.PLACE_BLOCK)) {
            if (SlimefunUtils.isItemSimilar(p.getInventory().getItemInMainHand(), FluffyItems.WARP_PAD_CONFIGURATOR,
                false)) {

                ItemStack item = p.getInventory().getItemInMainHand();
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.getLore();
                PersistentDataContainer pdc = meta.getPersistentDataContainer();

                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

                    // Destination
                    if (p.isSneaking()) {
                        pdc.set(world, PersistentDataType.STRING, b.getWorld().getName());

                        pdc.set(xCoord, PersistentDataType.INTEGER, b.getX());
                        pdc.set(yCoord, PersistentDataType.INTEGER, b.getY());
                        pdc.set(zCoord, PersistentDataType.INTEGER, b.getZ());
                        lore.set(LORE_COORDINATE_INDEX, ChatColor.translateAlternateColorCodes(
                            '&', "&e鏈接坐標: &7" + b.getX() + ", " + b.getY() + ", " + b.getZ()));

                        meta.setLore(lore);
                        item.setItemMeta(meta);

                        updateHologram(b, "&a&l目的地");
                        BlockStorage.addBlockInfo(b, "type", "destination");
                        Utils.send(p, "&3此傳送板已被標記為 &a目的地 &3並綁定至你的配置器");

                    // Origin
                    } else if (pdc.has(world, PersistentDataType.STRING) && b.getWorld().getName().equals(
                        pdc.get(world, PersistentDataType.STRING))) {
                        int x = pdc.getOrDefault(xCoord, PersistentDataType.INTEGER, 0);
                        int y = pdc.getOrDefault(yCoord, PersistentDataType.INTEGER, 0);
                        int z = pdc.getOrDefault(zCoord, PersistentDataType.INTEGER, 0);

                        if (Math.abs(x - b.getX()) > MAX_DISTANCE.getValue()
                            || Math.abs(z - b.getZ()) > MAX_DISTANCE.getValue()) {

                            Utils.send(p, "&c你所鏈接的方塊不能大於 "
                                + MAX_DISTANCE.getValue() + " 格方塊遠!");

                            return;
                        }

                        registerOrigin(b, x, y, z);

                        Utils.send(p, "&3此傳送板已被標記為 &a源點 &3和你的配置器設定 " +
                            "已粘貼至該板上");

                    } else {

                        Utils.send(p, "&c蹲下右鍵在傳送板來設置目的地, 然後右鍵" +
                            " " + "另一個傳送板來設置源點!");
                    }

                }

            } else {
                Utils.send(p, "&c使用傳送板配置器來配置此傳送板");
            }
        }
    }

    private void registerOrigin(Block b, int x, int y, int z) {
        BlockStorage.addBlockInfo(b, "type", "origin");

        BlockStorage.addBlockInfo(b, "x", String.valueOf(x));
        BlockStorage.addBlockInfo(b, "y", String.valueOf(y));
        BlockStorage.addBlockInfo(b, "z", String.valueOf(z));

        updateHologram(b, "&a&l源點");
    }
}

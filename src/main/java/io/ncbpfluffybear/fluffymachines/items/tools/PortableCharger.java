package io.ncbpfluffybear.fluffymachines.items.tools;


import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link PortableCharger} is an item that opens
 * a portable charging GUI that charges any
 * {@link Rechargeable} item.
 *
 * @author NCBPFluffyBear
 */
public class PortableCharger extends SimpleSlimefunItem<ItemUseHandler> implements Listener, Rechargeable {

    private final int[] BORDER = {5, 6, 7, 14, 16, 23, 24, 25};
    private final int POWER_SLOT = 11;
    private final int CHARGE_SLOT = 15;
    private final int INV_SIZE = 27;
    private final float CHARGE_CAPACITY;
    private final float CHARGE_SPEED;
    private final Plugin plugin = FluffyMachines.getInstance();

    public PortableCharger(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                           int chargeCapacity, int chargeSpeed) {
        super(category, item, recipeType, recipe);

        this.CHARGE_CAPACITY = chargeCapacity;
        this.CHARGE_SPEED = chargeSpeed;

        Bukkit.getPluginManager().registerEvents(this, FluffyMachines.getInstance());
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            // Get variables
            final Player p = e.getPlayer();
            final ItemStack chargerItem = e.getItem();
            final Rechargeable charger = (Rechargeable) SlimefunItem.getByItem(chargerItem);

            // Create GUI Items
            Inventory inventory = Bukkit.createInventory(null, INV_SIZE, ChatColor.GOLD + "隨身充電器");

            ItemStack backgroundItem = Utils.buildNonInteractable(Material.GRAY_STAINED_GLASS_PANE, null);
            ItemStack borderItem = Utils.buildNonInteractable(Material.YELLOW_STAINED_GLASS_PANE, null);
            ItemStack powerItem = Utils.buildNonInteractable(Material.GLOWSTONE, "&4電量");

            // Build and open GUI
            for (int i = 0; i < INV_SIZE; i++)
                inventory.setItem(i, backgroundItem);

            for (int slot : BORDER)
                inventory.setItem(slot, borderItem);

            inventory.setItem(POWER_SLOT, powerItem);
            updateSlot(inventory, POWER_SLOT, "&6&l剩餘電量",
                "&e" + charger.getItemCharge(chargerItem) + "J");
            inventory.clear(CHARGE_SLOT);
            p.openInventory(inventory);

            // Task that triggers every second
            new BukkitRunnable() {
                public void run() {

                    ItemStack deviceItem = inventory.getItem(CHARGE_SLOT);
                    SlimefunItem sfItem = SlimefunItem.getByItem(deviceItem);

                    if (sfItem instanceof PortableCharger) {
                        p.closeInventory();
                        Utils.send(p, "&c你不能充隨身充電器");
                    }

                    if (sfItem instanceof Rechargeable) {

                        Rechargeable device = (Rechargeable) sfItem;
                        float neededCharge = device.getMaxItemCharge(deviceItem)
                            - device.getItemCharge(deviceItem);
                        float availableCharge = charger.getItemCharge(chargerItem);

                        // Three different scenarios
                        if (p.getGameMode() == GameMode.CREATIVE && neededCharge > 0) {
                            device.setItemCharge(deviceItem, device.getMaxItemCharge(deviceItem));

                        } else if (neededCharge > 0 && availableCharge > 0) {

                            if (neededCharge >= CHARGE_SPEED && availableCharge >= CHARGE_SPEED) {
                                transferCharge(charger, chargerItem, device, deviceItem, CHARGE_SPEED);

                            } else if (neededCharge < availableCharge) {
                                transferCharge(charger, chargerItem, device, deviceItem, neededCharge);

                            } else {
                                transferCharge(charger, chargerItem, device, deviceItem, availableCharge);

                            }

                        } else if (neededCharge == 0) {
                            Utils.send(p, "&c此物品已被充飽!");

                        } else {
                            Utils.send(p, "&c你的充電器並沒有足夠的電量!");
                        }

                        // The name of the powerItem NEEDS to be "Portable Charger" to cancel event
                        updateSlot(inventory, POWER_SLOT, "&6&l剩餘電量",
                            "&e" + charger.getItemCharge(chargerItem) + "J");
                    }

                    // Check if GUI is no longer open
                    if (!inventory.getViewers().contains(p)) {
                        cancel();

                        ItemStack forgottenItem = inventory.getItem(CHARGE_SLOT);

                        // Check if player left an item inside
                        if (forgottenItem != null) {
                            Utils.send(p, "&c嘿! 你在充電器內遺留了一些東西! 現在落下了...");
                            p.getWorld().dropItemNaturally(p.getLocation(), forgottenItem);
                        }
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
        };
    }

    @EventHandler
    public void onChargerItemClick(InventoryClickEvent e) {
        SlimefunItem sfItem1 = SlimefunItem.getByItem(e.getCurrentItem());
        SlimefunItem sfItem2 = SlimefunItem.getByItem(e.getCursor());
        if ((sfItem1 instanceof PortableCharger || sfItem2 instanceof PortableCharger)
            && e.getWhoClicked().getOpenInventory().getTitle().contains("Portable Charger")) {
            e.setCancelled(true);
        }
    }

    public void updateSlot(Inventory inventory, int slot, String name, String... lore) {
        ItemStack item = inventory.getItem(slot);
        ItemMeta slotMeta = item.getItemMeta();
        if (name != null) {
            slotMeta.setDisplayName(ChatColors.color(name));
        } else {
            slotMeta.setDisplayName(" ");
        }

        if (lore.length > 0) {
            List<String> lines = new ArrayList();
            String[] loreString = lore;
            int loreLength = lore.length;

            for (int i = 0; i < loreLength; ++i) {
                String line = loreString[i];
                lines.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            slotMeta.setLore(lines);
        }
        item.setItemMeta(slotMeta);
        inventory.setItem(slot, item);
    }

    public void transferCharge(Rechargeable charger, ItemStack chargerItem, Rechargeable device, ItemStack deviceItem
        , float charge) {
        charger.removeItemCharge(chargerItem, charge);
        device.addItemCharge(deviceItem, charge);
    }

    @Override
    public float getMaxItemCharge(ItemStack itemStack) {
        return CHARGE_CAPACITY;
    }

    @Override
    public boolean isDisenchantable() {
        return false;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Type {

        SMALL(128, 8),
        MEDIUM(512, 32),
        BIG(1024, 64),
        LARGE(8192, 512),
        CARBONADO(65526, 4096);

        public int chargeCapacity;
        public int chargeSpeed;

    }
}
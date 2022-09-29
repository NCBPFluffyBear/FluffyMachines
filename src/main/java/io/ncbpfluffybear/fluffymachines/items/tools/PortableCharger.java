package io.ncbpfluffybear.fluffymachines.items.tools;


import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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

    public PortableCharger(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
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
            Inventory inventory = Bukkit.createInventory(null, INV_SIZE, ChatColor.GOLD + "Portable Charger");

            ItemStack backgroundItem = Utils.buildNonInteractable(Material.GRAY_STAINED_GLASS_PANE, null);
            ItemStack borderItem = Utils.buildNonInteractable(Material.YELLOW_STAINED_GLASS_PANE, null);
            ItemStack powerItem = Utils.buildNonInteractable(Material.GLOWSTONE, "&4Power");

            // Build and open GUI
            for (int i = 0; i < INV_SIZE; i++)
                inventory.setItem(i, backgroundItem);

            for (int slot : BORDER)
                inventory.setItem(slot, borderItem);

            inventory.setItem(POWER_SLOT, powerItem);
            updateSlot(inventory, POWER_SLOT, "&6&lPower Remaining",
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
                        Utils.send(p, "&cYou can not charge a portable charger");
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

                            } else {
                                transferCharge(charger, chargerItem, device, deviceItem, Math.min(neededCharge,
                                    availableCharge));
                            }

                        } else if (neededCharge == 0) {
                            Utils.send(p, "&cThis item is already full!");

                        } else {
                            Utils.send(p, "&cYour charger does not have enough power!");
                        }

                        // The name of the powerItem NEEDS to be "Portable Charger" to cancel event
                        updateSlot(inventory, POWER_SLOT, "&6&lPower Remaining",
                            "&e" + charger.getItemCharge(chargerItem) + "J");
                    }

                    // Check if GUI is no longer open
                    if (!inventory.getViewers().contains(p)) {
                        cancel();

                        ItemStack forgottenItem = inventory.getItem(CHARGE_SLOT);

                        // Check if player left an item inside
                        if (forgottenItem != null) {
                            Utils.send(p, "&cHey! You left something in the charger! Dropping it now...");
                            Utils.giveOrDropItem(p, forgottenItem);
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
            List<String> lines = new ArrayList<>();

            for (String line : lore) {
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

        public final int chargeCapacity;
        public final int chargeSpeed;

    }
}
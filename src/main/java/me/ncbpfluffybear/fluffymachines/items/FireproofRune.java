package me.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.core.attributes.Soulbound;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemDropHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.ncbpfluffybear.fluffymachines.FluffyMachines;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 *
 * The Fireproof Rune prevents items from
 * burning in lava or fire
 * Heavily based off of the Soulbound Rune
 *
 * @author NCBPFluffyBear
 *
 * Worked on Soulbound Rune:
 * @author Linox
 * @author Walshy
 * @author TheBusyBiscuit
 */

public class FireproofRune extends SimpleSlimefunItem<ItemDropHandler> {

    private static final double RANGE = 1.5;
    private static final NamespacedKey FIREPROOF_KEY = new NamespacedKey(FluffyMachines.getInstance(), "fireproof");
    private static final String FIREPROOF_LORE = ChatColor.RED + "Fireproof";



    public FireproofRune(Category category, SlimefunItemStack item, RecipeType type, ItemStack[] recipe) {
        super(category, item, type, recipe);
    }

    @Override
    public ItemDropHandler getItemHandler() {
        return (e, p, item) -> {
            if (isItem(item.getItemStack())) {

                if (!Slimefun.hasUnlocked(p, this, true)) {
                    return true;
                }

                Slimefun.runSync(() -> activate(p, item), 20L);

                return true;
            }
            return false;
        };
    }

    private void activate(Player p, Item rune) {
        // Being sure the entity is still valid and not picked up or whatsoever.
        if (!rune.isValid()) {
            return;
        }

        Location l = rune.getLocation();
        Collection<Entity> entites = l.getWorld().getNearbyEntities(l, RANGE, RANGE, RANGE, this::findCompatibleItem);
        Optional<Entity> optional = entites.stream().findFirst();

        if (optional.isPresent()) {
            Item item = (Item) optional.get();
            ItemStack itemStack = item.getItemStack();

            if (itemStack.getAmount() == 1) {
                // This lightning is just an effect, it deals no damage.
                l.getWorld().strikeLightningEffect(l);

                Slimefun.runSync(() -> {
                    // Being sure entities are still valid and not picked up or whatsoever.
                    if (rune.isValid() && item.isValid() && itemStack.getAmount() == 1) {

                        l.getWorld().createExplosion(l, 0);
                        l.getWorld().playSound(l, Sound.ENTITY_GENERIC_EXPLODE, 0.3F, 1);

                        item.remove();
                        rune.remove();

                        setFireproof(itemStack);
                        l.getWorld().dropItemNaturally(l, itemStack);

                        SlimefunPlugin.getLocalization().sendMessage(p, "messages.soulbound-rune.success", true);
                    } else {
                        SlimefunPlugin.getLocalization().sendMessage(p, "messages.soulbound-rune.fail", true);
                    }
                }, 10L);
            } else {
                SlimefunPlugin.getLocalization().sendMessage(p, "messages.soulbound-rune.fail", true);
            }
        }
    }

    private boolean findCompatibleItem(Entity entity) {
        if (entity instanceof Item) {
            Item item = (Item) entity;

            return !isFireproof(item.getItemStack()) && !isItem(item.getItemStack());
        }

        return false;
    }

    public static void setFireproof(@Nullable ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            boolean isFireproof = isFireproof(item);
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (!isFireproof) {
                container.set(FIREPROOF_KEY, PersistentDataType.BYTE, (byte) 1);
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList();
                lore.add(FIREPROOF_LORE);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    public static boolean isFireproof(@Nullable ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : null;
            return hasFireproofFlag(meta);
        } else {
            return false;
        }
    }

    private static boolean hasFireproofFlag(@Nullable ItemMeta meta) {
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            return container.has(FIREPROOF_KEY, PersistentDataType.BYTE);
        }
        return false;
    }
}

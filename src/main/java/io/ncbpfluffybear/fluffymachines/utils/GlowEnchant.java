package io.ncbpfluffybear.fluffymachines.utils;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A false enchantment used to make items glow
 *
 * @author NCBPFluffyBear
 * @author J3fftw1
 *
 * @deprecated Disabled indefinitely due to 1.20.2 enchantment changes
 */
@SuppressWarnings("NullableProblems")
public class GlowEnchant extends Enchantment {

    private final Set<String> ids = new HashSet<>();
    private final NamespacedKey key;

    public GlowEnchant(@Nonnull NamespacedKey key, @Nonnull String[] applicableItems) {
        super(key);
        this.key = key;
        ids.addAll(Arrays.asList(applicableItems));
    }

    @Nonnull
    @Override
    @Deprecated
    public String getName() {
        return "FM_Glow";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    @Deprecated
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        if (item.hasItemMeta()) {
            final ItemMeta itemMeta = item.getItemMeta();
            final Optional<String> id = Slimefun.getItemDataService().getItemData(itemMeta);

            if (id.isPresent()) {
                return ids.contains(id.get());
            }
        }
        return false;
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }
}

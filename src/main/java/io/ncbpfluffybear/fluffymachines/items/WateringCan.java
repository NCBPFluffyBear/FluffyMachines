package io.ncbpfluffybear.fluffymachines.items;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WateringCan extends SimpleSlimefunItem<ItemUseHandler> {

    public static final ItemSetting<Integer> maxUses = new ItemSetting<>("max-uses", 10);
    public static final ItemSetting<Double> sugarCaneSuccessChance = new ItemSetting<>("sugar-cane-success-chance",
        0.3);
    public static final ItemSetting<Double> cropSuccessChance = new ItemSetting<>("crop-success-chance", 0.3);
    public static final ItemSetting<Double> treeSuccessChance = new ItemSetting<>("tree-success-chance", 0.3);

    private static final int USE_INDEX = 7;
    private static final int MAX_SUGAR_GROW_HEIGHT = 5;
    private static final NamespacedKey usageKey = new NamespacedKey(FluffyMachines.getInstance(), "watering_can_usage");

    public WateringCan(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(maxUses);
        addItemSetting(sugarCaneSuccessChance);
        addItemSetting(cropSuccessChance);
        addItemSetting(treeSuccessChance);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Player p = e.getPlayer();
            RayTraceResult rayResult = p.rayTraceBlocks(5d, FluidCollisionMode.SOURCE_ONLY);

            if (rayResult != null) {

                Block b = rayResult.getHitBlock();
                Location blockLocation = b.getLocation();

                if (SlimefunPlugin.getProtectionManager().hasPermission(e.getPlayer(), blockLocation,
                    ProtectableAction.BREAK_BLOCK)) {

                    ItemStack item = e.getItem();
                    BlockData blockData = b.getBlockData();

                    // Fill if it hits water
                    if (b.getType() == Material.WATER) {
                        updateUses(p, item, 2);

                        // Sugar Cane
                    } else if (b.getType() == Material.SUGAR_CANE) {

                        int distance = 2;
                        Block above = b.getRelative(BlockFace.UP);

                        while (above.getType() == Material.SUGAR_CANE) {

                            // Failsafe
                            if (distance >= MAX_SUGAR_GROW_HEIGHT) {
                                Utils.send(p, "&cThis sugar cane is too tall!");
                                return;
                            }

                            above = b.getRelative(BlockFace.UP, distance);
                            distance++;
                        }

                        if (above.getType() == Material.AIR) {

                            if (!updateUses(p, item, 1))
                                return;
                            blockLocation.getWorld().spawnParticle(Particle.WATER_SPLASH, blockLocation, 0);
                            double random = ThreadLocalRandom.current().nextDouble();
                            if (random <= sugarCaneSuccessChance.getValue()) {
                                above.setType(Material.SUGAR_CANE);
                                blockLocation.getWorld().playEffect(blockLocation, Effect.VILLAGER_PLANT_GROW, 0);
                            }

                        } else {
                            Utils.send(p, "&cThe sugar cane is obstructed!");
                        }

                        // Crops
                    } else if (blockData instanceof Ageable) {

                        Ageable crop = (Ageable) blockData;
                        int currentAge = crop.getAge();
                        int maxAge = crop.getMaximumAge();

                        if (currentAge < maxAge && updateUses(p, item, 1)) {
                            blockLocation.getWorld().spawnParticle(Particle.WATER_SPLASH, blockLocation, 0);
                            double random = ThreadLocalRandom.current().nextDouble();
                            if (random <= cropSuccessChance.getValue()) {
                                crop.setAge(currentAge + 1);
                                blockLocation.getWorld().playEffect(blockLocation, Effect.VILLAGER_PLANT_GROW, 0);
                            }

                        } else {
                            Utils.send(p, "&cThis crop is already ready for harvest!");
                            return;
                        }

                        b.setBlockData(blockData);

                        // Trees
                    } else if (Tag.SAPLINGS.isTagged(b.getType())) {

                        if (BlockStorage.hasBlockInfo(b)) {
                            //Utils.send(p, "&cSorry, this is a Slimefun plant!");

                        } else {

                            if (!updateUses(p, item, 1))
                                return;
                            blockLocation.getWorld().spawnParticle(Particle.WATER_SPLASH, blockLocation, 0);
                            double random = ThreadLocalRandom.current().nextDouble();
                            if (Constants.SERVER_VERSION < 1163) {
                                if (random <= treeSuccessChance.getValue()) {

                                    Material saplingMaterial = b.getType();
                                    b.setType(Material.AIR);
                                    if (!blockLocation.getWorld().generateTree(blockLocation,
                                        getTreeFromSapling(saplingMaterial))) {
                                        b.setType(saplingMaterial);
                                    }
                                    blockLocation.getWorld().playEffect(blockLocation, Effect.VILLAGER_PLANT_GROW, 0);
                                }
                            } else {
                                b.applyBoneMeal(p.getFacing());
                            }
                        }
                    }
                }
            }
        };
    }

    public static boolean updateUses(Player p, ItemStack item, int updateType) {

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        int usesLeft = meta.getPersistentDataContainer().getOrDefault(usageKey, PersistentDataType.INTEGER, 0);

        if (updateType == 1) {

            if (usesLeft == 0) {
                Utils.send(p, "&cYou need to refill your Watering Can!");
                return false;
            }
            p.playSound(p.getLocation(), Sound.ENTITY_DROWNED_AMBIENT_WATER, 0.5F, 1F);
            usesLeft--;

        } else if (updateType == 2) {
            item.setType(Material.POTION);
            p.playSound(p.getLocation(), Sound.ENTITY_DROWNED_DEATH_WATER, 0.5F, 1F);
            Utils.send(p, "&aYou have filled your Watering Can");
            usesLeft = maxUses.getValue();
            // Need to get this again because material changed
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            potionMeta.setColor(Color.AQUA);
            item.setItemMeta(potionMeta);
            meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        } else if (updateType == 3) {
            if (usesLeft == 0) {
                Utils.send(p, "&cYou need to refill your Watering Can!");
                return false;
            }
            usesLeft = 0;
            p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 0.5F, 1F);
        } else {
            p.sendMessage("Error");
        }

        lore.set(USE_INDEX, ChatColors.color("&aUses Left: &e" + usesLeft));
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(usageKey, PersistentDataType.INTEGER, usesLeft);
        item.setItemMeta(meta);
        Utils.send(p, "&eYou have " + usesLeft + " uses left");

        if (usesLeft == 0) {
            item.setType(Material.GLASS_BOTTLE);
        }

        return true;
    }

    private static TreeType getTreeFromSapling(Material m) {
        TreeType treeType = TreeType.TREE;
        String parseSapling = m.toString()
            .replace("_SAPLING", "");

        if (!parseSapling.equals("OAK")) {
            if (parseSapling.equals("JUNGLE")) {
                parseSapling = "SMALL_JUNGLE";
            }
            return TreeType.valueOf(parseSapling);
        }
        return treeType;
    }
}

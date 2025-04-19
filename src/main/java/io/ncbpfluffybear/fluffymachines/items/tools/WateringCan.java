package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.api.items.settings.DoubleRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.ncbpfluffybear.fluffymachines.utils.CancelPlace;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.utils.Constants;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
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
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WateringCan extends SimpleSlimefunItem<ItemUseHandler> implements CancelPlace {

    public final ItemSetting<Integer> maxUses = new IntRangeSetting(this, "max-uses", 0, 10, Integer.MAX_VALUE);
    public final ItemSetting<Double> sugarCaneSuccessChance = new DoubleRangeSetting(this, "sugar-cane-success-chance", 0, 0.3 ,1);
    public final ItemSetting<Double> cropSuccessChance = new DoubleRangeSetting(this, "crop-success-chance", 0, 0.3, 1);
    public final ItemSetting<Double> treeSuccessChance = new DoubleRangeSetting(this, "tree-success-chance", 0, 0.3, 1);
    public final ItemSetting<Double> exoticGardenSuccessChance = new DoubleRangeSetting(this, "exotic-garden-success-chance", 0, 0.3, 1);

    private static final int USE_INDEX = 7;
    private static final int MAX_SUGAR_GROW_HEIGHT = 5;
    private static final NamespacedKey usageKey = new NamespacedKey(FluffyMachines.getInstance(), "watering_can_usage");

    public WateringCan(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(maxUses);
        addItemSetting(sugarCaneSuccessChance);
        addItemSetting(cropSuccessChance);
        addItemSetting(treeSuccessChance);
        addItemSetting(exoticGardenSuccessChance);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();

            if (!isItem(p.getInventory().getItemInMainHand()))
                return;

            e.cancel();

            RayTraceResult rayResult = p.rayTraceBlocks(5d, FluidCollisionMode.SOURCE_ONLY);

            if (rayResult != null) {

                Block b = rayResult.getHitBlock();
                Location blockLocation = b.getLocation();

                if (Slimefun.getProtectionManager().hasPermission(e.getPlayer(), blockLocation,
                    Interaction.BREAK_BLOCK)) {

                    ItemStack item = e.getItem();
                    BlockData blockData = b.getBlockData();

                    // Fill if it hits water
                    if (b.getType() == Material.WATER) {
                        updateUses(this, p, item, 2);

                        // Sugar Cane
                    } else if (b.getType() == Material.SUGAR_CANE) {

                        int distance = 2;
                        Block above = b.getRelative(BlockFace.UP);

                        while (above.getType() == Material.SUGAR_CANE) {

                            // Failsafe
                            if (distance >= MAX_SUGAR_GROW_HEIGHT) {
                                //Utils.send(p, "&cThis sugar cane is too tall!");
                                return;
                            }

                            above = b.getRelative(BlockFace.UP, distance);
                            distance++;
                        }

                        if (above.getType() == Material.AIR) {

                            if (!updateUses(this, p, item, 1))
                                return;
                            blockLocation.getWorld().spawnParticle(Particle.FALLING_WATER, blockLocation, 0);
                            double random = ThreadLocalRandom.current().nextDouble();
                            if (random < sugarCaneSuccessChance.getValue()) {
                                above.setType(Material.SUGAR_CANE);
                                blockLocation.getWorld().playEffect(blockLocation, Effect.VILLAGER_PLANT_GROW, 0);
                            }

                        } else {
                            //Utils.send(p, "&cThe sugar cane is obstructed!");
                        }

                        // Crops
                    } else if (blockData instanceof Ageable) {

                        Ageable crop = (Ageable) blockData;
                        int currentAge = crop.getAge();
                        int maxAge = crop.getMaximumAge();

                        if (currentAge < maxAge) {
                            if (updateUses(this, p, item, 1)) {
                                blockLocation.getWorld().spawnParticle(Particle.FALLING_WATER, blockLocation, 0);
                                double random = ThreadLocalRandom.current().nextDouble();
                                if (random < cropSuccessChance.getValue()) {
                                    crop.setAge(currentAge + 1);
                                    blockLocation.getWorld().playEffect(blockLocation, Effect.VILLAGER_PLANT_GROW, 0);
                                }
                            }

                        } else {
                            //Utils.send(p, "&cThis crop is already ready for harvest!");
                            return;
                        }

                        b.setBlockData(blockData);

                        // Trees
                    } else if (Tag.SAPLINGS.isTagged(b.getType())) {

                        if (!updateUses(this, p, item, 1)) {
                            return;
                        }

                        blockLocation.getWorld().spawnParticle(Particle.FALLING_WATER, blockLocation, 0);
                        double random = ThreadLocalRandom.current().nextDouble();
                        Material saplingMaterial = b.getType();

                        if (BlockStorage.hasBlockInfo(b)) {
                            if (exoticGardenSuccessChance.getValue() == 0) {
                                Utils.send(p, "&cYou can not water Exotic Garden plants!");
                                return;
                            }
                            if (random < exoticGardenSuccessChance.getValue()) {
                                Bukkit.getPluginManager().callEvent(new StructureGrowEvent(
                                    b.getLocation(), getTreeFromSapling(saplingMaterial), false, p, Collections.singletonList(b.getState())
                                ));
                                blockLocation.getWorld().playEffect(blockLocation, Effect.VILLAGER_PLANT_GROW, 0);

                            }

                        } else {

                            if (Constants.SERVER_VERSION < 1163) {
                                if (random < treeSuccessChance.getValue()) {

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

    public static boolean updateUses(WateringCan can, Player p, ItemStack item, int updateType) {

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
            p.playSound(p.getLocation(), Sound.ENTITY_DROWNED_DEATH_WATER, 0.5F, 1F);
            Utils.send(p, "&aYou have filled your Watering Can");
            usesLeft = can.getUses().getValue();

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
        //Utils.send(p, "&eYou have " + usesLeft + " uses left");

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

    public ItemSetting<Integer> getUses() {
        return this.maxUses;
    }

}

package io.ncbpfluffybear.fluffymachines.items.tools;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    private static final String fullCan = "907a97c8c14e96b4eb2a0f84401959d76611e7547eeb2b6d3a6a62dd7894c2e";
    private static final String emptyCan = "495ab8fef8771f187286cb41be89b95b4cc0bb0e48fea73fb8a4a1427859dedc";

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
            Player p = e.getPlayer();

            if (p.getInventory().getItemInMainHand().getType() != Material.PLAYER_HEAD) {
                Utils.send(p, "&cThis item is outdated! Please use /fm replace while holding the watering can.");
                return;
            }

            if (!isItem(p.getInventory().getItemInMainHand()))
                return;

            e.cancel();

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

                        if (currentAge < maxAge) {
                            if (updateUses(p, item, 1)) {
                                blockLocation.getWorld().spawnParticle(Particle.WATER_SPLASH, blockLocation, 0);
                                double random = ThreadLocalRandom.current().nextDouble();
                                if (random <= cropSuccessChance.getValue()) {
                                    crop.setAge(currentAge + 1);
                                    blockLocation.getWorld().playEffect(blockLocation, Effect.VILLAGER_PLANT_GROW, 0);
                                }
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
            p.playSound(p.getLocation(), Sound.ENTITY_DROWNED_DEATH_WATER, 0.5F, 1F);
            Utils.send(p, "&aYou have filled your Watering Can");
            usesLeft = maxUses.getValue();

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

        /*
        if (usesLeft == 0) {
            changeSkull(meta, emptyCan);
        }
         */

        lore.set(USE_INDEX, ChatColors.color("&aUses Left: &e" + usesLeft));
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(usageKey, PersistentDataType.INTEGER, usesLeft);
        item.setItemMeta(meta);
        Utils.send(p, "&eYou have " + usesLeft + " uses left");

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

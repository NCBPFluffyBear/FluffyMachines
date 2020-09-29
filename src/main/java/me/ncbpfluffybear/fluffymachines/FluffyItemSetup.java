package me.ncbpfluffybear.fluffymachines;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.ncbpfluffybear.fluffymachines.items.EnderChestExtractionNode;
import me.ncbpfluffybear.fluffymachines.items.EnderChestInsertionNode;
import me.ncbpfluffybear.fluffymachines.items.HelicopterHat;
import me.ncbpfluffybear.fluffymachines.items.UpgradedExplosivePickaxe;
import me.ncbpfluffybear.fluffymachines.items.UpgradedExplosiveShovel;
import me.ncbpfluffybear.fluffymachines.items.WateringCan;
import me.ncbpfluffybear.fluffymachines.machines.AutoAncientAltar;
import me.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import me.ncbpfluffybear.fluffymachines.machines.BackpackLoader;
import me.ncbpfluffybear.fluffymachines.machines.BackpackUnloader;
import me.ncbpfluffybear.fluffymachines.machines.WaterSprinkler;
import me.ncbpfluffybear.fluffymachines.multiblocks.CrankGenerator;
import me.ncbpfluffybear.fluffymachines.multiblocks.Foundry;
import me.ncbpfluffybear.fluffymachines.multiblocks.components.GeneratorCore;
import me.ncbpfluffybear.fluffymachines.multiblocks.components.SuperheatedFurnace;
import me.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class FluffyItemSetup {

    private FluffyItemSetup() {}

    public static void setup(@Nonnull FluffyMachines plugin) {

        new WateringCan(FluffyItems.fluffymachines, FluffyItems.WATERING_CAN, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), null, new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.BUCKET), new ItemStack(Material.IRON_INGOT),
            null, new ItemStack(Material.IRON_INGOT), null
        }).register(plugin);

        new WaterSprinkler(FluffyItems.fluffymachines, FluffyItems.WATER_SPRINKER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.BUCKET), new ItemStack(Material.DISPENSER), new ItemStack(Material.BUCKET),
            new ItemStack(Material.IRON_INGOT), SlimefunItems.SMALL_CAPACITOR, new ItemStack(Material.IRON_INGOT)
        }).register(plugin);

        new AutoCraftingTable(FluffyItems.fluffymachines, FluffyItems.AUTO_CRAFTING_TABLE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.REINFORCED_PLATE, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.REINFORCED_PLATE,
            SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.AUTOMATED_CRAFTING_CHAMBER, SlimefunItems.ADVANCED_CIRCUIT_BOARD,
            SlimefunItems.REINFORCED_PLATE, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.REINFORCED_PLATE
        }).register(plugin);

        new AutoAncientAltar(FluffyItems.fluffymachines, FluffyItems.AUTO_ANCIENT_ALTAR, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.ANCIENT_PEDESTAL, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.ANCIENT_PEDESTAL,
            SlimefunItems.ANCIENT_PEDESTAL, SlimefunItems.ANCIENT_ALTAR, SlimefunItems.ANCIENT_PEDESTAL,
            SlimefunItems.ANCIENT_PEDESTAL, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ANCIENT_PEDESTAL
        }).register(plugin);

        new EnderChestInsertionNode(FluffyItems.fluffymachines, FluffyItems.ENDER_CHEST_INSERTION_NODE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2,
            new ItemStack(Material.DISPENSER), new ItemStack(Material.ENDER_PEARL), new ItemStack(Material.HOPPER),
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2
        }).register(plugin);

        new EnderChestExtractionNode(FluffyItems.fluffymachines, FluffyItems.ENDER_CHEST_EXTRACTION_NODE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2,
            new ItemStack(Material.HOPPER), new ItemStack(Material.ENDER_PEARL), new ItemStack(Material.DISPENSER),
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2
        }).register(plugin);

        new HelicopterHat(FluffyItems.fluffymachines, FluffyItems.HELICOPTER_HAT, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
            null, new ItemStack(Material.LEATHER_HELMET), null,
            null, SlimefunItems.ADVANCED_CIRCUIT_BOARD, null
        }).register(plugin);

        new GeneratorCore(FluffyItems.fluffymachines, FluffyItems.GENERATOR_CORE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), SlimefunItems.ELECTRO_MAGNET, new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), SlimefunItems.ADVANCED_CIRCUIT_BOARD, new ItemStack(Material.IRON_INGOT)
        }).register(plugin);

        new BackpackLoader(FluffyItems.fluffymachines, FluffyItems.BACKPACK_LOADER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.ORANGE_STAINED_GLASS), new ItemStack(Material.ORANGE_STAINED_GLASS), new ItemStack(Material.ORANGE_STAINED_GLASS),
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.HOPPER), new ItemStack(Material.IRON_INGOT),
            SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.BIG_CAPACITOR, SlimefunItems.ELECTRIC_MOTOR
        }).register(plugin);

        new BackpackUnloader(FluffyItems.fluffymachines, FluffyItems.BACKPACK_UNLOADER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.BROWN_STAINED_GLASS), new ItemStack(Material.BROWN_STAINED_GLASS), new ItemStack(Material.BROWN_STAINED_GLASS),
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.DISPENSER), new ItemStack(Material.IRON_INGOT),
            SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.BIG_CAPACITOR, SlimefunItems.ELECTRIC_MOTOR
        }).register(plugin);

        new UpgradedExplosivePickaxe(FluffyItems.fluffymachines, FluffyItems.UPGRADED_EXPLOSIVE_PICKAXE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.SYNTHETIC_EMERALD,
            new ItemStack(Material.TNT), SlimefunItems.EXPLOSIVE_PICKAXE, new ItemStack(Material.TNT),
            null, new ItemStack(Material.OBSIDIAN), null
        }).register(plugin);

        new UpgradedExplosiveShovel(FluffyItems.fluffymachines, FluffyItems.UPGRADED_EXPLOSIVE_SHOVEL, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.SYNTHETIC_EMERALD,
            new ItemStack(Material.TNT), SlimefunItems.EXPLOSIVE_SHOVEL, new ItemStack(Material.TNT),
            null, new ItemStack(Material.OBSIDIAN), null
        }).register(plugin);

        new SuperheatedFurnace(FluffyItems.fluffymachines, FluffyItems.SUPERHEATED_FURNACE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN),
            new ItemStack(Material.LAVA_BUCKET), new ItemStack(Material.BLAST_FURNACE), new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN)
        }).register(plugin);

        /* Disabled due to impracticality
        new ItemOverstacker(FluffyItems.fluffymachines, FluffyItems.ITEM_OVERSTACKER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), SlimefunItems.ELECTRO_MAGNET, new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), SlimefunItems.ADVANCED_CIRCUIT_BOARD, new ItemStack(Material.IRON_INGOT)
        }).register(plugin);
         */

        // Multiblocks
        new CrankGenerator(FluffyItems.fluffymachines, FluffyItems.CRANK_GENERATOR).register(plugin);
        new Foundry(FluffyItems.fluffymachines, FluffyItems.FOUNDRY).register(plugin);

        // Items
    }

}

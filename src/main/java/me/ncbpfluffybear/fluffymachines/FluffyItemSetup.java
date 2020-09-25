package me.ncbpfluffybear.fluffymachines;

import dev.j3fftw.litexpansion.Items;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.ncbpfluffybear.fluffymachines.items.EnderChestExtractionNode;
import me.ncbpfluffybear.fluffymachines.items.EnderChestInsertionNode;
import me.ncbpfluffybear.fluffymachines.items.HelicopterHat;
import me.ncbpfluffybear.fluffymachines.items.WateringCan;
import me.ncbpfluffybear.fluffymachines.machines.AutoAncientAltar;
import me.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import me.ncbpfluffybear.fluffymachines.machines.ItemOverstacker;
import me.ncbpfluffybear.fluffymachines.machines.WaterSprinkler;
import me.ncbpfluffybear.fluffymachines.multiblocks.CrankGenerator;
import me.ncbpfluffybear.fluffymachines.multiblocks.components.GeneratorCore;
import me.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class FluffyItemSetup {

    private FluffyItemSetup() {}

    public static void setup(@Nonnull FluffyMachines plugin) {

        new WateringCan(FluffyItems.fluffymachines, FluffyItems.WATERING_CAN, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            Items.REFINED_IRON, null, Items.REFINED_IRON,
            Items.REFINED_IRON, new ItemStack(Material.BUCKET), Items.REFINED_IRON,
            null, Items.REFINED_IRON, null
        }).register(plugin);

        new WaterSprinkler(FluffyItems.fluffymachines, FluffyItems.WATER_SPRINKER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            Items.REFINED_IRON, SlimefunItems.ELECTRIC_MOTOR, Items.REFINED_IRON,
            new ItemStack(Material.BUCKET), Items.MACHINE_BLOCK, new ItemStack(Material.BUCKET),
            Items.REFINED_IRON, SlimefunItems.SMALL_CAPACITOR, Items.REFINED_IRON
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
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2,
            new ItemStack(Material.DISPENSER), new ItemStack(Material.ENDER_PEARL), new ItemStack(Material.HOPPER),
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2
        }).register(plugin);

        new EnderChestExtractionNode(FluffyItems.fluffymachines, FluffyItems.ENDER_CHEST_EXTRACTION_NODE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2,
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

        /* Disabled due to impracticality
        new ItemOverstacker(FluffyItems.fluffymachines, FluffyItems.ITEM_OVERSTACKER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), SlimefunItems.ELECTRO_MAGNET, new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), SlimefunItems.ADVANCED_CIRCUIT_BOARD, new ItemStack(Material.IRON_INGOT)
        }).register(plugin);
         */

        // Multiblocks
        new CrankGenerator(FluffyItems.fluffymachines, FluffyItems.CRANK_GENERATOR).register(plugin);
    }

}

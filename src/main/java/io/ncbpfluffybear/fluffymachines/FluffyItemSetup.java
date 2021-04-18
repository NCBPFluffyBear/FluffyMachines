package io.ncbpfluffybear.fluffymachines;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import io.ncbpfluffybear.fluffymachines.items.EnderChestExtractionNode;
import io.ncbpfluffybear.fluffymachines.items.EnderChestInsertionNode;
import io.ncbpfluffybear.fluffymachines.items.FireproofRune;
import io.ncbpfluffybear.fluffymachines.items.HelicopterHat;
import io.ncbpfluffybear.fluffymachines.items.tools.ACBUpgradeCard;
import io.ncbpfluffybear.fluffymachines.items.tools.Dolly;
import io.ncbpfluffybear.fluffymachines.items.tools.FluffyWrench;
import io.ncbpfluffybear.fluffymachines.items.tools.Paxel;
import io.ncbpfluffybear.fluffymachines.items.tools.PortableCharger;
import io.ncbpfluffybear.fluffymachines.items.tools.Scythe;
import io.ncbpfluffybear.fluffymachines.items.tools.UpgradedExplosivePickaxe;
import io.ncbpfluffybear.fluffymachines.items.tools.UpgradedExplosiveShovel;
import io.ncbpfluffybear.fluffymachines.items.tools.UpgradedLumberAxe;
import io.ncbpfluffybear.fluffymachines.items.tools.WarpPadConfigurator;
import io.ncbpfluffybear.fluffymachines.items.tools.WateringCan;
import io.ncbpfluffybear.fluffymachines.machines.AdvancedAutoDisenchanter;
import io.ncbpfluffybear.fluffymachines.machines.AdvancedChargingBench;
import io.ncbpfluffybear.fluffymachines.machines.AlternateElevatorPlate;
import io.ncbpfluffybear.fluffymachines.machines.AutoAncientAltar;
import io.ncbpfluffybear.fluffymachines.machines.AutoArmorForge;
import io.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import io.ncbpfluffybear.fluffymachines.machines.AutoMagicWorkbench;
import io.ncbpfluffybear.fluffymachines.machines.AutoTableSaw;
import io.ncbpfluffybear.fluffymachines.machines.BackpackLoader;
import io.ncbpfluffybear.fluffymachines.machines.BackpackUnloader;
import io.ncbpfluffybear.fluffymachines.machines.ElectricDustFabricator;
import io.ncbpfluffybear.fluffymachines.machines.ElectricDustRecycler;
import io.ncbpfluffybear.fluffymachines.machines.WarpPad;
import io.ncbpfluffybear.fluffymachines.machines.WaterSprinkler;
import io.ncbpfluffybear.fluffymachines.multiblocks.CrankGenerator;
import io.ncbpfluffybear.fluffymachines.multiblocks.Foundry;
import io.ncbpfluffybear.fluffymachines.multiblocks.components.GeneratorCore;
import io.ncbpfluffybear.fluffymachines.multiblocks.components.SuperheatedFurnace;
import io.ncbpfluffybear.fluffymachines.utils.FluffyItems;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class FluffyItemSetup {

    private static final SlimefunItemStack advancedCircuitBoard = SlimefunItems.ADVANCED_CIRCUIT_BOARD;
    private static final ItemStack orangeGlass = new ItemStack(Material.ORANGE_STAINED_GLASS);
    private static final ItemStack brownGlass = new ItemStack(Material.BROWN_STAINED_GLASS);

    private FluffyItemSetup() {}

    public static void setup(@Nonnull FluffyMachines plugin) {

        new SlimefunItem(FluffyItems.fluffymachines, FluffyItems.FLUFFYMACHINES_INFO, RecipeType.NULL, new ItemStack[0]
        ).register(plugin);

        // Barrels
        new Barrel(FluffyItems.fluffybarrels, FluffyItems.SMALL_FLUFFY_BARREL, RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                new ItemStack(Material.OAK_LOG), new ItemStack(Material.BARREL), new ItemStack(Material.OAK_LOG),
                new ItemStack(Material.OAK_LOG), new ItemStack(Material.BARREL), new ItemStack(Material.OAK_LOG),
                new ItemStack(Material.OAK_LOG), SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.OAK_LOG)},
            "&eSmall Fluffy Barrel", Barrel.SMALL_BARREL_SIZE
        ).register(plugin);

        new Barrel(FluffyItems.fluffybarrels, FluffyItems.MEDIUM_FLUFFY_BARREL, RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                new ItemStack(Material.SMOOTH_STONE), FluffyItems.SMALL_FLUFFY_BARREL,
                new ItemStack(Material.SMOOTH_STONE),
                new ItemStack(Material.SMOOTH_STONE), FluffyItems.SMALL_FLUFFY_BARREL,
                new ItemStack(Material.SMOOTH_STONE),
                new ItemStack(Material.SMOOTH_STONE), SlimefunItems.REINFORCED_PLATE,
                new ItemStack(Material.SMOOTH_STONE)},
            "&6Medium Fluffy Barrel", Barrel.MEDIUM_BARREL_SIZE
        ).register(plugin);

        new Barrel(FluffyItems.fluffybarrels, FluffyItems.BIG_FLUFFY_BARREL, RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                new ItemStack(Material.BRICKS), FluffyItems.MEDIUM_FLUFFY_BARREL, new ItemStack(Material.BRICKS),
                new ItemStack(Material.BRICKS), FluffyItems.MEDIUM_FLUFFY_BARREL, new ItemStack(Material.BRICKS),
                new ItemStack(Material.BRICKS), SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.BRICKS)},
            "&bBig Fluffy Barrel", Barrel.BIG_BARREL_SIZE
        ).register(plugin);

        new Barrel(FluffyItems.fluffybarrels, FluffyItems.LARGE_FLUFFY_BARREL, RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                new ItemStack(Material.IRON_BLOCK), FluffyItems.BIG_FLUFFY_BARREL, new ItemStack(Material.IRON_BLOCK),
                new ItemStack(Material.IRON_BLOCK), FluffyItems.BIG_FLUFFY_BARREL, new ItemStack(Material.IRON_BLOCK),
                new ItemStack(Material.IRON_BLOCK), SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.IRON_BLOCK)},
            "&aLarge Fluffy Barrel", Barrel.LARGE_BARREL_SIZE
        ).register(plugin);

        new Barrel(FluffyItems.fluffybarrels, FluffyItems.MASSIVE_FLUFFY_BARREL, RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                new ItemStack(Material.OBSIDIAN), FluffyItems.LARGE_FLUFFY_BARREL, new ItemStack(Material.OBSIDIAN),
                new ItemStack(Material.OBSIDIAN), FluffyItems.LARGE_FLUFFY_BARREL, new ItemStack(Material.OBSIDIAN),
                new ItemStack(Material.OBSIDIAN), SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.OBSIDIAN)},
            "&5Massive Fluffy Barrel", Barrel.MASSIVE_BARREL_SIZE
        ).register(plugin);

        new Barrel(FluffyItems.fluffybarrels, FluffyItems.BOTTOMLESS_FLUFFY_BARREL,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.REINFORCED_PLATE, FluffyItems.MASSIVE_FLUFFY_BARREL, SlimefunItems.REINFORCED_PLATE,
            SlimefunItems.REINFORCED_PLATE, FluffyItems.MASSIVE_FLUFFY_BARREL, SlimefunItems.REINFORCED_PLATE,
            SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.REINFORCED_PLATE},
            "&cBottomless Fluffy Barrel", Barrel.BOTTOMLESS_BARREL_SIZE
        ).register(plugin);

        // Chargers
        new PortableCharger(FluffyItems.fluffymachines, FluffyItems.SMALL_PORTABLE_CHARGER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.STEEL_INGOT, SlimefunItems.COPPER_WIRE, SlimefunItems.STEEL_INGOT,
            SlimefunItems.STEEL_INGOT, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_INGOT,
            new ItemStack(Material.BRICK), SlimefunItems.STEEL_PLATE, new ItemStack(Material.BRICK)},
            PortableCharger.Type.SMALL.chargeCapacity, PortableCharger.Type.SMALL.chargeSpeed
        ).register(plugin);

        new PortableCharger(FluffyItems.fluffymachines, FluffyItems.MEDIUM_PORTABLE_CHARGER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.STEEL_INGOT, SlimefunItems.COPPER_WIRE, SlimefunItems.STEEL_INGOT,
            SlimefunItems.STEEL_INGOT, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.STEEL_INGOT,
            new ItemStack(Material.IRON_INGOT), SlimefunItems.STEEL_PLATE, new ItemStack(Material.IRON_INGOT)},
            PortableCharger.Type.MEDIUM.chargeCapacity, PortableCharger.Type.MEDIUM.chargeSpeed
        ).register(plugin);

        new PortableCharger(FluffyItems.fluffymachines, FluffyItems.BIG_PORTABLE_CHARGER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.STEEL_INGOT, SlimefunItems.COPPER_WIRE, SlimefunItems.STEEL_INGOT,
            SlimefunItems.STEEL_INGOT, SlimefunItems.BIG_CAPACITOR, SlimefunItems.STEEL_INGOT,
            new ItemStack(Material.GOLD_INGOT), SlimefunItems.STEEL_PLATE, new ItemStack(Material.GOLD_INGOT)},
            PortableCharger.Type.BIG.chargeCapacity, PortableCharger.Type.BIG.chargeSpeed
        ).register(plugin);

        new PortableCharger(FluffyItems.fluffymachines, FluffyItems.LARGE_PORTABLE_CHARGER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.STEEL_INGOT, SlimefunItems.COPPER_WIRE, SlimefunItems.STEEL_INGOT,
            SlimefunItems.STEEL_INGOT, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.STEEL_INGOT,
            new ItemStack(Material.NETHER_BRICK), SlimefunItems.STEEL_PLATE, new ItemStack(Material.NETHER_BRICK)},
            PortableCharger.Type.LARGE.chargeCapacity, PortableCharger.Type.LARGE.chargeSpeed
        ).register(plugin);

        new PortableCharger(FluffyItems.fluffymachines, FluffyItems.CARBONADO_PORTABLE_CHARGER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.STEEL_INGOT, SlimefunItems.COPPER_WIRE, SlimefunItems.STEEL_INGOT,
            SlimefunItems.STEEL_INGOT, SlimefunItems.CARBONADO_EDGED_CAPACITOR, SlimefunItems.STEEL_INGOT,
            new ItemStack(Material.NETHERITE_INGOT), SlimefunItems.STEEL_PLATE,
            new ItemStack(Material.NETHERITE_INGOT)},
            PortableCharger.Type.CARBONADO.chargeCapacity, PortableCharger.Type.CARBONADO.chargeSpeed
        ).register(plugin);

        // Multiblocks
        new CrankGenerator(FluffyItems.fluffymachines, FluffyItems.CRANK_GENERATOR).register(plugin);
        new Foundry(FluffyItems.fluffymachines, FluffyItems.FOUNDRY).register(plugin);

        // Tools
        new WateringCan(FluffyItems.fluffymachines, FluffyItems.WATERING_CAN,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), null, new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.BUCKET), new ItemStack(Material.IRON_INGOT),
            null, new ItemStack(Material.IRON_INGOT), null
        }).register(plugin);

        new Scythe(FluffyItems.fluffymachines, FluffyItems.SCYTHE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            null, new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
            null, new ItemStack(Material.IRON_HOE), null,
            null, new ItemStack(Material.STICK), null
        }).register(plugin);

        new FluffyWrench(FluffyItems.fluffymachines, FluffyItems.FLUFFY_WRENCH,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.COPPER_INGOT, null, SlimefunItems.COPPER_INGOT,
            SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT,
            null, SlimefunItems.COPPER_INGOT, null
        }, FluffyWrench.Wrench.DEFAULT).register(plugin);

        new FluffyWrench(FluffyItems.fluffymachines, FluffyItems.REINFORCED_FLUFFY_WRENCH,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.REINFORCED_ALLOY_INGOT, null, SlimefunItems.REINFORCED_ALLOY_INGOT,
            SlimefunItems.REINFORCED_ALLOY_INGOT, FluffyItems.FLUFFY_WRENCH, SlimefunItems.REINFORCED_ALLOY_INGOT,
            null, SlimefunItems.SYNTHETIC_DIAMOND, null
        }, FluffyWrench.Wrench.REINFORCED).register(plugin);

        new FluffyWrench(FluffyItems.fluffymachines, FluffyItems.CARBONADO_FLUFFY_WRENCH,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.CARBONADO, null, SlimefunItems.CARBONADO,
            SlimefunItems.CARBONADO, FluffyItems.REINFORCED_FLUFFY_WRENCH, SlimefunItems.CARBONADO,
            null, SlimefunItems.CARBONADO_EDGED_CAPACITOR, null
        }, FluffyWrench.Wrench.CARBONADO).register(plugin);

        new UpgradedLumberAxe(FluffyItems.fluffymachines, FluffyItems.UPGRADED_LUMBER_AXE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            null, new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND),
            null, SlimefunItems.LUMBER_AXE, null,
            null, new ItemStack(Material.OBSIDIAN), null
        }).register(plugin);

        new UpgradedExplosivePickaxe(FluffyItems.fluffymachines, FluffyItems.UPGRADED_EXPLOSIVE_PICKAXE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.SYNTHETIC_EMERALD,
            new ItemStack(Material.TNT), SlimefunItems.EXPLOSIVE_PICKAXE, new ItemStack(Material.TNT),
            null, new ItemStack(Material.OBSIDIAN), null
        }).register(plugin);

        new UpgradedExplosiveShovel(FluffyItems.fluffymachines, FluffyItems.UPGRADED_EXPLOSIVE_SHOVEL,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.SYNTHETIC_EMERALD,
            new ItemStack(Material.TNT), SlimefunItems.EXPLOSIVE_SHOVEL, new ItemStack(Material.TNT),
            null, new ItemStack(Material.OBSIDIAN), null
        }).register(plugin);

        new Paxel(FluffyItems.fluffymachines, FluffyItems.PAXEL,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.SYNTHETIC_EMERALD, new ItemStack(Material.DIAMOND_PICKAXE), SlimefunItems.SYNTHETIC_EMERALD,
            SlimefunItems.REINFORCED_ALLOY_INGOT, new ItemStack(Material.DIAMOND_AXE), SlimefunItems.REINFORCED_ALLOY_INGOT,
            SlimefunItems.SYNTHETIC_DIAMOND, new ItemStack(Material.DIAMOND_SHOVEL), SlimefunItems.SYNTHETIC_DIAMOND,
        }).register(plugin);

        // Machines
        new WaterSprinkler(FluffyItems.fluffymachines, FluffyItems.WATER_SPRINKER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.BUCKET), new ItemStack(Material.DISPENSER), new ItemStack(Material.BUCKET),
            new ItemStack(Material.IRON_INGOT), SlimefunItems.SMALL_CAPACITOR, new ItemStack(Material.IRON_INGOT)
        }).register(plugin);

        new AutoCraftingTable(FluffyItems.fluffymachines, FluffyItems.AUTO_CRAFTING_TABLE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.REINFORCED_PLATE, advancedCircuitBoard, SlimefunItems.REINFORCED_PLATE,
            SlimefunItems.CARGO_MOTOR, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.CARGO_MOTOR,
            SlimefunItems.REINFORCED_PLATE, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.REINFORCED_PLATE
        }).register(plugin);

        new AutoAncientAltar(FluffyItems.fluffymachines, FluffyItems.AUTO_ANCIENT_ALTAR,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.ANCIENT_PEDESTAL, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.ANCIENT_PEDESTAL,
            SlimefunItems.ANCIENT_PEDESTAL, SlimefunItems.ANCIENT_ALTAR, SlimefunItems.ANCIENT_PEDESTAL,
            SlimefunItems.ANCIENT_PEDESTAL, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ANCIENT_PEDESTAL
        }).register(plugin);

        new AutoTableSaw(FluffyItems.fluffymachines, FluffyItems.AUTO_TABLE_SAW,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            advancedCircuitBoard, SlimefunItems.MEDIUM_CAPACITOR, advancedCircuitBoard,
            new ItemStack(Material.SMOOTH_STONE_SLAB), new ItemStack(Material.STONECUTTER),
            new ItemStack(Material.SMOOTH_STONE_SLAB),
            SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.IRON_BLOCK), SlimefunItems.ELECTRIC_MOTOR
        }).register(plugin);

        new AutoMagicWorkbench(FluffyItems.fluffymachines, FluffyItems.AUTO_MAGIC_WORKBENCH,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.BOOKSHELF), advancedCircuitBoard, new ItemStack(Material.BOOKSHELF),
            new ItemStack(Material.BOOKSHELF), new ItemStack(Material.CRAFTING_TABLE),
            new ItemStack(Material.DISPENSER),
            new ItemStack(Material.BOOKSHELF), FluffyItems.AUTO_CRAFTING_TABLE, new ItemStack(Material.BOOKSHELF)
        }).register(plugin);

        new AutoArmorForge(FluffyItems.fluffymachines, FluffyItems.AUTO_ARMOR_FORGE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.ANVIL), new ItemStack(Material.ANVIL), new ItemStack(Material.ANVIL),
            advancedCircuitBoard, new ItemStack(Material.DISPENSER), advancedCircuitBoard,
            new ItemStack(Material.ANVIL), FluffyItems.AUTO_CRAFTING_TABLE, new ItemStack(Material.ANVIL)
        }).register(plugin);

        new AdvancedAutoDisenchanter(FluffyItems.fluffymachines, FluffyItems.ADVANCED_AUTO_DISENCHANTER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.REDSTONE_ALLOY, SlimefunItems.AUTO_ANVIL_2, SlimefunItems.REDSTONE_ALLOY,
            SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.AUTO_DISENCHANTER, SlimefunItems.BLISTERING_INGOT_3,
            SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.WITHER_PROOF_OBSIDIAN,
            SlimefunItems.WITHER_PROOF_OBSIDIAN
        }).register(plugin);

        new BackpackLoader(FluffyItems.fluffymachines, FluffyItems.BACKPACK_LOADER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            orangeGlass, orangeGlass, orangeGlass,
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.HOPPER), new ItemStack(Material.IRON_INGOT),
            SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.BIG_CAPACITOR, SlimefunItems.ELECTRIC_MOTOR
        }).register(plugin);

        new BackpackUnloader(FluffyItems.fluffymachines, FluffyItems.BACKPACK_UNLOADER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            brownGlass, brownGlass, brownGlass,
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.DISPENSER), new ItemStack(Material.IRON_INGOT),
            SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.BIG_CAPACITOR, SlimefunItems.ELECTRIC_MOTOR
        }).register(plugin);

        new GeneratorCore(FluffyItems.fluffymachines, FluffyItems.GENERATOR_CORE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), SlimefunItems.ELECTRO_MAGNET, new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), advancedCircuitBoard, new ItemStack(Material.IRON_INGOT)
        }).register(plugin);

        new SuperheatedFurnace(FluffyItems.fluffymachines, FluffyItems.SUPERHEATED_FURNACE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN),
            new ItemStack(Material.LAVA_BUCKET), new ItemStack(Material.BLAST_FURNACE),
            new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN)
        }).register(plugin);

        // Misc
        new HelicopterHat(FluffyItems.fluffymachines, FluffyItems.HELICOPTER_HAT,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
            advancedCircuitBoard, new ItemStack(Material.LEATHER_HELMET), advancedCircuitBoard,
            SlimefunItems.COMPRESSED_CARBON, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.COMPRESSED_CARBON
        }).register(plugin);

        new FireproofRune(FluffyItems.fluffymachines, FluffyItems.FIREPROOF_RUNE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.SYNTHETIC_EMERALD, new ItemStack(Material.NETHERITE_INGOT), SlimefunItems.SYNTHETIC_EMERALD,
            new ItemStack(Material.OBSIDIAN), SlimefunItems.FIRE_RUNE, new ItemStack(Material.OBSIDIAN),
            SlimefunItems.SYNTHETIC_EMERALD, new ItemStack(Material.OBSIDIAN), SlimefunItems.SYNTHETIC_EMERALD
        }).register(plugin);

        new EnderChestInsertionNode(FluffyItems.fluffymachines, FluffyItems.ENDER_CHEST_INSERTION_NODE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2,
            new ItemStack(Material.DISPENSER), new ItemStack(Material.ENDER_PEARL), new ItemStack(Material.HOPPER),
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2
        }).register(plugin);

        new EnderChestExtractionNode(FluffyItems.fluffymachines, FluffyItems.ENDER_CHEST_EXTRACTION_NODE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.ENDER_LUMP_2, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENDER_LUMP_2,
            new ItemStack(Material.HOPPER), new ItemStack(Material.ENDER_PEARL), new ItemStack(Material.DISPENSER),
            SlimefunItems.ENDER_LUMP_2, advancedCircuitBoard, SlimefunItems.ENDER_LUMP_2
        }).register(plugin);

        new Dolly(FluffyItems.fluffymachines, FluffyItems.DOLLY, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.LEATHER), new ItemStack(Material.LEATHER), new ItemStack(Material.LEATHER),
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.MINECART), new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT)
        }).register(plugin);

        new SlimefunItem(FluffyItems.fluffymachines, FluffyItems.ANCIENT_BOOK,
            RecipeType.ANCIENT_ALTAR, new ItemStack[] {
            new ItemStack(Material.BOOK), SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, new ItemStack(Material.BOOK),
            SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, SlimefunItems.ENCHANTMENT_RUNE,
            SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE,
            new ItemStack(Material.BOOK), SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, new ItemStack(Material.BOOK)
        }).register(plugin);

        new WarpPad(FluffyItems.fluffymachines, FluffyItems.WARP_PAD,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.ENDER_EYE), new ItemStack(Material.ENDER_EYE), new ItemStack(Material.ENDER_EYE),
            new ItemStack(Material.ENDER_EYE), SlimefunItems.GPS_TELEPORTER_PYLON, new ItemStack(Material.ENDER_EYE),
            new ItemStack(Material.ENDER_EYE), new ItemStack(Material.ENDER_EYE), new ItemStack(Material.ENDER_EYE)
        }).register(plugin);

        new WarpPadConfigurator(FluffyItems.fluffymachines, FluffyItems.WARP_PAD_CONFIGURATOR,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            null, new ItemStack(Material.ENDER_EYE), null,
            null, SlimefunItems.MAGNESIUM_INGOT, null,
            null, SlimefunItems.MAGNESIUM_INGOT, null
        }).register(plugin);

        new ElectricDustFabricator(FluffyItems.fluffymachines, FluffyItems.ELECTRIC_DUST_FABRICATOR,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.ELECTRIC_ORE_GRINDER_2, SlimefunItems.ELECTRIC_ORE_GRINDER_2,
            SlimefunItems.ELECTRIC_ORE_GRINDER_2,
            SlimefunItems.ELECTRIC_GOLD_PAN_3, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.ELECTRIC_GOLD_PAN_3,
            SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ELECTRIC_DUST_WASHER_3, SlimefunItems.ELECTRIC_MOTOR
        }).register(plugin);

        new ElectricDustRecycler(FluffyItems.fluffymachines, FluffyItems.ELECTRIC_DUST_RECYCLER,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.LAVA_BUCKET), new ItemStack(Material.PISTON), new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.LAVA_BUCKET), SlimefunItems.ELECTRIFIED_CRUCIBLE_3,
            new ItemStack(Material.LAVA_BUCKET),
            SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.PISTON), SlimefunItems.ELECTRIC_MOTOR
        }).register(plugin);

        new AlternateElevatorPlate(FluffyItems.fluffymachines, FluffyItems.ALTERNATE_ELEVATOR_PLATE,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            new ItemStack(Material.STONE_PRESSURE_PLATE), new ItemStack(Material.STONE_PRESSURE_PLATE), new ItemStack(Material.STONE_PRESSURE_PLATE),
            new ItemStack(Material.PISTON), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.PISTON),
            SlimefunItems.ALUMINUM_BRONZE_INGOT, SlimefunItems.ALUMINUM_BRONZE_INGOT,
            SlimefunItems.ALUMINUM_BRONZE_INGOT},
            new SlimefunItemStack(FluffyItems.ALTERNATE_ELEVATOR_PLATE, 2)
        ).register(plugin);

        new AdvancedChargingBench(FluffyItems.fluffymachines, FluffyItems.ADVANCED_CHARGING_BENCH,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.CORINTHIAN_BRONZE_INGOT, advancedCircuitBoard, SlimefunItems.CORINTHIAN_BRONZE_INGOT,
            advancedCircuitBoard, SlimefunItems.CHARGING_BENCH, advancedCircuitBoard,
            SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.ELECTRIC_MOTOR
        }).register(plugin);

        new ACBUpgradeCard(FluffyItems.fluffymachines, FluffyItems.ACB_UPGRADE_CARD,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.CORINTHIAN_BRONZE_INGOT, advancedCircuitBoard, SlimefunItems.CORINTHIAN_BRONZE_INGOT,
            advancedCircuitBoard, SlimefunItems.ELECTRIC_MOTOR, advancedCircuitBoard,
            SlimefunItems.GOLD_24K, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.GOLD_24K
        }).register(plugin);
    }

}

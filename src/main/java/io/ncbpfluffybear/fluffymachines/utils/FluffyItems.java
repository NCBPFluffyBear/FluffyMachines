package io.ncbpfluffybear.fluffymachines.utils;

import dev.j3fftw.extrautils.utils.LoreBuilderDynamic;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineTier;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineType;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ColoredFireworkStar;
import io.ncbpfluffybear.fluffymachines.items.FireproofRune;
import io.ncbpfluffybear.fluffymachines.items.MiniBarrel;
import io.ncbpfluffybear.fluffymachines.items.tools.FluffyWrench;
import io.ncbpfluffybear.fluffymachines.items.tools.PortableCharger;
import io.ncbpfluffybear.fluffymachines.machines.AdvancedAutoDisenchanter;
import io.ncbpfluffybear.fluffymachines.machines.AdvancedChargingBench;
import io.ncbpfluffybear.fluffymachines.machines.AutoAncientAltar;
import io.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import io.ncbpfluffybear.fluffymachines.machines.AutoTableSaw;
import io.ncbpfluffybear.fluffymachines.machines.BackpackLoader;
import io.ncbpfluffybear.fluffymachines.machines.BackpackUnloader;
import io.ncbpfluffybear.fluffymachines.machines.ElectricDustFabricator;
import io.ncbpfluffybear.fluffymachines.machines.ElectricDustRecycler;
import io.ncbpfluffybear.fluffymachines.machines.SmartFactory;
import io.ncbpfluffybear.fluffymachines.machines.WaterSprinkler;
import io.ncbpfluffybear.fluffymachines.multiblocks.CrankGenerator;
import io.ncbpfluffybear.fluffymachines.objects.AutoCrafter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Specifies all plugin items
 */
public class FluffyItems {

    private FluffyItems() {
    }

    // Barrels
    public static final SlimefunItemStack MINI_FLUFFY_BARREL = new SlimefunItemStack("MINI_FLUFFY_BARREL",
            Material.COMPOSTER,
            "&eMini Fluffy Barrel",
            "",
            "&7Stores a large amount of an item",
            "&7Has a changeable capacity",
            "",
            "&bMax Capacity: &e" + MiniBarrel.getDisplayCapacity() + " Items"
    );

    // Portable Chargers
    public static final SlimefunItemStack SMALL_PORTABLE_CHARGER = new SlimefunItemStack("SMALL_PORTABLE_CHARGER",
            Material.BRICK,
            "&eSmall Portable Charger",
            "",
            "&7A handheld charger that holds a lot of power",
            "",
            "&eCharge Speed: &7" + PortableCharger.Type.SMALL.chargeSpeed + " J/s",
            LoreBuilder.powerCharged(0, PortableCharger.Type.SMALL.chargeCapacity)
    );

    public static final SlimefunItemStack MEDIUM_PORTABLE_CHARGER = new SlimefunItemStack("MEDIUM_PORTABLE_CHARGER",
            Material.IRON_INGOT,
            "&6Medium Portable Charger",
            "",
            "&7A handheld charger that holds a lot of power",
            "",
            "&eCharge Speed: &7" + PortableCharger.Type.MEDIUM.chargeSpeed + " J/s",
            LoreBuilder.powerCharged(0, PortableCharger.Type.MEDIUM.chargeCapacity)
    );

    public static final SlimefunItemStack BIG_PORTABLE_CHARGER = new SlimefunItemStack("BIG_PORTABLE_CHARGER",
            Material.GOLD_INGOT,
            "&aBig Portable Charger",
            "",
            "&7A handheld charger that holds a lot of power",
            "",
            "&eCharge Speed: &7" + PortableCharger.Type.BIG.chargeSpeed + " J/s",
            LoreBuilder.powerCharged(0, PortableCharger.Type.BIG.chargeCapacity)
    );

    public static final SlimefunItemStack LARGE_PORTABLE_CHARGER = new SlimefunItemStack("LARGE_PORTABLE_CHARGER",
            Material.NETHER_BRICK,
            "&2Large Portable Charger",
            "",
            "&7A handheld charger that holds a lot of power",
            "",
            "&eCharge Speed: &7" + PortableCharger.Type.LARGE.chargeSpeed + " J/s",
            LoreBuilder.powerCharged(0, PortableCharger.Type.LARGE.chargeCapacity)
    );

    public static final SlimefunItemStack CARBONADO_PORTABLE_CHARGER = new SlimefunItemStack(
            "CARBONADO_PORTABLE_CHARGER",
            Material.NETHERITE_INGOT,
            "&4Carbonado Portable Charger",
            "",
            "&7A handheld charger that holds a lot of power",
            "",
            "&eCharge Speed: &7" + PortableCharger.Type.CARBONADO.chargeSpeed + " J/s",
            LoreBuilder.powerCharged(0, PortableCharger.Type.CARBONADO.chargeCapacity)
    );

    // Items
    public static final SlimefunItemStack ANCIENT_BOOK = new SlimefunItemStack("ANCIENT_BOOK",
            Material.BOOK,
            "&6Ancient Book",
            "",
            "&7Used in the &cAdvanced Auto Disenchanter",
            "",
            "&6&oContains concentrated amounts of power"
    );
    public static final SlimefunItemStack HELICOPTER_HAT = new SlimefunItemStack("HELICOPTER_HAT",
            Material.LEATHER_HELMET, Color.AQUA,
            "&1Helicopter Hat",
            "",
            "&7brrrrrrrrRRRRRRRR",
            "",
            "&eSneak &7to use"
    );
    public static final SlimefunItemStack WATERING_CAN = new SlimefunItemStack("WATERING_CAN",
            "6484da45301625dee79ae29ff513efa583f1ed838033f20db80963cedf8aeb0e",
            "&bWatering Can",
            "",
            "&fWaters Plants",
            "",
            "&7> &eRight Click &7a water to fill your watering can",
            "&7> &eRight Click &7a plant to speed up growth.",
            "&7> &eRight Click &7a player to slow them down",
            "",
            "&aUses Left: &e0"
    );
    public static final SlimefunItemStack ENDER_CHEST_EXTRACTION_NODE = new SlimefunItemStack(
            "ENDER_CHEST_EXTRACTION_NODE",
            "e707c7f6c3a056a377d4120028405fdd09acfcd5ae804bfde0f653be866afe39",
            "&6Ender Chest Extraction Node",
            "",
            "&7Place this on the side of an &5Ender Chest &7to bind",
            "",
            "&7This will move items from the facing &5Ender Chest",
            "&7to the &6Container &7behind it"
    );
    public static final SlimefunItemStack ENDER_CHEST_INSERTION_NODE = new SlimefunItemStack(
            "ENDER_CHEST_INSERTION_NODE",
            "7e5dc50c0186d53381d9430a2eff4c38f816b8791890c7471ffdb65ba202bc5",
            "&bEnder Chest Insertion Node",
            "",
            "&7Place this on the side of an &5Ender Chest &7to bind",
            "",
            "&7This will move items to the facing &5Ender Chest",
            "&7from the &6Container &7behind it"
    );
    // Machines
    public static final SlimefunItemStack AUTO_CRAFTING_TABLE = new SlimefunItemStack("AUTO_CRAFTING_TABLE",
            Material.CRAFTING_TABLE,
            "&6Auto Crafting Table",
            "",
            "&7Automatically crafts &fvanilla &7recipes",
            "",
            LoreBuilderDynamic.powerBuffer(AutoCraftingTable.CAPACITY),
            LoreBuilderDynamic.powerPerTick(AutoCraftingTable.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack AUTO_ANCIENT_ALTAR = new SlimefunItemStack("AUTO_ANCIENT_ALTAR",
            Material.ENCHANTING_TABLE,
            "&5Auto Ancient Altar",
            "",
            "&7Automatically crafts &5Ancient Altar &7recipes",
            "",
            LoreBuilderDynamic.powerBuffer(AutoAncientAltar.CAPACITY),
            LoreBuilderDynamic.powerPerTick(AutoAncientAltar.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack AUTO_TABLE_SAW = new SlimefunItemStack("AUTO_TABLE_SAW",
            Material.STONECUTTER,
            "&6Auto Table Saw",
            "",
            "&7Automatically crafts &6Table Saw &7recipes",
            "",
            LoreBuilderDynamic.powerBuffer(AutoTableSaw.CAPACITY),
            LoreBuilderDynamic.powerPerTick(AutoTableSaw.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack WATER_SPRINKER = new SlimefunItemStack("WATER_SPRINKLER",
            "d6b13d69d1929dcf8edf99f3901415217c6a567d3a6ead12f75a4de3ed835e85",
            "&bWater Sprinkler",
            "",
            "&7Sprinkly sprinkly",
            "",
            LoreBuilderDynamic.powerBuffer(WaterSprinkler.CAPACITY),
            LoreBuilderDynamic.powerPerTick(WaterSprinkler.ENERGY_CONSUMPTION) + " per crop"
    );
    public static final SlimefunItemStack GENERATOR_CORE = new SlimefunItemStack("GENERATOR_CORE",
            Material.BLAST_FURNACE,
            "&7Generator Core",
            "",
            "&7Multiblock component of generators"
    );
    public static final SlimefunItemStack CRANK_GENERATOR = new SlimefunItemStack("CRANK_GENERATOR",
            Material.BLAST_FURNACE,
            "&7Crank Generator",
            "",
            "&eRight click &7the lever to generate power",
            "",
            LoreBuilderDynamic.power(CrankGenerator.RATE, "/Crank"),
            LoreBuilderDynamic.powerBuffer(CrankGenerator.CAPACITY),
            "",
            Utils.multiBlockWarning()
    );

    public static final SlimefunItemStack FOUNDRY = new SlimefunItemStack("FOUNDRY",
            Material.BLAST_FURNACE,
            "&cFoundry",
            "",
            "&eMelts and stores dusts and ingots",
            "&7Stores 138,240 dust (40 Double Chests)",
            "",
            Utils.multiBlockWarning()
    );

    public static final SlimefunItemStack BACKPACK_UNLOADER = new SlimefunItemStack("BACKPACK_UNLOADER",
            Material.BROWN_STAINED_GLASS,
            "&eBackpack Unloader",
            "",
            "&7Empties the contents of backpacks",
            "",
            LoreBuilderDynamic.powerBuffer(BackpackUnloader.CAPACITY),
            LoreBuilderDynamic.powerPerTick(BackpackUnloader.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack BACKPACK_LOADER = new SlimefunItemStack("BACKPACK_LOADER",
            Material.ORANGE_STAINED_GLASS,
            "&eBackpack Loader",
            "",
            "&7Moves items from inventory to backpack",
            "",
            LoreBuilderDynamic.powerBuffer(BackpackLoader.CAPACITY),
            LoreBuilderDynamic.powerPerTick(BackpackLoader.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack UPGRADED_EXPLOSIVE_PICKAXE = new SlimefunItemStack(
            "UPGRADED_EXPLOSIVE_PICKAXE",
            Material.DIAMOND_PICKAXE,
            "&e&lUpgraded Explosive Pickaxe",
            "",
            "&7Breaks all mineable blocks in a 5x5 radius"
    );
    public static final SlimefunItemStack UPGRADED_EXPLOSIVE_SHOVEL = new SlimefunItemStack("UPGRADED_EXPLOSIVE_SHOVEL",
            Material.DIAMOND_SHOVEL,
            "&e&lUpgraded Explosive Shovel",
            "",
            "&7Breaks all shovelable blocks in a 5x5 radius"
    );
    public static final SlimefunItemStack FIREPROOF_RUNE = new SlimefunItemStack(
            "FIREPROOF_RUNE",
            ColoredFireworkStar.create(Color.fromRGB(255, 165, 0),
                    "&7Ancient Rune &8&l[&c&lFireproof&8&l]",
                    "",
                    "&eDrop this rune onto a dropped item to",
                    "&emake it &cfireproof",
                    ""
            ));
    public static final SlimefunItemStack SUPERHEATED_FURNACE = new SlimefunItemStack("SUPERHEATED_FURNACE",
            Material.BLAST_FURNACE,
            "&cSuper Heated Furnace",
            "",
            "&7Multiblock component of the Foundry",
            "&cMust be used in the Foundry"
    );
    public static final SlimefunItemStack AUTO_ENHANCED_CRAFTING_TABLE = new SlimefunItemStack("AUTO_ENHANCED_CRAFTING_TABLE",
            Material.CRAFTING_TABLE,
            "&eAuto Enhanced Crafting Table",
            "",
            "&7Automatically crafts &eEnhanced Crafting Table &7recipes",
            "",
            LoreBuilderDynamic.powerBuffer(AutoCrafter.CAPACITY),
            LoreBuilderDynamic.powerPerTick(AutoCrafter.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack AUTO_MAGIC_WORKBENCH = new SlimefunItemStack("AUTO_MAGIC_WORKBENCH",
            Material.BOOKSHELF,
            "&6Auto Magic Workbench",
            "",
            "&7Automatically crafts &6Magic Workbench &7recipes",
            "",
            LoreBuilderDynamic.powerBuffer(AutoCrafter.CAPACITY),
            LoreBuilderDynamic.powerPerTick(AutoCrafter.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack AUTO_ARMOR_FORGE = new SlimefunItemStack("AUTO_ARMOR_FORGE",
            Material.SMITHING_TABLE,
            "&7Auto Armor Forge",
            "",
            "&7Automatically crafts Armor Forge recipes",
            "",
            LoreBuilderDynamic.powerBuffer(AutoCrafter.CAPACITY),
            LoreBuilderDynamic.powerPerTick(AutoCrafter.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack ADVANCED_AUTO_DISENCHANTER = new SlimefunItemStack(
            "ADVANCED_AUTO_DISENCHANTER",
            Material.ENCHANTING_TABLE,
            "&cAdvanced Auto Disenchanter",
            "",
            "&7Removes one enchant from an item",
            "&7Requires an &6Ancient Book &7to operate",
            "",
            LoreBuilderDynamic.powerBuffer(AdvancedAutoDisenchanter.CAPACITY),
            LoreBuilderDynamic.powerPerTick(AdvancedAutoDisenchanter.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack SCYTHE = new SlimefunItemStack("SCYTHE",
            Material.IRON_HOE,
            "&eScythe",
            "",
            "&7Breaks 5 crops at once"
    );
    public static final SlimefunItemStack UPGRADED_LUMBER_AXE = new SlimefunItemStack("UPGRADED_LUMBER_AXE",
            Material.DIAMOND_AXE,
            "&6&lUpgraded Lumber Axe",
            "",
            "&7Chops down an entire tree at once",
            "&72 block reach and works on diagonal blocks too"
    );
    public static final SlimefunItemStack DOLLY = new SlimefunItemStack("DOLLY",
            Material.MINECART,
            "&bDolly",
            "",
            "&7Right click a chest to pick it up",
            "",
            "&7ID: <ID>"
    );

    public static final SlimefunItemStack WARP_PAD = new SlimefunItemStack("WARP_PAD",
            Material.SMOKER,
            "&6Warp Pad",
            "",
            "&eCrouch &7on this block to teleport to",
            "&7the linked destination pad",
            "",
            "&7Use a Warp Pad Configurator to link Warp Pads"
    );

    public static final SlimefunItemStack WARP_PAD_CONFIGURATOR = new SlimefunItemStack("WARP_PAD_CONFIGURATOR",
            Material.BLAZE_ROD,
            "&6Warp Pad Configurator",
            "",
            "&eSneak and Right Click &7on a Warp Pad to set the destination",
            "&eRight Click &7on a Warp Pad to set the origin",
            "",
            "&eLinked Coordinates: &7None"
    );

    public static final SlimefunItemStack ELECTRIC_DUST_FABRICATOR = new SlimefunItemStack("ELECTRIC_DUST_FABRICATOR",
            Material.BLAST_FURNACE,
            "&6Electric Dust Fabricator",
            "",
            "&7An all-in-one machine that grinds, pans, and washes",
            LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE),
            LoreBuilder.speed(10),
            LoreBuilderDynamic.powerBuffer(ElectricDustFabricator.CAPACITY),
            LoreBuilderDynamic.powerPerTick(ElectricDustFabricator.ENERGY_CONSUMPTION)
    );

    public static final SlimefunItemStack ELECTRIC_DUST_RECYCLER = new SlimefunItemStack("ELECTRIC_DUST_RECYCLER",
            Material.IRON_BLOCK,
            "&fElectric Dust Recycler",
            "",
            "&7Recycles dust back into sifted ore",
            LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE),
            LoreBuilder.speed(1),
            LoreBuilderDynamic.powerBuffer(ElectricDustRecycler.CAPACITY),
            LoreBuilderDynamic.powerPerTick(ElectricDustRecycler.ENERGY_CONSUMPTION)
    );

    public static final SlimefunItemStack ALTERNATE_ELEVATOR_PLATE = new SlimefunItemStack("ALTERNATE_ELEVATOR_PLATE",
            Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            "&3Alternate Elevator Plate",
            "",
            "&fPlace an Elevator Plate on every floor",
            "&fand you will be able to teleport between them.",
            "",
            "&eRight Click this Block &7to name it",
            "&7Uses a Chest GUI instead of a Book GUI"
    );

    public static final SlimefunItemStack FLUFFY_WRENCH = new SlimefunItemStack("FLUFFY_WRENCH",
            FluffyWrench.Wrench.DEFAULT.getMaterial(),
            "&6Fluffy Wrench",
            "",
            "&7Used to quickly remove Slimefun cargo nodes",
            "&7and electricity components",
            "",
            "&eLeft&7/&eRight Click &7a compatible block to break it"
    );

    public static final SlimefunItemStack REINFORCED_FLUFFY_WRENCH =
            new SlimefunItemStack("REINFORCED_FLUFFY_WRENCH", FluffyWrench.Wrench.REINFORCED.getMaterial(),
                    "&bReinforced Fluffy Wrench",
                    "",
                    "&7Used to quickly remove Slimefun cargo nodes",
                    "&7and electricity components",
                    "",
                    "&eLeft&7/&eRight Click &7a compatible block to break it"
            );

    public static final SlimefunItemStack CARBONADO_FLUFFY_WRENCH =
            new SlimefunItemStack("CARBONADO_FLUFFY_WRENCH", FluffyWrench.Wrench.CARBONADO.getMaterial(),
                    "&7Carbonado Fluffy Wrench",
                    "",
                    "&7Used to quickly remove Slimefun cargo nodes",
                    "&7and electricity components",
                    "",
                    "&eLeft&7/&eRight Click &7a compatible block to break it",
                    "",
                    LoreBuilder.powerCharged(0, FluffyWrench.Wrench.CARBONADO.getMaxCharge())
            );

    public static final SlimefunItemStack PAXEL = new SlimefunItemStack("PAXEL",
            Material.DIAMOND_PICKAXE,
            "&bPaxel",
            "",
            "&7A pickaxe, axe, and shovel in one tool!"
    );

    public static final SlimefunItemStack ADVANCED_CHARGING_BENCH = new SlimefunItemStack(
            "ADVANCED_CHARGING_BENCH",
            Material.SMITHING_TABLE,
            "&cAdvanced Charging Bench",
            "",
            "&7Charges items",
            "&7Can be upgraded using an &6ACB Upgrade Card"
    );

    public static final SlimefunItemStack ACB_UPGRADE_CARD = new SlimefunItemStack(
            "ACB_UPGRADE_CARD",
            Material.PAPER,
            "&6ACB Upgrade Card",
            "",
            "&eRight Click &7onto an &cAdvanced Charging Bench",
            "",
            "&6Charge Speed &a+" + AdvancedChargingBench.CHARGE + "J",
            "&6Capacity &a+" + AdvancedChargingBench.CAPACITY + "J",
            "&6Energy Consumption &c+" + AdvancedChargingBench.ENERGY_CONSUMPTION + "J"
    );

    public static final SlimefunItemStack CARGO_MANIPULATOR = new SlimefunItemStack("CARGO_MANIPULATOR",
            Material.SEA_PICKLE,
            "&9Cargo Manipulator",
            "",
            "&eRight Click &7to copy cargo node settings",
            "&eLeft Click &7to paste cargo node settings",
            "&eSneak and Right Click &7to clear a cargo node"
    );

    public static final SlimefunItemStack EXP_DISPENSER = new SlimefunItemStack("EXP_DISPENSER",
            Material.DISPENSER,
            "&aExp Dispenser",
            "",
            "&7Right click to receive all exp",
            "&7from exp bottles in the dispenser",
            "&7and barrel the dispenser is facing",
            "",
            Utils.multiBlockWarning()
    );

    public static final SlimefunItemStack SMART_FACTORY = new SlimefunItemStack("SMART_FACTORY",
            Material.SMOKER,
            "&cSmart Factory",
            "",
            "&7An all-in-one machine that crafts",
            "&7resources from raw materials",
            "",
            LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE),
            LoreBuilder.speed(1),
            LoreBuilderDynamic.powerBuffer(SmartFactory.getEnergyCapacity()),
            LoreBuilderDynamic.powerPerTick(SmartFactory.getEnergyConsumption())
    );

    static {
        ItemStack runeClone = FIREPROOF_RUNE.item();
        FireproofRune.setFireproof(runeClone);
        FIREPROOF_RUNE.setItemMeta(runeClone.getItemMeta());

        addGlow(SMALL_PORTABLE_CHARGER);
        addGlow(MEDIUM_PORTABLE_CHARGER);
        addGlow(BIG_PORTABLE_CHARGER);
        addGlow(LARGE_PORTABLE_CHARGER);
        addGlow(CARBONADO_PORTABLE_CHARGER);
    }

    private static void addGlow(SlimefunItemStack item) {
        item.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }
}

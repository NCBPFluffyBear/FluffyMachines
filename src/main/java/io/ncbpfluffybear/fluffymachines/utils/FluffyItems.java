package io.ncbpfluffybear.fluffymachines.utils;

import io.github.thebusybiscuit.slimefun4.utils.itemstack.ColoredFireworkStar;
import io.ncbpfluffybear.fluffymachines.items.FireproofRune;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.machines.AutoAncientAltar;
import io.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import io.ncbpfluffybear.fluffymachines.machines.BackpackLoader;
import io.ncbpfluffybear.fluffymachines.machines.BackpackUnloader;
import io.ncbpfluffybear.fluffymachines.machines.WaterSprinkler;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;


public class FluffyItems {

    private FluffyItems() {
    }

    // Category
    public static final Category fluffymachines = new Category(new NamespacedKey(FluffyMachines.getInstance(),
        "fluffymachines"),
        new CustomItem(Material.SMOKER, "&6Fluffy Machines")
    );

    // Items
    public static final SlimefunItemStack HELICOPTER_HAT = new SlimefunItemStack("HELICOPTER_HAT",
        Material.LEATHER_HELMET, Color.AQUA,
        "&1Helicopter Hat",
        "",
        "&7brrrrrrrrRRRRRRRR",
        "",
        "&eSneak &7to use"
    );
    public static final SlimefunItemStack WATERING_CAN = new SlimefunItemStack("WATERING_CAN",
        Material.GLASS_BOTTLE,
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
    public static final SlimefunItemStack ENDER_CHEST_EXTRACTION_NODE = new SlimefunItemStack("ENDER_CHEST_EXTRACTION_NODE",
        new CustomItem(SkullItem.fromHash("e707c7f6c3a056a377d4120028405fdd09acfcd5ae804bfde0f653be866afe39")),
        "&6Ender Chest Extraction Node",
        "",
        "&7Place this on the side of an &5Ender Chest &7to bind",
        "",
        "&7This will move items from the facing &5Ender Chest",
        "&7to the &6Container &7behind it"
    );
    public static final SlimefunItemStack ENDER_CHEST_INSERTION_NODE = new SlimefunItemStack("ENDER_CHEST_INSERTION_NODE",
        new CustomItem(SkullItem.fromHash("7e5dc50c0186d53381d9430a2eff4c38f816b8791890c7471ffdb65ba202bc5")),
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
    public static final SlimefunItemStack WATER_SPRINKER = new SlimefunItemStack("WATER_SPRINKLER",
        new CustomItem(SkullItem.fromHash("d6b13d69d1929dcf8edf99f3901415217c6a567d3a6ead12f75a4de3ed835e85"),
            "Water Sprinkler"),
        "&bWater Sprinkler",
        "",
        "&7Sprinkly sprinkly",
        "",
        LoreBuilderDynamic.powerBuffer(WaterSprinkler.CAPACITY),
        LoreBuilderDynamic.powerPerTick(WaterSprinkler.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack ITEM_OVERSTACKER = new SlimefunItemStack("ITEM_OVERSTACKER",
        Material.PISTON,
        "&eItem Overstacker",
        "",
        "&7Compresses nonstackable items"
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
        LoreBuilderDynamic.powerBuffer(BackpackUnloader.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack BACKPACK_LOADER = new SlimefunItemStack("BACKPACK_LOADER",
        Material.ORANGE_STAINED_GLASS,
        "&eBackpack Loader",
        "",
        "&7Moves items from inventory to backpack",
        "",
        LoreBuilderDynamic.powerBuffer(BackpackLoader.CAPACITY),
        LoreBuilderDynamic.powerBuffer(BackpackLoader.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack UPGRADED_EXPLOSIVE_PICKAXE = new SlimefunItemStack("UPGRADED_EXPLOSIVE_PICKAXE",
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
        new ColoredFireworkStar(Color.fromRGB(255, 165, 0),
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

    static {
        FireproofRune.setFireproof(FIREPROOF_RUNE);
    }
}

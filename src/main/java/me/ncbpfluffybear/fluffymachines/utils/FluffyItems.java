package me.ncbpfluffybear.fluffymachines.utils;

import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import me.ncbpfluffybear.fluffymachines.FluffyMachines;
import me.ncbpfluffybear.fluffymachines.machines.AutoAncientAltar;
import me.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import me.ncbpfluffybear.fluffymachines.machines.WaterSprinkler;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class FluffyItems {

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
        dev.j3fftw.litexpansion.utils.LoreBuilderDynamic.powerBuffer(WaterSprinkler.CAPACITY),
        dev.j3fftw.litexpansion.utils.LoreBuilderDynamic.powerPerTick(WaterSprinkler.ENERGY_CONSUMPTION)
    );

    private FluffyItems() {
    }
}

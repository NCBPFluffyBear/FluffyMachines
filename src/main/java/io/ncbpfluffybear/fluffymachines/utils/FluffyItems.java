package io.ncbpfluffybear.fluffymachines.utils;

import dev.j3fftw.extrautils.utils.LoreBuilderDynamic;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineTier;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineType;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ColoredFireworkStar;
import io.ncbpfluffybear.fluffymachines.FluffyMachines;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import io.ncbpfluffybear.fluffymachines.items.FireproofRune;
import io.ncbpfluffybear.fluffymachines.items.tools.FluffyWrench;
import io.ncbpfluffybear.fluffymachines.items.tools.PortableCharger;
import io.ncbpfluffybear.fluffymachines.machines.AdvancedAutoDisenchanter;
import io.ncbpfluffybear.fluffymachines.machines.AdvancedChargingBench;
import io.ncbpfluffybear.fluffymachines.machines.AutoAncientAltar;
import io.ncbpfluffybear.fluffymachines.objects.AutoCrafter;
import io.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import io.ncbpfluffybear.fluffymachines.machines.AutoTableSaw;
import io.ncbpfluffybear.fluffymachines.machines.BackpackLoader;
import io.ncbpfluffybear.fluffymachines.machines.BackpackUnloader;
import io.ncbpfluffybear.fluffymachines.machines.ElectricDustFabricator;
import io.ncbpfluffybear.fluffymachines.machines.ElectricDustRecycler;
import io.ncbpfluffybear.fluffymachines.machines.WaterSprinkler;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;


public class FluffyItems {

    private FluffyItems() {
    }

    // ItemGroup
    public static final ItemGroup fluffymachines = new ItemGroup(new NamespacedKey(FluffyMachines.getInstance(),
        "fluffymachines"),
        new CustomItemStack(Material.SMOKER, "&6Fluffy機器")
    );

    public static final ItemGroup fluffybarrels = new ItemGroup(new NamespacedKey(FluffyMachines.getInstance(),
        "fluffybarrels"),
        new CustomItemStack(Material.BARREL, "&6Fluffy木桶")
    );

    public static final SlimefunItemStack FLUFFYMACHINES_INFO = new SlimefunItemStack("FLUFFYMACHINES_INFO",
        Material.ORANGE_WOOL,
        "&6&lFluffy機器 版本資訊",
        "",
        "&e" + FluffyMachines.getInstance().getName() + " " + FluffyMachines.getInstance().getPluginVersion()
    );

    // Barrels
    public static final SlimefunItemStack SMALL_FLUFFY_BARREL = new SlimefunItemStack("SMALL_FLUFFY_BARREL",
        Material.BEEHIVE,
        "&e小型 Fluffy木桶",
        "",
        "&7可存儲多個相同的物品",
        "&c不要使用爆炸工具破壞...",
        "&c除非你想要失去裡面的所有東西",
        "",
        "&b容量: &e" + Barrel.SMALL_BARREL_SIZE + " 物品"
    );

    public static final SlimefunItemStack MEDIUM_FLUFFY_BARREL = new SlimefunItemStack("MEDIUM_FLUFFY_BARREL",
        Material.BARREL,
        "&6中型 Fluffy木桶",
        "",
        "&7可存儲多個相同的物品",
        "&c不要使用爆炸工具破壞...",
        "&c除非你想要失去裡面的所有東西",
        "",
        "&b容量: &e" + Barrel.MEDIUM_BARREL_SIZE + " 物品"
    );

    public static final SlimefunItemStack BIG_FLUFFY_BARREL = new SlimefunItemStack("BIG_FLUFFY_BARREL",
        Material.SMOKER,
        "&b大型 Fluffy木桶",
        "",
        "&7可存儲多個相同的物品",
        "&c不要使用爆炸工具破壞...",
        "&c除非你想要失去裡面的所有東西",
        "",
        "&b容量: &e" + Barrel.BIG_BARREL_SIZE + " 物品"
    );

    public static final SlimefunItemStack LARGE_FLUFFY_BARREL = new SlimefunItemStack("LARGE_FLUFFY_BARREL",
        Material.LODESTONE,
        "&a巨型 Fluffy木桶",
        "",
        "&7可存儲多個相同的物品",
        "&c不要使用爆炸工具破壞...",
        "&c除非你想要失去裡面的所有東西",
        "",
        "&b容量: &e" + Barrel.LARGE_BARREL_SIZE + " 物品"
    );

    public static final SlimefunItemStack MASSIVE_FLUFFY_BARREL = new SlimefunItemStack("MASSIVE_FLUFFY_BARREL",
        Material.CRYING_OBSIDIAN,
        "&5超大 Fluffy木桶",
        "",
        "&7可存儲多個相同的物品",
        "&c不要使用爆炸工具破壞...",
        "&c除非你想要失去裡面的所有東西",
        "",
        "&b容量: &e" + Barrel.MASSIVE_BARREL_SIZE + " 物品"
    );

    public static final SlimefunItemStack BOTTOMLESS_FLUFFY_BARREL = new SlimefunItemStack("BOTTOMLESS_FLUFFY_BARREL",
        Material.RESPAWN_ANCHOR,
        "&c無底洞 Fluffy木桶",
        "",
        "&7可存儲多個相同的物品",
        "&c不要使用爆炸工具破壞...",
        "&c除非你想要失去裡面的所有東西",
        "",
        "&b容量: &e" + Barrel.BOTTOMLESS_BARREL_SIZE + " Items"
    );

    // Portable Chargers
    public static final SlimefunItemStack SMALL_PORTABLE_CHARGER = new SlimefunItemStack("SMALL_PORTABLE_CHARGER",
        Material.BRICK,
        "&e小型隨身充電器",
        "",
        "&7攜帶型充電器,可容納大量電量",
        "",
        "&e充電速度: &7" + PortableCharger.Type.SMALL.chargeSpeed + " J/s",
        LoreBuilder.powerCharged(0, PortableCharger.Type.SMALL.chargeCapacity)
    );

    public static final SlimefunItemStack MEDIUM_PORTABLE_CHARGER = new SlimefunItemStack("MEDIUM_PORTABLE_CHARGER",
        Material.IRON_INGOT,
        "&6中型隨身充電器",
        "",
        "&7攜帶型充電器,可容納大量電量",
        "",
        "&e充電速度: &7" + PortableCharger.Type.MEDIUM.chargeSpeed + " J/s",
        LoreBuilder.powerCharged(0, PortableCharger.Type.MEDIUM.chargeCapacity)
    );

    public static final SlimefunItemStack BIG_PORTABLE_CHARGER = new SlimefunItemStack("BIG_PORTABLE_CHARGER",
        Material.GOLD_INGOT,
        "&a大型隨身充電器",
        "",
        "&7攜帶型充電器,可容納大量電量",
        "",
        "&e充電速度: &7" + PortableCharger.Type.BIG.chargeSpeed + " J/s",
        LoreBuilder.powerCharged(0, PortableCharger.Type.BIG.chargeCapacity)
    );

    public static final SlimefunItemStack LARGE_PORTABLE_CHARGER = new SlimefunItemStack("LARGE_PORTABLE_CHARGER",
        Material.NETHER_BRICK,
        "&2巨型隨身充電器",
        "",
        "&7攜帶型充電器,可容納大量電量",
        "",
        "&e充電速度: &7" + PortableCharger.Type.LARGE.chargeSpeed + " J/s",
        LoreBuilder.powerCharged(0, PortableCharger.Type.LARGE.chargeCapacity)
    );

    public static final SlimefunItemStack CARBONADO_PORTABLE_CHARGER = new SlimefunItemStack(
        "CARBONADO_PORTABLE_CHARGER",
        Material.NETHERITE_INGOT,
        "&4黑鑽石隨身充電器",
        "",
        "&7攜帶型充電器,可容納大量電量",
        "",
        "&e充電速度: &7" + PortableCharger.Type.CARBONADO.chargeSpeed + " J/s",
        LoreBuilder.powerCharged(0, PortableCharger.Type.CARBONADO.chargeCapacity)
    );

    // Items
    public static final SlimefunItemStack ANCIENT_BOOK = new SlimefunItemStack("ANCIENT_BOOK",
        Material.BOOK,
        "&6古代書籍",
        "",
        "&7用於&c高級自動退魔器",
        "",
        "&6&o含有密集的力量"
    );
    public static final SlimefunItemStack HELICOPTER_HAT = new SlimefunItemStack("HELICOPTER_HAT",
        Material.LEATHER_HELMET, Color.AQUA,
        "&1直升機帽",
        "",
        "&7brrrrrrrrRRRRRRRR",
        "",
        "&e蹲下 &7來使用"
    );
    public static final SlimefunItemStack WATERING_CAN = new SlimefunItemStack("WATERING_CAN",
        "6484da45301625dee79ae29ff513efa583f1ed838033f20db80963cedf8aeb0e",
        "&b灑水壺",
        "",
        "&f灌溉植物",
        "",
        "&7> &e右鍵 &7對著水源來填滿灑水壺",
        "&7> &e右鍵 &7對著植物來加速生長.",
        "&7> &e右鍵 &7玩家讓它緩速",
        "",
        "&a剩餘次數: &e0"
    );
    public static final SlimefunItemStack ENDER_CHEST_EXTRACTION_NODE = new SlimefunItemStack(
        "ENDER_CHEST_EXTRACTION_NODE",
        "e707c7f6c3a056a377d4120028405fdd09acfcd5ae804bfde0f653be866afe39",
        "&6終界箱提取節點",
        "",
        "&7將此放置在&5終界箱&7來綁定",
        "",
        "&7這會移動物品至朝向的&5終界箱",
        "&7移動到後面的&6容器"
    );
    public static final SlimefunItemStack ENDER_CHEST_INSERTION_NODE = new SlimefunItemStack(
        "ENDER_CHEST_INSERTION_NODE",
        "7e5dc50c0186d53381d9430a2eff4c38f816b8791890c7471ffdb65ba202bc5",
        "&b終界箱存放節點",
        "",
        "&7將此放置在&5終界箱&7來綁定",
        "",
        "&7這會移動物品至朝向的&5終界箱",
        "&7從它後面的&6容器"
    );
    // Machines
    public static final SlimefunItemStack AUTO_CRAFTING_TABLE = new SlimefunItemStack("AUTO_CRAFTING_TABLE",
        Material.CRAFTING_TABLE,
        "&6自動工作台",
        "",
        "&7自動製作&f原版&7配方",
        "",
        LoreBuilderDynamic.powerBuffer(AutoCraftingTable.CAPACITY),
        LoreBuilderDynamic.powerPerTick(AutoCraftingTable.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack AUTO_ANCIENT_ALTAR = new SlimefunItemStack("AUTO_ANCIENT_ALTAR",
        Material.ENCHANTING_TABLE,
        "&5自動古代祭壇",
        "",
        "&7自動製作&5古代祭壇&7配方",
        "",
        LoreBuilderDynamic.powerBuffer(AutoAncientAltar.CAPACITY),
        LoreBuilderDynamic.powerPerTick(AutoAncientAltar.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack AUTO_TABLE_SAW = new SlimefunItemStack("AUTO_TABLE_SAW",
        Material.STONECUTTER,
        "&6自動鋸木機",
        "",
        "&7自動製作&6鋸木機&7配方",
        "",
        LoreBuilderDynamic.powerBuffer(AutoTableSaw.CAPACITY),
        LoreBuilderDynamic.powerPerTick(AutoTableSaw.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack WATER_SPRINKER = new SlimefunItemStack("WATER_SPRINKLER",
        "d6b13d69d1929dcf8edf99f3901415217c6a567d3a6ead12f75a4de3ed835e85",
        "&b灑水器",
        "",
        "&7灑水 灑水",
        "",
        LoreBuilderDynamic.powerBuffer(WaterSprinkler.CAPACITY),
        LoreBuilderDynamic.powerPerTick(WaterSprinkler.ENERGY_CONSUMPTION) + " 個作物"
    );
    public static final SlimefunItemStack ITEM_OVERSTACKER = new SlimefunItemStack("ITEM_OVERSTACKER",
        Material.PISTON,
        "&e物品過度堆疊機",
        "",
        "&7壓縮不可堆疊物品"
    );
    public static final SlimefunItemStack GENERATOR_CORE = new SlimefunItemStack("GENERATOR_CORE",
        Material.BLAST_FURNACE,
        "&7發電機核心",
        "",
        "&7多重方塊的發電機部件"
    );
    public static final SlimefunItemStack CRANK_GENERATOR = new SlimefunItemStack("CRANK_GENERATOR",
        Material.BLAST_FURNACE,
        "&7曲柄發電機",
        "",
        "&e右鍵 &7控制桿來產生動力",
        "",
        Utils.multiBlockWarning()
    );

    public static final SlimefunItemStack FOUNDRY = new SlimefunItemStack("FOUNDRY",
        Material.BLAST_FURNACE,
        "&c鑄造廠",
        "",
        "&e融化並儲存粉與錠",
        "&7儲存138,240個粉 (40個大型儲物箱)",
        "",
        Utils.multiBlockWarning()
    );

    public static final SlimefunItemStack BACKPACK_UNLOADER = new SlimefunItemStack("BACKPACK_UNLOADER",
        Material.BROWN_STAINED_GLASS,
        "&e背包卸載器",
        "",
        "&7清空背包內的內容物",
        "",
        LoreBuilderDynamic.powerBuffer(BackpackUnloader.CAPACITY),
        LoreBuilderDynamic.powerPerTick(BackpackUnloader.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack BACKPACK_LOADER = new SlimefunItemStack("BACKPACK_LOADER",
        Material.ORANGE_STAINED_GLASS,
        "&e背包裝載器",
        "",
        "&7將物品從庫存移至背包內",
        "",
        LoreBuilderDynamic.powerBuffer(BackpackLoader.CAPACITY),
        LoreBuilderDynamic.powerPerTick(BackpackLoader.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack UPGRADED_EXPLOSIVE_PICKAXE = new SlimefunItemStack(
        "UPGRADED_EXPLOSIVE_PICKAXE",
        Material.DIAMOND_PICKAXE,
        "&e&l升級版爆炸鎬",
        "",
        "&7以5x5範圍破壞可挖掘的方塊"
    );
    public static final SlimefunItemStack UPGRADED_EXPLOSIVE_SHOVEL = new SlimefunItemStack("UPGRADED_EXPLOSIVE_SHOVEL",
        Material.DIAMOND_SHOVEL,
        "&e&l升級版爆炸鏟",
        "",
        "&7以5x5範圍破壞可鏟的方塊"
    );
    public static final SlimefunItemStack FIREPROOF_RUNE = new SlimefunItemStack(
        "FIREPROOF_RUNE",
        new ColoredFireworkStar(Color.fromRGB(255, 165, 0),
            "&7古代魔法符文 &8&l[&c&l防火&8&l]",
            "",
            "&e將此符文放在掉落物品上",
            "&e讓它&c防火",
            ""
        ));
    public static final SlimefunItemStack SUPERHEATED_FURNACE = new SlimefunItemStack("SUPERHEATED_FURNACE",
        Material.BLAST_FURNACE,
        "&c超級炙熱熔爐",
        "",
        "&7鑄造廠的多重方塊一部份",
        "&c必須在鑄造廠上使用",
        "&c不要使用爆炸工具破壞!"
    );
    public static final SlimefunItemStack AUTO_MAGIC_WORKBENCH = new SlimefunItemStack("AUTO_MAGIC_WORKBENCH",
        Material.BOOKSHELF,
        "&6自動魔法合成台",
        "",
        "&7自動製作&6魔法合成台&7配方",
        "",
        LoreBuilderDynamic.powerBuffer(AutoCrafter.CAPACITY),
        LoreBuilderDynamic.powerPerTick(AutoCrafter.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack AUTO_ARMOR_FORGE = new SlimefunItemStack("AUTO_ARMOR_FORGE",
        Material.SMITHING_TABLE,
        "&7自動盔甲鍛造台",
        "",
        "&7自動製作盔甲鍛造台的配方",
        "",
        LoreBuilderDynamic.powerBuffer(AutoCrafter.CAPACITY),
        LoreBuilderDynamic.powerPerTick(AutoCrafter.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack ADVANCED_AUTO_DISENCHANTER = new SlimefunItemStack(
        "ADVANCED_AUTO_DISENCHANTER",
        Material.ENCHANTING_TABLE,
        "&c高級自動退魔器",
        "",
        "&7從物品上移除一個附魔",
        "&7需要&6古代書籍&7來運作",
        "",
        LoreBuilderDynamic.powerBuffer(AdvancedAutoDisenchanter.CAPACITY),
        LoreBuilderDynamic.powerPerTick(AdvancedAutoDisenchanter.ENERGY_CONSUMPTION)
    );
    public static final SlimefunItemStack SCYTHE = new SlimefunItemStack("SCYTHE",
        Material.IRON_HOE,
        "&e鐮刀",
        "",
        "&7一次採收五個農作物"
    );
    public static final SlimefunItemStack UPGRADED_LUMBER_AXE = new SlimefunItemStack("UPGRADED_LUMBER_AXE",
        Material.DIAMOND_AXE,
        "&6&l升級版伐木斧",
        "",
        "&7一次砍倒整棵樹",
        "&72格方塊遠和可以運作於對角方塊上"
    );
    public static final SlimefunItemStack DOLLY = new SlimefunItemStack("DOLLY",
        Material.MINECART,
        "&b搬運器",
        "",
        "&7右鍵拿起箱子",
        "",
        "&7ID: <ID>"
    );

    public static final SlimefunItemStack WARP_PAD = new SlimefunItemStack("WARP_PAD",
        Material.SMOKER,
        "&6傳送板",
        "",
        "&e蹲下 &7在此方塊上傳送到",
        "&7鏈接的目的地板",
        "",
        "&7使用傳送板配置器來連接傳送板"
    );

    public static final SlimefunItemStack WARP_PAD_CONFIGURATOR = new SlimefunItemStack("WARP_PAD_CONFIGURATOR",
        Material.BLAZE_ROD,
        "&6傳送板配置器",
        "",
        "&e蹲下右鍵 &7在傳送板上設置目的地",
        "&e右鍵 &7在傳送板上設置源點",
        "",
        "&e鏈接坐標: &7無"
    );

    public static final SlimefunItemStack ELECTRIC_DUST_FABRICATOR = new SlimefunItemStack("ELECTRIC_DUST_FABRICATOR",
        Material.BLAST_FURNACE,
        "&6電動粉工廠",
        "",
        "&7一機多用的機器,它有研磨機,掏金機與礦物洗滌機的功能",
        LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE),
        LoreBuilder.speed(10),
        LoreBuilderDynamic.powerBuffer(ElectricDustFabricator.CAPACITY),
        LoreBuilderDynamic.powerPerTick(ElectricDustFabricator.ENERGY_CONSUMPTION)
    );

    public static final SlimefunItemStack ELECTRIC_DUST_RECYCLER = new SlimefunItemStack("ELECTRIC_DUST_RECYCLER",
        Material.IRON_BLOCK,
        "&f電動粉回收機",
        "",
        "&7將粉回收為篩礦",
        LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE),
        LoreBuilder.speed(1),
        LoreBuilderDynamic.powerBuffer(ElectricDustRecycler.CAPACITY),
        LoreBuilderDynamic.powerPerTick(ElectricDustRecycler.ENERGY_CONSUMPTION)
    );

    public static final SlimefunItemStack ALTERNATE_ELEVATOR_PLATE = new SlimefunItemStack("ALTERNATE_ELEVATOR_PLATE",
        Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
        "&3備用電梯板",
        "",
        "&f在每個樓層上放置一個電梯板",
        "&f你將可以在各樓層之間傳送.",
        "",
        "&e右鍵此板 &7來命名",
        "&7使用的是箱子介面而不是書本介面 "
    );

    public static final SlimefunItemStack FLUFFY_WRENCH = new SlimefunItemStack("FLUFFY_WRENCH",
        FluffyWrench.Wrench.DEFAULT.getMaterial(),
        "&6Fluffy扳手",
        "",
        "&7用於快速移除黏液科技",
        "&7的物流節點與能源組件",
        "",
        "&e左&7/&e右鍵 &7相容方塊來破壞它"
    );

    public static final SlimefunItemStack REINFORCED_FLUFFY_WRENCH =
        new SlimefunItemStack("REINFORCED_FLUFFY_WRENCH",
        FluffyWrench.Wrench.REINFORCED.getMaterial(),
        "&b強化合金Fluffy扳手",
        "",
        "&7用於快速移除黏液科技",
        "&7的物流節點與能源組件",
        "",
        "&e左&7/&e右鍵 &7相容方塊來破壞它"
    );

    public static final SlimefunItemStack CARBONADO_FLUFFY_WRENCH =
        new SlimefunItemStack("CARBONADO_FLUFFY_WRENCH",
        FluffyWrench.Wrench.CARBONADO.getMaterial(),
        "&7黑鑽石Fluffy扳手",
        "",
        "&7用於快速移除黏液科技",
        "&7的物流節點與能源組件",
        "",
        "&e左&7/&e右鍵 &7相容方塊來破壞它",
        "",
        LoreBuilder.powerCharged(0, FluffyWrench.Wrench.CARBONADO.getMaxCharge())
    );

    public static final SlimefunItemStack PAXEL = new SlimefunItemStack("PAXEL",
        Material.DIAMOND_PICKAXE,
        "&b鎬尖斧",
        "",
        "&7一個包含了鎬子, 斧頭和鏟子的工具!"
    );

    public static final SlimefunItemStack ADVANCED_CHARGING_BENCH = new SlimefunItemStack(
        "ADVANCED_CHARGING_BENCH",
        Material.SMITHING_TABLE,
        "&c進階充電台",
        "",
        "&7給物品充電",
        "&7可以用&6進階充電升級卡&7升級"
    );

    public static final SlimefunItemStack ACB_UPGRADE_CARD = new SlimefunItemStack(
        "ACB_UPGRADE_CARD",
        Material.PAPER,
        "&6進階充電升級卡",
        "",
        "&e右鍵&7點擊在&c進階充電台",
        "",
        "&6充電速度 &a+" + AdvancedChargingBench.CHARGE + "J",
        "&6容量 &a+" + AdvancedChargingBench.CAPACITY +"J",
        "&6能源消耗 &c+" + AdvancedChargingBench.ENERGY_CONSUMPTION + "J"
    );

    private static final Enchantment glowEnchant = Enchantment.getByKey(Constants.GLOW_ENCHANT);

    static {
        FireproofRune.setFireproof(FIREPROOF_RUNE);
        SMALL_PORTABLE_CHARGER.addEnchantment(glowEnchant, 1);
        MEDIUM_PORTABLE_CHARGER.addEnchantment(glowEnchant, 1);
        BIG_PORTABLE_CHARGER.addEnchantment(glowEnchant, 1);
        LARGE_PORTABLE_CHARGER.addEnchantment(glowEnchant, 1);
        CARBONADO_PORTABLE_CHARGER.addEnchantment(glowEnchant, 1);
    }
}

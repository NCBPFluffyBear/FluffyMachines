package me.ncbpfluffybear.fluffymachines.multiblocks.components;

import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import me.ncbpfluffybear.fluffymachines.FluffyMachines;
import me.ncbpfluffybear.fluffymachines.multiblocks.Foundry;
import me.ncbpfluffybear.fluffymachines.utils.Utils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * Melts and stores dusts and ingots
 * and can be withdrawn in either form.
 * Component of {@link Foundry}
 *
 * @author NCBPFluffyBear
 */
public class SuperheatedFurnace extends SlimefunItem implements Listener {

    private BlockMenu menu;
    private Block foundry;

    private static final int[] inputBorder = {0, 2, 9, 11, 18, 19, 20};
    private static final int[] dustOutputBorder = {3, 5, 12, 14, 21, 22, 23};
    private static final int[] ingotOutputBorder = {6, 8, 15, 17, 24, 25, 26};
    private static final int INPUT_SLOT = 10;
    private static final int DUST_OUTPUT_SLOT = 13;
    private static final int INGOT_OUTPUT_SLOT = 16;
    private static final int INPUT_INDICATOR = 1;
    private static final int DUST_INDICATOR = 4;
    private static final int INGOT_INDICATOR = 7;

    private static final ItemStack redNonClickable = Utils.buildNonInteractable(Material.RED_STAINED_GLASS_PANE, " ");
    private static final ItemStack orangeNonClickable = Utils.buildNonInteractable(Material.ORANGE_STAINED_GLASS_PANE, " ");
    private static final ItemStack cyanNonClickable = Utils.buildNonInteractable(Material.CYAN_STAINED_GLASS_PANE, " ");

    private static final SlimefunItemStack[] dusts = new SlimefunItemStack[] {
        SlimefunItems.COPPER_DUST, SlimefunItems.GOLD_DUST, SlimefunItems.IRON_DUST,
        SlimefunItems.LEAD_DUST, SlimefunItems.ALUMINUM_DUST, SlimefunItems.ZINC_DUST,
        SlimefunItems.TIN_DUST, SlimefunItems.SILVER_DUST, SlimefunItems.MAGNESIUM_DUST};

    private static final SlimefunItemStack[] ingots = new SlimefunItemStack[] {
        SlimefunItems.COPPER_INGOT,
        SlimefunItems.LEAD_INGOT, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ZINC_INGOT,
        SlimefunItems.TIN_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.MAGNESIUM_INGOT};

    public SuperheatedFurnace(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getID(), "&cFoundry") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return p.hasPermission("slimefun.inventory.bypass") || SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.ACCESS_INVENTORIES);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                return new int[0];
            }
        };

        registerBlockHandler(getID(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {

                String type = getBlockInfo(b.getLocation(), "type");

                inv.dropItems(b.getLocation(), INPUT_SLOT);
                inv.dropItems(b.getLocation(), DUST_OUTPUT_SLOT);
                inv.dropItems(b.getLocation(), INGOT_OUTPUT_SLOT);

                if (type != null) {
                    int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));
                    b.getWorld().dropItem(b.getLocation(), new CustomItem(SlimefunItem.getByID(type + "_DUST").getItem(), stored));

                }


            }

            if (BlockStorage.getLocationInfo(b.getLocation(), "stand") != null) {
                Bukkit.getEntity(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "stand"))).remove();
            }

            return true;
        });

        addItemHandler(onInteract());
        Bukkit.getPluginManager().registerEvents(this, FluffyMachines.getInstance());

    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : dustOutputBorder) {
            preset.addItem(i, orangeNonClickable);
        }

        for (int i : inputBorder) {
            preset.addItem(i,cyanNonClickable);
        }

        for (int i : ingotOutputBorder) {
            preset.addItem(i, redNonClickable);
        }
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                SuperheatedFurnace.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block b) {
        BlockMenu inv = BlockStorage.getInventory(b);
        ItemStack inputItem = inv.getItemInSlot(INPUT_SLOT);

        if (inputItem != null) {

            int amount = inputItem.getAmount();
            String type = getBlockInfo(b.getLocation(), "type");
            SlimefunItem sfItem = SlimefunItem.getByItem(inputItem);

            if (type.equals("none")) {
                if (sfItem != null) {
                    if (sfItem.getID().endsWith("_DUST")) {
                        for (SlimefunItemStack dust : dusts) {
                            if (sfItem == dust.getItem()) {

                                inv.consumeItem(INPUT_SLOT, amount);

                                registerDust(b, dust.getItemId().replace("_DUST", ""), amount);
                                break;

                            }
                        }
                    } else if (sfItem.getID().endsWith("_INGOT")) {
                        for (SlimefunItemStack ingot : ingots) {
                            if (sfItem == ingot.getItem()) {

                                inv.consumeItem(INPUT_SLOT, amount);

                                registerDust(b, ingot.getItemId().replace("_INGOT", ""), amount);
                                break;
                            }
                        }
                    } else if (sfItem.getID().equals(SlimefunItems.GOLD_4K.getItemId())) {
                        inv.consumeItem(INPUT_SLOT, amount);

                        registerDust(b, "GOLD", amount);
                    }
                } else if (inputItem.getItemMeta().equals(new ItemStack(Material.IRON_INGOT).getItemMeta())) {
                    inv.consumeItem(INPUT_SLOT, amount);

                    registerDust(b, "IRON", amount);
                }

            } else {
                if (sfItem!= null && sfItem.getID().equals(type + "_DUST")
                    || (type.equals("GOLD") && sfItem.getID().equals(SlimefunItems.GOLD_4K.getItemId()))
                    || (type.equals("IRON") && inputItem.getItemMeta().equals(
                        new ItemStack(Material.IRON_INGOT).getItemMeta()))) {
                    addDust(b, amount);;
                }
            }
        }
    }

    private ItemHandler onInteract() {
        return (BlockUseHandler) e -> {
            Block b = e.getClickedBlock().get();
            Location l = b.getLocation();
            Player p = e.getPlayer();
            Furnace furnace = (Furnace) b.getState();

            // Glass check somewhat hacky, but people who know about it dont have any advantage
            if (getBlockInfo(l, "accessible") == null) {
                if (b.getRelative(BlockFace.DOWN).getType() != Material.GLASS) {
                    Utils.send(p, "&cThis belongs in the Foundry Multiblock structure! &cUsing it outside of one " +
                        "will act like a regular blast furnace");
                    p.closeInventory();
                }
                return;
            }

            boolean accessible = getBlockInfo(l, "accessible").equals("true");

            if (accessible) {
                if (furnace.getBurnTime() == 0) {
                    if (p.getInventory().getItemInMainHand().equals(new ItemStack(Material.LAVA_BUCKET))
                        && getBlockInfo(l, "reignitable") == null) {

                        furnace.setBurnTime((short) 1000000);
                        furnace.update(true);
                        setBlockInfo(b, "reignitable", "true");

                        ArmorStand lavaStand = (ArmorStand) p.getWorld().spawnEntity(l.add(0.5, -3, 0.5),
                            EntityType.ARMOR_STAND);
                        lavaStand.getEquipment().setHelmet(new CustomItem(
                            SkullItem.fromHash("b6965e6a58684c277d18717cec959f2833a72dfa95661019dbcdf3dbf66b048")));
                        lavaStand.setCanPickupItems(false);
                        lavaStand.setGravity(false);
                        lavaStand.setVisible(false);

                        setBlockInfo(b, "stand", String.valueOf(lavaStand.getUniqueId()));
                        p.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));

                    // Reignite furnace
                    } else if (getBlockInfo(l, "reignitable") != null) {
                        furnace.setBurnTime((short) 1000000);
                        furnace.update(true);
                        BlockMenu inv = BlockStorage.getInventory(b);
                        p.openInventory(inv.toInventory());
                        this.menu = inv;
                        this.foundry = b;
                        updateIndicator(b);
                    } else {
                        Utils.send(p, "&cYou must fill the foundry with lava!");
                    }

                } else if (furnace.getBurnTime() > 0) {
                    // Reignite the furnace
                    furnace.setBurnTime((short) 1000000);
                    furnace.update(true);
                    BlockMenu inv = BlockStorage.getInventory(b);
                    p.openInventory(inv.toInventory());
                    this.menu = inv;
                    this.foundry = b;
                    updateIndicator(b);
                }
            }
        };
    }

    private void setBlockInfo(Block b, String key, String data) {
        BlockStorage.addBlockInfo(b, key, data);
    }

    private String getBlockInfo(Location l, String key) {
        return BlockStorage.getLocationInfo(l, key);
    }

    private void registerDust(Block b, String type, int amount) {
        int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));
        setBlockInfo(b, "stored", String.valueOf(stored + amount));
        setBlockInfo(b, "type", type);
        updateIndicator(b);
    }

    private void addDust(Block b, int amount) {
        int stored = Integer.parseInt(getBlockInfo(b.getLocation(), "stored"));
        setBlockInfo(b, "stored", String.valueOf(stored + amount));
        updateIndicator(b);
    }

    private void updateIndicator(Block b) {
        BlockMenu inv = BlockStorage.getInventory(b);
        String stored = getBlockInfo(b.getLocation(), "stored");
        String type = WordUtils.capitalizeFully(getBlockInfo(b.getLocation(), "type"));
        inv.replaceExistingItem(INPUT_INDICATOR, Utils.buildNonInteractable(
            Material.CHEST, "&6Melted Dust", "&e" + stored, "&bType: " + type));
        inv.replaceExistingItem(DUST_INDICATOR, buildButton(
            6969420, Material.GUNPOWDER, "&6Dust Available", "&e" + stored, "&a> Click here to retrieve"));
        inv.replaceExistingItem(INGOT_INDICATOR, buildButton(
            6969421, Material.IRON_INGOT, "&6Ingots available", "&e" + stored, "&a> Click here to retrieve"));
        if (stored.equals("0")) {
            setBlockInfo(b, "type", "none");
        }
    }

    // All these things are outside because the blast furnace screws with the menu and non clickables

    public static ItemStack buildButton(int id, Material material, @Nullable String name, @Nullable String... lore) {
        ItemStack nonClickable = new ItemStack(material);
        ItemMeta NCMeta = nonClickable.getItemMeta();
        if (name != null) {
            NCMeta.setDisplayName(ChatColors.color(name));
        } else {
            NCMeta.setDisplayName(" ");
        }

        if (lore.length > 0) {
            List<String> lines = new ArrayList();
            String[] loreString = lore;
            int loreLength = lore.length;

            for (int i = 0; i < loreLength; ++i) {
                String line = loreString[i];
                lines.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            NCMeta.setLore(lines);
        }
        NCMeta.setCustomModelData(id);
        nonClickable.setItemMeta(NCMeta);
        return nonClickable;
    }

    @EventHandler
    public void onButtonClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();

        if (item != null && item.getType() != Material.AIR && item.getItemMeta().hasCustomModelData()) {
            e.setCancelled(true);
            int stored = Integer.parseInt(getBlockInfo(foundry.getLocation(), "stored"));
            String type = getBlockInfo(foundry.getLocation(), "type");

            if (stored > 0) {
                // Dust withdraw
                if (item.getItemMeta().getCustomModelData() == 6969420
                    && (menu.getItemInSlot(DUST_OUTPUT_SLOT) == null
                    || menu.getItemInSlot(DUST_OUTPUT_SLOT).getAmount() < 64)) {
                    setBlockInfo(foundry, "stored", String.valueOf(stored - 1));
                    menu.pushItem(SlimefunItem.getByID(type + "_DUST").getItem(), DUST_OUTPUT_SLOT);
                    updateIndicator(foundry);

                // Ingot withdraw
                } else if (item.getItemMeta().getCustomModelData() == 6969421
                    && (menu.getItemInSlot(INGOT_OUTPUT_SLOT) == null
                    || menu.getItemInSlot(INGOT_OUTPUT_SLOT).getAmount() < 64)) {
                    setBlockInfo(foundry, "stored", String.valueOf(stored - 1));
                    if (type.equals("GOLD")) {
                        menu.pushItem(SlimefunItems.GOLD_4K.getItem().getItem(), INGOT_OUTPUT_SLOT);
                    }
                    else if (type.equals("IRON")) {
                        menu.pushItem(new ItemStack(Material.IRON_INGOT), INGOT_OUTPUT_SLOT);

                    } else {
                        menu.pushItem(SlimefunItem.getByID(type + "_INGOT").getItem(), INGOT_OUTPUT_SLOT);
                    }
                    updateIndicator(foundry);
                }
            }
        }
    }
}

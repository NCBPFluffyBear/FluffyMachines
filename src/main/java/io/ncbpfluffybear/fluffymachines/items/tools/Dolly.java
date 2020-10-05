package io.ncbpfluffybear.fluffymachines.items.tools;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dolly extends SimpleSlimefunItem<ItemUseHandler> {

    private static final ItemStack lockItem = Utils.buildNonInteractable(Material.DIRT, "&4&lDolly empty", "&cHow did you get in here?");

    public Dolly(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Player p = e.getPlayer();
            ItemStack dolly = e.getItem();

            if (!e.getClickedBlock().isPresent()) {
                return;
            }

            Block b = e.getClickedBlock().get();

            if (!SlimefunPlugin.getProtectionManager().hasPermission(e.getPlayer(), b.getLocation(),
                ProtectableAction.BREAK_BLOCK)) {
                return;
            }

            Block relative = b.getRelative(e.getClickedFace());

            if (b.getType() == Material.CHEST && !BlockStorage.hasBlockInfo(b)) {

                ItemMeta dollyMeta = dolly.getItemMeta();
                for (String line : dollyMeta.getLore()) {
                    if (line.contains("ID: <ID>")) {
                        PlayerProfile.get(p, profile -> {
                            int backpackId = profile.createBackpack(27).getId();
                            SlimefunPlugin.getBackpackListener().setBackpackId(p, dolly, 3, backpackId);
                            PlayerProfile.getBackpack(dolly, backpack -> {
                                backpack.getInventory().setItem(0, lockItem);
                            });
                        });
                    }
                }

                Inventory chest = ((InventoryHolder) b.getState()).getInventory();

                if (chest.getSize() > 27) {
                    Utils.send(p, "&cYou can only pick up single chests!");
                    return;
                }

                ItemStack[] contents = chest.getContents();

                AtomicBoolean exists = new AtomicBoolean(false);
                PlayerProfile.getBackpack(dolly, backpack -> {
                    if (backpack != null && backpack.getInventory().getItem(0) != null
                        && Utils.checkNonInteractable(backpack.getInventory().getItem(0))) {
                        backpack.getInventory().setStorageContents(contents);
                        chest.clear();
                        PlayerProfile.getBackpack(dolly, PlayerBackpack::markDirty);
                        exists.set(true);
                        dolly.setType(Material.CHEST_MINECART);
                    } else {
                        Utils.send(p, "&cThis dolly is already carrying a chest!");
                    }
                });

                // Deals with async problems
                if (exists.get()) {
                    b.setType(Material.AIR);
                    Utils.send(p, "&aYou have picked up this chest");
                }


            } else if (relative.getType() == Material.AIR) {

                PlayerProfile.getBackpack(dolly, backpack -> {
                    if (backpack != null && (backpack.getInventory().getItem(0) == null || !Utils.checkNonInteractable(backpack.getInventory().getItem(0)))) {
                        ItemStack[] bpcontents = backpack.getInventory().getContents();
                        backpack.getInventory().clear();
                        backpack.getInventory().setItem(0, lockItem);
                        relative.setType(Material.CHEST);
                        ((InventoryHolder) relative.getState()).getInventory().setStorageContents(bpcontents);
                        dolly.setType(Material.MINECART);
                        Utils.send(p, "&aChest has been placed");
                    } else {
                        Utils.send(p, "&cYou must pick up a chest first!");
                    }
                });
            }
        };
    }


}

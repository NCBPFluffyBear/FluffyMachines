package io.ncbpfluffybear.fluffymachines.listeners;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.ncbpfluffybear.fluffymachines.machines.AutoCraftingTable;
import io.ncbpfluffybear.fluffymachines.machines.SmartFactory;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import java.util.Optional;
import javax.annotation.Nullable;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class KeyedCrafterListener implements Listener {

    public KeyedCrafterListener() {
    }

    @EventHandler
    private void onSmartFactoryInteract(PlayerRightClickEvent e) {
        Optional<Block> clickedBlock = e.getClickedBlock();

        if (e.getHand() == EquipmentSlot.HAND && e.useBlock() != Event.Result.DENY && clickedBlock.isPresent() && e.getPlayer().isSneaking()) {
            Optional<SlimefunItem> slimefunBlock = e.getSlimefunBlock();

            if (!slimefunBlock.isPresent()) {
                return;
            }

            SlimefunItem sfBlock = slimefunBlock.get();
            ItemStack item = e.getItem();
            Player p = e.getPlayer();
            SlimefunItem key = SlimefunItem.getByItem(item);
            Block b = clickedBlock.get();

            // Handle SmartFactory recipe setting
            if (sfBlock instanceof SmartFactory) {

                if (isCargoNode(key)) {
                    return;
                }
                e.cancel();

                if (key == null) {
                    Utils.send(p, "&c你不能在此機器上使用原版物品!");
                    return;
                }

                if (SmartFactory.getAcceptedItems().contains((SlimefunItemStack) key.getItem())) {

                    BlockStorage.addBlockInfo(b, "recipe", key.getId());
                    BlockStorage.getInventory(b).replaceExistingItem(SmartFactory.RECIPE_SLOT,
                            SmartFactory.getDisplayItem(key, ((RecipeDisplayItem) sfBlock).getDisplayRecipes())
                    );
                    Utils.send(p, "&a目標配方設定為 " + key.getItemName());
                } else {
                    Utils.send(p, "&c這個物品並不支持!");
                }

            } else if (sfBlock instanceof AutoCraftingTable) {

                if (isCargoNode(key)) {
                    return;
                }
                e.cancel();

                if (item.getType() == Material.AIR) {
                    Utils.send(p, "&c拿著物品對機器右鍵來設定原版配方");
                    return;
                }

                BlockStorage.getInventory(b).replaceExistingItem(AutoCraftingTable.KEY_SLOT,
                        AutoCraftingTable.createKeyItem(item.getType())
                );

                Utils.send(p, "&a目標配方設定為 "
                        + WordUtils.capitalizeFully(item.getType().name().replace("_", " "))
                );
            }
        }
    }

    private boolean isCargoNode(@Nullable SlimefunItem recipe) {
        return recipe != null && (recipe.getItem() == SlimefunItems.CARGO_INPUT_NODE
                || recipe.getItem() == SlimefunItems.CARGO_OUTPUT_NODE || recipe.getItem() == SlimefunItems.CARGO_OUTPUT_NODE_2);
    }
}

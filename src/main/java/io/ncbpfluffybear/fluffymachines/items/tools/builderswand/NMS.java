package io.ncbpfluffybear.fluffymachines.items.tools.builderswand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface NMS {
    void spawnParticle(String particle, Location location);

    void spawnParticle(String particle, Location location, Player player);

    ItemStack getItemInHand(Player player);

    boolean isMainHand(PlayerInteractEvent event);

    String getDefaultParticle();

    void addShapelessRecipe(List<String> recipeStrings, HashMap<String, Material> ingredients, ItemStack resultItemStack);

    void addShapedRecipe(List<String> recipeStrings, HashMap<String, Material> ingredients, ItemStack resultItemStack);

    ItemStack setTag(ItemStack itemStack, String path, String value);

    String getTag(ItemStack itemStack, String path);

    Block setBlockData(Block against, Block SelectionBlock);
}

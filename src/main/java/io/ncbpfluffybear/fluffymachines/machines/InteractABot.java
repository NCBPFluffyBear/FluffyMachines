package io.ncbpfluffybear.fluffymachines.machines;

import io.github.thebusybiscuit.slimefun4.implementation.items.androids.AndroidType;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.ProgrammableAndroid;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InteractABot extends ProgrammableAndroid {

    public InteractABot(Category category, int tier, SlimefunItemStack item, RecipeType recipeType,
                        ItemStack[] recipe) {
        super(category, tier, item, recipeType, recipe);
    }

    @Override
    public AndroidType getAndroidType() {
        return AndroidType.MINER;
    }

    @Override
    protected void dig(Block b, BlockMenu menu, Block block) {
        PlayerInteractEvent interactEvent =
            new PlayerInteractEvent(Bukkit.getPlayer(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(),
                "owner"))), Action.RIGHT_CLICK_BLOCK, null, block, b.getFace(block));
        Bukkit.getPluginManager().callEvent(interactEvent);
    }

}

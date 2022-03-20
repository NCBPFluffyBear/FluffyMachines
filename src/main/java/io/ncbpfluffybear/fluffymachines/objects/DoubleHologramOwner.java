package io.ncbpfluffybear.fluffymachines.objects;


import io.github.thebusybiscuit.slimefun4.core.attributes.ItemAttribute;
import io.github.thebusybiscuit.slimefun4.core.services.holograms.HologramsService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.HologramProjector;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * This {@link ItemAttribute} manages holograms.
 * Modified version of {@link io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner}
 * Didn't extend because all methods are being modified
 *
 * @author TheBusyBiscuit
 * @author NCBPFluffyBear
 *
 * @see HologramProjector
 * @see HologramsService
 *
 */
public interface DoubleHologramOwner extends ItemAttribute {

    default void updateHologram(@Nonnull Block b, @Nonnull String topText, @Nonnull String bottomText) {
        Location locTop = b.getLocation().add(getTopHologramOffset(b));
        Location locBot = b.getLocation().add(getBottomHologramOffset(b));
        Slimefun.getHologramsService().setHologramLabel(locTop, ChatColors.color(topText));
        Slimefun.getHologramsService().setHologramLabel(locBot, ChatColors.color(bottomText));
    }

    default void removeHologram(@Nonnull Block b) {
        Location locTop = b.getLocation().add(getTopHologramOffset(b));
        Location locBot = b.getLocation().add(getBottomHologramOffset(b));
        Slimefun.getHologramsService().removeHologram(locTop);
        Slimefun.getHologramsService().removeHologram(locBot);
    }

    @Nonnull
    default Vector getHologramOffset(@Nonnull Block block) {
        return Slimefun.getHologramsService().getDefaultOffset();
    }

    @Nonnull
    default double getHologramSpacing() {
        return 0.5;
    }

    @Nonnull
    default Vector getTopHologramOffset(@Nonnull Block block) {
        return getHologramOffset(block).add(new Vector(0.0, getHologramSpacing() / 2, 0.0));
    }

    @Nonnull
    default Vector getBottomHologramOffset(@Nonnull Block block) {
        return getHologramOffset(block).add(new Vector(0.0, -getHologramSpacing() / 2, 0.0));
    }

}

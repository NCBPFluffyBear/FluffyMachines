package io.ncbpfluffybear.fluffymachines.objects;


import io.github.thebusybiscuit.slimefun4.core.attributes.ItemAttribute;
import io.github.thebusybiscuit.slimefun4.core.services.holograms.HologramsService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.HologramProjector;
import io.ncbpfluffybear.fluffymachines.utils.Utils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    default void updateHologram(@Nonnull Block b, @Nullable String topText, @Nonnull String bottomText) {
        Location locTop = b.getLocation().add(getTopHologramOffset(b));
        Location locBot = b.getLocation().add(getBottomHologramOffset(b));
        Slimefun.getHologramsService().setHologramLabel(locTop, Utils.color(topText));
        Slimefun.getHologramsService().setHologramLabel(locBot, Utils.color(bottomText));
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
        return 0.2;
    }

    @Nonnull
    default Vector getTopHologramOffset(@Nonnull Block block) {
        return getHologramOffset(block).add(new Vector(0.0, getHologramSpacing(), 0.0));
    }

    @Nonnull
    default Vector getBottomHologramOffset(@Nonnull Block block) {
        return getHologramOffset(block);
    }

}

package io.ncbpfluffybear.fluffymachines.items.tools;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * The {@link BlockBreakEvent} used by the {@link UpgradedExplosiveTool}
 * to prevent a chain breaking nuclear explosion.
 *
 * @author NCBPFluffyBear
 */
public class ExplosiveToolEvent extends BlockBreakEvent {

    public ExplosiveToolEvent(Block b, Player p) {
        super(b, p);
    }
}

package io.ncbpfluffybear.fluffymachines.items.tools;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ExplosiveToolEvent extends BlockBreakEvent {

    public ExplosiveToolEvent(Block b, Player p) {
        super(b, p);
    }
}

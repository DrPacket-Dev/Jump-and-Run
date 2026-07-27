package de.drpacket.jumpplugin.listener;

import de.drpacket.jumpplugin.JumpPlugin;
import de.drpacket.jumpplugin.arena.ArenaManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final JumpPlugin plugin;
    private final ArenaManager arenaManager;

    public PlayerMoveListener(JumpPlugin plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!event.hasChangedBlock()) {
            return;
        }

        Block block = player.getLocation().getBlock().getRelative(0, -1, 0);
        if (block.getType() == Material.GLASS) {
            arenaManager.handleBlockTouch(player, block);
        }
    }
}

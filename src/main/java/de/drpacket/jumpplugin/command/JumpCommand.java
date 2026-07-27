package de.drpacket.jumpplugin.command;

import de.drpacket.jumpplugin.arena.ArenaManager;
import de.drpacket.jumpplugin.JumpPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JumpCommand implements CommandExecutor {

    private final JumpPlugin plugin;
    private final ArenaManager arenaManager;

    public JumpCommand(JumpPlugin plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        arenaManager.startCourse(player);
        return true;
    }
}

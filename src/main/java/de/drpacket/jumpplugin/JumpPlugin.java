package de.drpacket.jumpplugin;

import de.drpacket.jumpplugin.arena.ArenaManager;
import de.drpacket.jumpplugin.command.JumpCommand;
import de.drpacket.jumpplugin.listener.PlayerMoveListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class JumpPlugin extends JavaPlugin {

    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.arenaManager = new ArenaManager(this);
        getCommand("jump").setExecutor(new JumpCommand(this, arenaManager));
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this, arenaManager), this);
        getLogger().info("Jump-and-Run-Lobby plugin enabled");
    }

    @Override
    public void onDisable() {
        arenaManager.clearAll();
        getLogger().info("Jump-and-Run-Lobby plugin disabled");
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }
}

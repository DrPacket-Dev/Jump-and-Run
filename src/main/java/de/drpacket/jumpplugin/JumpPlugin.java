package de.drpacket.jumpplugin;

import de.drpacket.jumpplugin.arena.ArenaManager;
import de.drpacket.jumpplugin.command.JumpCommand;
import de.drpacket.jumpplugin.command.NavigatorCommand;
import de.drpacket.jumpplugin.listener.PlayerMoveListener;
import de.drpacket.jumpplugin.listener.NavigatorListener;
import de.drpacket.jumpplugin.navigator.NavigatorManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class JumpPlugin extends JavaPlugin {

    private ArenaManager arenaManager;
    private NavigatorManager navigatorManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.arenaManager = new ArenaManager(this);
        this.navigatorManager = new NavigatorManager(this);
        getCommand("jump").setExecutor(new JumpCommand(this, arenaManager));
        NavigatorCommand navigatorCommand = new NavigatorCommand(navigatorManager);
        getCommand("navigator").setExecutor(navigatorCommand);
        getCommand("navigator").setTabCompleter(navigatorCommand);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this, arenaManager), this);
        getServer().getPluginManager().registerEvents(new NavigatorListener(this, navigatorManager), this);
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

    public NavigatorManager getNavigatorManager() {
        return navigatorManager;
    }
}

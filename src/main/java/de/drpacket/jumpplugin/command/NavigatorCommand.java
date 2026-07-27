package de.drpacket.jumpplugin.command;

import de.drpacket.jumpplugin.navigator.NavigatorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class NavigatorCommand implements CommandExecutor, TabCompleter {
    private final NavigatorManager navigatorManager;

    public NavigatorCommand(NavigatorManager navigatorManager) {
        this.navigatorManager = navigatorManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                navigatorManager.openMenu(player);
                return true;
            }
            sender.sendMessage("Nur Spieler können den Navigator öffnen.");
            return true;
        }
        if (args[0].equalsIgnoreCase("admin")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Nur Spieler können die Admin-UI öffnen.");
                return true;
            }
            if (!sender.hasPermission("jumpplugin.navigator.admin")) {
                sender.sendMessage("Dafür hast du keine Rechte.");
                return true;
            }
            navigatorManager.openAdmin(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("edit") && args.length >= 3) {
            if (!sender.hasPermission("jumpplugin.navigator.admin")) {
                sender.sendMessage("Dafür hast du keine Rechte.");
                return true;
            }
            String itemName = args[1].toLowerCase();
            String commandText = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
            if (navigatorManager.setCommand(itemName, commandText)) {
                sender.sendMessage("Command für " + itemName + " gespeichert.");
            } else {
                sender.sendMessage("Dieses Navigator-Feld existiert nicht.");
            }
            return true;
        }
        sender.sendMessage("/navigator, /navigator admin oder /navigator edit <name> <command>");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("admin", "edit");
        }
        return new ArrayList<>();
    }
}

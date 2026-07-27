package de.drpacket.jumpplugin.listener;

import de.drpacket.jumpplugin.JumpPlugin;
import de.drpacket.jumpplugin.navigator.NavigatorAnimation;
import de.drpacket.jumpplugin.navigator.NavigatorHolder;
import de.drpacket.jumpplugin.navigator.NavigatorManager;
import de.drpacket.jumpplugin.navigator.NavigatorView;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public final class NavigatorListener implements Listener {
    private final JumpPlugin plugin;
    private final NavigatorManager navigatorManager;

    public NavigatorListener(JumpPlugin plugin, NavigatorManager navigatorManager) {
        this.plugin = plugin;
        this.navigatorManager = navigatorManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        navigatorManager.giveHotbarItem(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!navigatorManager.isNavigator(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        navigatorManager.openMenu(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || !(event.getInventory().getHolder() instanceof NavigatorHolder holder)) {
            return;
        }
        if (holder.view() == NavigatorView.MENU) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && !clicked.getType().isAir() && clicked.hasItemMeta()) {
                navigatorManager.runEntry(player, clicked);
            }
            return;
        }
        if (holder.view() == NavigatorView.ADMIN) {
            event.setCancelled(true);
            switch (event.getRawSlot()) {
                case 10 -> navigatorManager.setAnimation(NavigatorAnimation.NONE);
                case 12 -> navigatorManager.setAnimation(NavigatorAnimation.ROWS);
                case 14 -> navigatorManager.setAnimation(NavigatorAnimation.RANDOM);
                case 16 -> navigatorManager.openEditor(player);
                default -> { return; }
            }
            if (event.getRawSlot() != 16) {
                player.sendMessage(navigatorManager.message("<green>Animation gespeichert."));
            }
            return;
        }
        if (holder.view() == NavigatorView.EDITOR && event.getClickedInventory() == event.getInventory()) {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().isAir()) {
                return;
            }
            event.setCancelled(true);
            navigatorManager.beginNaming(player, cursor);
            player.closeInventory();
            player.sendMessage(navigatorManager.message("<yellow>Schreibe jetzt <white>!Name <yellow>in den Chat. MiniMessage wird unterstützt."));
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String text = PlainTextComponentSerializer.plainText().serialize(event.message());
        if (!text.startsWith("!")) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        player.getServer().getScheduler().runTask(plugin, () -> navigatorManager.finishNaming(player, text));
    }
}

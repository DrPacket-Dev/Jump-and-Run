package de.drpacket.jumpplugin.navigator;

import de.drpacket.jumpplugin.JumpPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class NavigatorManager {
    private static final int MENU_SIZE = 54;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final JumpPlugin plugin;
    private final NamespacedKey navigatorKey;
    private final NamespacedKey entryKey;
    private final Map<UUID, ItemStack> pendingNameItems = new HashMap<>();

    public NavigatorManager(JumpPlugin plugin) {
        this.plugin = plugin;
        this.navigatorKey = new NamespacedKey(plugin, "navigator");
        this.entryKey = new NamespacedKey(plugin, "navigator_entry");
    }

    public ItemStack createHotbarItem() {
        Material material = Material.matchMaterial(plugin.getConfig().getString("navigator.item.material", "COMPASS"));
        ItemStack item = new ItemStack(material == null ? Material.COMPASS : material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(message(plugin.getConfig().getString("navigator.item.name", "<gold>Navigator")));
        meta.getPersistentDataContainer().set(navigatorKey, PersistentDataType.STRING, "navigator");
        item.setItemMeta(meta);
        return item;
    }

    public boolean isNavigator(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        String value = item.getItemMeta().getPersistentDataContainer().get(navigatorKey, PersistentDataType.STRING);
        return "navigator".equals(value);
    }

    public void openMenu(Player player) {
        Inventory inventory = createInventory(NavigatorView.MENU, MENU_SIZE, plugin.getConfig().getString("navigator.menu.title", "<dark_gray>Navigator"));
        List<NavigatorItem> entries = loadEntries();
        NavigatorAnimation animation = NavigatorAnimation.fromConfig(plugin.getConfig().getString("navigator.menu.animation", "NONE"));
        if (animation == NavigatorAnimation.NONE) {
            entries.forEach(entry -> inventory.setItem(slotFor(entry.name()), tagEntry(entry)));
            player.openInventory(inventory);
            return;
        }
        player.openInventory(inventory);
        animateOpen(inventory, entries, animation);
    }

    public void openAdmin(Player player) {
        Inventory inventory = createInventory(NavigatorView.ADMIN, 27, plugin.getConfig().getString("navigator.admin.title", "<red>Navigator Admin"));
        inventory.setItem(10, named(Material.CLOCK, "<green>Animation: <white>NONE"));
        inventory.setItem(12, named(Material.REPEATER, "<green>Animation: <white>ROWS"));
        inventory.setItem(14, named(Material.ENDER_PEARL, "<green>Animation: <white>RANDOM"));
        inventory.setItem(16, named(Material.CHEST, "<yellow>Bearbeiten"));
        player.openInventory(inventory);
    }

    public void openEditor(Player player) {
        Inventory inventory = createInventory(NavigatorView.EDITOR, MENU_SIZE, plugin.getConfig().getString("navigator.editor.title", "<dark_green>Navigator bearbeiten"));
        loadEntries().forEach(entry -> inventory.setItem(slotFor(entry.name()), tagEntry(entry)));
        player.openInventory(inventory);
    }

    public void setAnimation(NavigatorAnimation animation) {
        plugin.getConfig().set("navigator.menu.animation", animation.name());
        plugin.saveConfig();
    }

    public void beginNaming(Player player, ItemStack item) {
        pendingNameItems.put(player.getUniqueId(), item.clone());
    }

    public boolean finishNaming(Player player, String message) {
        if (!message.startsWith("!")) {
            return pendingNameItems.containsKey(player.getUniqueId());
        }
        ItemStack item = pendingNameItems.remove(player.getUniqueId());
        if (item == null) {
            return false;
        }
        String name = message.substring(1).trim();
        if (name.isEmpty()) {
            player.sendMessage(message("<red>Der Name darf nicht leer sein."));
            return true;
        }
        saveEntry(name, item, "");
        player.sendMessage(message("<green>Navigator-Feld <white>" + name + " <green>wurde gespeichert."));
        return true;
    }

    public boolean setCommand(String name, String command) {
        String path = "navigator.entries." + name.toLowerCase(Locale.ROOT);
        if (!plugin.getConfig().isConfigurationSection(path)) {
            return false;
        }
        plugin.getConfig().set(path + ".command", command);
        plugin.saveConfig();
        return true;
    }

    public void runEntry(Player player, ItemStack item) {
        String name = item.getItemMeta().getPersistentDataContainer().get(entryKey, PersistentDataType.STRING);
        if (name == null) {
            return;
        }
        String command = plugin.getConfig().getString("navigator.entries." + name + ".command", "");
        if (command.isBlank()) {
            player.sendMessage(message("<red>Für dieses Feld ist noch kein Command gesetzt."));
            return;
        }
        player.closeInventory();
        Bukkit.dispatchCommand(player, command.replace("%player%", player.getName()).replaceFirst("^/", ""));
    }

    private void saveEntry(String name, ItemStack item, String command) {
        String key = name.toLowerCase(Locale.ROOT);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(message(name));
        item.setItemMeta(meta);
        plugin.getConfig().set("navigator.entries." + key + ".item", item);
        plugin.getConfig().set("navigator.entries." + key + ".command", command);
        plugin.saveConfig();
    }

    private List<NavigatorItem> loadEntries() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("navigator.entries");
        if (section == null) {
            return List.of();
        }
        List<NavigatorItem> items = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ItemStack item = section.getItemStack(key + ".item");
            if (item != null) {
                items.add(new NavigatorItem(key, item, section.getString(key + ".command", "")));
            }
        }
        return items;
    }

    private void animateOpen(Inventory inventory, List<NavigatorItem> entries, NavigatorAnimation animation) {
        List<NavigatorItem> ordered = new ArrayList<>(entries);
        if (animation == NavigatorAnimation.RANDOM) {
            Collections.shuffle(ordered);
        }
        for (int i = 0; i < ordered.size(); i++) {
            NavigatorItem entry = ordered.get(i);
            Bukkit.getScheduler().runTaskLater(plugin, () -> inventory.setItem(slotFor(entry.name()), tagEntry(entry)), i * 2L);
        }
    }

    private Inventory createInventory(NavigatorView view, int size, String title) {
        NavigatorHolder holder = new NavigatorHolder(view);
        Inventory inventory = Bukkit.createInventory(holder, size, message(title));
        holder.setInventory(inventory);
        return inventory;
    }

    private ItemStack tagEntry(NavigatorItem entry) {
        ItemStack item = entry.item().clone();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(entryKey, PersistentDataType.STRING, entry.name());
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack named(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(message(name));
        item.setItemMeta(meta);
        return item;
    }

    private int slotFor(String key) {
        return Math.floorMod(key.hashCode(), MENU_SIZE);
    }

    public void giveHotbarItem(Player player) {
        int slot = Math.max(0, Math.min(8, plugin.getConfig().getInt("navigator.item.slot", 4)));
        player.getInventory().setItem(slot, createHotbarItem());
    }

    public Component message(String input) {
        return MINI_MESSAGE.deserialize(input == null ? "" : input);
    }
}

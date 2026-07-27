package de.drpacket.jumpplugin.arena;

import de.drpacket.jumpplugin.JumpPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaManager {

    private final JumpPlugin plugin;
    private final Map<UUID, Integer> activeStepIndex = new HashMap<>();
    private final Map<UUID, Location> startLocations = new HashMap<>();
    private final Map<UUID, Integer> completedJumps = new HashMap<>();
    private final Map<UUID, Integer> highscore = new HashMap<>();
    private final Map<UUID, BukkitTask> hudTasks = new HashMap<>();

    public ArenaManager(JumpPlugin plugin) {
        this.plugin = plugin;
    }

    public void startCourse(Player player) {
        Location start = resolveStartLocation();
        player.teleportAsync(start);
        player.sendMessage("§aJump-and-Run gestartet!");

        UUID uuid = player.getUniqueId();
        startLocations.put(uuid, start);
        activeStepIndex.put(uuid, 1);
        completedJumps.put(uuid, 0);
        highscore.putIfAbsent(uuid, 0);
        buildCourse(player, 1);
        startHud(player);
    }

    public void clearAll() {
        activeStepIndex.clear();
        startLocations.clear();
        completedJumps.clear();
        highscore.clear();
        hudTasks.values().forEach(BukkitTask::cancel);
        hudTasks.clear();
    }

    public void handleBlockTouch(Player player, Block block) {
        UUID uuid = player.getUniqueId();
        Integer activeIndex = activeStepIndex.get(uuid);
        if (activeIndex == null) {
            return;
        }

        Location expectedLocation = getBlockLocationForIndex(player, activeIndex);
        if (block.getLocation().equals(expectedLocation)) {
            block.setType(Material.WHITE_WOOL);
            block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation().add(0.5, 1.0, 0.5), 8, 0.2, 0.2, 0.2, 0.0);

            int currentProgress = completedJumps.getOrDefault(uuid, 0) + 1;
            completedJumps.put(uuid, currentProgress);
            int best = highscore.getOrDefault(uuid, 0);
            if (currentProgress > best) {
                highscore.put(uuid, currentProgress);
            }

            int nextIndex = activeIndex + 2;
            if (nextIndex > 10) {
                player.sendMessage("§aDu hast die Strecke geschafft!");
                activeStepIndex.remove(uuid);
                startLocations.remove(uuid);
                stopHud(uuid);
                return;
            }

            activeStepIndex.put(uuid, nextIndex);
            buildCourse(player, nextIndex);
            player.sendMessage("§eWeiter geht's!");
        }
    }

    private void buildCourse(Player player, int activeIndex) {
        Location base = startLocations.get(player.getUniqueId());
        if (base == null) {
            return;
        }

        for (int index = 0; index <= 10; index++) {
            Location location = base.clone().add(index * 2.0, 0.0, 0.0);
            Block block = location.getBlock();
            if (index == activeIndex) {
                block.setType(Material.GLASS);
                block.getWorld().spawnParticle(Particle.END_ROD, block.getLocation().add(0.5, 1.0, 0.5), 8, 0.1, 0.1, 0.1, 0.0);
            } else if (index == activeIndex - 1 || index == activeIndex - 2) {
                block.setType(Material.WHITE_WOOL);
            } else {
                block.setType(Material.WHITE_WOOL);
            }
        }
    }

    private void startHud(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask existing = hudTasks.get(uuid);
        if (existing != null) {
            existing.cancel();
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    stopHud(uuid);
                    return;
                }
                Integer current = completedJumps.get(uuid);
                Integer best = highscore.get(uuid);
                if (current == null || best == null) {
                    stopHud(uuid);
                    return;
                }
                String bar = "§b§lJUMP §7| §aHighscore: §f" + best + " §7| §eSprünge: §f" + current;
                player.sendActionBar(bar);
            }
        }.runTaskTimer(plugin, 0L, 20L);

        hudTasks.put(uuid, task);
    }

    private void stopHud(UUID uuid) {
        BukkitTask task = hudTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    private Location resolveStartLocation() {
        var config = plugin.getConfig();
        return new Location(
                plugin.getServer().getWorld(config.getString("start.world", "world")),
                config.getDouble("start.x", 0.0),
                config.getDouble("start.y", 80.0),
                config.getDouble("start.z", 0.0),
                (float) config.getDouble("start.yaw", 0.0),
                (float) config.getDouble("start.pitch", 0.0)
        );
    }

    private Location getBlockLocationForIndex(Player player, int index) {
        Location base = startLocations.get(player.getUniqueId());
        if (base == null) {
            return player.getLocation();
        }
        return base.clone().add(index * 2.0, 0.0, 0.0);
    }
}

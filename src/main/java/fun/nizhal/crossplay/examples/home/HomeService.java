package fun.nizhal.crossplay.examples.home;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class HomeService {

    private final Plugin plugin;
    private final int maxHomes;
    // uuid -> (name -> location)
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();

    public HomeService(Plugin plugin) {
        this.plugin = plugin;
        this.maxHomes = plugin.getConfig().getInt("homes.max-homes", 5);
        load();
    }

    public List<String> namesFor(UUID uuid) {
        Map<String, Location> map = homes.get(uuid);
        return map == null ? List.of() : new ArrayList<>(map.keySet());
    }

    public boolean canAdd(UUID uuid) {
        if (maxHomes < 0) return true;
        return namesFor(uuid).size() < maxHomes;
    }

    public void set(UUID uuid, String name, Location location) {
        homes.computeIfAbsent(uuid, k -> new HashMap<>()).put(name.toLowerCase(), location);
        save();
    }

    public boolean delete(UUID uuid, String name) {
        Map<String, Location> map = homes.get(uuid);
        if (map == null || !map.containsKey(name.toLowerCase())) return false;
        map.remove(name.toLowerCase());
        save();
        return true;
    }

    public void teleport(Player player, String name) {
        Map<String, Location> map = homes.get(player.getUniqueId());
        Location loc = map == null ? null : map.get(name.toLowerCase());
        if (loc == null) {
            player.sendMessage("Home '" + name + "' not found.");
            return;
        }
        player.teleport(loc);
        player.sendMessage("Teleported to home " + name + ".");
    }

    private void load() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection players = cfg.getConfigurationSection("player-homes");
        if (players == null) return;
        for (String uuidStr : players.getKeys(false)) {
            UUID uuid;
            try { uuid = UUID.fromString(uuidStr); } catch (IllegalArgumentException e) { continue; }
            ConfigurationSection playerSection = players.getConfigurationSection(uuidStr);
            if (playerSection == null) continue;
            Map<String, Location> map = new HashMap<>();
            for (String homeName : playerSection.getKeys(false)) {
                ConfigurationSection entry = playerSection.getConfigurationSection(homeName);
                if (entry == null) continue;
                String worldName = entry.getString("world", "world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;
                map.put(homeName, new Location(world,
                    entry.getDouble("x"), entry.getDouble("y"), entry.getDouble("z"),
                    (float) entry.getDouble("yaw"), (float) entry.getDouble("pitch")));
            }
            homes.put(uuid, map);
        }
    }

    private void save() {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("player-homes", null);
        for (Map.Entry<UUID, Map<String, Location>> playerEntry : homes.entrySet()) {
            String uuidPath = "player-homes." + playerEntry.getKey();
            for (Map.Entry<String, Location> homeEntry : playerEntry.getValue().entrySet()) {
                String path = uuidPath + "." + homeEntry.getKey();
                Location loc = homeEntry.getValue();
                cfg.set(path + ".world", loc.getWorld().getName());
                cfg.set(path + ".x", loc.getX());
                cfg.set(path + ".y", loc.getY());
                cfg.set(path + ".z", loc.getZ());
                cfg.set(path + ".yaw", (double) loc.getYaw());
                cfg.set(path + ".pitch", (double) loc.getPitch());
            }
        }
        plugin.saveConfig();
    }
}

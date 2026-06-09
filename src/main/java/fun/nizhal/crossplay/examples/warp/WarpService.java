package fun.nizhal.crossplay.examples.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WarpService {

    private final Plugin plugin;
    private final Map<String, Location> warps = new HashMap<>();

    public WarpService(Plugin plugin) {
        this.plugin = plugin;
        load();
    }

    public List<String> names() {
        return new ArrayList<>(warps.keySet());
    }

    public boolean exists(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public void set(String name, Location location) {
        warps.put(name.toLowerCase(), location);
        save();
    }

    public void delete(String name) {
        warps.remove(name.toLowerCase());
        save();
    }

    public void teleport(org.bukkit.entity.Player player, String name) {
        Location loc = warps.get(name.toLowerCase());
        if (loc == null) {
            player.sendMessage("Warp '" + name + "' does not exist.");
            return;
        }
        player.teleport(loc);
        player.sendMessage("Teleported to warp " + name + ".");
    }

    private void load() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection section = cfg.getConfigurationSection("warps");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            ConfigurationSection entry = section.getConfigurationSection(key);
            if (entry == null) continue;
            String worldName = entry.getString("world", "world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;
            double x = entry.getDouble("x");
            double y = entry.getDouble("y");
            double z = entry.getDouble("z");
            float yaw = (float) entry.getDouble("yaw");
            float pitch = (float) entry.getDouble("pitch");
            warps.put(key.toLowerCase(), new Location(world, x, y, z, yaw, pitch));
        }
    }

    private void save() {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("warps", null);
        for (Map.Entry<String, Location> e : warps.entrySet()) {
            String path = "warps." + e.getKey();
            Location loc = e.getValue();
            cfg.set(path + ".world", loc.getWorld().getName());
            cfg.set(path + ".x", loc.getX());
            cfg.set(path + ".y", loc.getY());
            cfg.set(path + ".z", loc.getZ());
            cfg.set(path + ".yaw", (double) loc.getYaw());
            cfg.set(path + ".pitch", (double) loc.getPitch());
        }
        plugin.saveConfig();
    }
}

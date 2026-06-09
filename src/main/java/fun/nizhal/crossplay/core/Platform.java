package fun.nizhal.crossplay.core;

import org.bukkit.Bukkit;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public final class Platform {

    private final boolean floodgate;

    public Platform() {
        this.floodgate = Bukkit.getPluginManager().getPlugin("floodgate") != null;
    }

    public boolean hasFloodgate() {
        return floodgate;
    }

    /** Returns true if the UUID belongs to a Bedrock player via Floodgate. */
    public boolean isBedrock(UUID uuid) {
        if (!floodgate) return false;
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }
}

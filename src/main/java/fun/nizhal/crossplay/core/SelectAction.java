package fun.nizhal.crossplay.core;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface SelectAction {
    /** Always called on the main server thread. */
    void run(Player player, String value);
}
